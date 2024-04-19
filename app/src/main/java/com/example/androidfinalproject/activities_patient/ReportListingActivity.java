package com.app.onetapmedico.activities_patient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.onetapmedico.BuildConfig;
import com.app.onetapmedico.R;
import com.app.onetapmedico.cells.ReportCell;
import com.app.onetapmedico.connection.API;
import com.app.onetapmedico.connection.Connect;
import com.app.onetapmedico.connection.DownloadService;
import com.app.onetapmedico.databinding.ActivityReportListingBinding;
import com.app.onetapmedico.models.ReportItem;
import com.app.onetapmedico.tools.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import atirek.pothiwala.connection.Connector;
import atirek.pothiwala.utility.helper.Loader;
import atirek.pothiwala.utility.helper.PermissionHelper;
import okhttp3.RequestBody;
import retrofit2.Call;

public class ReportListingActivity extends AppCompatActivity {

    ActivityReportListingBinding binding;
    List<ReportItem> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportListingBinding.inflate(getLayoutInflater());
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

    private void downloadReport(String reportUrl) {
        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        if (!PermissionHelper.checkPermissions(ReportListingActivity.this, permissions)) {
            PermissionHelper.requestPermissions(ReportListingActivity.this, permissions, 0);
            return;
        }
        startService(DownloadService.intent(ReportListingActivity.this, "Report", reportUrl));
    }

    RecyclerView.Adapter<ReportCell> adapter = new RecyclerView.Adapter<ReportCell>() {
        @NonNull
        @Override
        public ReportCell onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return ReportCell.instance(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ReportCell holder, int position) {
            ReportItem reportItem = list.get(position);
            holder.set(reportItem);
            holder.download(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadReport(API.baseUrl + reportItem.preFile);
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    };

    private void listing() {
        JSONObject params = new JSONObject();
        try {
            params.put("pid", Constants.shared().get("Pid"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Connector connector = new Connector(ReportListingActivity.this, BuildConfig.DEBUG);
        connector.setListener(new Connector.ConnectListener() {
            @Override
            public void onSuccess(int statusCode, @Nullable String json, @NonNull String message) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    list = ReportItem.list(jsonObject.optJSONArray("result").toString());
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(boolean isNetworkIssue, @NonNull String errorMessage) {
                Toast.makeText(ReportListingActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        Connect.Patient connect = Connector.getClient(API.baseUrl).create(Connect.Patient.class);
        RequestBody requestBody = Connector.createPartFromJsonObject(params.toString());
        Call<String> stringCall = connect.reports(requestBody);
        Loader loader = new Loader(this);
        loader.setCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                stringCall.cancel();
            }
        });
        loader.setColor(R.color.otmRed);
        connector.setLoaderDialog(loader.getDialog());
        connector.Request("reports", stringCall);

    }
}