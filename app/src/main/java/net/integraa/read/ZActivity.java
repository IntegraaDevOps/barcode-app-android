package net.integraa.read;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import net.integraa.read.dbhelper.DialogHelper;

public abstract class ZActivity extends AppCompatActivity {

    private static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;
    private static int ACTION_MANAGE_UNKNOWN_APP_SOURCES_REQUEST_CODE = 7456;
    private static int ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION_REQUEST_CODE = 8436;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.checkPermissions(this);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE || requestCode == ACTION_MANAGE_UNKNOWN_APP_SOURCES_REQUEST_CODE) {
            checkPermission();
        }
    }

    protected boolean checkPermission() {
        boolean ret = true;
        Context ctx = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()){
                DialogHelper.alertWithAction(ctx,getString(R.string.alert),getString(R.string.needs_allfiles_permission),new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!Environment.isExternalStorageManager()){
                            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                                    Uri.parse("package:" + getPackageName()));
                            startActivityForResult(intent, ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION_REQUEST_CODE);
                        }
                    }
                });
                ret = false;
            }
        }
        return ret;
    }

}