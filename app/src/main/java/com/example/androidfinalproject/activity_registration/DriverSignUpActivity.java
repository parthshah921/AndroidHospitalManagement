package com.app.onetapmedico.activity_registration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.app.onetapmedico.activities_patient.PatientDashboardActivity;
import com.app.onetapmedico.activity_driver.DriverDashboardActivity;
import com.app.onetapmedico.connection.API;
import com.app.onetapmedico.connection.Connect;
import com.app.onetapmedico.tools.Constants;
import org.json.JSONException;
import org.json.JSONObject;


public class DriverSignUpActivity extends AppCompatActivity {

    ActivityDriverSignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDriverSignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });
    }

    private void validate() {
        if (!ValidationHelper.isNonEmpty(binding.etName)) {
            return;
        }
        if (!ValidationHelper.isValidEmail(binding.etEmail)) {
            return;
        }
        if (!ValidationHelper.isValidPhoneNumber(binding.etPhone)) {
            return;
        }
        if (!ValidationHelper.isValidString(binding.etPassword, 3)) {
            return;
        }
        signUp();
    }

    private void signUp() {

        String name = binding.etName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        JSONObject params = new JSONObject();
        try {
            params.put("uname", name);
            params.put("uemailid", email);
            params.put("upassword", password);
            params.put("uphoneno", phone);
            params.put("ulattitude", "0");
            params.put("ulongitude", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Connector connector = new Connector(DriverSignUpActivity.this, BuildConfig.DEBUG);
        connector.setListener(new Connector.ConnectListener() {
            @Override
            public void onSuccess(int statusCode, @Nullable String json, @NonNull String message) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    boolean success = jsonObject.optInt("response", 0) == 1;
                    if (success) {
                        Constants.shared().set(jsonObject.optJSONObject("data").toString(), 2);
                        Toast.makeText(DriverSignUpActivity.this, "Signed up successfully.", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(DriverSignUpActivity.this, DriverDashboardActivity.class));
                    } else {
                        Toast.makeText(DriverSignUpActivity.this, "Failed to sign up.", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(boolean isNetworkIssue, @NonNull String errorMessage) {
                Toast.makeText(DriverSignUpActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
        Connect.Driver connect = Connector.getClient(API.baseUrl).create(Connect.Driver.class);

        RequestBody requestBody = Connector.createPartFromJsonObject(params.toString());
        Call<String> stringCall = connect.signUp(requestBody);
        Loader loader = new Loader(this);
        loader.setCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                stringCall.cancel();
            }
        });
        loader.setColor(R.color.otmRed);
        connector.setLoaderDialog(loader.getDialog());
        connector.Request("signUp", stringCall);
    }
}