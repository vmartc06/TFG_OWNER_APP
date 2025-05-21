package com.martinez.victor.tfg_owner_app.utilities.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.martinez.victor.tfg_owner_app.R;
import com.martinez.victor.tfg_owner_app.utilities.DeviceInfo;
import com.martinez.victor.tfg_owner_app.utilities.JSONObjectCustom;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Registrator {
    public static final String tag = "Registrator";
    public static final HashMap<String,String> headers = new HashMap<>() {{
        put("Accept", "application/json");
        put("Content-Type", "application/json");
    }};

    public interface EnrollmentCallback {
        void onSuccess(String serverURL, String appApiKey);

        void onError(String error);
    }

    public static void manualEnrollment(Context context, String serverURL, String enrollmentCode, EnrollmentCallback callback) throws JSONException {
        String apiURL = serverURL + "/api/v1/devices/enroll";
        Log.d(tag, "Making request to " + apiURL);
        DeviceInfo deviceInfo = new DeviceInfo(context);
        JSONObjectCustom deviceInfoJson = deviceInfo.toJson();
        deviceInfoJson.put("enrollment_code", enrollmentCode);
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, apiURL, deviceInfoJson,
                response -> {
                    try {
                        String appApiKey = response.get("api_key").toString();
                        if (appApiKey.isEmpty()) {
                            Log.e(tag, "api_key received was empty");
                            callback.onError(context.getString(R.string.error_registration_unknown_response_error));
                        }
                        callback.onSuccess(serverURL, appApiKey);
                    } catch (JSONException e) {
                        Log.e(tag, "api_key not found in JSON");
                        callback.onError(context.getString(R.string.error_registration_unknown_response_error));
                    }
                },
                error -> {
                    Log.e(tag, "There was an error on the enrollment: " + error.toString());
                    if (error.networkResponse != null) {
                        String errorBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        Log.e(tag, "Status code: " + error.networkResponse.statusCode + ", Body: " + errorBody);
                    }
                    callback.onError(context.getString(R.string.error_registration_unknown_response_error));
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }
        };

        queue.add(request);
    }
}
