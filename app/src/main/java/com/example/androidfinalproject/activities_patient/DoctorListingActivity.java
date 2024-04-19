package com.app.onetapmedico.activities_patient;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.app.onetapmedico.BuildConfig;
import com.app.onetapmedico.R;
import com.app.onetapmedico.cells.DoctorCell;
import com.app.onetapmedico.connection.API;
import com.app.onetapmedico.connection.Connect;
import com.app.onetapmedico.databinding.ActivityDoctorListingBinding;
import com.app.onetapmedico.models.DoctorItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import atirek.pothiwala.connection.Connector;
import atirek.pothiwala.utility.helper.IntentHelper;
import atirek.pothiwala.utility.helper.Loader;
import atirek.pothiwala.utility.helper.PermissionHelper;
import retrofit2.Call;

public class DoctorListingActivity extends AppCompatActivity {

    ActivityDoctorListingBinding binding;
    List<DoctorItem> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoctorListingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.recyclerView.setAdapter(adapter);
        listing();
    }

    private void callWithPermission(String phone) {
        String[] permissions = new String[]{
                Manifest.permission.CALL_PHONE
        };
        if (!PermissionHelper.checkPermissions(DoctorListingActivity.this, permissions)) {
            PermissionHelper.requestPermissions(DoctorListingActivity.this, permissions, 0);
            return;
        }
        IntentHelper.phoneCall(DoctorListingActivity.this, phone);
    }

    private void sendEmail(String name, String email) {
        String[] TO = {email};
        String[] CC = {""};

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        emailIntent.putExtra(Intent.EXTRA_TEXT, String.format(Locale.getDefault(), "Hello Dr. %s", name));

        try {
            startActivity(Intent.createChooser(emailIntent, "Send Mail"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(DoctorListingActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    RecyclerView.Adapter<DoctorCell> adapter = new RecyclerView.Adapter<DoctorCell>() {
        @NonNull
        @Override
        public DoctorCell onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return DoctorCell.instance(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull DoctorCell holder, int position) {
            DoctorItem doctorItem = list.get(position);
            holder.set(doctorItem);

            holder.call(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callWithPermission(doctorItem.dcontact);
                }
            });

            holder.email(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendEmail(doctorItem.dname, doctorItem.demail);
                }
            });

            holder.appointment(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DoctorListingActivity.this, TakeAppointmentActivity.class);
                    intent.putExtra("doctor", doctorItem.toString());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            int size = list.size();
            binding.tvEmpty.setVisibility(size > 0 ? View.GONE : View.VISIBLE);
            return size;
        }
    };

    private void listing() {
        Connector connector = new Connector(DoctorListingActivity.this, BuildConfig.DEBUG);
        connector.setListener(new Connector.ConnectListener() {
            @Override
            public void onSuccess(int statusCode, @Nullable String json, @NonNull String message) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    list = DoctorItem.list(jsonObject.optJSONArray("result").toString());
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(boolean isNetworkIssue, @NonNull String errorMessage) {
                Toast.makeText(DoctorListingActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        Connect.Patient connect = Connector.getClient(API.baseUrl).create(Connect.Patient.class);

        Call<String> stringCall = connect.doctors();
        Loader loader = new Loader(this);
        loader.setCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                stringCall.cancel();
            }
        });
        loader.setColor(R.color.otmRed);
        connector.setLoaderDialog(loader.getDialog());
        connector.Request("doctors", stringCall);

    }
}