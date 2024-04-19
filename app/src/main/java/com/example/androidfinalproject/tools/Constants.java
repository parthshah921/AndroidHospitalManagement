package com.app.onetapmedico.tools;

import android.content.Context;
import android.content.SharedPreferences;

import com.app.onetapmedico.models.DriverItem;
import com.app.onetapmedico.models.PatientItem;

import org.json.JSONObject;

/**
 * Created by Atirek Pothiwala on 10/17/2018.
 */

public class Constants {

    private static Constants constants;
    private final SharedPreferences sharedPreferences;

    private Constants(Context context) {
        sharedPreferences = context.getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
    }

    public static Constants shared() {
        if (constants == null) {
            constants = new Constants(MyApplication.shared());
        }
        return constants;
    }

    public void set(String json, int userType) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user", json);
        editor.putInt("userType", userType);
        editor.apply();
    }

    public void set(PatientItem PatientItem) {
        String jsonString = MyApplication.toJson(PatientItem);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user", jsonString);
        editor.putInt("userType", 1);
        editor.apply();
    }

    public void set(DriverItem driverItem) {
        String jsonString = MyApplication.toJson(driverItem);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user", jsonString);
        editor.putInt("userType", 2);
        editor.apply();
    }

    public String get(String key) {
        try {
            JSONObject jsonObject = new JSONObject(sharedPreferences.getString("user", ""));
            return jsonObject.optString(key, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public PatientItem getPatient() {
        if (sharedPreferences.contains("user")) {
            return MyApplication.fromJson(sharedPreferences.getString("user", ""), PatientItem.class);
        }
        return null;
    }

    public DriverItem getDriver() {
        if (sharedPreferences.contains("user")) {
            return MyApplication.fromJson(sharedPreferences.getString("user", ""), DriverItem.class);
        }
        return null;
    }

    public boolean exists() {
        return sharedPreferences.contains("user");
    }

    public int userType() {
        return sharedPreferences.getInt("userType", 0);
    }

    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
