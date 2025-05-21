package com.martinez.victor.tfg_owner_app.utilities;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import com.martinez.victor.tfg_owner_app.DeviceOwnerReceiver;

public class DeviceOwner {
    public static boolean isDeviceOwner(Context context) {
        DevicePolicyManager dpm = getDPM(context);
        String packageName = context.getPackageName();
        return dpm != null && dpm.isDeviceOwnerApp(packageName);
    }

    public static DevicePolicyManager getDPM(Context context) {
        return (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
    }

    public static void grantAllPermissions(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Below android 6 runtime permissions do not exist
            return;
        }
        DevicePolicyManager dpm = DeviceOwner.getDPM(context);
        ComponentName adminComponent = new ComponentName(context, DeviceOwnerReceiver.class);
        String[] permissions = {
            Manifest.permission.READ_PHONE_STATE
        };
        for (String permission: permissions) {
            dpm.setPermissionGrantState(
                adminComponent, context.getPackageName(),
                permission, DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED
            );
        }
    }
}
