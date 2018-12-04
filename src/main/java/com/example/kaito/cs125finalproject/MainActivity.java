package com.example.kaito.cs125finalproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


public class MainActivity extends AppCompatActivity {

    private static RequestQueue requestQueue;
    private TextView summonerNameView;
    String id;
    String summonerName;
    JSONArray rankedDataArray;
    String APIKey = "?api_key=" + "RGAPI-0e734b22-4580-405d-8e47-51316b9e705b";

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
    public void summonerNameCall(View v) {
        String SummonerName = searchedSummoner();
        String webAPI = "https://na1.api.riotgames.com/lol/summoner/v3/summoners/by-name/";
        String url = webAPI + SummonerName + APIKey;

        final String dataDragon = "http://ddragon.leagueoflegends.com/cdn/8.23.1/img/profileicon/";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject summonerData) {
                        try {
                            ImageView profile = findViewById(R.id.imageView);
                            Log.d("Tag", summonerData.toString());
                            summonerName = summonerData.getString("name");
                            id = summonerData.getString("id");
                            TextView playerLevelView = findViewById(R.id.textView11);
                            playerLevelView.setText("Summoner Level: " + summonerData.getString("summonerLevel"));
                            summonerRankedCall(id);
                            Picasso.get().load("https://avatar.leagueoflegends.com/na1/" + searchedSummoner() +".png").into(profile);

                            summonerNameView.setText( "Summoner: " + summonerName);
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
            public void onResponse(JSONArray rankedDataArray) {
                try {
                    //Log.d("rankedData", rankedDataArray.toString());
                    Log.d("rankedData", rankedDataArray.getJSONObject(0).toString());
                    JSONObject flexData;
                    JSONObject soloData;
                    if (rankedDataArray.getJSONObject(0).getString("queueType").equals("RANKED_SOLO_5x5") && rankedDataArray.getJSONObject(1).getString("queueType").equals("RANKED_FLEX_SR") ) {
                        soloData = rankedDataArray.getJSONObject(0);
                        flexData = rankedDataArray.getJSONObject(1);
                    } else {
                        soloData = rankedDataArray.getJSONObject(1);
                        flexData = rankedDataArray.getJSONObject(0);

                    }
                    TextView soloQView = findViewById(R.id.textView15);
                    TextView flexQView = findViewById(R.id.textView14);

                    int wins = soloData.getInt("wins");
                    double soloWins = wins;
                    int losses = soloData.getInt("losses");
                    double soloLosses = losses;
                    int ratio = (int) (soloWins/(soloWins+soloLosses) * 100);
                    String soloRatio = String.valueOf(ratio) + "%";
                    TextView ratioView = findViewById(R.id.textView13);
                    String ratioString = "Win/Loss: " + soloRatio + " " + soloData.getString("wins")+"W "+soloData.get("losses") +"L";
                    ratioView.setText(ratioString);


                    String soloText = "Solo: " + soloData.getString("tier") + " " + soloData.getString("rank");
                    String flexText = "Flex: " + flexData.getString("tier") + " " + flexData.getString("rank");
                    soloQView.setText(soloText);
                    flexQView.setText(flexText);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    public static Drawable imageDisplay(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }


    public String searchedSummoner() {

        SearchView searchBar = findViewById(R.id.searchView);
        String name = String.valueOf(searchBar.getQuery());
        name = name.trim();
        name = name.replace(" ","");
        return name;
    }
}