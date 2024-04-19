package com.app.onetapmedico.activities_patient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.app.onetapmedico.BuildConfig;
import com.app.onetapmedico.R;
import com.app.onetapmedico.connection.API;
import com.app.onetapmedico.connection.Connect;
import com.app.onetapmedico.databinding.ActivityTakeAppointmentBinding;
import com.app.onetapmedico.models.DoctorItem;
import com.app.onetapmedico.models.PatientItem;
import com.app.onetapmedico.tools.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import atirek.pothiwala.connection.Connector;
import atirek.pothiwala.utility.helper.DateHelper;
import atirek.pothiwala.utility.helper.Loader;
import atirek.pothiwala.utility.helper.ValidationHelper;
import okhttp3.RequestBody;
import retrofit2.Call;

import static atirek.pothiwala.utility.helper.ValidationHelper.ErrorText.cannotBeEmpty;

public class TakeAppointmentActivity extends AppCompatActivity {

    ActivityTakeAppointmentBinding binding;
    DoctorItem doctorItem;
    PatientItem patientItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTakeAppointmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("doctor")) {
            doctorItem = DoctorItem.object(intent.getStringExtra("doctor"));
            patientItem = Constants.shared().getPatient();
            binding.etName.setText(patientItem.pname);

            binding.tvDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    datePicker(true);
                }
            });
            binding.btnBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!ValidationHelper.isNonEmpty(binding.etName)) {
                        return;
                    }
                    if (!isNonEmpty(binding.tvDate)) {
                        return;
                    }
                    if (!ValidationHelper.isNonEmpty(binding.etDetails)) {
                        return;
                    }

                    takeAppointment();
                }
            });
        } else {
            finish();
        }
    }

    public static boolean isNonEmpty(TextView textView) {
        String string = textView.getText().toString().trim();
        if (string.isEmpty()) {
            textView.setError(cannotBeEmpty);
            textView.requestFocus();
            return false;
        }
        return true;
    }

    private void takeAppointment() {

        String pname = binding.etName.getText().toString();
        String date = binding.tvDate.getText().toString();
        String details = binding.etDetails.getText().toString();

        Connector connector = new Connector(TakeAppointmentActivity.this, BuildConfig.DEBUG);
        connector.setListener(new Connector.ConnectListener() {
            @Override
            public void onSuccess(int statusCode, @Nullable String json, @NonNull String message) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    boolean success = jsonObject.optInt("response", 0) == 1;
                    if (success) {
                        Toast.makeText(TakeAppointmentActivity.this, "Appointment successful.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(TakeAppointmentActivity.this, "Failed to get appointment, try again.", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(boolean isNetworkIssue, @NonNull String errorMessage) {
                Toast.makeText(TakeAppointmentActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
        Connect.Patient connect = Connector.getClient(API.baseUrl).create(Connect.Patient.class);

        JSONObject params = new JSONObject();
        try {
            params.put("pname", pname);
            params.put("dname", doctorItem.dname);
            params.put("adate", date);
            params.put("pid", patientItem.pid);
            params.put("did", doctorItem.did);
            params.put("details", details);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = Connector.createPartFromJsonObject(params.toString());
        Call<String> stringCall = connect.takeAppointment(requestBody);
        Loader loader = new Loader(this);
        loader.setCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                stringCall.cancel();
            }
        });
        loader.setColor(R.color.otmRed);
        connector.setLoaderDialog(loader.getDialog());
        connector.Request("take_appointment", stringCall);
    }

    private DatePickerDialog dialogDatePicker;

    private void datePicker(boolean enable) {
        if (enable) {
            if (dialogDatePicker == null) {
                dialogDatePicker = DateHelper.getDatePicker(this, null, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        String date = String.format(Locale.getDefault(), "%d-%d-%d", year, month + 1, day);
                        binding.tvDate.setText(date);
                    }
                });

                Date minDate = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(minDate);
                calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 7);
                dialogDatePicker.getDatePicker().setMinDate(minDate.getTime());
                dialogDatePicker.getDatePicker().setMaxDate(calendar.getTimeInMillis());
            }

            if (!dialogDatePicker.isShowing()) {
                dialogDatePicker.show();
            }
        } else {
            if (dialogDatePicker != null && dialogDatePicker.isShowing()) {
                dialogDatePicker.dismiss();
            }
        }
    }
}