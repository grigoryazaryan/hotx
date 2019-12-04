package app.hotx.helper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class GifFromImages {
    private Context context;
    private ImageView imageView;
    private String[] urls;
    private Handler h = new Handler();
    private boolean isPlaying = false;
    private RequestBuilder<Drawable> requestBuilder;
    private RequestListener<Drawable> requestListener;
    private int i = 0;

    public GifFromImages(Context context, ImageView imageView, String[] urls) {
        this.context = context;
        this.imageView = imageView;
        this.urls = urls;

        requestBuilder = Glide.with(context.getApplicationContext()).asDrawable();
        requestListener = new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                if (isPlaying) h.postDelayed(() -> loadNext(), 700);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                if (isPlaying) h.postDelayed(() -> loadNext(), 700);
                return false;
            }
        };
    }

    public void start() {
        if (isPlaying) return;
        isPlaying = true;
        i = 0;
        loadNext();
    }

    private void loadNext() {
        String thumb = urls[i % urls.length];
        requestBuilder.load(thumb)
                .listener(requestListener)
//                .transition(DrawableTransitionOptions.withCrossFade(200))
                .into(imageView);
        i++;
    }

    public void stop() {
        isPlaying = false;
        h.removeCallbacksAndMessages(null);
    }

    public boolean isPlaying() {
        return isPlaying;
    }
}
