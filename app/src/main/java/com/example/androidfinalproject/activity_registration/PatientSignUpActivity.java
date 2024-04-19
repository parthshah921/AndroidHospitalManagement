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
import com.app.onetapmedico.connection.API;
import com.app.onetapmedico.connection.Connect;
import com.app.onetapmedico.databinding.ActivityPatientSignupBinding;
import com.app.onetapmedico.tools.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import atirek.pothiwala.connection.Connector;
import atirek.pothiwala.utility.helper.Loader;
import atirek.pothiwala.utility.helper.ValidationHelper;
import okhttp3.RequestBody;
import retrofit2.Call;

public class PatientSignUpActivity extends AppCompatActivity {

    ActivityPatientSignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPatientSignupBinding.inflate(getLayoutInflater());
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
        if (!ValidationHelper.isNonEmpty(binding.etAge)) {
            return;
        }
        if (!ValidationHelper.isValidEmail(binding.etEmail)) {
            return;
        }
        if (!ValidationHelper.isValidPhoneNumber(binding.etPhone)) {
            return;
        }

        if (!ValidationHelper.isNonEmpty(binding.etAddress)) {
            return;
        }

        if (!ValidationHelper.isNonEmpty(binding.etCity)) {
            return;
        }

        if (!ValidationHelper.isValidString(binding.etPassword, 3)) {
            return;
        }
        signUp();
    }

    private void signUp() {

        String name = binding.etName.getText().toString().trim();
        boolean isChecked = binding.rbMale.isChecked();
        String gender = binding.rbMale.getText().toString();
        if (!isChecked) {
            gender = binding.rbFemale.getText().toString();
        }
        String age = binding.etAge.getText().toString();
        String email = binding.etEmail.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String address = binding.etAddress.getText().toString().trim();
        String city = binding.etCity.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        JSONObject params = new JSONObject();
        try {
            params.put("uname", name);
            params.put("uemailid", email);
            params.put("upassword", password);
            params.put("uage", age);
            params.put("uaddress", address);
            params.put("uphoneno", phone);
            params.put("ugender", gender);
            params.put("ucity", city);
            params.put("ulattitude", "0");
            params.put("ulongitude", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Connector connector = new Connector(PatientSignUpActivity.this, BuildConfig.DEBUG);
        connector.setListener(new Connector.ConnectListener() {
            @Override
            public void onSuccess(int statusCode, @Nullable String json, @NonNull String message) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    boolean success = jsonObject.optInt("response", 0) == 1;
                    if (success) {
                        Constants.shared().set(jsonObject.optJSONObject("data").toString(), 1);
                        Toast.makeText(PatientSignUpActivity.this, "Signed up successfully.", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(PatientSignUpActivity.this, PatientDashboardActivity.class));
                    } else {
                        Toast.makeText(PatientSignUpActivity.this, "Failed to sign up.", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(boolean isNetworkIssue, @NonNull String errorMessage) {
                Toast.makeText(PatientSignUpActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
        Connect.Patient connect = Connector.getClient(API.baseUrl).create(Connect.Patient.class);

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