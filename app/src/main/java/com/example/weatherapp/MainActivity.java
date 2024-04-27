package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout homeRL;
    private ProgressBar loadingPB;
    private TextView cityNameTV , temperatureTV , conditionTV;
    private TextInputEditText cityEdit;
    private ImageView backIV , iconIV , searchIV;
    private RecyclerView weatherRV;
    private ArrayList<WeatherRVModel> weatherRVModelArrayList;
    private WeatherAdapter weatherAdapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private String cityName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);

        getWindow().setFlags (WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS , WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView (R.layout.activity_main);

        homeRL = findViewById (R.id.RLHome);
        loadingPB = findViewById (R.id.PB_loading);
        cityNameTV = findViewById (R.id.TVCityName);
        temperatureTV = findViewById (R.id.TVTemperature);
        conditionTV = findViewById (R.id.TVCondition);
        cityEdit = findViewById (R.id.editCity);
        backIV = findViewById (R.id.IVBack);
        iconIV = findViewById (R.id.IVIcon);
        searchIV = findViewById (R.id.IVSearch);
        weatherRV = findViewById (R.id.RVWeather);
        weatherRVModelArrayList = new ArrayList<> ();
        weatherAdapter = new WeatherAdapter (this , weatherRVModelArrayList);
        weatherRV.setAdapter (weatherAdapter);

        locationManager = (LocationManager) getSystemService (Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission (this , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission (this , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions (MainActivity.this , new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION , Manifest.permission.ACCESS_COARSE_LOCATION
            } , PERMISSION_CODE);
        }
        Location location = locationManager.getLastKnownLocation (LocationManager.NETWORK_PROVIDER);

        cityName = getCityName (location.getLongitude () , location.getLatitude ());

        getWeatherInfo (cityName);

        searchIV.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                String city = cityEdit.getText ().toString ();
                if (city.isEmpty ())
                {
                    Toast.makeText (MainActivity.this, "Please Enter a City Name..", Toast.LENGTH_SHORT).show ();
                }
                else
                {
                    cityNameTV.setText (cityName);
                    getWeatherInfo (city);
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult (requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_CODE)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText (this, "PERMISSION GRANTED..", Toast.LENGTH_SHORT).show ();
            }
            else
            {
                Toast.makeText (this, "Please provide permissions", Toast.LENGTH_SHORT).show ();
                finish ();
            }
        }
    }

    private String getCityName(double longitude , double latitude)
    {
        String cityName = "Not Found";
        Geocoder geocoder = new Geocoder (getBaseContext () , Locale.getDefault ());
        try{
            List<Address> addressesList = geocoder.getFromLocation (latitude , longitude , 10);
            for(Address address : addressesList)
            {
                if(address != null)
                {
                    String city = address.getLocality ();
                    if(city != null && !city.equals (""))
                    {
                        cityName = city;
                    }
                    else
                    {
                        Log.d ("TAG" , "CITY NOT FOUND");
                        Toast.makeText (this, "User City Not Found..", Toast.LENGTH_SHORT).show ();
                    }
                }
            }

        }catch (IOException e )
        {
            e.printStackTrace ();
        }
        return cityName;
    }

    private void getWeatherInfo(String cityName)
    {
        String url = "http://api.weatherapi.com/v1/forecast.json?key=954234c8412b4e1d870181658220707&q=Sargodha&days=1&aqi=yes&alerts=yes";
        cityNameTV.setText (cityName);
        RequestQueue requestQueue = Volley.newRequestQueue (MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null, new Response.Listener<JSONObject> () {
            @Override
            public void onResponse(JSONObject response) {

                loadingPB.setVisibility (View.GONE);
                homeRL.setVisibility (View.VISIBLE);
                weatherRVModelArrayList.clear ();

                try {
                    String temperature = response.getJSONObject ("current").getString ("temp_c");
                    temperatureTV.setText (temperature+"°c");
                    int isDay =response.getJSONObject ("current").getInt ("is_day");
                    String condition = response.getJSONObject ("current").getJSONObject ("condition").getString ("text");
                    String conditionIcon = response.getJSONObject ("current").getJSONObject ("condition").getString ("icon");
                    Picasso.get ().load ("http:".concat (conditionIcon)).into (iconIV);
                    conditionTV.setText (condition);
                    if (isDay==1)
                    {
                        Picasso.get ().load ("https://www.google.com/search?q=morning+view&sxsrf=ALiCzsZkPAi8_h5YSJ3wsW-Ui8m3WMFtWg:1657281398068&source=lnms&tbm=isch&sa=X&ved=2ahUKEwjF65ionun4AhUHm_0HHfURC5gQ_AUoAXoECAIQAw&biw=1366&bih=668&dpr=1#imgrc=OrD5fPNksteDfM").into (backIV);
                    }
                    else
                    {
                        Picasso.get ().load ("https://www.google.com/search?q=night+view&tbm=isch&ved=2ahUKEwiHk6m3nun4AhWBhM4BHaOzCRcQ2-cCegQIABAA&oq=night+view&gs_lcp=CgNpbWcQA1DHBFi2JmCBKmgAcAB4AYABAIgBAJIBAJgBAKABAaoBC2d3cy13aXotaW1nwAEB&sclient=img&ei=lRvIYsf0L4GJur4Po-emuAE&bih=668&biw=1366#imgrc=cc_2xy-e95VeAM").into (backIV);
                    }

                    JSONObject forecastObj = response.getJSONObject ("forecast");
                    JSONObject forecast0 = forecastObj.getJSONArray ("forecastday").getJSONObject (0);
                    JSONArray hourArray = forecast0.getJSONArray ("hour");

                    for(int i =0 ; i<hourArray.length (); i++)
                    {
                        JSONObject hourObject = hourArray.getJSONObject (i);
                        String time = hourObject.getString ("time");
                        String temper = hourObject.getString ("temp_c");
                        String img = hourObject.getJSONObject ("time").getString ("icon");
                        String wind = hourObject.getString ("wind_kph");
                        weatherRVModelArrayList.add (new WeatherRVModel(time , temper , img , wind));
                    }
                    weatherAdapter.notifyDataSetChanged ();

                } catch (JSONException e) {
                    e.printStackTrace ();
                }

            }
        }, new Response.ErrorListener () {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText (MainActivity.this, "Please enter valid city name..", Toast.LENGTH_SHORT).show ();
            }
        });
        requestQueue.add (jsonObjectRequest);
    }

}