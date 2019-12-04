package app.hotx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import app.hotx.R;
import app.hotx.app.App;
import app.hotx.eventbus.Event;
import app.hotx.eventbus.RxBus;
import app.hotx.fragment.HostMainFragment;
import app.hotx.fragment.HostVideoFragment;
import app.hotx.fragment.SearchFragment;
import app.hotx.fragment.StarFragment;
import app.hotx.helper.AppHelper;
import app.hotx.helper.Const;
import app.hotx.helper.Settings;
import app.hotx.helper.UpdateChecker;
import app.hotx.log.Analytics;
import app.hotx.log.ViewScreenSession;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {
    final String TAG = "MainActivity";

    private BottomNavigationView bottomNavigationView;
    private BottomSheetBehavior bottomSheetBehavior;
    private View overlay;

    private HostVideoFragment hostVideoFragment;
    private HostMainFragment hostMainFragment;

    private UpdateChecker updateChecker;
    private Disposable searchStarSubscriber;
    private Disposable searchTagSubscriber;
    private Disposable searchCategorySubscriber;
    private Disposable categoryItemClickSubscriber;
    private Disposable videoItemClickSubscriber;
    private Disposable starItemClickSubscriber;
    private Disposable starTagClickSubscriber;
    private Disposable videoPreviewRemoveClickSubscriber;

    private ViewScreenSession session = new ViewScreenSession(Analytics.Value.MAIN);

    @Inject
    AppHelper appHelper;
    @Inject
    Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        App.getAppComponent().inject(this);
        setContentView(R.layout.activity_main);
        updateChecker = new UpdateChecker(this);
        if (AppHelper.isClientBuild())
            updateChecker.checkForUpdate();
        session.start();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        overlay = findViewById(R.id.overlay);

        hostVideoFragment = (HostVideoFragment) getSupportFragmentManager().findFragmentById(R.id.video_fragment);
        hostMainFragment = (HostMainFragment) getSupportFragmentManager().findFragmentById(R.id.main_content);

        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.video_fragment));
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
//                AppHelper.log(this, "onStateChange " + newState);
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    RxBus.INSTANCE.publish(new Event.VideoPanelStateChanged(Const.STATE_COLLAPSED));
                    getSupportFragmentManager().beginTransaction().setPrimaryNavigationFragment(hostMainFragment).commit();
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    RxBus.INSTANCE.publish(new Event.VideoPanelStateChanged(Const.STATE_EXPANDED));
                    getSupportFragmentManager().beginTransaction().setPrimaryNavigationFragment(hostVideoFragment).commit();
                    if (bottomSheetBehavior.getPeekHeight() == 0) {
                        // pop bottomSheet up for VideoPreview
                        bottomSheetBehavior.setPeekHeight(getResources().getDimensionPixelOffset(R.dimen.video_player_view_height_collapsed));
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//                AppHelper.log(this, "onSlide " + slideOffset);
                RxBus.INSTANCE.publish(new Event.VideoPanelSlideOffsetChanged(slideOffset));
                bottomNavigationView.setTranslationY(bottomNavigationView.getHeight() * slideOffset);
            }
        });
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        setupNavigation();

        searchStarSubscriber = RxBus.INSTANCE.listen(Event.SearchStar.class).subscribe(searchStar -> {
            collapseVideoFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Const.KEY_STAR, searchStar.star);
            bundle.putString(Analytics.Param.ORIGIN, Analytics.Value.STARS_ALL);
            openNewStarFragment(bundle);
        });
        starTagClickSubscriber = RxBus.INSTANCE.listen(Event.StarTagClick.class).subscribe(starTagClick -> {
            collapseVideoFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Const.KEY_STAR, starTagClick.starTag);
            bundle.putString(Analytics.Param.ORIGIN, Analytics.Value.VIDEO);
            openNewStarFragment(bundle);
        });
        searchTagSubscriber = RxBus.INSTANCE.listen(Event.SearchTag.class).subscribe(searchTag -> {
            collapseVideoFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Const.KEY_TAG, searchTag.tag);
            bundle.putString(Analytics.Param.ORIGIN, Analytics.Value.VIDEO);
            openNewSearchFragment(bundle);
        });
        searchCategorySubscriber = RxBus.INSTANCE.listen(Event.SearchCategory.class).subscribe(searchCategory -> {
            collapseVideoFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Const.KEY_CATEGORY_ID, searchCategory.categoryId);
            bundle.putString(Analytics.Param.ORIGIN, Analytics.Value.VIDEO);
            openNewSearchFragment(bundle);
        });

        categoryItemClickSubscriber = RxBus.INSTANCE.listen(Event.CategoryItemClick.class).subscribe(categoryItemClickEvent -> {
            collapseVideoFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Const.KEY_CATEGORY_ID, categoryItemClickEvent.category.getId());
            bundle.putString(Analytics.Param.ORIGIN, Analytics.Value.CATEGORIES);
            openNewSearchFragment(bundle);
        });
        videoItemClickSubscriber = RxBus.INSTANCE.listen(Event.VideoItemClick.class).subscribe(videoItemClickEvent -> {
            openNewVideoFragment(videoItemClickEvent.video.getVkey(), videoItemClickEvent.origin);
        });
        starItemClickSubscriber = RxBus.INSTANCE.listen(Event.StarItemClick.class).subscribe(starItemClickEvent -> {
            collapseVideoFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Const.KEY_STAR, starItemClickEvent.star.getSlug());
            bundle.putString(Analytics.Param.ORIGIN, Analytics.Value.STARS_ALL);
            openNewStarFragment(bundle);
        });
        videoPreviewRemoveClickSubscriber = RxBus.INSTANCE.listen(Event.VideoPreviewRemoveClick.class).subscribe(videoPreviewRemoveClick -> {
            bottomSheetBehavior.setPeekHeight(0);
            hostVideoFragment.removeAllFragments();
        });

        checkDataFromIntent();
    }

    private void openNewSearchFragment(Bundle bundle) {
        hostMainFragment.openNewFragment(SearchFragment.newInstance(bundle));
    }

    private void openNewStarFragment(Bundle bundle) {
        hostMainFragment.openNewFragment(StarFragment.newInstance(bundle));
    }

    @Override
    protected void onPause() {
        super.onPause();
        overlay.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (settings.isPasswordUsed()) {
            appHelper.showPinDialog(getSupportFragmentManager(), (dialog, password) -> {
                if (password.equals(settings.getPassword())) {
                    overlay.setVisibility(View.GONE);
                    dialog.dismiss();
                } else {
                    dialog.animateWrongPassword();
                }
            }, dialog -> finish());
        } else {
            overlay.setVisibility(View.GONE);
        }
    }

    public void openNewVideoFragment(String vkey, String origin) {
        appHelper.hideKeyboard(this);
        hostVideoFragment.openNewVideo(vkey, origin);
        expandVideoFragment();
    }

    public void collapseVideoFragment() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public void expandVideoFragment() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void setupNavigation() {
        getSupportFragmentManager().beginTransaction()
                .setPrimaryNavigationFragment(hostMainFragment)
                .commit();

        setDefaultFragment();

        setupBottomNavigationView(bottomNavigationView);
    }

    private void setDefaultFragment() {
        hostMainFragment.navigationItemSelected(R.id.action_search);
        bottomNavigationView.getMenu().findItem(R.id.action_search).setChecked(true);
    }

    private void setupBottomNavigationView(BottomNavigationView bottomNavigationView) {
//        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
//        layoutParams.setBehavior(new BottomNavigationViewBehavior());
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            appHelper.hideKeyboard(this);

            if (bottomNavigationView.getSelectedItemId() == item.getItemId())
                return true;

            hostMainFragment.navigationItemSelected(item.getItemId());
            collapseVideoFragment();
            return true;
        });
    }

    private void checkDataFromIntent() {
        if (getIntent() != null) {
            String vkey = getIntent().getStringExtra(Const.KEY_VIDEO_VKEY);
            if (vkey != null) {
                ViewTreeObserver vto = bottomNavigationView.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        bottomNavigationView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        openNewVideoFragment(vkey, Analytics.Value.BROWSER_LINK);
                        session.setOrigin(Analytics.Value.BROWSER_LINK);
                        session.start();
                    }
                });
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            String vkey = intent.getStringExtra(Const.KEY_VIDEO_VKEY);
            if (vkey != null) {
                openNewVideoFragment(vkey, Analytics.Value.BROWSER_LINK);
                session.setOrigin(Analytics.Value.BROWSER_LINK);
                session.start();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (hostVideoFragment.getFragmentsCount() == 1 &&
                bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) { // do not remove last video, keep it collapsed
            collapseVideoFragment();
        } else
            super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        updateChecker.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        session.stop();
        searchStarSubscriber.dispose();
        starTagClickSubscriber.dispose();
        searchTagSubscriber.dispose();
        searchCategorySubscriber.dispose();
        categoryItemClickSubscriber.dispose();
        videoItemClickSubscriber.dispose();
        starItemClickSubscriber.dispose();
        videoPreviewRemoveClickSubscriber.dispose();
    }

}
