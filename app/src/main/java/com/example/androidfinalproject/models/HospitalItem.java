package com.app.onetapmedico.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.onetapmedico.tools.MyApplication;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HospitalItem {

    @SerializedName("Hid")
    public String hid;

    @SerializedName("Hcontact")
    public String hcontact;

    @SerializedName("Hcity")
    public String hcity;

    @SerializedName("Hname")
    public String hname;

    @SerializedName("Htime")
    public String htime;

    @SerializedName("Hdays")
    public String hdays;

    @SerializedName("Hspecialfor")
    public String hspecialfor;

    public static HospitalItem object(String json) {
        return MyApplication.fromJson(json, HospitalItem.class);
    }

    public static List<HospitalItem> list(@NonNull String json) {
        Type listType = new TypeToken<List<HospitalItem>>() {
        }.getType();
        List<HospitalItem> list = MyApplication.fromJson(json, listType);
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public static List<HospitalItem> list(@Nullable JSONArray json) {
        if (json != null) {
            return list(json.toString());
        } else {
            return Collections.emptyList();
        }
    }

    @NonNull
    @Override
    public String toString() {
        return MyApplication.toJson(this);
    }
}