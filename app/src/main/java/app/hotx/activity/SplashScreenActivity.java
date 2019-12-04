package app.hotx.activity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import app.hotx.BuildConfig;
import app.hotx.R;
import app.hotx.app.App;
import app.hotx.helper.AppHelper;
import app.hotx.helper.Categories;
import app.hotx.helper.Const;
import app.hotx.helper.Utils;
import app.hotx.model.RegisterResponse;
import app.hotx.networking.ServerResponse;
import app.hotx.networking.ServerResponseParser;
import app.hotx.networking.WebService;
import app.hotx.networking.WebServiceCallback;

public class SplashScreenActivity extends AppCompatActivity {
    private final String TAG = "SplashScreenActivity";

    @Inject
    WebService webService;
    @Inject
    SharedPreferences sharedPreferences;
    @Inject
    ServerResponseParser serverResponseParser;
    @Inject
    Utils utils;
    @Inject
    AppHelper appHelper;
    @Inject
    Categories categories;

    private TextView status;
    private Button reload;
    private ImageView logo;
    private ObjectAnimator animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        App.getAppComponent().inject(this);

        status = findViewById(R.id.status);
        reload = findViewById(R.id.reload);
        logo = findViewById(R.id.logo);

        reload.setOnClickListener(v -> load());

        load();
    }

    private String getViewKeyFromIntent(Intent intent) {
        String appLinkAction = intent.getAction();
        Uri appLinkData = intent.getData();
        if (Intent.ACTION_VIEW.equals(appLinkAction) && appLinkData != null) {
            return appLinkData.getQueryParameter("viewkey");
        }
        return null;
    }

    private void startAnimation() {
        animation = ObjectAnimator.ofPropertyValuesHolder(logo,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f), PropertyValuesHolder.ofFloat("scaleY", 1.2f));
        animation.setDuration(2000);
        animation.setRepeatCount(ObjectAnimator.INFINITE);
        animation.setRepeatMode(ObjectAnimator.REVERSE);
        animation.start();
    }

    private void stopAnimation() {
        if (animation != null) animation.cancel();
    }

    private void load() {
        reload.setClickable(false);
        startAnimation();
        checkRegister(loadCategories(() -> {
            startActivity(new Intent(this, MainActivity.class).putExtra(Const.KEY_VIDEO_VKEY, getViewKeyFromIntent(getIntent())));
        })).run();
    }

    private Runnable checkRegister(Runnable callback) {
        return () -> {
            if (sharedPreferences.contains(Const.KEY_AUTH_TOKEN))
                callback.run();
            else {
                webService.auth(Locale.getDefault().getLanguage(), Build.MANUFACTURER + " " + Build.MODEL, Build.VERSION.SDK_INT,
                        BuildConfig.VERSION_CODE, utils.getDeviceID())
                        .enqueue(new WebServiceCallback() {
                            @Override
                            public void onResponse(@NotNull ServerResponse response) {
                                if (!response.getStatus()) {
                                    showError();
                                    return;
                                }
                                RegisterResponse registerResponse = serverResponseParser.parseRegisterResponse(response);
                                categories.setNotSupportedCategories(registerResponse.getNotSupportedCategories());
                                String token = registerResponse.getToken();
                                if (token != null && !token.isEmpty()) {
                                    sharedPreferences.edit().putString(Const.KEY_AUTH_TOKEN, token).apply();
                                }
                                callback.run();
                            }
                        });
            }
        };
    }

    private Runnable loadCategories(Runnable callback) {
        return () -> {
            if (!AppHelper.isClientBuild() && categories.getCategories().size() > 0) {
                callback.run();
                return;
            }
            categories.load(c -> {
                if (c.size() > 0)
                    callback.run();
                else
                    showError();
            });
        };
    }

    private void showError() {
        reload.setClickable(true);
        stopAnimation();
        reload.setVisibility(View.VISIBLE);
        status.setText(R.string.something_went_wrong);
    }
}
