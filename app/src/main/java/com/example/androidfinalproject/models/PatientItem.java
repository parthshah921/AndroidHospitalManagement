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

public class PatientItem {

    @SerializedName("Pname")
    public String pname;

    @SerializedName("Paddress")
    public String paddress;

    @SerializedName("Pcontact")
    public String pcontact;

    @SerializedName("Pemail")
    public String pemail;

    @SerializedName("Pgender")
    public String pgender;

    @SerializedName("Pid")
    public String pid;

    @SerializedName("Page")
    public String page;

    @SerializedName("Ppwd")
    public String ppwd;

    @SerializedName("Pcity")
    public String pcity;

    public static PatientItem object(String json) {
        return MyApplication.fromJson(json, PatientItem.class);
    }

    public static List<PatientItem> list(@NonNull String json) {
        Type listType = new TypeToken<List<PatientItem>>() {
        }.getType();
        List<PatientItem> list = MyApplication.fromJson(json, listType);
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public static List<PatientItem> list(@Nullable JSONArray json) {
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