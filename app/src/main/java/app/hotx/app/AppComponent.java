package app.hotx.app;


import javax.inject.Singleton;

import app.hotx.activity.MainActivity;
import app.hotx.activity.SplashScreenActivity;
import app.hotx.activity.UploadVideoActivity;
import app.hotx.adapter.HotxVideosListAdapter;
import app.hotx.adapter.MainVideoListRecyclerAdapter;
import app.hotx.adapter.VideoFragmentPagerAdapter;
import app.hotx.dialog.FiltersDialogBuilder;
import app.hotx.dialog.PhFiltersDialogBuilder;
import app.hotx.fragment.CategoriesFragment;
import app.hotx.fragment.FavoritesFragment;
import app.hotx.fragment.HostMainFragment;
import app.hotx.fragment.HotxFragment;
import app.hotx.fragment.ProfileFragment;
import app.hotx.fragment.SearchFragment;
import app.hotx.fragment.StarFragment;
import app.hotx.fragment.StarsAllFragment;
import app.hotx.fragment.VideoFragment;
import app.hotx.fragment.VideoInfoFragment;
import app.hotx.fragment.VideoRelatedFragment;
import app.hotx.helper.AppHelper;
import app.hotx.helper.Toolbar;
import app.hotx.helper.UpdateChecker;
import app.hotx.helper.Utils;
import app.hotx.log.ViewScreenSession;
import app.hotx.networking.PHWebServiceCallback;
import app.hotx.networking.WebServiceModule;
import app.hotx.social.RedditModule;
import dagger.Component;

@Singleton
@Component(modules = {WebServiceModule.class, AppModule.class, RedditModule.class})
public interface AppComponent {
    void inject(SplashScreenActivity splashScreenActivity);
    void inject(MainActivity mainActivity);
    void inject(UploadVideoActivity uploadVideoActivity);

    void inject(HostMainFragment fragment);
    void inject(SearchFragment fragment);
    void inject(FavoritesFragment fragment);
    void inject(VideoFragment fragment);
    void inject(VideoInfoFragment fragment);
    void inject(VideoRelatedFragment fragment);
    void inject(CategoriesFragment fragment);
    void inject(ProfileFragment fragment);
    void inject(HotxFragment fragment);
    void inject(StarFragment fragment);
    void inject(StarsAllFragment fragment);

    void inject(VideoFragmentPagerAdapter adapter);
    void inject(MainVideoListRecyclerAdapter adapter);
    void inject(HotxVideosListAdapter adapter);

    void inject(FiltersDialogBuilder filtersDialogBuilder);
    void inject(PhFiltersDialogBuilder filtersDialogBuilder);
    void inject(Toolbar view);

    void inject(PHWebServiceCallback callback);

    void inject(UpdateChecker updateChecker);
    void inject(ViewScreenSession session);
    void inject(Utils utils);

    AppHelper getAppHelper();

}