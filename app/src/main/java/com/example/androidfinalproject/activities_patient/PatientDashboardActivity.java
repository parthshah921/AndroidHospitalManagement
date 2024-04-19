package com.app.onetapmedico.activities_patient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.app.onetapmedico.BuildConfig;
import com.app.onetapmedico.R;
import com.app.onetapmedico.connection.API;
import com.app.onetapmedico.connection.Connect;
import com.app.onetapmedico.databinding.ActivityPatientDashboardBinding;
import com.app.onetapmedico.models.DriverItem;
import com.app.onetapmedico.tools.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import atirek.pothiwala.connection.Connector;
import atirek.pothiwala.utility.helper.FusedLocationHelper;
import atirek.pothiwala.utility.helper.IntentHelper;
import atirek.pothiwala.utility.helper.Loader;
import atirek.pothiwala.utility.helper.PermissionHelper;
import okhttp3.RequestBody;
import retrofit2.Call;

public class PatientDashboardActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityPatientDashboardBinding binding;
    Dialog loaderDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPatientDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnLogout.setOnClickListener(this);
        //binding.btnEmergency.setOnClickListener(this);
        binding.btnAppointments.setOnClickListener(this);
        binding.btnCallAmbulance.setOnClickListener(this);
        binding.btnHospitals.setOnClickListener(this);
        binding.btnMyDoctor.setOnClickListener(this);
        binding.btnReports.setOnClickListener(this);
        binding.btnProfile.setOnClickListener(this);

        binding.btnEmergency.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                permissionToFindNearestAmbulance();
                return true;
            }
        });
    }

    private void lookForAmbulance() {
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
                startActivity(new Intent(PatientDashboardActivity.this, AmbulanceActivity.class));
            }
        }
    }

    private void permissionToFindNearestAmbulance() {
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
                Loader loader = new Loader(this);
                FusedLocationHelper fusedLocationHelper = new FusedLocationHelper(PatientDashboardActivity.this, "location", BuildConfig.DEBUG);
                fusedLocationHelper.setListener(new FusedLocationHelper.LocationListener() {
                    @Override
                    public void onLocationReceived(@NonNull Location location) {
                        fusedLocationHelper.onDestroy();
                        loaderDialog.dismiss();
                        callAmbulance(location);
                    }

                    @Override
                    public void onLocationAvailability(boolean isAvailable) {

                    }
                });
                loader.setCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        fusedLocationHelper.onDestroy();
                    }
                });
                loader.setColor(R.color.otmRed);
                loaderDialog = loader.getDialog();
                loaderDialog.show();

                fusedLocationHelper.initializeLocationProviders();
            }
        }
    }

    private void findNearestAmbulance(Location currentLocation, List<DriverItem> list) {

        DriverItem selectDriverItem = null;
        for (DriverItem driverItem : list) {

            Location driverLocation = new Location(driverItem.drvname);
            driverLocation.setLongitude(Double.parseDouble(driverItem.drvLongitude));
            driverLocation.setLatitude(Double.parseDouble(driverItem.drvLatitude));

            if (selectDriverItem != null) {
                Location selectedDriverLocation = new Location(selectDriverItem.drvname);
                selectedDriverLocation.setLongitude(Double.parseDouble(selectDriverItem.drvLongitude));
                selectedDriverLocation.setLatitude(Double.parseDouble(selectDriverItem.drvLatitude));

                float selectedDriverDistance = currentLocation.distanceTo(selectedDriverLocation);
                float driverDistance = currentLocation.distanceTo(driverLocation);

                if (selectedDriverDistance > driverDistance) {
                    selectDriverItem = driverItem;
                }
            } else {
                selectDriverItem = driverItem;
            }
        }

        if (selectDriverItem != null) {
            alert(currentLocation, selectDriverItem.drvid);
            //callWithPermission(selectDriverItem.drvcontact);
        } else {
            Toast.makeText(this, "Failed to find ambulance near by.", Toast.LENGTH_SHORT).show();
        }
    }

    private void callWithPermission(String phone) {
        String[] permissions = new String[]{
                Manifest.permission.CALL_PHONE
        };
        if (!PermissionHelper.checkPermissions(PatientDashboardActivity.this, permissions)) {
            PermissionHelper.requestPermissions(PatientDashboardActivity.this, permissions, 0);
            return;
        }
        IntentHelper.phoneCall(PatientDashboardActivity.this, phone);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == binding.btnEmergency.getId()) {
            permissionToFindNearestAmbulance();
        } else if (viewId == binding.btnAppointments.getId()) {
            startActivity(new Intent(PatientDashboardActivity.this, AppointmentListingActivity.class));
        } else if (viewId == binding.btnCallAmbulance.getId()) {
            lookForAmbulance();
        } else if (viewId == binding.btnHospitals.getId()) {
            startActivity(new Intent(PatientDashboardActivity.this, HospitalListingActivity.class));
        } else if (viewId == binding.btnMyDoctor.getId()) {
            startActivity(new Intent(PatientDashboardActivity.this, DoctorListingActivity.class));
        } else if (viewId == binding.btnProfile.getId()) {
            startActivity(new Intent(PatientDashboardActivity.this, EditPatientDetailsActivity.class));
        } else if (viewId == binding.btnReports.getId()) {
            startActivity(new Intent(PatientDashboardActivity.this, ReportListingActivity.class));
        } else if (viewId == binding.btnLogout.getId()) {
            Constants.shared().clear();
            IntentHelper.restartApp(this);
        }
    }

    private void callAmbulance(Location currentLocation) {
        Connector connector = new Connector(PatientDashboardActivity.this, BuildConfig.DEBUG);
        connector.setListener(new Connector.ConnectListener() {
            @Override
            public void onSuccess(int statusCode, @Nullable String json, @NonNull String message) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    List<DriverItem> list = DriverItem.list(jsonObject.optJSONArray("result").toString());
                    findNearestAmbulance(currentLocation, list);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(boolean isNetworkIssue, @NonNull String errorMessage) {
                Toast.makeText(PatientDashboardActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        Connect.Patient connect = Connector.getClient(API.baseUrl).create(Connect.Patient.class);
        Call<String> stringCall = connect.drivers();
        Loader loader = new Loader(this);
        loader.setCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                stringCall.cancel();
            }
        });
        loader.setColor(R.color.otmRed);
        connector.setLoaderDialog(loader.getDialog());
        connector.Request("drivers", stringCall);
    }

    private void alert(Location currentLocation, String driverId) {
        Connector connector = new Connector(PatientDashboardActivity.this, BuildConfig.DEBUG);
        connector.setListener(new Connector.ConnectListener() {
            @Override
            public void onSuccess(int statusCode, @Nullable String json, @NonNull String message) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    boolean success = jsonObject.optInt("response", 0) == 1;
                    if (success) {
                        Toast.makeText(PatientDashboardActivity.this, "Ambulance driver has been alerted.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PatientDashboardActivity.this, "Failed to alert ambulance driver.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(boolean isNetworkIssue, @NonNull String errorMessage) {
                Toast.makeText(PatientDashboardActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        JSONObject params = new JSONObject();
        try {
            params.put("driverid", driverId);
            params.put("patientid", Constants.shared().get("Pid"));
            params.put("p_latitude", currentLocation.getLatitude());
            params.put("p_longitude", currentLocation.getLongitude());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Connect.Patient connect = Connector.getClient(API.baseUrl).create(Connect.Patient.class);

        RequestBody requestBody = Connector.createPartFromJsonObject(params.toString());
        Call<String> stringCall = connect.alertDriver(requestBody);
        Loader loader = new Loader(this);
        loader.setCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                stringCall.cancel();
            }
        });
        loader.setColor(R.color.otmRed);
        connector.setLoaderDialog(loader.getDialog());
        connector.Request("alert", stringCall);
    }
}