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

public class ReportItem {


    @SerializedName("Pname")
    public String pname;

    @SerializedName("Pre_File")
    public String preFile;

    @SerializedName("Pid")
    public String pid;

    @SerializedName("Aid")
    public String aid;

    @SerializedName("Pre_date")
    public String preDate;

    @SerializedName("Pre_id")
    public String preId;

    @SerializedName("Did")
    public String did;

    @SerializedName("Dname")
    public String dname;

    @SerializedName("Prescription_dtls")
    public String prescriptionDtls;
    

    public static ReportItem object(String json) {
        return MyApplication.fromJson(json, ReportItem.class);
    }

    public static List<ReportItem> list(@NonNull String json) {
        Type listType = new TypeToken<List<ReportItem>>() {
        }.getType();
        List<ReportItem> list = MyApplication.fromJson(json, listType);
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public static List<ReportItem> list(@Nullable JSONArray json) {
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