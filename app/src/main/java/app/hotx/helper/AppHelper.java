package app.hotx.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import javax.inject.Inject;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import app.hotx.BuildConfig;
import app.hotx.R;
import app.hotx.activity.SplashScreenActivity;
import app.hotx.dialog.PinDialog;

public class AppHelper {

    final Context context;
    final SharedPreferences sharedPreferences;

    @Inject
    public AppHelper(Context context, SharedPreferences sharedPreferences) {
        this.context = context;
        this.sharedPreferences = sharedPreferences;
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocus = activity.getCurrentFocus();
        if (currentFocus != null) {
            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
            currentFocus.clearFocus();
        }
    }

    public void showToast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public void showToast(int textResourceId) {
        Toast.makeText(context, textResourceId, Toast.LENGTH_SHORT).show();
    }

    public StateListDrawable createClickableViewBackground(@DrawableRes int id) {
        StateListDrawable res = new StateListDrawable();
        res.setExitFadeDuration(50);
        Drawable drawable = ContextCompat.getDrawable(context, id);
        Drawable drawablePressed = ContextCompat.getDrawable(context, id);
        drawablePressed.setColorFilter(new PorterDuffColorFilter(Color.TRANSPARENT, PorterDuff.Mode.MULTIPLY));
        res.addState(new int[]{android.R.attr.state_pressed}, drawablePressed);
        res.addState(new int[]{}, drawable);
        return res;
    }

    public void showPinDialog(FragmentManager fragmentManager, PinDialog.PasswordListener passwordListener, PinDialog.CancelListener cancelListener) {
        PinDialog pinDialog = (PinDialog) fragmentManager.findFragmentByTag(Const.TAG_DIALOG_PIN);
        if (pinDialog != null && pinDialog.isAdded()) {
//            pinDialog.show(fragmentManager, Const.TAG_DIALOG_PIN);
            return;
        }

        final PinDialog dialog = new PinDialog();
        dialog.setCancelListener(cancelListener)
                .setPasswordListener(passwordListener);

        Bundle bundle = new Bundle();
        bundle.putString(Const.KEY_TITLE, context.getString(R.string.enter_password));
        dialog.setArguments(bundle);
        dialog.show(fragmentManager, Const.TAG_DIALOG_PIN);

    }

    public void hidePinDialog(FragmentManager fragmentManager) {
        PinDialog pinDialog = (PinDialog) fragmentManager.findFragmentByTag(Const.TAG_DIALOG_PIN);
        if (pinDialog != null)
            pinDialog.dismiss();
    }

    public void performLogout(){
        sharedPreferences.edit().remove(Const.KEY_AUTH_TOKEN).apply();
        context.startActivity(new Intent(context, SplashScreenActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public static void log(Object object, String message) {
        if (BuildConfig.FLAVOR.equalsIgnoreCase("manager"))
            Log.v(object.getClass().getName(), message);
    }

    public static boolean isClientBuild() {
        return BuildConfig.FLAVOR == "user";
    }

    public static void payWithYandex() {
//                val phoneParams = ShopParams("p2p",
//                        mapOf("amount" to "12", "destination" to "to-account", "to" to "grigory.azaryan2017@yandex.ru", "test_payment" to "true",
// "test_card" to "available", "test_result" to "success"))
//                val intent = PaymentActivity.getBuilder(context!!)
//                        .setPaymentParams(phoneParams)
//                        .setClientId("552B6009A9F70C0AD9CAE7024CAE99124EEB2DC69C3456D7F9146A6C92D9307D")
//                        .build()
//                startActivityForResult(intent, 1)
    }

}