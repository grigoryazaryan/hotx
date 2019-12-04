package app.hotx.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import javax.inject.Singleton;

import app.hotx.helper.Utils;
import app.hotx.log.Analytics;
import app.hotx.model.MyObjectBox;
import dagger.Module;
import dagger.Provides;
import io.objectbox.BoxStore;

@Module
public class AppModule {

    private Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Singleton
    @Provides
    public Context provideContext() {
        return application;
    }

    @Singleton
    @Provides
    public Gson provideGson() {
        return new Gson();
    }

    @Singleton
    @Provides
    public SharedPreferences providePreferences(Context context) {
        return context.getSharedPreferences("app-prefs", Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    public Utils provideUtils(SharedPreferences preferences, Context context) {
        return new Utils(preferences, context);
    }

    @Provides
    @Singleton
    public BoxStore provideBoxStore(Context context) {
        return MyObjectBox.builder().androidContext(context).build();
    }

    @Singleton
    @Provides
    public Analytics provideAnalytics(Context context) {
        return new Analytics(context);
    }
}
