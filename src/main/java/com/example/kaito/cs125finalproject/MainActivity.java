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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private static RequestQueue requestQueue;
    private TextView summonerNameView;
    String id;
    String summonerName;
    JSONObject copyOfSummonerData;
    JSONArray rankedDataArray;
    String APIKey = "?api_key=RGAPI-23623849-6cf7-4e5f-b206-95ede3eb4c62";

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
                summonerNameCall(v);
            }
        });
    }

    public interface VolleyCallback {
        void onSuccessResponse(String result);
    }

    public void getResponse(int method, String url, JSONArray array, final VolleyCallback callback) {

    }


    public void summonerNameCall(View v) {

        String SummonerName = searchedSummoner();
        String webAPI = "https://na1.api.riotgames.com/lol/summoner/v3/summoners/by-name/";
        String url = webAPI + SummonerName + APIKey;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject summonerData) {
                        try {
                            copyOfSummonerData = summonerData;
                            Log.d("Tag", summonerData.toString());
                            summonerName = summonerData.getString("name");
                            id = summonerData.getString("id");
                            summonerRankedCall(id);
                            summonerNameView.setText(summonerName);
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    public void summonerRankedCall(String id) {
        String rankedURL = "https://na1.api.riotgames.com/lol/league/v3/positions/by-summoner/";
        String url2 = rankedURL + id + APIKey;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url2, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("rankedData", response.toString());
                rankedDataArray = response;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonArrayRequest);
    }


    public String searchedSummoner() {

        SearchView searchBar = findViewById(R.id.searchView);
        String summonerName  = String.valueOf(searchBar.getQuery());
        return summonerName;
    }
}