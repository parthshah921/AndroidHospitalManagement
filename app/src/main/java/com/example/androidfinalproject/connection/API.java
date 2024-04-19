package com.app.onetapmedico.connection;

public interface API {

    String baseUrl = "http://192.168.29.112/OTM_Web/API/";

    interface Driver {
        String login = "driver_login.php";
        String signUp = "Dsignup.php";
        String forgotPassword = "driver_forget_pswd.php";
        String editProfile = "update_profile_driver.php";
        String updateLocation = "update_driver_location.php";
        String appointments = "Get_Appointments_Doctor.php";
        String notifications = "Get_Notification_driver.php";
    }

    interface Patient {
        String login = "patient_login.php";
        String signUp = "Psignup.php";
        String forgotPassword = "patient_forget_pswd.php";
        String editProfile = "update_profile_patient.php";
        String doctors = "get_doctor_all.php";
        String hospitals = "get_hospital_all.php";
        String drivers = "get_driver_all.php";
        String appointments = "Get_My_Appointments.php";
        String takeAppointment = "insert_appointment.php";
        String reports = "Get_Report_List.php";
        String alertDriver = "insert_notification.php";
    }

}
