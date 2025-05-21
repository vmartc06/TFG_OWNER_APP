package com.martinez.victor.tfg_owner_app.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.martinez.victor.tfg_owner_app.R;
import com.martinez.victor.tfg_owner_app.utilities.network.Registrator;

import org.json.JSONException;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ManualRegistration extends AppCompatActivity {
    private final String tag = "ManualRegistration";
    private TextInputEditText urlEditText;
    private TextInputEditText stringEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manual_registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        String errorMessage = getIntent().getStringExtra("ERROR_MESSAGE");
        String errorMessageRuntime = getIntent().getStringExtra("ERROR_MESSAGE_RUNTIME");
        boolean errorCritical = getIntent().getBooleanExtra("ERROR_CRITICAL", true);
        if (errorMessage != null || errorMessageRuntime != null) {
            new AlertDialog.Builder(ManualRegistration.this)
                .setTitle(errorMessage == null ? R.string.error_permissions_mbox_title :
                        R.string.error_registration_mbox_title)
                .setMessage(errorMessage == null ? errorMessageRuntime : errorMessage)
                .setPositiveButton("Exit", (dialog, which) -> {
                    if (errorCritical) {
                        finishAffinity();
                        System.exit(1);
                    }
                })
                .setCancelable(false)
                .show();
        }

        urlEditText = findViewById(R.id.urlEditText);
        stringEditText = findViewById(R.id.stringEditText);
        Button registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> onRegisterClicked());
    }

    @SuppressLint("HardwareIds")
    private void onRegisterClicked() {
        String url = urlEditText.getText() != null ? urlEditText.getText().toString().trim() : "";
        String registrationCode = stringEditText.getText() != null ? stringEditText.getText().toString().trim() : "";

        if (!validateInputs(url, registrationCode)) {
            String unknownError = getString(R.string.error_registration_validation_failed);
            Toast.makeText(this, unknownError, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Registrator.manualEnrollment(getApplicationContext(), url, registrationCode, new Registrator.EnrollmentCallback() {
                @Override
                public void onSuccess(String serverURL, String appApiKey) {
                    SharedPreferences prefs = getSharedPreferences("MyPreferences", MODE_PRIVATE);
                    SharedPreferences.Editor prefsEditor = prefs.edit();
                    prefsEditor.putString("appApiKey", appApiKey)
                               .putString("serverURL", serverURL)
                               .apply();
                    Intent launch = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(launch);
                    finish();
                }

                @Override
                public void onError(String errorMessage) {
                    runOnUiThread(() -> new AlertDialog.Builder(ManualRegistration.this)
                        .setTitle(R.string.error_registration_mbox_title)
                        .setMessage(errorMessage)
                        .setPositiveButton("Exit", (dialog, which) -> {
                            finishAffinity();
                            System.exit(1);
                        })
                        .setCancelable(false)
                        .show()
                    );
                }
            });
        } catch (JSONException e) {
            String err = getString(R.string.error_registration_unknown_request_error);
            Log.e(tag, "There was a JSONException: " + e.getMessage());
            Toast.makeText(this, err, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInputs(String url, String code) {
        boolean valid = true;

        if (TextUtils.isEmpty(url)) {
            urlEditText.setError(getString(R.string.error_url_required));
            valid = false;
        }

        if (!Patterns.WEB_URL.matcher(url).matches()) {
            urlEditText.setError(getString(R.string.error_invalid_url));
            valid = false;
        }

        if (TextUtils.isEmpty(code)) {
            stringEditText.setError(getString(R.string.error_registration_code_required));
            valid = false;
        }

        try {
            valid = Pattern.matches("[A-Za-z0-9]+", code);
        } catch (PatternSyntaxException e) {
            stringEditText.setError(getString(R.string.error_registration_code_invalid));
            valid = false;
        }

        return valid;
    }


}