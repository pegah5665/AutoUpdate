package com.pegahahvapp.autoupdate;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    String version;
    int verCode;
    private final String url="http://webserver adreess/AutoUpdate.apk";


    String destination;
    File file;
    Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
            verCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

//inja bayad version az server check shavad va ba version feli moghayese shavad  agar version jadid amade bood version jadid dawnload shavad

        if(checkPermition()) {

        }


        }


    public void update(String apkurl){

        destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";

        String fileName = "AutoUpdate.apk";
        destination += fileName;
        final Uri uri = Uri.parse("file://" + destination);

        //Delete update file if exists
        file = new File(destination);

        if (file.exists())
            //file.delete() - test this, I think sometimes it doesnt work
            file.delete();

        //get url of app on server


        //g.i("111","7");set downloadmanager
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkurl));

        request.setDescription(MainActivity.this.getString(R.string.notification_description));

        request.setTitle(MainActivity.this.getString(R.string.app_name));

        //set destination
        request.setDestinationUri(uri);

        final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        final long downloadId = manager.enqueue(request);



        File fileInstall = new File(Environment.getExternalStorageDirectory() + "/download/" + "AutoUpdate.apk");
        fileUri = Uri.fromFile(fileInstall);
        if (Build.VERSION.SDK_INT >= 24) {
            fileUri = android.support.v4.content.FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider",
                    fileInstall);
        }


        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                //set BroadcastReceiver to install app when .apk is downloaded
                intent = new Intent(Intent.ACTION_VIEW, fileUri);
                intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                intent.setDataAndType(fileUri,  manager.getMimeTypeForDownloadedFile(downloadId));
                //install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                ctxt.startActivity(intent);

                unregisterReceiver(this);
                finish();

            }
        };
        //register receiver for when .apk download is compete
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

    public boolean checkPermition(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getBaseContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //All Permissions are granted
                return true;
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 99);
                //permissions are not granted so this check has failed
                return false;
            }
        }
        //The device is <Android M and so manifest declarations should be enough
        return true;
    }

    public void update(View v){
        update(url);
    }
}
