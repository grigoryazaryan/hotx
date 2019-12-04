package app.hotx.networking;

import android.content.SharedPreferences;

import java.io.IOException;

import javax.inject.Inject;

import app.hotx.helper.Const;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInterceptor implements Interceptor {
    private final String TAG = "HeaderInterceptor";

    private SharedPreferences preferences;

    @Inject
    public HeaderInterceptor(SharedPreferences sharedPreferences) {
        this.preferences = sharedPreferences;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        Response response;
//        if (original.url().toString().contains("user/register") && !preferences.contains(Const.KEY_AUTH_TOKEN)) {
//            response = chain.proceed(original);
//        } else {
            Request request = original.newBuilder()
                    .header("Authorization", "xxx-app <" + preferences.getString(Const.KEY_AUTH_TOKEN, "") + ">").build();
            response = chain.proceed(request);
//        }

        return response;
    }
}