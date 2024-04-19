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

public class AppointmentItem {

    @SerializedName("Pname")
    public String pname;

    @SerializedName("Astatus")
    public String astatus;

    @SerializedName("Atime")
    public Object atime;

    @SerializedName("Pid")
    public String pid;

    @SerializedName("Aid")
    public String aid;

    @SerializedName("Adate")
    public String adate;

    @SerializedName("Adetail")
    public String adetail;

    @SerializedName("Did")
    public String did;

    @SerializedName("Dname")
    public String dname;

    public static AppointmentItem object(String json) {
        return MyApplication.fromJson(json, AppointmentItem.class);
    }

    public static List<AppointmentItem> list(@NonNull String json) {
        Type listType = new TypeToken<List<AppointmentItem>>() {
        }.getType();
        List<AppointmentItem> list = MyApplication.fromJson(json, listType);
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public static List<AppointmentItem> list(@Nullable JSONArray json) {
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