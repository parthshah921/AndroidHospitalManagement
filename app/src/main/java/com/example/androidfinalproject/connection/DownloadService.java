package com.app.onetapmedico.connection;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.Locale;

public class DownloadService extends IntentService {

    private static final String NOTIFICATION_TITLE = "notification_title";
    private static final String DOWNLOAD_PATH = "download_url";

    public DownloadService() {
        super("DownloadService");
    }

    public static Intent intent(@NonNull Context context, @NonNull String title, @NonNull String downloadPath) {
        String text = String.format(Locale.getDefault(), "Downloading %s file", title.toLowerCase());
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();

        return new Intent(context, DownloadService.class)
                .putExtra(NOTIFICATION_TITLE, title)
                .putExtra(DOWNLOAD_PATH, downloadPath);
    }

    @Override
    protected void onHandleIntent(@NonNull Intent intent) {
        startDownload(intent.getStringExtra(NOTIFICATION_TITLE), intent.getStringExtra(DOWNLOAD_PATH));
    }

    private void startDownload(String title, String downloadPath) {
        Uri uri = Uri.parse(downloadPath); // Path where you want to download file.
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);  // Tell on which network you want to download file.
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);  // This will show notification on top when downloading the file.
        request.setTitle(title); // Title for notification.
        //request.setDescription("Downloading..."); // Description for notification.
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, uri.getLastPathSegment());  // Storage directory path

        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadManager.enqueue(request); // This will start downloading
    }
}