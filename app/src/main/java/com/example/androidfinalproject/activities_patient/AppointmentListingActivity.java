package com.app.onetapmedico.activities_patient;

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
import com.app.onetapmedico.cells.AppointmentCell;
import com.app.onetapmedico.connection.API;
import com.app.onetapmedico.connection.Connect;
import com.app.onetapmedico.databinding.ActivityAppointmentListingBinding;
import com.app.onetapmedico.models.AppointmentItem;
import com.app.onetapmedico.tools.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import atirek.pothiwala.connection.Connector;
import atirek.pothiwala.utility.helper.Loader;
import okhttp3.RequestBody;
import retrofit2.Call;

public class AppointmentListingActivity extends AppCompatActivity {

    ActivityAppointmentListingBinding binding;
    List<AppointmentItem> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppointmentListingBinding.inflate(getLayoutInflater());
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

    RecyclerView.Adapter<AppointmentCell> adapter = new RecyclerView.Adapter<AppointmentCell>() {
        @NonNull
        @Override
        public AppointmentCell onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return AppointmentCell.instance(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull AppointmentCell holder, int position) {
            holder.set(list.get(position));
        }

        @Override
        public int getItemCount() {
            int size = list.size();
            binding.tvEmpty.setVisibility(size > 0 ? View.GONE : View.VISIBLE);
            return size;
        }
    };

    private void listing() {
        JSONObject params = new JSONObject();
        try {
            params.put("pid", Constants.shared().get("Pid"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Connector connector = new Connector(AppointmentListingActivity.this, BuildConfig.DEBUG);
        connector.setListener(new Connector.ConnectListener() {
            @Override
            public void onSuccess(int statusCode, @Nullable String json, @NonNull String message) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    list = AppointmentItem.list(jsonObject.optJSONArray("result").toString());
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(boolean isNetworkIssue, @NonNull String errorMessage) {
                Toast.makeText(AppointmentListingActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        Connect.Patient connect = Connector.getClient(API.baseUrl).create(Connect.Patient.class);
        RequestBody requestBody = Connector.createPartFromJsonObject(params.toString());
        Call<String> stringCall = connect.appointments(requestBody);
        Loader loader = new Loader(this);
        loader.setCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                stringCall.cancel();
            }
        });
        loader.setColor(R.color.otmRed);
        connector.setLoaderDialog(loader.getDialog());
        connector.Request("appointments", stringCall);

    }
}