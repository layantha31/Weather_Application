package com.example.weather_application;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    EditText edit_City,edit_Country;
    TextView text_Result;

    private final String url = "http://api.openweathermap.org/data/2.5/weather";
    private final String appid = "b539800a9533c16fa2372bd3ef96575b";

    DecimalFormat df = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edit_City = findViewById(R.id.textView_City);
        edit_Country = findViewById(R.id.textView_Country);
        text_Result = findViewById(R.id.textView_Result);
    }

    @SuppressLint("SetTextI18n")
    public void getWeatherDetails(View view) {

        String tempUrl = "";
        String city = edit_City.getText().toString().trim();
        String country = edit_Country.getText().toString().trim();

        if (city.equals("")){
            text_Result.setText("City field can not be empty!");
        } else {
            if (!country.equals("")){
                tempUrl = url + "?q=" + city + "," + country + "&appid=" + appid;
            } else{
                tempUrl = url + "?q=" + city + "&appid=" + appid;
            }

            StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //Log.d("response", response);
                    String output = "";
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                        JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                        String description = jsonObjectWeather.getString("description");
                        JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                        double temp = jsonObjectMain.getDouble("temp") - 273.15;
                        double feelsLike = jsonObjectMain.getDouble("feels_like") - 273.15;
                        float pressure = jsonObjectMain.getInt("pressure");
                        int humidity = jsonObjectMain.getInt("humidity");
                        JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                        String wind = jsonObjectWind.getString("speed");
                        JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                        String clouds = jsonObjectClouds.getString("all");
                        JSONObject jsonObjectsys = jsonResponse.getJSONObject("sys");
                        String countryName = jsonObjectsys.getString("country");
                        String cityName = jsonResponse.getString("name");
                        text_Result.setTextColor(Color.WHITE);

                        output += "Current weather of " + cityName + "(" + countryName + ")" +
                                "\n  Temp : " + df.format(temp) + (char) 0x00B0+" C" +
                                "\n  Feels Like : " + df.format(feelsLike) + (char) 0x00B0+" C" +
                                "\n  Humidity : " + humidity + "%" +
                                "\n  Description : " + description +
                                "\n  Wind Speed : " + wind + "m/s (meters per second)" +
                                "\n  Cloudiness : " + clouds + "%" +
                                "\n  Pressure : " + pressure + "hPa";

                        text_Result.setText(output);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(),error.toString().trim(),Toast.LENGTH_SHORT).show();
                }
            });

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }
}