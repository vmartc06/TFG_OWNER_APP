package com.martinez.victor.tfg_owner_app.utilities;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.martinez.victor.tfg_owner_app.R;
import com.martinez.victor.tfg_owner_app.activities.MainActivity;
import com.martinez.victor.tfg_owner_app.activities.ManualRegistration;

public class Doctor {
    public static void checkHealth(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("MyPreferences", MODE_PRIVATE);
        String apiKey = preferences.getString("appApiKey", null);
        String serverURL = preferences.getString("serverURL", null);
        if (apiKey == null || serverURL == null) {
            Intent launch = new Intent(context, ManualRegistration.class);
            launch.putExtra("ERROR_MESSAGE_RUNTIME", context.getString(R.string.error_runtime_credentials));
            launch.putExtra("ERROR_CRITICAL", false);
            launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(launch);
        }
    }
}
