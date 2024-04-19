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

public class DoctorItem {

    @SerializedName("Haddress")
    public String haddress;

    @SerializedName("Demail")
    public String demail;

    @SerializedName("Hcity")
    public String hcity;

    @SerializedName("Dpwd")
    public String dpwd;

    @SerializedName("Dspeciality")
    public String dspeciality;

    @SerializedName("Dcontact")
    public String dcontact;

    @SerializedName("Did")
    public String did;

    @SerializedName("Dname")
    public String dname;

    public static DoctorItem object(String json) {
        return MyApplication.fromJson(json, DoctorItem.class);
    }

    public static List<DoctorItem> list(@NonNull String json) {
        Type listType = new TypeToken<List<DoctorItem>>() {
        }.getType();
        List<DoctorItem> list = MyApplication.fromJson(json, listType);
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public static List<DoctorItem> list(@Nullable JSONArray json) {
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