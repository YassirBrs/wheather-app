package com.example.wheatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public EditText editTextVille;
    public ListView listViewMeteo;
    List<MeteoItem> data = new ArrayList<>();
    private MeteoListModel model;
    private ImageButton ButtonOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextVille = findViewById(R.id.editText);
        listViewMeteo = findViewById(R.id.list1);
        ButtonOK = findViewById(R.id.imageButton);
        model = new MeteoListModel(getApplicationContext(), R.layout.list, data);
        listViewMeteo.setAdapter(model);

        ButtonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("MyLog", ".....");
                data.clear();
                model.notifyDataSetChanged();
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                String ville = editTextVille.getText().toString();
                Log.i("MyLog", ville);
                String url="https://samples.openweathermap.org/data/2.5/forecast?q=" + ville + "&appid=6c4d236693f76e92a698d8b24a2a434a";
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,

                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    Log.i("MyLog", "----------------------------");
                                    Log.i("MyLog", response);
                                    //List<MeteoItem> meteoItems = new ArrayList<>();
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONArray jsonArray = jsonObject.getJSONArray("list");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        MeteoItem meteoItem = new MeteoItem();
                                        JSONObject d = jsonArray.getJSONObject(i);
                                        Date date = new Date(d.getLong("dt") * 1000);
                                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy'T'HH:mm");
                                        String dateString = sdf.format(date);
                                        JSONObject main = d.getJSONObject("main");
                                        JSONArray weather = d.getJSONArray("weather");
                                        int tempMin = (int) (main.getDouble("temp_min") - 273.15);
                                        int tempMax = (int) (main.getDouble("temp_max") - 273.15);
                                        int pression = main.getInt("pressure");
                                        int humidity = main.getInt("humidity");
//data.add(String.format(dateString+"\n"+"Min=%d °c, Max=%d °C",tempMin,tempMax));
                                        meteoItem.tempMax = tempMax;
                                        meteoItem.tempMin = tempMin;
                                        meteoItem.pression = pression;
                                        meteoItem.humidite = humidity;
                                        meteoItem.date = dateString;
                                        meteoItem.image = weather.getJSONObject(0).getString("main");
                                        //meteoItems.add(meteoItem);
                                        data.add(meteoItem);
                                    }
                                    model.notifyDataSetChanged();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("MyLog", "Connection problem!");
                    }
                });

// Add the request to the RequestQueue.
                queue.add(stringRequest);
            }
        });
    }
}


