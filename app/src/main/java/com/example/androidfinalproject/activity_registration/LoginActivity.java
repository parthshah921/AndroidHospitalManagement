package com.app.onetapmedico.activity_registration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.app.onetapmedico.BuildConfig;
import com.app.onetapmedico.R;
import com.app.onetapmedico.activities_patient.PatientDashboardActivity;
import com.app.onetapmedico.activity_driver.DriverDashboardActivity;
import com.app.onetapmedico.connection.API;
import com.app.onetapmedico.connection.Connect;
import com.app.onetapmedico.databinding.ActivityLoginBinding;
import com.app.onetapmedico.tools.Constants;

import org.json.JSONException;
import org.json.JSONObject;


import atirek.pothiwala.connection.Connector;
import atirek.pothiwala.utility.helper.Loader;
import atirek.pothiwala.utility.helper.ValidationHelper;
import okhttp3.RequestBody;
import retrofit2.Call;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    boolean isDriver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        isDriver = intent != null && intent.getBooleanExtra("driver", false);

        binding.ivUser.setImageResource(isDriver ? R.drawable.ic_driver : R.drawable.ic_patient);
        binding.tvTitle.setText(isDriver ? "Driver Login" : "Patient Login");

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ValidationHelper.isValidEmail(binding.etEmail)) {
                    return;
                }
                if (!ValidationHelper.isNonEmpty(binding.etPassword)) {
                    return;
                }

                if (isDriver) {
                    loginDriver();
                } else {
                    loginPatient();
                }

            }
        });

        binding.btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                intent.putExtra("driver", isDriver);
                startActivity(intent);
            }
        });

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDriver) {
                    startActivity(new Intent(LoginActivity.this, DriverSignUpActivity.class));
                } else {
                    startActivity(new Intent(LoginActivity.this, PatientSignUpActivity.class));
                }
            }
        });

    }

    private void loginPatient() {

        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        Connector connector = new Connector(LoginActivity.this, BuildConfig.DEBUG);
        connector.setListener(new Connector.ConnectListener() {
            @Override
            public void onSuccess(int statusCode, @Nullable String json, @NonNull String message) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    boolean success = jsonObject.optInt("response", 0) == 1;
                    if (success) {
                        Constants.shared().set(jsonObject.optJSONObject("data").toString(), 1);
                        Toast.makeText(LoginActivity.this, "Login successful.", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(LoginActivity.this, PatientDashboardActivity.class));
                        finish();

                    } else {
                        Toast.makeText(LoginActivity.this, "Failed to login.", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(boolean isNetworkIssue, @NonNull String errorMessage) {
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
        Connect.Patient connect = Connector.getClient(API.baseUrl).create(Connect.Patient.class);

        JSONObject params = new JSONObject();
        try {
            params.put("email", email);
            params.put("user_password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = Connector.createPartFromJsonObject(params.toString());
        Call<String> stringCall = connect.login(requestBody);
        Loader loader = new Loader(this);
        loader.setCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                stringCall.cancel();
            }
        });
        loader.setColor(R.color.otmRed);
        connector.setLoaderDialog(loader.getDialog());
        connector.Request("login", stringCall);
    }

    private void loginDriver() {

        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        JSONObject params = new JSONObject();
        try {
            params.put("email", email);
            params.put("user_password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = Connector.createPartFromJsonObject(params.toString());

        Connector connector = new Connector(LoginActivity.this, BuildConfig.DEBUG);
        connector.setListener(new Connector.ConnectListener() {
            @Override
            public void onSuccess(int statusCode, @Nullable String json, @NonNull String message) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    boolean success = jsonObject.optInt("response", 0) == 1;
                    if (success) {
                        Constants.shared().set(jsonObject.optJSONObject("data").toString(), 2);
                        Toast.makeText(LoginActivity.this, "Login successful.", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(LoginActivity.this, DriverDashboardActivity.class));
                    } else {
                        Toast.makeText(LoginActivity.this, "Failed to login.", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(boolean isNetworkIssue, @NonNull String errorMessage) {
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
        Connect.Driver connect = Connector.getClient(API.baseUrl).create(Connect.Driver.class);


        Call<String> stringCall = connect.login(requestBody);
        Loader loader = new Loader(this);
        loader.setCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                stringCall.cancel();
            }
        });
        loader.setColor(R.color.otmRed);
        connector.setLoaderDialog(loader.getDialog());

        connector.Request("login", stringCall);
    }
}