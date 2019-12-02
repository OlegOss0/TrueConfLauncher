package launcher.example.trueconflauncher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;

import com.example.trueconflauncher.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.List;

public class LauncherActivity extends AppCompatActivity {
    private final String TRUECONF_PATCH = "com.trueconf.videochat";
    public final static String TRUECONF_GOOGLE_PLAY_LINK = "https://play.google.com/store/apps/details?id=com.trueconf.videochat";
    public final static String TRUECONF_WEB_LINK = "https://trueconf.com/downloads/android.html";
    public final static String argsKey = "gsAvailable";
    public final static int PERMISSION_CODE = 11455;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(getIntent() != null &&  getIntent().getBooleanExtra("Change launcher", false)){
            PackageManager p =  getPackageManager();
            ComponentName cN = new ComponentName(this, FakeHomeActivity.class);
            p.setComponentEnabledSetting(cN, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            Intent selector = new Intent(Intent.ACTION_MAIN);
            selector.addCategory((Intent.CATEGORY_HOME));
            startActivity(selector);
            p.setComponentEnabledSetting(cN, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            finish();
        }else if (isAppIstalled(TRUECONF_PATCH)) {
            Intent startIntent = getPackageManager()
                    .getLaunchIntentForPackage(TRUECONF_PATCH);
            startIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(startIntent);
        } else {
            Intent openLinckIntent = null;
            if (isGooglePlayServicesAvailable()) {
                openLinckIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(LauncherActivity.TRUECONF_GOOGLE_PLAY_LINK));
                startActivity(openLinckIntent);
            } else {
                openLinckIntent = new Intent(Intent.ACTION_VIEW);
                if (!isInstalledBrowser()) {
                    openLinckIntent = new Intent(this, SimpleBrowser.class);
                } else {
                    openLinckIntent.setData(Uri.parse(LauncherActivity.TRUECONF_WEB_LINK));
                }
                startActivity(openLinckIntent);
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    private boolean isAppIstalled(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return false;
    }

    public boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(getBaseContext());
        if (status != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }

    private boolean isInstalledBrowser() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(LauncherActivity.TRUECONF_WEB_LINK));
        List<ResolveInfo> browserList = getPackageManager().queryIntentActivities(intent, 0);
        return !browserList.isEmpty();
    }
}

