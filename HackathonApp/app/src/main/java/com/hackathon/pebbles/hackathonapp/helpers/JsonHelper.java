package com.hackathon.pebbles.hackathonapp.helpers;

import android.util.Log;

import com.hackathon.pebbles.hackathonapp.models.Venue;
import com.hackathon.pebbles.hackathonapp.models.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by @priyankvex on 4/3/16.
 * Class that handles json parsing
 */
public class JsonHelper {

    public static Weather getWeatherData(JSONObject json){
        Weather weather = new Weather();
        Log.d("test", json.toString());
        try {
            JSONObject weatherJson = json.getJSONArray("weather").getJSONObject(0);
            JSONObject mainJson = json.getJSONObject("main");
            JSONObject windJson = json.getJSONObject("wind");
            String mainWeather = weatherJson.getString("main");
            String weatherDescription = weatherJson.getString("description");
            String iconId = weatherJson.getString("icon");
            String temp = mainJson.getString("temp");
            String minTemp = mainJson.getString("temp_min");
            String maxTemp = mainJson.getString("temp_max");
            String humidity = mainJson.getString("humidity");
            String windSpeed = windJson.getString("speed");
            String placeName = json.getString("name");
            weather.setMainWeather(mainWeather);
            weather.setIconId(iconId);
            weather.setWeatherDescription(weatherDescription);
            weather.setTemperature(temp);
            weather.setMinTemperature(minTemp);
            weather.setMaxTemperature(maxTemp);
            weather.setHumidity(humidity);
            weather.setWindSpeed(windSpeed);
            weather.setPlaceName(placeName);
        } catch (JSONException e) {
            e.printStackTrace();
            weather.setMainWeather("Not Available");
            weather.setIconId("");
            weather.setWeatherDescription("");
            weather.setTemperature("");
            weather.setMinTemperature("");
            weather.setMaxTemperature("");
            weather.setHumidity("");
            weather.setWindSpeed("");
            weather.setPlaceName("");
        }
        return weather;
    }

    public static ArrayList<Venue> getTop3Places(JSONObject json){
        ArrayList<Venue> venues = new ArrayList<>();
        Venue.deleteAll(Venue.class);
        try {
            JSONObject responseJson = json.getJSONObject("response");
            JSONObject groupsJson = responseJson.getJSONArray("groups").getJSONObject(0);
            JSONArray itemsJsonArray = groupsJson.getJSONArray("items");
            for (int i = 0 ; i < itemsJsonArray.length(); i++){
                JSONObject itemJson = itemsJsonArray.getJSONObject(i);
                JSONObject venueJson = itemJson.getJSONObject("venue");
                String venueName = venueJson.getString("name");
                String address = venueJson.getJSONObject("location").getJSONArray("formattedAddress").getString(0);
                String category = venueJson.getJSONArray("categories").getJSONObject(0).getString("name");
                String checkIns = venueJson.getJSONObject("stats").getString("checkinsCount");
                Venue venue = new Venue();
                venue.setName(venueName);
                venue.setAddress(address);
                venue.setCategory(category);
                venue.setCheckIns(checkIns);
                venues.add(venue);
                venue.save();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return venues;
    }
}
