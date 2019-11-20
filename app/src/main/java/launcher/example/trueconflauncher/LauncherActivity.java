package launcher.example.trueconflauncher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.trueconflauncher.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class LauncherActivity extends AppCompatActivity {
    private final String TC_APP = "com.trueconf.videochat";
    public final static String TC_GOOGLE_PLAY_LINK = "https://play.google.com/store/apps/details?id=com.trueconf.videochat";
    public final static String TC_LINK = "https://trueconf.com/downloads/android.html";
    public final static String argsKey = "gsAvailable";
    public final static int PERMISSION_CODE = 11455;
    private boolean openChooseLauncherDialog = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openChooseLauncherDialog = getIntent().getBooleanExtra("CHOOSE_LAUNCHER", false);
        setContentView(R.layout.activity_launcher);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(openChooseLauncherDialog){
            final NoTrueConfAppDialog noTrueConfAppDialog = NoTrueConfAppDialog.newInstance();
            Bundle args = new Bundle();
            args.putBoolean(argsKey, isGooglePlayServicesAvailable());
            noTrueConfAppDialog.setArguments(args);
            noTrueConfAppDialog.show(getSupportFragmentManager(), "No application dialog");
        }
        if (isAppIstalled(TC_APP)) {
            Intent LaunchIntent = getPackageManager()
                    .getLaunchIntentForPackage(TC_APP);
            startActivity(LaunchIntent);
        } else {
            final NoTrueConfAppDialog noTrueConfAppDialog = NoTrueConfAppDialog.newInstance();
            Bundle args = new Bundle();
            args.putBoolean(argsKey, isGooglePlayServicesAvailable());
            noTrueConfAppDialog.setArguments(args);
            noTrueConfAppDialog.show(getSupportFragmentManager(), "No application dialog");
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
}

