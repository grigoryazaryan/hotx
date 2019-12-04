package app.hotx.helper;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import app.hotx.BuildConfig;
import app.hotx.R;
import app.hotx.app.App;
import app.hotx.networking.ServerResponse;
import app.hotx.networking.WebService;
import app.hotx.networking.WebServiceCallback;

import static android.content.Context.DOWNLOAD_SERVICE;

public class UpdateChecker {
    final static String TAG = "UpdateChecker";
    private static final int REQUEST_WRITE_PERMISSION = 2;

    private String apkUrl;
    private String fileName = "hotx.apk";
    private AppCompatActivity activity;

    @Inject
    WebService webService;

    public UpdateChecker(AppCompatActivity activity) {
        App.getAppComponent().inject(this);
        this.activity = activity;
    }

    public UpdateChecker checkForUpdate() {
        webService.getVersion().enqueue(new WebServiceCallback() {
            @Override
            public void onResponse(@NotNull ServerResponse response) {
                if (response.getStatus()) {
                    apkUrl = response.getResult().getAsJsonObject().get("file").getAsString();
                    int newVersion = response.getResult().getAsJsonObject().get("current").getAsInt();
                    if (BuildConfig.VERSION_CODE >= newVersion) return;
                    new AlertDialog.Builder(activity)
                            .setTitle(R.string.update_available)
                            .setMessage(R.string.download_now)
                            .setNegativeButton(R.string.cancel, null)
                            .setPositiveButton(R.string.download, (dialog, which) -> {
                                ActivityCompat.requestPermissions(activity,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
                            }).show();
                }
            }
        });
        return this;
    }

    void startDownloadManager(String apkDownloadUrl) {
        String apkPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + fileName;
        File apkFile = new File(apkPath);
        //first delete
        apkFile.delete();

        DownloadManager mgr = (DownloadManager) activity.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkDownloadUrl))
                .setTitle(activity.getString(R.string.app_name))
                .setAllowedOverMetered(true)
                .setDescription("Downloading new version..")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                .setVisibleInDownloadsUi(true);
        mgr.enqueue(request);

        activity.registerReceiver(getDownloadCompleteReceiver(activity, apkPath), new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startDownloadManager(apkUrl);
        }
    }

    private BroadcastReceiver getDownloadCompleteReceiver(AppCompatActivity activity, String apkPath) {
        return new BroadcastReceiver() {
            private void openDownloadDirectory() {
                Intent downloadDirIntent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
                downloadDirIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(downloadDirIntent);
            }

            public void onReceive(Context ctx, Intent intent) {
                if (!DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) return;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri downloadedFileUri = FileProvider.getUriForFile(activity, activity.getString(R.string.file_provider_authority), new File(apkPath));
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    install.setData(downloadedFileUri);

                    activity.startActivity(install);
                } else {
                    DownloadManager mgr = (DownloadManager) activity.getSystemService(DOWNLOAD_SERVICE);
                    long downloadedId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    Uri downloadedFileUri = mgr.getUriForDownloadedFile(downloadedId);
//                    Uri downloadedFileUri = Uri.parse("file://" + apkPath);
                    if (downloadedFileUri != null) {
                        Intent install = new Intent(Intent.ACTION_VIEW);
                        install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        install.setDataAndType(downloadedFileUri, "application/vnd.android.package-archive");

                        activity.startActivity(install);
                    } else openDownloadDirectory();
                }

                activity.unregisterReceiver(this);
            }
        };
    }
}
