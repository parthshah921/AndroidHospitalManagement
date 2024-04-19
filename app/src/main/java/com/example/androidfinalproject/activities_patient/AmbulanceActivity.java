package com.app.onetapmedico.activities_patient;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.app.onetapmedico.BuildConfig;
import com.app.onetapmedico.R;
import com.app.onetapmedico.connection.API;
import com.app.onetapmedico.connection.Connect;
import com.app.onetapmedico.databinding.ActivityPickerBinding;
import com.app.onetapmedico.models.DriverItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import atirek.pothiwala.connection.Connector;
import atirek.pothiwala.utility.helper.FusedLocationHelper;
import atirek.pothiwala.utility.helper.IntentHelper;
import atirek.pothiwala.utility.helper.Loader;
import atirek.pothiwala.utility.helper.PermissionHelper;
import retrofit2.Call;

public class AmbulanceActivity extends AppCompatActivity implements OnMapReadyCallback, FusedLocationHelper.LocationListener {

    Handler handler;
    private static final int DEFAULT_ZOOM = 18;
    private static final int DEFAULT_TILT = 0;

    private GoogleMap googleMap;
    private Marker pickMarker;
    private FusedLocationHelper locationHelper;

    private Dialog dialogLoader = null;

    private void loader(boolean enable) {
        if (enable) {
            if (dialogLoader == null) {
                Loader loader = new Loader(this);
                loader.setColor(R.color.otmRed);
                dialogLoader = loader.getDialog();
            }
            if (!dialogLoader.isShowing()) {
                dialogLoader.show();
            }
        } else {
            if (dialogLoader != null && dialogLoader.isShowing()) {
                dialogLoader.dismiss();
            }
        }
    }

    private List<DriverItem> list = new ArrayList<>();
    private ActivityPickerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPickerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        handler = new Handler(getMainLooper());
        locationHelper = new FusedLocationHelper(this, "location", BuildConfig.DEBUG);
        locationHelper.setListener(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        this.googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        this.googleMap.getUiSettings().setMapToolbarEnabled(false);
        this.googleMap.getUiSettings().setCompassEnabled(true);
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);
        this.googleMap.getUiSettings().setZoomGesturesEnabled(true);

        this.googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                pickMarker.setPosition(googleMap.getCameraPosition().target);
            }
        });

        this.googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return contactDriver(marker);
            }
        });

        this.googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                contactDriver(marker);
            }
        });

        pickMarker = this.googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location))
                .position(googleMap.getCameraPosition().target)
                .draggable(false));
        loader(true);
        locationHelper.initializeLocationProviders();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            onBackPressed();
        }
    }

    private void callWithPermission(String phone) {
        String[] permissions = new String[]{
                Manifest.permission.CALL_PHONE
        };
        if (!PermissionHelper.checkPermissions(AmbulanceActivity.this, permissions)) {
            PermissionHelper.requestPermissions(AmbulanceActivity.this, permissions, 0);
            return;
        }
        IntentHelper.phoneCall(AmbulanceActivity.this, phone);
    }

    private boolean contactDriver(Marker marker) {
        marker.showInfoWindow();

        Object object = marker.getTag();
        if (object != null) {
            DriverItem driverItem = (DriverItem) object;
            callWithPermission(driverItem.drvcontact);
            return true;
        }
        return false;
    }

    void adjustZoomLevel(LatLng source) {
        CameraPosition cameraPosition = CameraPosition.builder().target(source).tilt(DEFAULT_TILT).zoom(DEFAULT_ZOOM).bearing(0).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationHelper.onDestroy();
    }

    @Override
    public void onLocationReceived(@NonNull Location location) {
        locationHelper.onDestroy();
        loader(false);

        adjustZoomLevel(new LatLng(location.getLatitude(), location.getLongitude()));
        listing();
    }

    @Override
    public void onLocationAvailability(boolean isAvailable) {
        loader(false);
    }

    private void listing() {
        Connector connector = new Connector(AmbulanceActivity.this, BuildConfig.DEBUG);
        connector.setListener(new Connector.ConnectListener() {
            @Override
            public void onSuccess(int statusCode, @Nullable String json, @NonNull String message) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    list = DriverItem.list(jsonObject.optJSONArray("result").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    plotDriverMarker();
                }
            }

            @Override
            public void onFailure(boolean isNetworkIssue, @NonNull String errorMessage) {
                Toast.makeText(AmbulanceActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
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

    private void plotDriverMarker() {
        for (DriverItem driverItem : list) {
            LatLng latLng = new LatLng(Double.parseDouble(driverItem.drvLatitude), Double.parseDouble(driverItem.drvLongitude));
            Marker marker = this.googleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ambulance))
                    .position(latLng)
                    .title(driverItem.drvname)
                    .draggable(false));
            marker.setTag(driverItem);
            marker.showInfoWindow();
        }
    }
}
