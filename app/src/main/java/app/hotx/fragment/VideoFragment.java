package app.hotx.fragment;


import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import app.hotx.BuildConfig;
import app.hotx.R;
import app.hotx.activity.MainActivity;
import app.hotx.adapter.VideoFragmentPagerAdapter;
import app.hotx.app.App;
import app.hotx.eventbus.Event;
import app.hotx.eventbus.RxBus;
import app.hotx.helper.AppHelper;
import app.hotx.helper.Const;
import app.hotx.helper.OrientationChangedListener;
import app.hotx.helper.Settings;
import app.hotx.helper.Utils;
import app.hotx.helper.VideoQualitySelector;
import app.hotx.log.Analytics;
import app.hotx.log.Problem;
import app.hotx.log.ViewScreenSession;
import app.hotx.model.PHSmallVideo;
import app.hotx.model.PHSmallVideo_;
import app.hotx.model.PHVideo;
import app.hotx.networking.PHResponseParser;
import app.hotx.networking.PHWebService;
import app.hotx.networking.PHWebServiceCallback;
import app.hotx.networking.ServerResponse;
import app.hotx.networking.ServerResponseParser;
import app.hotx.networking.WebService;
import app.hotx.networking.WebServiceCallback;
import app.hotx.social.RedditClient;
import io.objectbox.BoxStore;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import okhttp3.HttpUrl;
import solid.collectors.ToSolidMap;
import solid.stream.Stream;


public class VideoFragment extends Fragment {

    private MainActivity activity;
    private AppBarLayout appBarLayout;
    private View rootView;
    private View progressLayout, reload, closeButton, downloadButton;
    private ProgressBar progressBar;
    private ImageView favorite, share;
    private TextView title, likesCount, viewsCount;
    private PHVideo video;
    private PlayerView exoPlayerView;
    private FrameLayout videoPlayerContainer;
    private View exoPlayerControls, exoPlayerControlsOverlay, exoPlayerControlFullscreen, exoPlayerControlOptions;
    private ImageView exoPlayerControlCollapse;
    private SimpleExoPlayer exoPlayer;
    private Dialog exoPlayerFullScreenDialog;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Observable<Integer> orientationObservable;
    private Disposable orientationChangedListener;
    private int screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    private boolean isOpened = true;
    private boolean isFullscreen = false;
    private Disposable videoPanelStateChangedSubscriber;
    private Disposable videoPanelSlideOffsetSubscriber;
    private Disposable fastForwardSettingsChangedSubscriber;
    private String currentPlayingLink;
    private ViewScreenSession session = new ViewScreenSession(Analytics.Value.VIDEO);

    private int expandedHeight, collapsedHeight, widthDiff;

    @Inject
    WebService webService;
    @Inject
    PHWebService phWebService;
    @Inject
    PHResponseParser phResponseParser;
    @Inject
    ServerResponseParser serverResponseParser;
    @Inject
    AppHelper appHelper;
    @Inject
    Utils utils;
    @Inject
    BoxStore boxStore;
    @Inject
    Settings settings;
    @Inject
    Analytics analytics;
    @Inject
    RedditClient redditClient;


    public VideoFragment() {
        // Required empty public constructor
        App.getAppComponent().inject(this);
    }

    public static VideoFragment newInstance(Bundle bundle) {
        VideoFragment videoFragment = new VideoFragment();
        videoFragment.setArguments(bundle);
        return videoFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) return rootView;
        View view = inflater.inflate(R.layout.fragment_player, container, false);

        appBarLayout = view.findViewById(R.id.app_bar_layout);
        progressLayout = view.findViewById(R.id.progress_layout);
        progressBar = view.findViewById(R.id.progress_bar);
        reload = view.findViewById(R.id.reload);

        videoPlayerContainer = view.findViewById(R.id.video_player_container);
        exoPlayerView = view.findViewById(R.id.video_player_view);
        exoPlayerControls = view.findViewById(R.id.exo_playback_control_view);
        exoPlayerControlsOverlay = view.findViewById(R.id.video_player_overlay);

        exoPlayerControlFullscreen = exoPlayerControls.findViewById(R.id.exo_fullscreen);
        exoPlayerControlOptions = exoPlayerControls.findViewById(R.id.exo_options);
        exoPlayerControlCollapse = exoPlayerControls.findViewById(R.id.exo_collapse);

        closeButton = view.findViewById(R.id.video_player_close_button);
        downloadButton = view.findViewById(R.id.download);
        title = view.findViewById(R.id.title);
        favorite = view.findViewById(R.id.favorite);
        share = view.findViewById(R.id.share);
        likesCount = view.findViewById(R.id.likes_count);
        viewsCount = view.findViewById(R.id.views_count);

        viewPager = view.findViewById(R.id.video_view_pager);
        tabLayout = view.findViewById(R.id.video_tabs);

        expandedHeight = getResources().getDimensionPixelSize(R.dimen.video_player_view_height);
        collapsedHeight = getResources().getDimensionPixelSize(R.dimen.video_player_view_height_collapsed) -
                getResources().getDimensionPixelSize(R.dimen.bottom_nav_bar_height);
        widthDiff = getResources().getDimensionPixelSize(R.dimen.video_player_close_button);

        exoPlayerControlsOverlay.setOnClickListener(v -> activity.expandVideoFragment());
        exoPlayerControlsOverlay.setClickable(false);

        downloadButton.setOnClickListener(v -> {
            if (currentPlayingLink != null)
                startDownload(currentPlayingLink, video.getTitle());
        });
        closeButton.setOnClickListener(v -> RxBus.INSTANCE.publish(new Event.VideoPreviewRemoveClick()));
        exoPlayerControlCollapse.setOnClickListener(v -> {
            if (isFullscreen) {
                setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                closeFullscreen();
            } else
                activity.collapseVideoFragment();
        });

        setupFullscreenButton();

        exoPlayer = ExoPlayerFactory.newSimpleInstance(activity, new DefaultRenderersFactory(getActivity()), new DefaultTrackSelector());
//        exoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);

        exoPlayerFullScreenDialog = new Dialog(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        exoPlayerFullScreenDialog.setCancelable(false);


        orientationObservable = OrientationChangedListener.createObserver(activity)
//                .debounce(700, TimeUnit.MILLISECONDS)
//                .distinctUntilChanged()
                .throttleWithTimeout(700, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread());

        rootView = view;
        return rootView;
    }

    private void setVideoData(PHVideo video) {
        if (activity == null) return;

        this.video = video;

        title.setText(video.getTitle());
        likesCount.setText(String.format(Locale.US, "%d%s", video.getRating(), '%'));
        viewsCount.setText(Utils.formatDecimal(video.getViewCount()));

        favorite.setColorFilter(ContextCompat.getColor(activity, boxStore.boxFor(PHSmallVideo.class).query()
                .equal(PHSmallVideo_.vkey, video.getVkey()).build().findFirst() != null ? R.color.colorPrimary : R.color.inactive));
        favorite.setOnClickListener(v -> {
            changeFavorite(video);
        });

        share.setOnClickListener(v -> {
            String link = Const.PHVideoBaseUrl + video.getVkey();

            ShareCompat.IntentBuilder.from(activity)
                    .setType("text/*")
                    .setChooserTitle(getText(R.string.share))
                    .setSubject(video.getTitle())
                    .setText(video.getTitle() + " " + link)
                    .startChooser();
        });

        webService.isFavorite(video.getVkey()).enqueue(new WebServiceCallback() {
            @Override
            public void onResponse(@NotNull ServerResponse response) {
                if (!response.getStatus()) return;
                setFavorite(response.getResult().getAsBoolean(), new PHSmallVideo(video));
            }
        });

        setupPlayerView(video);
        setupPlayer(video.getEncodings());

//        appBarLayout.setExpanded(true, true);

        viewPager.setAdapter(new VideoFragmentPagerAdapter(getChildFragmentManager(), video));
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupPlayerView(PHVideo video) {

//        if (video.isVr()) {
//            exoPlayerView = rootView.findViewById(R.id.video_player_spherical_view);
//            ((SphericalSurfaceView) exoPlayerView.getVideoSurfaceView())
//                    .setDefaultStereoMode(video.getVrStereoType() == 2 ? C.STEREO_MODE_TOP_BOTTOM : C.STEREO_MODE_LEFT_RIGHT);
//        } else {
//        exoPlayerView = rootView.findViewById(R.id.video_player_view);
//        }
//        exoPlayerView.setVisibility(View.VISIBLE);
        setupPlayerControls();
        exoPlayerView.setKeepScreenOn(true);
        exoPlayerView.setPlayer(exoPlayer);
        exoPlayerView.setControllerShowTimeoutMs(2000);
    }

    private void setupPlayerControls() {
        exoPlayerView.setFastForwardIncrementMs(settings.getFastForwardSec() * 1000);
        exoPlayerView.setRewindIncrementMs(settings.getFastRewindSec() * 1000);
    }

    private void setupPlayer(Map<String, String> links) {
        if (video.isVr()) {
            analytics.logProblem(new Problem(Analytics.Value.VIDEO, Analytics.Value.VR_NOT_SUPPORTED, video.getVkey()));
            appHelper.showToast(R.string.vr_video_not_supported_yet);
            return;
        }
        if (links == null || links.size() == 0) {
            analytics.logProblem(new Problem(Analytics.Value.VIDEO, Analytics.Value.NO_LINKS, video.getVkey()));
            appHelper.showToast(R.string.video_has_been_removed);
            return;
        }

        session.setOrigin(getArguments().getString(Analytics.Param.ORIGIN));
        session.start();

        Map<String, String> validLinks = Stream.stream(links.entrySet()).filter(e -> HttpUrl.parse(e.getValue()) != null)
                .collect(ToSolidMap.toSolidMap()).asMap();

        String minimalQuality = validLinks.keySet().iterator().next();
        String defaultQuality = settings.getPreferredQuality();
        if (!validLinks.containsKey(defaultQuality))
            defaultQuality = minimalQuality;
        VideoQualitySelector videoQualitySelector = new VideoQualitySelector(activity, validLinks)
                .setItemSelectedListener((position, selected) -> {
                    currentPlayingLink = validLinks.get(selected);
                    settings.setPreferredQuality(selected);
                    MediaSource mediaSource = new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory(BuildConfig.APPLICATION_ID))
                            .createMediaSource(Uri.parse(currentPlayingLink));
                    exoPlayer.prepare(mediaSource, false, false);
                }).setSelectedItem(defaultQuality);
        exoPlayerControlOptions.setOnClickListener(v -> videoQualitySelector.show());


        currentPlayingLink = validLinks.get(defaultQuality);
        MediaSource mediaSource = new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory(BuildConfig.APPLICATION_ID))
                .createMediaSource(Uri.parse(currentPlayingLink));
        exoPlayer.prepare(mediaSource, false, false);
        exoPlayer.setPlayWhenReady(true);
    }

    private void changeFavorite(PHVideo video) {
        webService.setFavorite(video.getVkey())
                .enqueue(new WebServiceCallback() {
                    @Override
                    public void onResponse(@NotNull ServerResponse response) {
                        if (!response.getStatus()) return;
                        boolean isSaved = response.getResult().getAsBoolean();
                        setFavorite(isSaved, new PHSmallVideo(video));
//                        if (!AppHelper.isClientBuild() && isSaved) redditClient.submitVideo(new PHSmallVideo(video));
                    }
                });
    }

    private void setFavorite(boolean isFavorite, PHSmallVideo video) {
        if (activity == null) return;

        favorite.setColorFilter(ContextCompat.getColor(activity, isFavorite ? R.color.colorPrimary : R.color.inactive));
        favorite.setTag(isFavorite ? "favorite" : null);

        PHSmallVideo saved = boxStore.boxFor(PHSmallVideo.class).query()
                .equal(PHSmallVideo_.vkey, video.getVkey()).build().findFirst();

        if (isFavorite) {
            if (saved == null) {
                boxStore.boxFor(PHSmallVideo.class).put(video);
            }
        } else {
            boxStore.boxFor(PHSmallVideo.class).query().equal(PHSmallVideo_.vkey, video.getVkey()).build().remove();
        }
    }


    public void onClose() {
        isOpened = false;
//        orientationChangedListener.disable();
//        if (exoPlayer != null) {
//            exoPlayer.seekTo(0);
//            exoPlayer.setPlayWhenReady(false);
//        }
    }

    public void onOpen() {
        isOpened = true;
//        orientationChangedListener.enable();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity)
            activity = ((MainActivity) context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
        exoPlayer.release();
    }

    private void setVideoContainerSize(int width, int height) {
        ViewGroup.LayoutParams params = videoPlayerContainer.getLayoutParams();
        params.width = width;
        params.height = height;
        videoPlayerContainer.setLayoutParams(params);
    }

    @Override
    public void onResume() {
        super.onResume();
        videoPanelStateChangedSubscriber = RxBus.INSTANCE.listen(Event.VideoPanelStateChanged.class)
                .subscribe(changed -> {
                    isOpened = changed.state == Const.STATE_EXPANDED;
                    exoPlayerControlsOverlay.setClickable(!isOpened);
                    exoPlayerControls.setVisibility(changed.state == Const.STATE_EXPANDED ? View.VISIBLE : View.GONE);
                    appBarLayout.setExpanded(true, true);
                    if (isOpened) setVideoContainerSize(appBarLayout.getWidth(), expandedHeight);
                });
        videoPanelSlideOffsetSubscriber = RxBus.INSTANCE.listen(Event.VideoPanelSlideOffsetChanged.class)
                .subscribe(changed -> {
                    setVideoContainerSize((int) (appBarLayout.getWidth() - widthDiff * (1 - changed.offset)),
                            (int) (collapsedHeight + (expandedHeight - collapsedHeight) * changed.offset));
                });
        fastForwardSettingsChangedSubscriber = RxBus.INSTANCE.listen(Event.FastForwardSettingsChanged.class)
                .subscribe(fastForwardSettingsChanged -> setupPlayerControls());

        orientationChangedListener = orientationObservable.subscribe(newOrientation -> {
            screenOrientation = newOrientation;
            if (!isOpened) return;
            if (settings.isAutoFullscreen()) {
                if (newOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || newOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                    setScreenOrientation(newOrientation);
                    if (!isFullscreen)
                        openFullscreen();
                } else {
                    setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    if (isFullscreen) {
                        closeFullscreen();
                    }
                }
            } else {
                if (newOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || newOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                    if (isFullscreen)
                        setScreenOrientation(newOrientation);
                }
            }
        });

        if (video != null) {
            setupPlayer(video.getEncodings());
            exoPlayer.setPlayWhenReady(false);
        } else
            loadVideo(getArguments().getString(Const.KEY_VIDEO_VKEY));
    }

    @Override
    public void onPause() {
        super.onPause();
        session.stop();
        orientationChangedListener.dispose();
        videoPanelStateChangedSubscriber.dispose();
        videoPanelSlideOffsetSubscriber.dispose();
        fastForwardSettingsChangedSubscriber.dispose();
//        if (exoPlayer != null) exoPlayer.setPlayWhenReady(false);
        exoPlayer.stop();
    }

    private void loadVideo(String vkey) {
        reload.setOnClickListener(v -> loadVideo(vkey));
        showLoading();
        phWebService.getVideo(Const.PHAppKey, utils.getDeviceID(), vkey).enqueue(new PHWebServiceCallback(response -> {
            if (!response.isSuccess()) {
                analytics.logPhError(response.getCode(), response.getMessage());
                if (response.getCode() == PHWebService.ERR_CODE_PREMIUM_CONTENT) {
                    appHelper.showToast(R.string.premium_content_not_supported);
                } else {
                    appHelper.showToast(response.getMessage());
                }
                showReload();
                return;
            }

            hideLoading();

            setVideoData(phResponseParser.parseVideo(response.getData()));
        }));
    }

    public boolean onBackPressed() {
        return false;
    }

    private void setupFullscreenButton() {
        exoPlayerControlFullscreen.setOnClickListener(v -> {
            if (isFullscreen) {
                setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                closeFullscreen();
            } else {
                if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE && screenOrientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE)
                    screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                setScreenOrientation(screenOrientation);
                openFullscreen();
            }
        });
    }

    private void showLoading() {
        progressLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        reload.setVisibility(View.GONE);
    }

    private void hideLoading() {
        progressLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        reload.setVisibility(View.GONE);
    }

    private void showReload() {
        progressLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        reload.setVisibility(View.VISIBLE);
    }

    private void setScreenOrientation(int screenOrientation) {
        activity.setRequestedOrientation(screenOrientation);
    }

    private void openFullscreen() {
        exoPlayerFullScreenDialog.show();
        ((ViewGroup) exoPlayerView.getParent()).removeView(exoPlayerView);
        exoPlayerFullScreenDialog.addContentView(exoPlayerView,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        exoPlayerControlCollapse.setImageResource(R.drawable.ic_back_arrow);

        isFullscreen = true;
    }

    private void closeFullscreen() {
        ((ViewGroup) exoPlayerView.getParent()).removeView(exoPlayerView);
        videoPlayerContainer.addView(exoPlayerView, 0,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.video_player_view_height)));
        exoPlayerFullScreenDialog.dismiss();
        exoPlayerControlCollapse.setImageResource(R.drawable.ic_arrow_down);

        isFullscreen = false;
    }

    private void startDownload(String downloadPath, String fileName) {
        Uri uri = Uri.parse(downloadPath);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle(fileName);
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalFilesDir(getContext(), Environment.DIRECTORY_DOWNLOADS, fileName);
        ((DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request);
    }
}
