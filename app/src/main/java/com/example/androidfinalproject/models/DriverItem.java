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

public class DriverItem {

    @SerializedName("Drvid")
    public String drvid;

    @SerializedName("Drvemail")
    public String drvemail;

    @SerializedName("Drvname")
    public String drvname;

    @SerializedName("DrvLatitude")
    public String drvLatitude;

    @SerializedName("Drvpwd")
    public String drvpwd;

    @SerializedName("DrvLongitude")
    public String drvLongitude;

    @SerializedName("Drvcontact")
    public String drvcontact;

    public static DriverItem object(String json) {
        return MyApplication.fromJson(json, DriverItem.class);
    }

    public static List<DriverItem> list(@NonNull String json) {
        Type listType = new TypeToken<List<DriverItem>>() {
        }.getType();
        List<DriverItem> list = MyApplication.fromJson(json, listType);
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public static List<DriverItem> list(@Nullable JSONArray json) {
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