package com.khudrosoft.visiontube.Auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.TextureView;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.khudrosoft.visiontube.R;
import com.khudrosoft.visiontube.Update.UpdateHelper;

import java.util.Random;

public class LoginActivity extends AppCompatActivity implements UpdateHelper.onUpdateCheckListener{
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99 ;
    public WebView webView;
    public ImageView imageView;
    private VideoView videoView;

    @SuppressLint({"SetJavaScriptEnabled", "NewApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        UpdateHelper.with(this).onUpdateCheck(this).check();
        webView = findViewById(R.id.webview);
        imageView = findViewById(R.id.img);
        videoView = findViewById(R.id.videoview);
        videoView.setZOrderOnTop(true);

        WebClient webClient = new WebClient();
        String path = "android.resource://" + getPackageName() + "/" + R.raw.vv;
        videoView.setVideoURI(Uri.parse(path));
        videoView.start();

        imageView.setVisibility(View.GONE);
        webView.setVisibility(View.GONE);
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.loadUrl("https://www.w3schools.com/html/html5_video.asp");


        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.setVisibility(View.GONE);
            }
        });


        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) {
                    imageView.setVisibility(View.GONE);
                    videoView.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);


                }
            }


        });

        webView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                if(checkLocationPermission()) {
                    DownloadManager.Request request = new DownloadManager.Request(
                            Uri.parse(url));

                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,random());
                    DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    dm.enqueue(request);
                    Toast.makeText(getApplicationContext(), "Downloading File", //To notify the Client that the file is being downloaded
                            Toast.LENGTH_LONG).show();
                }

            }
        });
        webView.setWebViewClient(webClient);
//        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        webView.setBackgroundColor(Color.TRANSPARENT);

        if (isNetworkConnected()) {
            webView.loadUrl("http://v-tube.xyz/login");
            //  videoView.setVisibility(View.GONE);
        } else {
            imageView.setImageResource(R.drawable.nointernet);
            imageView.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void onUpdateCheckListener(String urlApp) {
        androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(this).setTitle("Update Your App").setMessage("Please Update Your App to new Version.").setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));



            }
        }).create();

        alertDialog.show();
    }


    public class WebClient extends WebViewClient {

        @Override
        @TargetApi(Build.VERSION_CODES.M)
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            webView.loadUrl("file:///android_asset/error.html");
        }

        @SuppressWarnings("deprecation")
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            webView.loadUrl("file:///android_asset/error.html");
        }

        private void handleError(WebView view, int errorCode, String description, final Uri uri) {
            webView.loadUrl("file:///android_asset/error.html");
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return false;


        }
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("Permission")
                        .setMessage("Please Share/on Your Location")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(LoginActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                                startActivity(new Intent(LoginActivity.this,LoginActivity.class));
                                LoginActivity.this.overridePendingTransition(0,0);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(LoginActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            }

            return false;
        } else {

            return true;

        }
    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(5);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
