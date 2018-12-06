package com.example.kaito.cs125finalproject;

import android.content.Context;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private static RequestQueue requestQueue;
    private TextView summonerNameView;
    String id;
    String summonerName;
    String APIKey = "?api_key=" + "RGAPI-2a1453c3-4535-4e3e-8043-c04130582a24";

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
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
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
                            summonerName = summonerData.getString("name");
                            String nameViewSet =  "Summoner: " + summonerName;
                            summonerNameView.setText(nameViewSet);
                            Log.d("tag", summonerData.toString());
                            TextView playerLevelView = findViewById(R.id.textView11);
                            id = summonerData.getString("id");
                            summonerRankedCall(id);
                            championMasterCall(id);
                            String levelTextSet = "Summoner Level: " + summonerData.getString("summonerLevel");
                            playerLevelView.setText(levelTextSet);

                            ImageView profile = findViewById(R.id.imageView);
                            Picasso.get().load("https://avatar.leagueoflegends.com/na1/" + summonerName +".png").into(profile);


                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        TextView one = findViewById(R.id.textView);
                        one.setText("Summoner: Not Found");
                        TextView two = findViewById(R.id.textView11);
                        two.setText("Summoner Level:");
                        TextView three= findViewById(R.id.textView14);
                        three.setText("Flex:");
                        TextView eleven= findViewById(R.id.textView13);
                        eleven.setText("Win/Loss:");
                        TextView twelve= findViewById(R.id.textView15);
                        twelve.setText("Solo");

                        ImageView profile = findViewById(R.id.imageView);
                        profile.setImageResource(0);
                        ImageView profile2 = findViewById(R.id.imageView3);
                        profile2.setImageResource(0);
                        ImageView profile3 = findViewById(R.id.imageView2);
                        profile3.setImageResource(0);
                        ImageView profile4 = findViewById(R.id.imageView4);
                        profile4.setImageResource(0);
                        ImageView profile5 = findViewById(R.id.imageView5);
                        profile5.setImageResource(0);
                        ImageView profile6 = findViewById(R.id.imageView6);
                        profile6.setImageResource(0);


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
                    JSONObject flexData;
                    JSONObject soloData;

                    int queueLength = rankedDataArray.length();
                    if (queueLength == 2) {
                        if (rankedDataArray.getJSONObject(0).getString("queueType").equals("RANKED_SOLO_5x5") && rankedDataArray.getJSONObject(1).getString("queueType").equals("RANKED_FLEX_SR")) {

                            soloData = rankedDataArray.getJSONObject(0);
                            flexData = rankedDataArray.getJSONObject(1);

                        } else {
                            soloData = rankedDataArray.getJSONObject(1);
                            flexData = rankedDataArray.getJSONObject(0);
                        }
                    } else {
                        if (rankedDataArray.getJSONObject(1).getString("queueType").equals("RANKED_SOLO_5x5") && rankedDataArray.getJSONObject(2).getString("queueType").equals("RANKED_FLEX_SR")) {

                            soloData = rankedDataArray.getJSONObject(1);
                            flexData = rankedDataArray.getJSONObject(2);

                        } else {
                            soloData = rankedDataArray.getJSONObject(2);
                            flexData = rankedDataArray.getJSONObject(1);
                        }
                    }
                    TextView soloQView = findViewById(R.id.textView15);
                    TextView flexQView = findViewById(R.id.textView14);
                    TextView ratioView = findViewById(R.id.textView13);

                    double wins = soloData.getInt("wins"); //+ flexData.getInt("wins");;
                    double losses = soloData.getInt("losses"); //+ flexData.getInt("losses");
                    int ratio = (int) ((wins)/(losses+wins) * 100);
                    int lossRatio = 100 - ratio;

                    String soloRatio = String.valueOf(ratio) + "%";
                    String soloLoseRatio = String.valueOf(lossRatio) + "%";
                    String ratioString = "Win/Loss: " + soloRatio + " " + soloData.getString("wins")+"W "+soloData.getString("losses") +"L";
                    ratioView.setText(ratioString);
                    int[] numbers = {ratio,lossRatio};
                    String[] winlose = {soloRatio, soloLoseRatio};



                    String soloText = "Solo: " + soloData.getString("tier") + " " + soloData.getString("rank");
                    String flexText = "Flex: " + flexData.getString("tier") + " " + flexData.getString("rank");
                    soloQView.setText(soloText);
                    flexQView.setText(flexText);
                    ImageView soloImage = findViewById(R.id.imageView5);
                    ImageView flexImage = findViewById(R.id.imageView6);

                    divisionImage(soloData.getString("tier"), soloImage);
                    divisionImage(flexData.getString("tier"), flexImage);









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



    public void championMasterCall(String id) {

        String championURL = "https://na1.api.riotgames.com/lol/champion-mastery/v3/champion-masteries/by-summoner/";
        championURL += id + APIKey;
        final String iconURL = "https://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/default/v1/champion-icons/";
        final String icon2 = ".png";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, championURL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    Map<String,String> champs = champions();
                    ImageView champ1 = findViewById(R.id.imageView4);
                    ImageView champ2 = findViewById(R.id.imageView2);
                    ImageView champ3 = findViewById(R.id.imageView3);

                    String champion1ID = response.getJSONObject(0).getString("championId");
                    TextView champText1 = findViewById(R.id.textView4);
                    champText1.setText(champs.get(champion1ID));

                    String champion2ID = response.getJSONObject(1).getString("championId");
                    TextView champText2 = findViewById(R.id.textView5);
                    champText2.setText(champs.get(champion2ID));

                    String champion3ID = response.getJSONObject(2).getString("championId");
                    TextView champText3 = findViewById(R.id.textView6);
                    champText3.setText(champs.get(champion3ID));

                    String mastery1 = response.getJSONObject(0).getString("championLevel");
                    TextView masteryOne = findViewById(R.id.textView7);
                    masteryOne.setText("Mastery " + mastery1);

                    String mastery2 = response.getJSONObject(1).getString("championLevel");
                    TextView masteryTwo = findViewById(R.id.textView8);
                    masteryTwo.setText("Mastery " + mastery2);

                    String mastery3 =response.getJSONObject(2).getString("championLevel");
                    TextView masteryThree = findViewById(R.id.textView9);
                    masteryThree.setText("Mastery " + mastery3);



                    Picasso.get().load(iconURL+champion1ID+icon2).into(champ1);
                    Picasso.get().load(iconURL+champion2ID+icon2).into(champ2);
                    Picasso.get().load(iconURL+champion3ID+icon2).into(champ3);



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

        public String searchedSummoner() {

        SearchView searchBar = findViewById(R.id.searchView);
        String name = String.valueOf(searchBar.getQuery());

        return name;
    }


    public void divisionImage(String rank, ImageView icon) {

        if (rank.equals("BRONZE")) {

            icon.setImageResource(R.drawable.bronze);

        } else if (rank.equals("SILVER")) {

            icon.setImageResource(R.drawable.silver);


        } else if (rank.equals("GOLD")) {

            icon.setImageResource(R.drawable.gold);

        } else if (rank.equals("PLATINUM")) {

            icon.setImageResource(R.drawable.platinum);

        } else if (rank.equals("DIAMOND")) {

            icon.setImageResource(R.drawable.diamond);

        } else if (rank.equals("MASTER")) {

            icon.setImageResource(R.drawable.master);

        } else if (rank.equals("CHALLENGER")) {

            icon.setImageResource(R.drawable.challenger);
        } else {

            icon.setImageResource(R.drawable.provisional);

        }
    }





    public Map<String, String> champions() {

        Map<String, String> champions = new HashMap<>();
        champions.put("266","Aatrox");
        champions.put("103","Ahri");
        champions.put("84","Akali");
        champions.put("32","Amumu");
        champions.put("34","Anivia");
        champions.put("1","Annie");
        champions.put("22","Ashe");
        champions.put("136","Aurelion Sol");
        champions.put("268","Azir");
        champions.put("432","Bard");
        champions.put("53","Blitzcrank");
        champions.put("63","Brand");
        champions.put("201","Braum");
        champions.put("51","Caitlyn");
        champions.put("164","Camille");
        champions.put("69","Cassiopeia");
        champions.put("31","Cho'Gath");
        champions.put("42","Corki");
        champions.put("122","Darius");
        champions.put("131","Diana");
        champions.put("119","Draven");
        champions.put("36","Dr. Mundo");
        champions.put("245","Ekko");
        champions.put("60","Elise");
        champions.put("28","Evelynn");
        champions.put("81","Ezreal");
        champions.put("9","Fiddlesticks");
        champions.put("114","Fiora");
        champions.put("105","Fizz");
        champions.put("3","Galio");
        champions.put("41","Gangplank");
        champions.put("86","Garen");
        champions.put("150","Gnar");
        champions.put("79","Gragas");
        champions.put("104","Graves");
        champions.put("120","Hecarim");
        champions.put("74","Heimerdinger");
        champions.put("420","Illaoi");
        champions.put("39","Irelia");
        champions.put("427","Ivern");
        champions.put("40","Janna");
        champions.put("59","Jarvan IV");
        champions.put("24","Jax");
        champions.put("126","Jayce");
        champions.put("202","Jhin");
        champions.put("222","Jinx");
        champions.put("145","Kai'Sa");
        champions.put("429","Kalista");
        champions.put("43","Karma");
        champions.put("30","Karthus");
        champions.put("38","Kassadin");
        champions.put("55","Katarina");
        champions.put("10","Kayle");
        champions.put("141","Kayn");
        champions.put("85","Kennen");
        champions.put("121","Khazix");
        champions.put("203","Kindred");
        champions.put("240","Kled");
        champions.put("96","Kog'Maw");
        champions.put("7","LeBlanc");
        champions.put("64","Lee Sin");
        champions.put("89","Leona");
        champions.put("127","Lissandra");
        champions.put("236","Lucian");
        champions.put("117","Lulu");
        champions.put("99","Lux");
        champions.put("54","Malphite");
        champions.put("90","Malzahar");
        champions.put("57","Maokai");
        champions.put("11","Master Yi");
        champions.put("21","Miss Fortune");
        champions.put("62","Wukong");
        champions.put("82","Mordekaiser");
        champions.put("25","Morgana");
        champions.put("267","Nami");
        champions.put("75","Nasus");
        champions.put("111","Nautilus");
        champions.put("76","Nidalee");
        champions.put("56","Nocturne");
        champions.put("20","Nunu");
        champions.put("2","Olaf");
        champions.put("516","Ornn");
        champions.put("80","Pantheon");
        champions.put("78","Poppy");
        champions.put("555","Pyke");
        champions.put("133","Quinn");
        champions.put("497","Rakan");
        champions.put("33","Rammus");
        champions.put("421","Rek'Sai");
        champions.put("58","Renekton");
        champions.put("107","Rengar");
        champions.put("92","Riven");
        champions.put("68","Rumble");
        champions.put("13","Ryze");
        champions.put("113","Sejuani");
        champions.put("35","Shaco");
        champions.put("98","Shen");
        champions.put("102","Shyvana");
        champions.put("27","Singed");
        champions.put("14","Sion");
        champions.put("15","Sivir");
        champions.put("72","Skarner");
        champions.put("37","Sona");
        champions.put("16","Soraka");
        champions.put("50","Swain");
        champions.put("134","Syndra");
        champions.put("223","Tahm Kench");
        champions.put("163","Taliyah");
        champions.put("91","Talon");
        champions.put("44","Taric");
        champions.put("17","Teemo");
        champions.put("412","Thresh");
        champions.put("18","Tristana");
        champions.put("48","Trundle");
        champions.put("4","Twisted Fate");
        champions.put("29","Twitch");
        champions.put("77","Udyr");
        champions.put("6","Urgot");
        champions.put("110","Varus");
        champions.put("67","Vayne");
        champions.put("45","Veigar");
        champions.put("161","Vel'Koz");
        champions.put("254","Vi");
        champions.put("112","Viktor");
        champions.put("8","Vladimir");
        champions.put("106","Volibear");
        champions.put("19","Warwick");
        champions.put("498","Xayah");
        champions.put("101","Xerath");
        champions.put("5","Xin Zhao");
        champions.put("157","Yasuo");
        champions.put("83","Yorick");
        champions.put("154","Zac");
        champions.put("238","Zed");
        champions.put("115","Ziggs");
        champions.put("26","Zilean");
        champions.put("142","Zoe");
        champions.put("143","Zyra");

        return champions;
    }

}