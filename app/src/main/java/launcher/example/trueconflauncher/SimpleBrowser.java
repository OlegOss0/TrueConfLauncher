package launcher.example.trueconflauncher;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.trueconflauncher.R;

import java.io.File;

import static launcher.example.trueconflauncher.LauncherActivity.PERMISSION_CODE;

public class SimpleBrowser extends AppCompatActivity {
    private String fileName = "";
    private static long mId = -1L;
    private String mMimetype = "";
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browser_activity);
        checkPermission(this);
        registerReceiver(onFileDownLoadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        initWebView();
    }

    private void initWebView() {
        WebView webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl(LauncherActivity.TRUECONF_WEB_LINK);

        webView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            String nameOfFile = URLUtil.guessFileName(url, null,
                    MimeTypeMap.getFileExtensionFromUrl(url));
            mMimetype = getMimeType(url);
            request.setMimeType(mMimetype);
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDescription(nameOfFile);
            request.setTitle(nameOfFile);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, nameOfFile);
            DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            Uri source = Uri.parse(url);
            fileName = source.getLastPathSegment();
            mId = dm.enqueue(request);
            Toast.makeText(getApplicationContext(), R.string.downloading, Toast.LENGTH_LONG).show();
        });
    }

    private String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(onFileDownLoadComplete);
        super.onDestroy();
    }

    protected void checkPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                String[] permArray = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setMessage(R.string.request_permission_message_text);
                    builder.setTitle(R.string.permission_message_title);
                    builder.setPositiveButton(R.string.ok, (dialog, which) -> {
                        ActivityCompat.requestPermissions(this, permArray, PERMISSION_CODE);
                    });
                    builder.setNeutralButton(R.string.cancel, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    ActivityCompat.requestPermissions(
                            activity,
                            permArray,
                            PERMISSION_CODE
                    );
                }
            }
        }
    }

    protected void openFile(String fileName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator +
                fileName);
        Uri path = Uri.fromFile(file);
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setDataAndType(path, mMimetype);
        try {
            mHandler.postDelayed(() ->{
                this.startActivity(install);
            },200);

        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "Can't open file", Toast.LENGTH_SHORT).show();
        }
    }

    private BroadcastReceiver onFileDownLoadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (mId == id) {
                Toast.makeText(getApplicationContext(), "Download completed...", Toast.LENGTH_SHORT).show();
                openFile(fileName);
                fileName = "";
                mId = -1;
            }
        }
    };
}

