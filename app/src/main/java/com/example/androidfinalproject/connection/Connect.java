package com.app.onetapmedico.connection;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;


/**
 * Created by android on 11/11/2016.
 */

public interface Connect {

    interface Driver {
        @POST(API.Driver.login)
        Call<String> login(@Body RequestBody requestBody);

        @POST(API.Driver.signUp)
        Call<String> signUp(@Body RequestBody requestBody);

        @POST(API.Driver.forgotPassword)
        Call<String> forgotPassword(@Body RequestBody requestBody);

        @POST(API.Driver.editProfile)
        Call<String> editProfile(@Body RequestBody requestBody);

        @POST(API.Driver.updateLocation)
        Call<String> updateLocation(@Body RequestBody requestBody);

        @POST(API.Driver.appointments)
        Call<String> appointments(@Body RequestBody requestBody);

        @POST(API.Driver.notifications)
        Call<String> notifications(@Body RequestBody requestBody);
    }

    interface Patient {
        @POST(API.Patient.login)
        Call<String> login(@Body RequestBody requestBody);

        @POST(API.Patient.signUp)
        Call<String> signUp(@Body RequestBody requestBody);

        @POST(API.Patient.forgotPassword)
        Call<String> forgotPassword(@Body RequestBody requestBody);

        @POST(API.Patient.editProfile)
        Call<String> editProfile(@Body RequestBody requestBody);

        @POST(API.Patient.appointments)
        Call<String> appointments(@Body RequestBody requestBody);

        @POST(API.Patient.takeAppointment)
        Call<String> takeAppointment(@Body RequestBody requestBody);

        @POST(API.Patient.reports)
        Call<String> reports(@Body RequestBody requestBody);

        @GET(API.Patient.doctors)
        Call<String> doctors();

        @GET(API.Patient.hospitals)
        Call<String> hospitals();

        @GET(API.Patient.drivers)
        Call<String> drivers();

        @POST(API.Patient.alertDriver)
        Call<String> alertDriver(@Body RequestBody requestBody);

    }

}



