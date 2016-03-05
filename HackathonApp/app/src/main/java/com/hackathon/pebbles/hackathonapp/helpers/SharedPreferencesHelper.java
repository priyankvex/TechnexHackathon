package com.hackathon.pebbles.hackathonapp.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.hackathon.pebbles.hackathonapp.Constants;
import com.hackathon.pebbles.hackathonapp.models.Venue;
import com.hackathon.pebbles.hackathonapp.models.Weather;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by @priyankvex on 4/3/16.
 * Class to perform all reading and writing to shared preferences.
 */
public class SharedPreferencesHelper {

    private static SharedPreferences getSharedPreferences(Context context){
        SharedPreferences prefs = context.getSharedPreferences(
                "prefs", Context.MODE_PRIVATE);
        return  prefs;
    }

    public static void setLocationSetStatus(Context context, boolean status){
        SharedPreferences prefs = getSharedPreferences(context);
        prefs.edit().putBoolean(Constants.KEY_LOCATION_SET, status).apply();
    }

    public static boolean getLocationSetStatus(Context context){
        SharedPreferences prefs = getSharedPreferences(context);
        return prefs.getBoolean(Constants.KEY_LOCATION_SET, false);
    }

    public static void setLatLongName(Context context, double latitude, double longitude, String name){
        SharedPreferences prefs = getSharedPreferences(context);
        long lat = (long) latitude;
        long longi = (long) longitude;
        prefs.edit().putLong(Constants.KEY_LATITUDE, lat).apply();
        prefs.edit().putLong(Constants.KEY_LONGITUDE, longi).apply();
        prefs.edit().putString(Constants.KEY_PLACE_NAME, name).apply();
    }

    public static Map<String, String> getLatLongName(Context context){
        SharedPreferences prefs = getSharedPreferences(context);
        String name = prefs.getString(Constants.KEY_PLACE_NAME, "");
        String latitude = String.valueOf(prefs.getLong(Constants.KEY_LATITUDE, 0));
        String longitude = String.valueOf(prefs.getLong(Constants.KEY_LONGITUDE, 0));
        Map<String, String> map = new HashMap<>();
        map.put(Constants.KEY_LATITUDE, latitude);
        map.put(Constants.KEY_LONGITUDE, longitude);
        map.put(Constants.KEY_PLACE_NAME, name);
        return map;
    }

    public static void setWeatherData(Context context, String mainWeather, String weatherDesc){
        SharedPreferences prefs = getSharedPreferences(context);
        prefs.edit().putString(Constants.KEY_MAIN_WEATHER, mainWeather).apply();
        prefs.edit().putString(Constants.KEY_DESC_WEATHER, weatherDesc).apply();
    }

    public static Map<String, String> getWeatherData(Context context){
        SharedPreferences prefs = getSharedPreferences(context);
        String defaultDesc = "Min Temp : NA\nMax Temp : NA\nHumidity : NA\nWind Speed : NA\n";
        String mainWeather = prefs.getString(Constants.KEY_MAIN_WEATHER, "NA");
        String descWeather = prefs.getString(Constants.KEY_DESC_WEATHER, defaultDesc);
        Map<String, String> map = new HashMap<>();
        map.put(Constants.KEY_MAIN_WEATHER, mainWeather);
        map.put(Constants.KEY_DESC_WEATHER, descWeather);
        return map;
    }

    public static void setFSQAccessToken(Context context, String accessToken){
        SharedPreferences prefs = getSharedPreferences(context);
        prefs.edit().putString(Constants.KEY_FSQ_TOKEN, accessToken).apply();
        prefs.edit().putBoolean(Constants.KEY_FSQ_SESSION, true).apply();
    }

    public static boolean getFSQSessionStatus(Context context){
        SharedPreferences prefs = getSharedPreferences(context);
        return prefs.getBoolean(Constants.KEY_FSQ_SESSION, false);
    }

    public static String getFSQAccessToken(Context context){
        SharedPreferences prefs = getSharedPreferences(context);
        return prefs.getString(Constants.KEY_FSQ_TOKEN, "");
    }

    public static void resetFSQSession(Context context){
        SharedPreferences prefs = getSharedPreferences(context);
        prefs.edit().putString(Constants.KEY_FSQ_TOKEN, "").apply();
        prefs.edit().putBoolean(Constants.KEY_FSQ_SESSION, false).apply();
        Venue.deleteAll(Venue.class);
    }

}
