package com.martinez.victor.tfg_owner_app;

import static android.content.Context.MODE_PRIVATE;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.martinez.victor.tfg_owner_app.activities.ManualRegistration;
import com.martinez.victor.tfg_owner_app.utilities.DeviceOwner;
import com.martinez.victor.tfg_owner_app.utilities.network.Registrator;

import org.json.JSONException;

public class DeviceOwnerReceiver extends DeviceAdminReceiver {
    private final String tag = "DeviceOwnerReceiver";

    @Override
    public void onProfileProvisioningComplete(@NonNull Context context, @NonNull Intent intent) {
        Bundle adminExtras = intent.getBundleExtra(DevicePolicyManager.EXTRA_PROVISIONING_ADMIN_EXTRAS_BUNDLE);
        if (adminExtras == null) {
            Intent launch = new Intent(context, ManualRegistration.class);
            launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            launch.putExtra("ERROR_MESSAGE", R.string.error_registration_no_server_data);
            return;
        }
        String serverUrl = adminExtras.getString("server_url");
        String registrationCode = adminExtras.getString("registration_code");
        if (serverUrl == null || registrationCode == null || serverUrl.isEmpty() || registrationCode.isEmpty()) {
            Log.e(tag, "Server URL or API Key invalid");
            Log.e(tag, "Server URL: " + serverUrl);
            Log.e(tag, "API Key: " + registrationCode);
            Intent launch = new Intent(context, ManualRegistration.class);
            launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            launch.putExtra("ERROR_MESSAGE", R.string.error_registration_corrupted_server_data);
            return;
        }

        try {
            Registrator.manualEnrollment(context, serverUrl, registrationCode, new Registrator.EnrollmentCallback() {
                @Override
                public void onSuccess(String serverURL, String appApiKey) {
                    SharedPreferences prefs = context.getSharedPreferences("MyPreferences", MODE_PRIVATE);
                    SharedPreferences.Editor prefsEditor = prefs.edit();
                    prefsEditor.putString("appApiKey", appApiKey)
                            .putString("serverURL", serverURL)
                            .apply();
                    Intent launch = new Intent(context, ManualRegistration.class);
                    launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(launch);
                }

                @Override
                public void onError(String errorMessage) {
                    Intent launch = new Intent(context, ManualRegistration.class);
                    launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    launch.putExtra("ERROR_MESSAGE", R.string.error_registration_corrupted_server_data);
                    Log.e(tag, "Could not enroll device: " + errorMessage);
                    context.startActivity(launch);
                }
            });
        } catch (JSONException e) {
            Intent launch = new Intent(context, ManualRegistration.class);
            launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            launch.putExtra("ERROR_MESSAGE", R.string.error_registration_unknown_error);
            Log.e(tag, "Could not enroll device, JSONException: " + e.getMessage());
            context.startActivity(launch);
        }
    }

    @Override
    public void onEnabled(@NonNull Context context, @NonNull Intent intent) {
        Intent launch = new Intent(context, ManualRegistration.class);
        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (DeviceOwner.isDeviceOwner(context)) {
            context.startActivity(launch);
        } else {
            launch.putExtra("ERROR_MESSAGE", R.string.error_registration_manual_bad_permissions);
        }
        context.startActivity(launch);
    }

    @Override
    public void onDisabled(@NonNull Context context, @NonNull Intent intent) {
        Intent launch = new Intent(context, ManualRegistration.class);
        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        launch.putExtra("ERROR_MESSAGE_RUNTIME", R.string.error_permissions);
        context.startActivity(launch);
    }
}
