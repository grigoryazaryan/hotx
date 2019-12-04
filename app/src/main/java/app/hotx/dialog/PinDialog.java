package app.hotx.dialog;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.LinearLayout;

import javax.inject.Inject;

import app.hotx.R;
import app.hotx.helper.AppHelper;
import app.hotx.helper.Const;

/**
 * Created by Grigory Azaryan on 10/14/18.
 */

public class PinDialog extends DialogFragment {

    private LinearLayout pinDots;
    private EditText invisibleInput;
    private View reset;

    private PasswordListener passwordListener;
    private CancelListener cancelListener;

    @Inject
    AppHelper appHelper;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.enter_pin_layout, null);

        pinDots = view.findViewById(R.id.enter_pin_dots);
        invisibleInput = view.findViewById(R.id.enter_pin_focus);
        reset = view.findViewById(R.id.reset);

        reset.setOnClickListener(v -> {
            if (invisibleInput.getText().length() == 0)
                animateWrongPassword();
            invisibleInput.setText("");
        });

        invisibleInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                Log.v("onTextChanged", s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
//                Log.v("afterTextChanged", s.toString());
                for (int i = 0; i < 4; i++) {
                    ((ViewGroup) pinDots.getChildAt(i)).getChildAt(0).setAlpha(s.length() > i ? 1 : 0);
                }
                if (s.length() == 4) {
                    passwordListener.onPassword(PinDialog.this, s.toString());
                }
            }
        });


        invisibleInput.requestFocus();

        String title = "";
        if (getArguments() != null)
            title = getArguments().getString(Const.KEY_TITLE);

        builder.setTitle(title)
                .setView(view)
                .setNegativeButton(R.string.cancel, (a, b) -> {
                    if (cancelListener != null)
                        cancelListener.onCancel(this);
                });


        return builder.create();
    }

    public void animateWrongPassword() {
        TranslateAnimation shake = new TranslateAnimation(0, 20, 0, 0);
        shake.setDuration(500);
        shake.setInterpolator(new CycleInterpolator(7));
        pinDots.startAnimation(shake);
    }

    public PinDialog setPasswordListener(PasswordListener passwordListener) {
        this.passwordListener = passwordListener;
        return this;
    }

    public PinDialog setCancelListener(CancelListener cancelListener) {
        this.cancelListener = cancelListener;
        return this;
    }

    public interface PasswordListener {
        void onPassword(PinDialog dialog, String password);
    }

    public interface CancelListener {
        void onCancel(PinDialog dialog);
    }
}
