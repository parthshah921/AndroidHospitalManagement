package com.app.onetapmedico.tools;

import android.app.Application;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public class MyApplication extends Application {

    private static MyApplication application;

    public static MyApplication shared() {
        if (application == null) {
            application = new MyApplication();
        }
        return application;
    }

    public Gson gson = new Gson();

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    private String toJSON(Object src) {
        return gson.toJson(src);
    }

    private <T> T fromJSON(String json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }


    public static String toJson(Object src) {
        return shared().toJSON(src);
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        return shared().fromJSON(json, typeOfT);
    }

}
