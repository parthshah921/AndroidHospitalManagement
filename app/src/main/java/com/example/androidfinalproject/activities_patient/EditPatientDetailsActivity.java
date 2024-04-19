package com.app.onetapmedico.activities_patient;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.app.onetapmedico.BuildConfig;
import com.app.onetapmedico.R;
import com.app.onetapmedico.connection.API;
import com.app.onetapmedico.connection.Connect;
import com.app.onetapmedico.databinding.ActivityEditPatientDetailsBinding;
import com.app.onetapmedico.models.PatientItem;
import com.app.onetapmedico.tools.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import atirek.pothiwala.connection.Connector;
import atirek.pothiwala.utility.helper.Loader;
import atirek.pothiwala.utility.helper.ValidationHelper;
import okhttp3.RequestBody;
import retrofit2.Call;

public class EditPatientDetailsActivity extends AppCompatActivity {

    ActivityEditPatientDetailsBinding binding;
    PatientItem patientItem = Constants.shared().getPatient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditPatientDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });

        boolean isMale = patientItem.pgender.equalsIgnoreCase("male");
        binding.rbMale.setChecked(isMale);
        binding.rbFemale.setChecked(!isMale);

        binding.etName.setText(patientItem.pname);
        binding.etAge.setText(patientItem.page);
        binding.etEmail.setText(patientItem.pemail);
        binding.etPhone.setText(patientItem.pcontact);
        binding.etAddress.setText(patientItem.paddress);
        binding.etCity.setText(patientItem.pcity);

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

        update();
    }

    private void update() {

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

        JSONObject params = new JSONObject();
        try {
            params.put("pid", patientItem.pid);
            params.put("pname", name);
            params.put("email", email);
            params.put("age", age);
            params.put("address", address);
            params.put("contact", phone);
            params.put("gender", gender);
            params.put("city", city);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Connector connector = new Connector(EditPatientDetailsActivity.this, BuildConfig.DEBUG);
        connector.setListener(new Connector.ConnectListener() {
            @Override
            public void onSuccess(int statusCode, @Nullable String json, @NonNull String message) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    boolean success = jsonObject.optInt("response", 0) == 1;
                    if (success) {
                        Constants.shared().set(jsonObject.optJSONObject("data").toString(), 1);
                        Toast.makeText(EditPatientDetailsActivity.this, "Updated successfully.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EditPatientDetailsActivity.this, "Failed to update.", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(boolean isNetworkIssue, @NonNull String errorMessage) {
                Toast.makeText(EditPatientDetailsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
        Connect.Patient connect = Connector.getClient(API.baseUrl).create(Connect.Patient.class);

        RequestBody requestBody = Connector.createPartFromJsonObject(params.toString());
        Call<String> stringCall = connect.editProfile(requestBody);
        Loader loader = new Loader(this);
        loader.setCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                stringCall.cancel();
            }
        });
        loader.setColor(R.color.otmRed);
        connector.setLoaderDialog(loader.getDialog());
        connector.Request("edit_profile", stringCall);

    }
}