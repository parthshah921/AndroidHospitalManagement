package com.app.onetapmedico.activity_registration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.app.onetapmedico.R;
import com.app.onetapmedico.activities_patient.PatientDashboardActivity;
import com.app.onetapmedico.activity_driver.DriverDashboardActivity;
import com.app.onetapmedico.tools.Constants;

public class SplashActivity extends AppCompatActivity {

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView SplashBg = (ImageView) findViewById(R.id.SplashBg);
        SplashBg.animate().setStartDelay(1850).scaleX(80f).scaleY(80f).setDuration(2000);
        SplashBg.animate().setStartDelay(1850).alpha(0).setDuration(800);


        handler = new Handler();
        handler.postDelayed(() -> {
            if (Constants.shared().exists()) {
                int userType = Constants.shared().userType();
                if (userType == 1) {
                    startActivity(new Intent(SplashActivity.this, PatientDashboardActivity.class));
                } else if (userType == 2) {
                    startActivity(new Intent(SplashActivity.this, DriverDashboardActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, UserSelectionActivity.class));
                }
            } else {
                startActivity(new Intent(SplashActivity.this, UserSelectionActivity.class));
            }
            finish();
        }, 2000);

    }
}