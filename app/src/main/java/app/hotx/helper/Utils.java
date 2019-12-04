package app.hotx.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.provider.Settings;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.Locale;

import javax.inject.Inject;

public class Utils {

    SharedPreferences preferences;
    Context context;

    @Inject
    public Utils(SharedPreferences preferences, Context context) {
        this.preferences = preferences;
        this.context = context;
    }

    public String getDeviceID() {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }


    public static String toString(Object abc) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Field field : abc.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            String name = field.getName();
            Object value = null;
            try {
                value = field.get(abc);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            stringBuilder.append(name).append(": ").append(value).append("\n");
        }
        return stringBuilder.toString();
    }

    public static String formatDecimal(double number) {
        return new DecimalFormat("#,###").format(number).replace(',', ' ');
    }

    public static String formatSecondsToTime(long seconds) {
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        if (h > 0) return String.format(Locale.ENGLISH, "%d:%02d:%02d", h, m, s);
        return String.format(Locale.ENGLISH, "%02d:%02d", m, s);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(float px) {
        return (int) (px * Resources.getSystem().getDisplayMetrics().density);
    }
}
