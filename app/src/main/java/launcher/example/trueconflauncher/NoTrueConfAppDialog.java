package launcher.example.trueconflauncher;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.trueconflauncher.R;

import java.util.List;

public class NoTrueConfAppDialog extends DialogFragment {

    public static NoTrueConfAppDialog newInstance() {
        NoTrueConfAppDialog frag = new NoTrueConfAppDialog();
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        final boolean isGoogleServicesAvailable = args.getBoolean(LauncherActivity.argsKey, false);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.trueconf_application_not_found)
                .setPositiveButton(R.string.install,
                        (dialog, whichButton) -> {
                            Intent openLinckIntent;
                            if(isGoogleServicesAvailable){
                                openLinckIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(LauncherActivity.TC_GOOGLE_PLAY_LINK));
                                startActivity(openLinckIntent);
                            }else{
                                if(!isInstalledBrowser());
                                openLinckIntent = new Intent(getActivity(), SimpleBrowser.class);
                                startActivity(openLinckIntent);
                            }

                        }
                )
                .setNegativeButton(R.string.cancel,
                        (dialog, whichButton) -> {
                            Intent startMain = new Intent(Intent.ACTION_MAIN);
                            startMain.addCategory(Intent.CATEGORY_HOME);
                            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(startMain);
                        }
                )
                .create();
    }

    private boolean isInstalledBrowser() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(LauncherActivity.TC_LINK));
        List<ResolveInfo> browserList = getActivity().getPackageManager().queryIntentActivities(intent, 0);
        return !browserList.isEmpty();
    }
}
