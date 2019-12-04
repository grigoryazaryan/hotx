package app.hotx.app;

import android.app.Application;
import android.content.Context;
import android.provider.Settings;

import com.crashlytics.android.Crashlytics;

import androidx.multidex.MultiDex;
import app.hotx.helper.AppHelper;


public class App extends Application {
    private static AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        if (!AppHelper.isClientBuild()) {
//            StrictMode.VmPolicy policy = new StrictMode.VmPolicy.Builder()
//                    .detectAll()
//                    .penaltyLog()
//                    .build();
//            StrictMode.setVmPolicy(policy);
        }

        appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();

        Crashlytics.setUserIdentifier(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static AppComponent getAppComponent() {
        return appComponent;
    }
}
