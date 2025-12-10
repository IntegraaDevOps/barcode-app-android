package net.integraa.read;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class Util {

    public static final String version_app = BuildConfig.VERSION_NAME;
    public static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    private static final ArrayList<String> permissionsRequired = new ArrayList<>(4);
    static {
        if(BuildConfig.DEBUG) {
            permissionsRequired.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            permissionsRequired.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            permissionsRequired.add(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE);
        }
        permissionsRequired.add(Manifest.permission.CAMERA);
    }

    public  static  boolean addPermission(Activity parent, List<String> permissionsList, String permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (parent.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
                // Check for Rationale Option
                if (!parent.shouldShowRequestPermissionRationale(permission))
                    return false;
            }
            return true;
        } else {
            return true;
        }
    }
    public static Boolean checkPermissions(final ZActivity activity) {
        activity.checkPermission();
        if (Build.VERSION.SDK_INT < 23) {
        }else {
            List<String> permissionsNeeded = new ArrayList<>();
            final List<String> permissionsList = new ArrayList<String>();
            for (String perm:permissionsRequired) {
                if (!addPermission(activity,permissionsList, perm))
                    permissionsNeeded.add(perm);
            }

            if (permissionsList.size() > 0) {
                if (permissionsNeeded.size() > 0) {
                    ActivityCompat.requestPermissions(activity,permissionsList.toArray(new String[permissionsList.size()]),
                            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                    return false;
                }
                ActivityCompat.requestPermissions(activity,permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                return false;
            }

            Log.i("infoSupportPermission","ci sono tutti i permessi");

        }
        return true;
    }

}