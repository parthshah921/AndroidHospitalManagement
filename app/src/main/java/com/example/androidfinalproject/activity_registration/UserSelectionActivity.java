package com.app.onetapmedico.activity_registration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.app.onetapmedico.databinding.ActivityUserSelectionBinding;

public class UserSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUserSelectionBinding binding = ActivityUserSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnPatient.setOnClickListener(v -> {
            Intent intent = new Intent(UserSelectionActivity.this, LoginActivity.class);
            intent.putExtra("driver", false);
            startActivity(intent);
        });
        binding.btnDriver.setOnClickListener(v -> {
            Intent intent = new Intent(UserSelectionActivity.this, LoginActivity.class);
            intent.putExtra("driver", true);
            startActivity(intent);
        });
    }
}