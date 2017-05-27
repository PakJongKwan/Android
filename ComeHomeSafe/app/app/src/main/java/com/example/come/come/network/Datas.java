package com.example.come.come.network;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by dsm on 2017-05-17.
 */

public class Datas {

    public static final String DATABASE_NAME = "come";
    public static final String EMERGENCY_PHONE = "emergency_phone";
    public static final String AUTO_LOGIN = "auto_login";
    public static final String ID = "id";
    public static final String PW = "pw";
    public static final String TAXI = "taxi";
    public static final String RIDE = "ride";
    public static final String ON_TAXI = "ontaxi";

    private static String id;
    private static String pw;

    public static String getEmergencyPhone(Context context) {
        SharedPreferences sharedPreferences = initSharedPreferences(context);

        String value = sharedPreferences.getString(EMERGENCY_PHONE, "");

        if ("".equals(value)) {
            return null;
        }

        return value;
    }

    public static void insertEmergencyPhone(Context context, String phone) {
        SharedPreferences.Editor editor = initEditor(context);
        editor.putString(EMERGENCY_PHONE, phone);
        editor.apply();
    }

    public static boolean isAutoLogin(Context context) {
        SharedPreferences sharedPreferences = initSharedPreferences(context);
        return sharedPreferences.getBoolean(AUTO_LOGIN, false);
    }

    public static void setAutoLogin(Context context, boolean status) {
        SharedPreferences.Editor editor = initEditor(context);
        editor.putBoolean(AUTO_LOGIN, status);
        editor.apply();
    }

    public static void insertIDPW(String id, String pw) {
        Datas.id = id;
        Datas.pw = pw;
    }

    public static void saveLastestIDPW(Context context, String id, String pw) {
        SharedPreferences.Editor editor = initEditor(context);
        editor.putString(ID, id);
        editor.putString(PW, pw);
        editor.apply();
    }

    public static HashMap<String, String> getLastestIDPW(Context context) {
        SharedPreferences sharedPreferences = initSharedPreferences(context);

        String id = sharedPreferences.getString(ID, "");
        String pw = sharedPreferences.getString(PW, "");

        if (id.equals("") || pw.equals("")) {
            return null;
        }

        HashMap<String, String> result = new HashMap<>();
        result.put(ID, id);
        result.put(PW, pw);

        return result;
    }

    public static void insertRide(Context context, boolean status) {
        SharedPreferences.Editor editor = initEditor(context);
        editor.putBoolean(RIDE, status);
        editor.apply();
    }

    public static boolean getRide(Context context) {
        SharedPreferences sharedPreferences = initSharedPreferences(context);

        return sharedPreferences.getBoolean(RIDE, false);
    }


    public static HashMap<String, String> getIDPW() {
        HashMap<String, String> idpw = new HashMap<>();

        idpw.put(ID, id);
        idpw.put(PW, pw);

        return idpw;
    }

    public static boolean isOnTaxi(Context context) {
        SharedPreferences sharedPreferences = initSharedPreferences(context);

        return sharedPreferences.getBoolean(ON_TAXI, false);
    }

    public static void onTaxi(Context context) {
        setTaxiStatus(context, true);
    }

    public static void offTaxi(Context context) {
        setTaxiStatus(context, false);
    }

    private static void setTaxiStatus(Context context, boolean status) {
        SharedPreferences.Editor editor = initEditor(context);
        editor.putBoolean(TAXI, status);
        editor.apply();
    }

    public static void insertTaxi(Context context, String taxi) {
        SharedPreferences.Editor editor = initEditor(context);
        editor.putString(TAXI, taxi);
        editor.apply();
    }

    public static String getTaxi(Context context) {
        SharedPreferences sharedPreferences = initSharedPreferences(context);

        String taxi = sharedPreferences.getString(TAXI, "");

        if ("".equals(taxi)) {
            return null;
        }

        return taxi;
    }

    public static void clearTaxi(Context context) {
        insertTaxi(context, "");
    }

    private static SharedPreferences initSharedPreferences(Context context) {
        return context.getSharedPreferences(DATABASE_NAME, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor initEditor(Context context) {
        return context.getSharedPreferences(DATABASE_NAME, Context.MODE_PRIVATE).edit();
    }


}
