package app.hotx.helper;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.SensorManager;
import android.view.OrientationEventListener;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public class OrientationChangedListener {

    public static Observable<Integer> createObserver(Context context) {
        BehaviorSubject<Integer> subject = BehaviorSubject.create();

        new OrientationEventListener(context, SensorManager.SENSOR_DELAY_NORMAL) {

            private int screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            private final int LANDSCAPE_DEG = 270, REVERSE_LANDSCAPE_DEG = 90, REVERSE_PORTRAIT = 180;
            private int spectrum = 30;

            @Override
            public void onOrientationChanged(int deg) {
//                Log.v("OrientationChanged", "" + deg);
                if (Math.abs(deg - LANDSCAPE_DEG) < 45) {
                    if (Math.abs(deg - LANDSCAPE_DEG) < spectrum) {
                        if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                            subject.onNext(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                            screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                        }
                    }
                } else if (Math.abs(deg - REVERSE_LANDSCAPE_DEG) < 45) {
                    if (Math.abs(deg - REVERSE_LANDSCAPE_DEG) < spectrum) {
                        if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                            subject.onNext(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                            screenOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                        }
                    }
                } else if (deg != -1) {
                    if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                        subject.onNext(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    }
                }

            }
        }.enable();

        return subject;
    }
}
