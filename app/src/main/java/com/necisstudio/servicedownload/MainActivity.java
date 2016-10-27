package com.necisstudio.servicedownload;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.necisstudio.servicedownload.item.Download;
import com.necisstudio.servicedownload.service.DownloadService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String MESSAGE_PROGRESS = "message_progress";
    private static final int PERMISSION_REQUEST_CODE = 1;

    Button btnDownload;
    ProgressBar progressBar;
    TextView txtProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Service Retrofit 2");
        btnDownload = (Button) findViewById(R.id.btn_download);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        txtProgress = (TextView) findViewById(R.id.progress_text);
        btnDownload.setOnClickListener(this);

        registerReceiver();

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_download) {
            if (checkPermission()) {
                if (!isServiceRunning(DownloadService.class)) {
                    startDownload();
                } else {
                    Toast.makeText(getApplicationContext(), "Download In Progress", Toast.LENGTH_SHORT).show();
                }
            } else {
                requestPermission();
            }
        }
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void startDownload() {
        Intent intent = new Intent(this, DownloadService.class);
        startService(intent);

    }

    private void registerReceiver() {
        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MESSAGE_PROGRESS);
        bManager.registerReceiver(broadcastReceiver, intentFilter);

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(MESSAGE_PROGRESS)) {
                Download download = intent.getParcelableExtra("download");
                progressBar.setProgress(download.getProgress());
                if (download.getProgress() == 100) {
                    txtProgress.setText("File Download Complete");

                } else {
                    txtProgress.setText(String.format("Downloaded (%d/%d) MB", download.getCurrentFileSize(), download.getTotalFileSize()));

                }
            }
        }
    };

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {

            return true;

        } else {

            return false;
        }
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startDownload();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied, Please allow to proceed !", Toast.LENGTH_SHORT).show();

                }
                break;
        }
    }
}
