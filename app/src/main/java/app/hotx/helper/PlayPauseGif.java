package app.hotx.helper;

import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.transition.Transition;

public class PlayPauseGif extends DrawableImageViewTarget {
    private final String TAG = "PlayPauseGif";
    GifDrawable gifDrawable;

    public PlayPauseGif(ImageView view) {
        super(view);
    }

    @Override
    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
        super.onResourceReady(resource, transition);
        Log.v(TAG, "onResourceReady");
        if (resource instanceof GifDrawable) {
            gifDrawable = ((GifDrawable) resource);
            gifDrawable.stop();
        }
    }

    public void stop() {
        if (gifDrawable != null && gifDrawable.isRunning())
            gifDrawable.stop();
    }

    public void start() {
        if (gifDrawable != null && !gifDrawable.isRunning())
            gifDrawable.start();
    }
}
