package com.hackathon.pebbles.hackathonapp.helpers;

import android.util.Log;

/**
 * Created by @priyankvex on 4/3/16.
 * Class to provide methods for formatting data
 */
public class FormatHelper {

    public static String getTemperature(String temperature){
        Double temp = Double.valueOf(temperature);
        int scale = (int) Math.pow(10, 1);
        temp = temp - 273;
        temp = (double) Math.round(temp * scale) / scale;
        String formattedTemp = String.valueOf(temp);
        Log.d("test", formattedTemp);
        return formattedTemp;
    }
}
