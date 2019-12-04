package app.hotx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import app.hotx.R;
import app.hotx.app.App;
import app.hotx.helper.AppHelper;
import app.hotx.helper.Utils;
import app.hotx.model.VideoObject;
import app.hotx.networking.ServerResponse;
import app.hotx.networking.WebService;
import app.hotx.networking.WebServiceCallback;


public class UploadVideoActivity extends AppCompatActivity {
    final String TAG = "VideoPlayerActivity";

    @Inject
    Utils utils;
    @Inject
    WebService webService;
    @Inject
    AppHelper appHelper;

    private Button send;
    private TextView tvlink, tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
        setContentView(R.layout.activity_video_player);

        send = findViewById(R.id.send);
        tvlink = findViewById(R.id.link);
        tvStatus = findViewById(R.id.upload_status);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null && "message/rfc822".equals(type)) {
            String link = intent.getStringExtra(Intent.EXTRA_TEXT);
            Log.v(TAG, link);
            Log.v(TAG, intent.getClipData().getItemAt(0).getText().toString());
            VideoObject video = new VideoObject();
            video.setLink(link);

            tvlink.setText(link);
            send.setOnClickListener(v -> {
                tvStatus.setText("uploading");
                webService.uploadVideo(link).enqueue(new WebServiceCallback() {
                    @Override
                    public void onResponse(@NotNull ServerResponse response) {
                        if (!response.getStatus()) {
                            tvStatus.setText(String.format("error: %s", response.getMessage()));
                        } else {
                            appHelper.showToast("ok!");
                            finish();
                        }
                    }
                });
            });
        } else
            finish();
    }
}
