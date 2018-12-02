package com.example.kaito.cs125finalproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    private static RequestQueue requestQueue;
    private TextView summonerNameView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button searchButton = findViewById(R.id.button);
        summonerNameView = findViewById(R.id.textView);
        requestQueue = Volley.newRequestQueue(this);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("tag1", "before api call");
                startAPICall(v);
                Log.d("tag2", "after api call");
            }
        });
    }
    public void startAPICall(View v) {

        String SummonerName = searchedSummoner();
        String APIKey = "?api_key=RGAPI-23623849-6cf7-4e5f-b206-95ede3eb4c62";
        String webAPI = "https://na1.api.riotgames.com/lol/summoner/v3/summoners/by-name/";
        String url = webAPI + SummonerName + APIKey;
        Log.d("tag3", "before request");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject summonerData) {
                        Log.d("tag10", "onResponse starts");
                        try {
                            Log.d("tag4", "onResponse went through");
                            String summonerName = summonerData.getString("Name");
                            summonerNameView.setText(summonerName);

                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("Taggers", "error in response");
                    }
                });
        requestQueue.add(jsonObjectRequest);
        Log.d("tag5", "end of response");
    }
    public String searchedSummoner() {

        SearchView searchBar = findViewById(R.id.searchView);
        String summonerName  = String.valueOf(searchBar.getQuery());

        return summonerName;
    }
}
