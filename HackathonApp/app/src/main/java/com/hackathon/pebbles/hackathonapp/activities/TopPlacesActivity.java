package com.hackathon.pebbles.hackathonapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.felipecsl.quickreturn.library.QuickReturnAttacher;
import com.felipecsl.quickreturn.library.widget.AbsListViewScrollTarget;
import com.felipecsl.quickreturn.library.widget.QuickReturnAdapter;
import com.felipecsl.quickreturn.library.widget.QuickReturnTargetView;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.hackathon.pebbles.hackathonapp.ApplicationController;
import com.hackathon.pebbles.hackathonapp.R;
import com.hackathon.pebbles.hackathonapp.adapters.TopPlacesListAdapter;
import com.hackathon.pebbles.hackathonapp.helpers.JsonHelper;
import com.hackathon.pebbles.hackathonapp.helpers.SharedPreferencesHelper;
import com.hackathon.pebbles.hackathonapp.models.Venue;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by @priyankvex on 5/3/16.
 * Activity to show top places near the user location using Foursquare API.
 */
public class TopPlacesActivity extends AppCompatActivity{

    int PLACE_PICKER_REQUEST = 4;

    ViewGroup quickHeaderView;
    private QuickReturnTargetView topTargetView;
    ListView listView;
    TextView textViewLocation;
    ArrayList<Venue> mVenues;
    ProgressBar progressBar;
    TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_places);
        quickHeaderView = (ViewGroup) findViewById(R.id.quickReturnTopTarget);
        quickHeaderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
                try {
                    PlacePicker.IntentBuilder intentBuilder =
                            new PlacePicker.IntentBuilder();
                    Intent intent = intentBuilder.build(TopPlacesActivity.this);
                    // Start the intent by requesting a result,
                    // identified by a request code.
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_LONG).show();
                }
            }
        });
        textViewLocation = (TextView) findViewById(R.id.textViewLocation);
        listView = (ListView) findViewById(R.id.listViewPlaces);
        registerForContextMenu(listView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textView = (TextView) findViewById(R.id.textView);
    }

    private void init(){
        progressBar.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        TopPlacesListAdapter adapter = new TopPlacesListAdapter(getApplicationContext(), mVenues);
        listView.setAdapter(new QuickReturnAdapter(adapter));
        QuickReturnAttacher quickReturnAttacher = QuickReturnAttacher.forView(listView);
        topTargetView = quickReturnAttacher.addTargetView(quickHeaderView,
                AbsListViewScrollTarget.POSITION_TOP,
                dpToPx(this, 80));

    }

    public static int dpToPx(Context context, float dp) {
        // Took from http://stackoverflow.com/questions/8309354/formula-px-to-dp-dp-to-px-android
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((dp * scale) + 0.5f);
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST
                && resultCode == Activity.RESULT_OK) {
            // The user has selected a place. Extract the name and address.
            final Place place = PlacePicker.getPlace(data, this);
            final String name = place.getName().toString();
            LatLng latLng  = place.getLatLng();
            if (!name.equals("") ){
                textViewLocation.setText(name);
            }
            else{
                textViewLocation.setText(latLng.toString());
            }
            exploreFoursquare(latLng.latitude + "", latLng.longitude + "");

        }

        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void exploreFoursquare(String lat, String longi){

        String fsqApiKey = SharedPreferencesHelper.getFSQAccessToken(getApplicationContext());
        String url = "https://api.foursquare.com/v2/venues/explore?ll="+lat+","+longi+"&oauth_token="+fsqApiKey+"&v=20160304&limit=15";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.d("test", jsonObject.toString());
                        // parse json response
                        mVenues = JsonHelper.getTop3Places(jsonObject);
                        init();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d("test", volleyError.toString());
                        Toast.makeText(getApplicationContext(), "Can't get places", Toast.LENGTH_LONG).show();
                    }
                }) {
        };

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, "Share");

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        Log.d("test", mVenues.get(info.position-1).getName());
        Venue venue = mVenues.get(info.position);
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/html");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml("<p>"+venue.getName() + ", "
                + venue.getAddress() + ", " + venue.getCategory() + "</p>"));
        startActivity(Intent.createChooser(sharingIntent, "Share using"));

        return true;

    }


}
