package com.hackathon.pebbles.hackathonapp.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cocosw.bottomsheet.BottomSheet;
import com.foursquare.android.nativeoauth.FoursquareOAuth;
import com.foursquare.android.nativeoauth.FoursquareOAuthException;
import com.foursquare.android.nativeoauth.model.AccessTokenResponse;
import com.foursquare.android.nativeoauth.model.AuthCodeResponse;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.hackathon.pebbles.hackathonapp.ApplicationController;
import com.hackathon.pebbles.hackathonapp.Constants;
import com.hackathon.pebbles.hackathonapp.R;
import com.hackathon.pebbles.hackathonapp.helpers.FormatHelper;
import com.hackathon.pebbles.hackathonapp.helpers.JsonHelper;
import com.hackathon.pebbles.hackathonapp.helpers.SharedPreferencesHelper;
import com.hackathon.pebbles.hackathonapp.models.Venue;
import com.hackathon.pebbles.hackathonapp.models.Weather;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

public class MainActivity extends AppCompatActivity {

    int PLACE_PICKER_REQUEST = 1;
    int REQUEST_CODE_FSQ_CONNECT = 2;
    int REQUEST_CODE_FSQ_TOKEN_EXCHANGE = 3;

    BottomSheet mBottomSheet;
    CardView cardLocation;
    TextView textViewLocation;
    ImageView imageViewWeather;
    TextView textViewMainWeather;
    TextView textViewWeatherDetails;
    TextView textViewViewMore;
    LinearLayout layoutPlaces;
    FancyButton buttonAuthFourSquare1;
    TextView textViewVenueName1;
    TextView textViewVenueAddress1;
    TextView textViewVenueCategory1;
    TextView textViewVenueRating1;
    TextView textViewVenueName2;
    TextView textViewVenueAddress2;
    TextView textViewVenueCategory2;
    TextView textViewVenueRating2;
    TextView textViewVenueName3;
    TextView textViewVenueAddress3;
    TextView textViewVenueCategory3;
    TextView textViewVenueRating3;
    CardView cardPlace1;
    CardView cardPlace2;
    CardView cardPlace3;
    TextView textViewMessage;

    private DisplayImageOptions options;

    Weather mWeather;
    ArrayList<Venue> mVenues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpToolbar();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.spinner)
                .showImageForEmptyUri(R.drawable.spinner)
                .showImageOnFail(R.drawable.spinner)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageOnFail(R.drawable.spinner)
                .cacheOnDisk(true)
                .build();
        setUpBottomSheetMenu();
        getViews();
        initData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void setUpBottomSheetMenu(){
        mBottomSheet = new BottomSheet.Builder(this, R.style.BottomSheet_CustomizedDialog)
                .grid()
                .sheet(R.menu.bottom_sheet_menu)
                .listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.action_auth_foursquare:
                                handleFSQDisconnect();
                                break;
                            case R.id.action_top_venues:
                                if (SharedPreferencesHelper.getFSQSessionStatus(getApplicationContext())){
                                    Intent i = new Intent(MainActivity.this, TopPlacesActivity.class);
                                    startActivity(i);
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "Connect to Foursquare First", Toast.LENGTH_LONG).show();
                                }
                                break;
                            case R.id.action_about:
                                Intent i2 = new Intent(MainActivity.this, AboutActivity.class);
                                startActivity(i2);
                                break;
                        }
                    }
                }).build();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mBottomSheet.show();
        return super.onOptionsItemSelected(item);
    }

    private void setUpToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void getViews(){
        cardLocation = (CardView) findViewById(R.id.cardLocation);
        textViewLocation = (TextView) findViewById(R.id.textViewLocation);
        imageViewWeather = (ImageView) findViewById(R.id.imageViewWeather);
        textViewMainWeather = (TextView) findViewById(R.id.textViewMainWeather);
        textViewWeatherDetails = (TextView) findViewById(R.id.textViewWeatherDetails);
        textViewMessage = (TextView) findViewById(R.id.textViewMessage);
        textViewViewMore = (TextView) findViewById(R.id.textViewViewMore);
        layoutPlaces = (LinearLayout) findViewById(R.id.layoutPlaces);
        buttonAuthFourSquare1 = (FancyButton) findViewById(R.id.buttonAuthFoursquare1);
        textViewVenueName1 = (TextView) findViewById(R.id.textViewVenueName1);
        textViewVenueAddress1 = (TextView) findViewById(R.id.textViewAddress1);
        textViewVenueCategory1 = (TextView) findViewById(R.id.textViewCategory1);
        textViewVenueRating1 = (TextView) findViewById(R.id.textViewRating1);
        textViewVenueName2 = (TextView) findViewById(R.id.textViewVenueName2);
        textViewVenueAddress2 = (TextView) findViewById(R.id.textViewAddress2);
        textViewVenueCategory2 = (TextView) findViewById(R.id.textViewCategory2);
        textViewVenueRating2 = (TextView) findViewById(R.id.textViewRating2);
        textViewVenueName3 = (TextView) findViewById(R.id.textViewVenueName3);
        textViewVenueAddress3 = (TextView) findViewById(R.id.textViewAddress3);
        textViewVenueCategory3 = (TextView) findViewById(R.id.textViewCategory3);
        textViewVenueRating3 = (TextView) findViewById(R.id.textViewRating3);
        cardPlace1 = (CardView) findViewById(R.id.cardPlace1);
        cardPlace2 = (CardView) findViewById(R.id.cardPlace2);
        cardPlace3 = (CardView) findViewById(R.id.cardPlace3);
        cardLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Construct an intent for the place picker
                try {
                    PlacePicker.IntentBuilder intentBuilder =
                            new PlacePicker.IntentBuilder();
                    Intent intent = intentBuilder.build(MainActivity.this);
                    // Start the intent by requesting a result,
                    // identified by a request code.
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_LONG).show();
                }
            }
        });
        buttonAuthFourSquare1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authFoursquare();
            }
        });
        textViewViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, TopPlacesActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST
                && resultCode == Activity.RESULT_OK) {
            // The user has selected a place. Extract the name and address.
            final Place place = PlacePicker.getPlace(data, this);
            final String name = place.getName().toString();
            final CharSequence address = place.getAddress();
            LatLng latLng  = place.getLatLng();
            if (!name.equals("") ){
                textViewLocation.setText(name);
            }
            else{
                textViewLocation.setText(latLng.toString());
            }
            // Save the latitude and longitude and name
            SharedPreferencesHelper.setLatLongName(getApplicationContext(),latLng.latitude, latLng.longitude, name);
            // re-init the data
            initData();
            Log.d("test", name + " " +  address + latLng);
            Log.d("test", place.toString());
        }

        if (requestCode == REQUEST_CODE_FSQ_CONNECT && resultCode == Activity.RESULT_OK){
            AuthCodeResponse codeResponse = FoursquareOAuth.getAuthCodeFromResult(resultCode, data);
            performTokenExchange(codeResponse.getCode());
            Log.d("test", codeResponse.getCode());
        }

        if (requestCode == REQUEST_CODE_FSQ_TOKEN_EXCHANGE && resultCode == Activity.RESULT_OK){
            onCompleteTokenExchange(resultCode, data);
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void callWeatherApi(String latitude, String longitude){

        String openWeatherApiKey = getResources().getString(R.string.open_weather_api_key);
        String url = "http://api.openweathermap.org/data/2.5/weather?lat="+latitude+"&lon="+longitude+"&appid="+openWeatherApiKey;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        // parse the json response
                        mWeather = JsonHelper.getWeatherData(jsonObject);
                        // update weather card UI
                        updateWeatherCardUI();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d("test", volleyError.toString());
                        updateUiFromOldData();
                    }
                }) {
        };

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    private void authFoursquare(){
        Intent intent = FoursquareOAuth.getConnectIntent(MainActivity.this, getResources()
                .getString(R.string.fsq_client_id));
        startActivityForResult(intent, REQUEST_CODE_FSQ_CONNECT);
    }

    private void exploreFoursquare(String lat, String longi){

        String fsqApiKey = SharedPreferencesHelper.getFSQAccessToken(getApplicationContext());
        String url = "https://api.foursquare.com/v2/venues/explore?ll="+lat+","+longi+"&oauth_token="+fsqApiKey+"&v=20160304&limit=10";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.d("test", jsonObject.toString());
                        // parse json response
                        mVenues = JsonHelper.getTop3Places(jsonObject);
                        updatePlacesViews();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d("test", volleyError.toString());
                        mVenues = new ArrayList<>();
                        mVenues.addAll(Venue.listAll(Venue.class));
                        updatePlacesViews();
                        Toast.makeText(getApplicationContext(), "Can't update places", Toast.LENGTH_LONG).show();
                    }
                }) {
        };

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    /**
     * Exchange a code for an OAuth Token. Note that we do not recommend you
     * do this in your app, rather do the exchange on your server. Added here
     * for demo purposes.
     *
     * @param code
     *          The auth code returned from the native auth flow.
     */
    private void performTokenExchange(String code) {

        Intent intent = FoursquareOAuth.getTokenExchangeIntent(this,
                getResources().getString(R.string.fsq_client_id),
                getResources().getString(R.string.fsq_client_secret), code);
        startActivityForResult(intent, REQUEST_CODE_FSQ_TOKEN_EXCHANGE);
    }

    private void onCompleteTokenExchange(int resultCode, Intent data) {
        AccessTokenResponse tokenResponse = FoursquareOAuth.getTokenFromResult(resultCode, data);
        Exception exception = tokenResponse.getException();

        if (exception == null) {
            String accessToken = tokenResponse.getAccessToken();
            // Success.
            Log.d("test", accessToken);
            SharedPreferencesHelper.setFSQAccessToken(getApplicationContext(), accessToken);
            Toast.makeText(getApplicationContext(), "Connected To Foursquare", Toast.LENGTH_SHORT).show();
            initData();

        } else {
            if (exception instanceof FoursquareOAuthException) {
                // OAuth error.
                String errorMessage = ((FoursquareOAuthException) exception).getMessage();
                String errorCode = ((FoursquareOAuthException) exception).getErrorCode();
            } else {
                // Other exception type.
            }
        }
    }

    private void initData(){
        // get previous selected location of available
        Map<String, String> locationValues = SharedPreferencesHelper.getLatLongName(getApplicationContext());
        String placeName = locationValues.get(Constants.KEY_PLACE_NAME);
        String latitude = locationValues.get(Constants.KEY_LATITUDE);
        String longitude = locationValues.get(Constants.KEY_LONGITUDE);
        if (!placeName.equals("")){
            textViewLocation.setText(placeName);
        }
        else if (!latitude.equals("0") || !longitude.equals("0")){
            textViewLocation.setText("(" + latitude + ", " + longitude + ")");
            Toast.makeText(getApplicationContext(), "Using your previous location", Toast.LENGTH_LONG).show();
        }
        // set the weather data
        callWeatherApi(latitude, longitude);
        // get places data
        exploreFoursquare(latitude, longitude);
        // set up places views
        setUpPlacesViews(latitude, longitude);
    }

    private void updateWeatherCardUI(){
        Log.d("test", mWeather.getPlaceName() + " " + mWeather.getMainWeather());
        String imageUrl = "http://openweathermap.org/img/w/" + mWeather.getIconId() + ".png";
        ImageLoader.getInstance()
                .displayImage(imageUrl, imageViewWeather, options);
        String mainWeather = FormatHelper.getTemperature(mWeather.getTemperature())
                + "\u2103 \n" + mWeather.getMainWeather();
        String weatherDetails = "Min Temp : " + mWeather.getMinTemperature() +
                "\nMax Temp : " + mWeather.getMaxTemperature() +
                "\nHumidity : " + mWeather.getHumidity() +
                "\nWind Speed : " + mWeather.getWindSpeed() +
                "\n" + mWeather.getWeatherDescription() +
                "\n" + mWeather.getPlaceName();
        textViewWeatherDetails.setText(weatherDetails);
        textViewMainWeather.setText(mainWeather);
        String message = getMessage(mWeather.getMainWeather());
        textViewMessage.setText(message);
        // Save weather data
        SharedPreferencesHelper.setWeatherData(getApplicationContext(), mainWeather, weatherDetails);
        Toast.makeText(getApplicationContext(), "Weather updated", Toast.LENGTH_SHORT).show();
    }

    private void updateUiFromOldData(){
        Map<String, String> weatherInfo = SharedPreferencesHelper.getWeatherData(getApplicationContext());
        String mainWeather = weatherInfo.get(Constants.KEY_MAIN_WEATHER);
        String descWeather = weatherInfo.get(Constants.KEY_DESC_WEATHER);
        String message = "Can't update weather. Try looking out of window.";
        textViewMessage.setText(message);
        textViewMainWeather.setText(mainWeather);
        textViewWeatherDetails.setText(descWeather);
        Toast.makeText(getApplicationContext(), "Failed to update weather", Toast.LENGTH_SHORT).show();
    }

    private void setUpPlacesViews(String latitude, String longitude){
        boolean connectedToFSQ = SharedPreferencesHelper.getFSQSessionStatus(getApplicationContext());
        if (connectedToFSQ){
            buttonAuthFourSquare1.setVisibility(View.GONE);
            layoutPlaces.setVisibility(View.VISIBLE);
            textViewViewMore.setVisibility(View.VISIBLE);
            exploreFoursquare(latitude, longitude);
        }
        else{
            // not connected to FSQ
            buttonAuthFourSquare1.setVisibility(View.VISIBLE);
            layoutPlaces.setVisibility(View.GONE);
            textViewViewMore.setVisibility(View.GONE);
        }
    }

    private void updatePlacesViews(){

        cardPlace1.setVisibility(View.GONE);
        cardPlace2.setVisibility(View.GONE);
        cardPlace3.setVisibility(View.GONE);
        Log.d("test", "No of places : " + mVenues.size());
        if (mVenues.size() >= 1){
            cardPlace1.setVisibility(View.VISIBLE);
            Venue venue = mVenues.get(0);
            textViewVenueName1.setText(venue.getName());
            textViewVenueAddress1.setText(venue.getAddress());
            textViewVenueCategory1.setText(venue.getCategory());
            textViewVenueRating1.setText(venue.getCheckIns());
        }
        if (mVenues.size() >= 2){
            cardPlace2.setVisibility(View.VISIBLE);
            Venue venue = mVenues.get(1);
            textViewVenueName2.setText(venue.getName());
            textViewVenueAddress2.setText(venue.getAddress());
            textViewVenueCategory2.setText(venue.getCategory());
            textViewVenueRating2.setText(venue.getCheckIns());
        }
        if (mVenues.size() >= 3){
            cardPlace3.setVisibility(View.VISIBLE);
            Venue venue = mVenues.get(0);
            textViewVenueName3.setText(venue.getName());
            textViewVenueAddress3.setText(venue.getAddress());
            textViewVenueCategory3.setText(venue.getCategory());
            textViewVenueRating3.setText(venue.getCheckIns());
        }
    }

    private void handleFSQDisconnect(){
        boolean connectedToFSQ = SharedPreferencesHelper.getFSQSessionStatus(getApplicationContext());
        if (connectedToFSQ){
            // reset the connection
            SharedPreferencesHelper.resetFSQSession(getApplicationContext());
            Intent i = new Intent(MainActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
        else{
            Toast.makeText(getApplicationContext(), "You are already disconnected", Toast.LENGTH_SHORT).show();
        }
    }

    private String getMessage(String word){
        String message = "";
        word = word.toLowerCase();
        if (word.contains("cloud")){
            message = "It can rain. Better go off with an umbrella.";
        }
        else if (word.contains("clear")){
            message  = "Nice clear weather for outing.";
        }
        else if (word.contains("rain")){
            message = "Raining outside. Still let's go.";
        }
        else{
            message = "Let's explore some new places today.";
        }
        return message;
    }
}
