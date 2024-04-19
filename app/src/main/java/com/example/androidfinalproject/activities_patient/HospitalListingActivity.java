package com.app.onetapmedico.activities_patient;

import android.Manifest;
import android.content.DialogInterface;
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
import com.app.onetapmedico.cells.HospitalCell;
import com.app.onetapmedico.connection.API;
import com.app.onetapmedico.connection.Connect;
import com.app.onetapmedico.databinding.ActivityHospitalListingBinding;
import com.app.onetapmedico.models.HospitalItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import atirek.pothiwala.connection.Connector;
import atirek.pothiwala.utility.helper.IntentHelper;
import atirek.pothiwala.utility.helper.Loader;
import atirek.pothiwala.utility.helper.PermissionHelper;
import retrofit2.Call;

public class HospitalListingActivity extends AppCompatActivity {

    ActivityHospitalListingBinding binding;
    List<HospitalItem> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHospitalListingBinding.inflate(getLayoutInflater());
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
        if (!PermissionHelper.checkPermissions(HospitalListingActivity.this, permissions)) {
            PermissionHelper.requestPermissions(HospitalListingActivity.this, permissions, 0);
            return;
        }
        IntentHelper.phoneCall(HospitalListingActivity.this, phone);
    }

    RecyclerView.Adapter<HospitalCell> adapter = new RecyclerView.Adapter<HospitalCell>() {
        @NonNull
        @Override
        public HospitalCell onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return HospitalCell.instance(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull HospitalCell holder, int position) {
            HospitalItem hospitalItem = list.get(position);
            holder.set(hospitalItem);
            holder.call(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callWithPermission(hospitalItem.hcontact);
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
        Connector connector = new Connector(HospitalListingActivity.this, BuildConfig.DEBUG);
        connector.setListener(new Connector.ConnectListener() {
            @Override
            public void onSuccess(int statusCode, @Nullable String json, @NonNull String message) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    list = HospitalItem.list(jsonObject.optJSONArray("result").toString());
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(boolean isNetworkIssue, @NonNull String errorMessage) {
                Toast.makeText(HospitalListingActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        Connect.Patient connect = Connector.getClient(API.baseUrl).create(Connect.Patient.class);
        Call<String> stringCall = connect.hospitals();
        Loader loader = new Loader(this);
        loader.setCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                stringCall.cancel();
            }
        });
        loader.setColor(R.color.otmRed);
        connector.setLoaderDialog(loader.getDialog());
        connector.Request("hospitals", stringCall);

    }
}