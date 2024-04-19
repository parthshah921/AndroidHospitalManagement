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

public class NotificationItem {

    @SerializedName("Drvid")
    public String drvid;

    @SerializedName("nid")
    public String nid;

    @SerializedName("P_longitude")
    public String pLongitude;

    @SerializedName("Pid")
    public String pid;

    @SerializedName("P_latitude")
    public String pLatitude;

    public static NotificationItem object(String json) {
        return MyApplication.fromJson(json, NotificationItem.class);
    }

    public static List<NotificationItem> list(@NonNull String json) {
        Type listType = new TypeToken<List<NotificationItem>>() {
        }.getType();
        List<NotificationItem> list = MyApplication.fromJson(json, listType);
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public static List<NotificationItem> list(@Nullable JSONArray json) {
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