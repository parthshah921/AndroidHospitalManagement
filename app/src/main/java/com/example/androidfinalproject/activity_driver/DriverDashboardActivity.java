package com.app.onetapmedico.activity_driver;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.app.onetapmedico.BuildConfig;
import com.app.onetapmedico.R;
import com.app.onetapmedico.connection.API;
import com.app.onetapmedico.connection.Connect;
import com.app.onetapmedico.databinding.ActivityDriverDashboardBinding;
import com.app.onetapmedico.models.NotificationItem;
import com.app.onetapmedico.tools.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

import atirek.pothiwala.connection.Connector;
import atirek.pothiwala.utility.helper.FusedLocationHelper;
import atirek.pothiwala.utility.helper.IntentHelper;
import atirek.pothiwala.utility.helper.NotificationHelper;
import atirek.pothiwala.utility.helper.PermissionHelper;
import okhttp3.RequestBody;
import retrofit2.Call;

public class DriverDashboardActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityDriverDashboardBinding binding;
    FusedLocationHelper fusedLocationHelper;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDriverDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        handler = new Handler(Looper.myLooper());
        fusedLocationHelper = new FusedLocationHelper(this, "location", BuildConfig.DEBUG);
        fusedLocationHelper.setListener(new FusedLocationHelper.LocationListener() {
            @Override
            public void onLocationReceived(@NonNull Location location) {
                binding.tvLocation.setText(String.format(Locale.getDefault(), "%f, %f", location.getLatitude(), location.getLongitude()));
                updateLocation(location);
            }

            @Override
            public void onLocationAvailability(boolean isAvailable) {

            }
        });

        binding.btnLogout.setOnClickListener(this);
        binding.btnProfile.setOnClickListener(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        startHandler();
        checkPermission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocation();
        stopHandler();
    }

    private void checkPermission() {
        String[] permissions = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION
        };
        boolean isPermission = PermissionHelper.checkPermissions(this, permissions);
        if (!isPermission) {
            PermissionHelper.requestPermissions(this, permissions, 0);
        } else {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            boolean isLocation = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!isLocation) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, 0);
            } else {
                startLocation();
            }
        }
    }

    private void startLocation() {
        stopLocation();
        fusedLocationHelper.initializeLocationProviders();
    }

    private void stopLocation() {
        if (fusedLocationHelper != null) {
            fusedLocationHelper.onDestroy();
        }
        cancelLocationCall();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == binding.btnProfile.getId()) {
            startActivity(new Intent(DriverDashboardActivity.this, EditDriverDetailsActivity.class));
        } else if (viewId == binding.btnLogout.getId()) {
            Constants.shared().clear();
            IntentHelper.restartApp(this);
        }
    }


    Call<String> locationCall;

    private void cancelLocationCall() {
        if (locationCall != null && locationCall.isExecuted()) {
            locationCall.cancel();
            locationCall = null;
        }
    }

    private void updateLocation(@NonNull Location location) {

        cancelLocationCall();

        JSONObject params = new JSONObject();
        try {
            params.put("did", Constants.shared().get("Drvid"));
            params.put("latitude", location.getLatitude());
            params.put("longitude", location.getLongitude());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Connector connector = new Connector(DriverDashboardActivity.this, BuildConfig.DEBUG);
        connector.setListener(new Connector.ConnectListener() {
            @Override
            public void onSuccess(int statusCode, @Nullable String json, @NonNull String message) {

            }

            @Override
            public void onFailure(boolean isNetworkIssue, @NonNull String errorMessage) {

            }
        });
        Connect.Driver connect = Connector.getClient(API.baseUrl).create(Connect.Driver.class);

        RequestBody requestBody = Connector.createPartFromJsonObject(params.toString());
        locationCall = connect.updateLocation(requestBody);
        connector.Request("update_location", locationCall);
    }


    private void startHandler() {
        handler.postDelayed(notificationRunnable, 10000);
    }

    private void stopHandler() {
        handler.removeCallbacks(notificationRunnable);
        cancelNotificationCall();
    }

    Runnable notificationRunnable = new Runnable() {
        @Override
        public void run() {
            notifications();
        }
    };

    Call<String> notificationCall;

    private void cancelNotificationCall() {
        if (notificationCall != null && notificationCall.isExecuted()) {
            notificationCall.cancel();
            notificationCall = null;
        }
    }

    private void notifications() {
        Connector connector = new Connector(DriverDashboardActivity.this, BuildConfig.DEBUG);
        connector.setListener(new Connector.ConnectListener() {
            @Override
            public void onSuccess(int statusCode, @Nullable String json, @NonNull String message) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    List<NotificationItem> list = NotificationItem.list(jsonObject.optJSONArray("result").toString());
                    intentNotifications(list);
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    startHandler();
                }
            }

            @Override
            public void onFailure(boolean isNetworkIssue, @NonNull String errorMessage) {
                Toast.makeText(DriverDashboardActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        JSONObject params = new JSONObject();
        try {
            params.put("Driverid", Constants.shared().get("Drvid"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Connect.Driver connect = Connector.getClient(API.baseUrl).create(Connect.Driver.class);
        RequestBody requestBody = Connector.createPartFromJsonObject(params.toString());
        notificationCall = connect.notifications(requestBody);
        connector.Request("notifications", notificationCall);
    }

    private void intentNotifications(List<NotificationItem> list) {
        NotificationHelper notificationHelper = new NotificationHelper(DriverDashboardActivity.this);
        notificationHelper.setColor(R.color.otmRed);
        notificationHelper.setIcon(android.R.drawable.ic_notification_overlay);
        notificationHelper.setVibrations(true);
        notificationHelper.setLights(true);
        notificationHelper.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.inflicted));

        for (NotificationItem notificationItem : list) {
            String direction = String.format(Locale.getDefault(), "%s,%s", notificationItem.pLatitude, notificationItem.pLongitude);
            Intent navigationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + direction.trim() + "&mode=d"));
            navigationIntent.setPackage("com.google.android.apps.maps");
            navigationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            notificationHelper.showNotification(Integer.parseInt(notificationItem.nid), "EMERGENCY ALERT", "Click to find the location.", navigationIntent);
        }
    }
}