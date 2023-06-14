package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;

public class recycler_view extends AppCompatActivity {

    private TextView dateTv;
    private TextView cityTv;
    private TextView tempTv;
    private TextView descTv;
    private TextView windTv;
    private TextView humidityTv;
    private TextView pressureTv;

    private ImageView imageView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        dateTv = findViewById(R.id.dateTv);
        cityTv = findViewById(R.id.cityTv);
        tempTv = findViewById(R.id.tempTv);
        descTv = findViewById(R.id.descTv);
        windTv = findViewById(R.id.windTv);
        humidityTv = findViewById(R.id.humidityTv);
        pressureTv = findViewById(R.id.pressureTv);
        imageView = findViewById(R.id.imageView);

        // ktm ko
        String kathmanduWeatherApiUrl = "https://api.openweathermap.org/data/2.5/weather?q=Kathmandu&appid=39689a6306ea2b90753a54149f60f803";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest kathmanduWeatherStringRequest = new StringRequest(
                Request.Method.GET,
                kathmanduWeatherApiUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String jsonData = response;
                        Log.d("JSON DATA (Kathmandu): ", jsonData);

                        parseWeatherData(jsonData);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(recycler_view.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(kathmanduWeatherStringRequest);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        ArrayList<MyDataModel> tasks = new ArrayList<MyDataModel>();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(layoutManager);

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, tasks);
        recyclerView.setAdapter(adapter);

        String forecastApiUrl = "https://api.openweathermap.org/data/2.5/forecast?q=Kathmandu&appid=39689a6306ea2b90753a54149f60f803";



        StringRequest forecastStringRequest = new StringRequest(
                Request.Method.GET,
                forecastApiUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String jsonData = response;
                        Log.d("JSON DATA (Forecast): ", jsonData);

                        jsonArrayDecode(jsonData, adapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(recycler_view.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(forecastStringRequest);

        EditText locationEt = findViewById(R.id.locationEt);

        locationEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    String location = locationEt.getText().toString();


                    String forecastApiUrl = "https://api.openweathermap.org/data/2.5/forecast?q=" + location + "&appid=39689a6306ea2b90753a54149f60f803";
                    String weatherApiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + location + "&appid=39689a6306ea2b90753a54149f60f803";

                    RequestQueue requestQueue = Volley.newRequestQueue(recycler_view.this);

                    StringRequest forecastStringRequest = new StringRequest(
                            Request.Method.GET,
                            forecastApiUrl,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    String jsonData = response;
                                    Log.d("JSON DATA (Forecast): ", jsonData);

                                    jsonArrayDecode(jsonData, adapter);
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(recycler_view.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    requestQueue.add(forecastStringRequest);

                    StringRequest weatherStringRequest = new StringRequest(
                            Request.Method.GET,
                            weatherApiUrl,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    String jsonData = response;
                                    Log.d("JSON DATA (Weather): ", jsonData);

                                    parseWeatherData(jsonData);
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(recycler_view.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    requestQueue.add(weatherStringRequest);

                    return true;
                }
                return false;
            }
        });
    }

    public void jsonArrayDecode(String jsonData, RecyclerViewAdapter adapter) {
        ArrayList<MyDataModel> data = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray listArray = jsonObject.getJSONArray("list");

            for (int i = 0; i < listArray.length(); i++) {
                JSONObject listItem = listArray.getJSONObject(i);

                String dateTxt = listItem.getString("dt_txt");
                String[] dateTimeParts = dateTxt.split(" ");
                String dayOfWeek = dateTimeParts[0];

                String date = convertToDayOfWeek(dayOfWeek);

                JSONObject mainObject = listItem.getJSONObject("main");
                double temperatureInKelvin = mainObject.getDouble("temp");
                double temperatureInCelsius = temperatureInKelvin - 273.15;
                String temp = String.format("%.2f", temperatureInCelsius) + " °C";

                JSONArray weatherArray = listItem.getJSONArray("weather");
                String iconCode = weatherArray.getJSONObject(0).getString("icon");
                String imageUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";

                data.add(new MyDataModel(date, temp, imageUrl));
            }

            adapter.setPosts(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void parseWeatherData(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONObject mainObject = jsonObject.getJSONObject("main");
            JSONArray weatherArray = jsonObject.getJSONArray("weather");
            JSONObject weatherObject = weatherArray.getJSONObject(0);



            JSONArray weatherArrayTwo = jsonObject.getJSONArray("weather");
            String iconCode = weatherArrayTwo.getJSONObject(0).getString("icon");
            String imageUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";

            String city = jsonObject.getString("name");

            String date = jsonObject.getString("dt");
            long timestamp = Long.parseLong(date);
            String formattedDate = formatDate(timestamp);

            double temperatureInKelvin = mainObject.getDouble("temp");
            double temperatureInCelsius = temperatureInKelvin - 273.15;
            String temp = String.format("%.2f", temperatureInCelsius) + " °C";

            String description = weatherObject.getString("description");
            String speed = jsonObject.getJSONObject("wind").getString("speed") + " m/s";
            String humidity = mainObject.getString("humidity") + "%";
            String pressure = mainObject.getString("pressure") + " mb";

            cityTv.setText(city);
            dateTv.setText(formattedDate);
            tempTv.setText(temp);
            descTv.setText(description);
            windTv.setText(speed);
            humidityTv.setText(humidity);
            pressureTv.setText(pressure);
            Picasso.get().load(imageUrl).into(imageView);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String formatDate(long timestamp) {
        Date date = new Date(timestamp * 1000);  // seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        return sdf.format(date);
    }


    private String convertToDayOfWeek(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date dateFormat = sdf.parse(date);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateFormat);

            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            return dayFormat.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }


}
