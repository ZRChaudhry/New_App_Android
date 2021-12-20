package com.example.news;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

public class NewsRunnable implements Runnable {
    private static final String TAG = "NewsRunnable";

    private final MainActivity mainActivity;
    HashMap<String, String> languageMap = new HashMap<>();




    //Api Call
    private static final String newsURL = "https://newsapi.org/v2/sources";
    private static final String APIKey = "6d3f2382b77f46699932c1028398c9c2";
            //"5f44a7f1a0e643ac967b129e65c0d8f8";

    private static final String newsSource= "https://newsapi.org/v2/top-headlines";

    NewsRunnable(MainActivity mainActivity, HashMap<String, String> languageMap) {
        this.mainActivity = mainActivity;
        this.languageMap=languageMap;
    }


    @Override
    public void run() {

        //Builder
        Uri.Builder buildURL = Uri.parse(newsURL).buildUpon();
        buildURL.appendQueryParameter("apiKey", APIKey);
        String urlToUse = buildURL.build().toString();
        Log.d(TAG, "doInBackground: " + urlToUse);


        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            // Establish Connection
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("User-Agent", "");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.connect();

            // If Connection failed
            if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                InputStream is = connection.getErrorStream();
                BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                handleError(sb.toString());
                return;
            }

            //If connection Successfull
            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            Log.d(TAG, "doInBackground: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            handleResults(null);
            return;
        }
        handleResults(sb.toString());
    }

    // If error in connection
    public void handleError(String s) {
        String msg = "Error: ";
        try {
            JSONObject jObjMain = new JSONObject(s);
            msg += jObjMain.getString("message");
        } catch (JSONException e) {
            msg += e.getMessage();
        }
        String finalMsg =  msg;
        mainActivity.runOnUiThread(() -> mainActivity.handleError(finalMsg));
    }

    //Gets the information from the API call then returns to Main Activity
    public void handleResults(final String jsonString) {
        if (jsonString == null) {
            Log.d(TAG, "handleResults: Failure in data download");
            mainActivity.runOnUiThread(mainActivity::downloadFailed);
            return;
        }

        final ArrayList<News> newsList = parseJSON(jsonString);
        if (newsList == null) {
            mainActivity.runOnUiThread(mainActivity::downloadFailed);
            return;
        }

        mainActivity.runOnUiThread(() -> mainActivity.updateData(newsList));
    }


    // Gets the Data from API call
    private ArrayList<News> parseJSON(String s) {

        /*


         */

        ArrayList<News> newsList = new ArrayList<>();

        try {
            JSONObject jObjMain = new JSONObject(s);
            JSONArray jSource= jObjMain.getJSONArray("sources");
            for (int i = 0; i < jSource.length(); i++) {
                JSONObject jNews = (JSONObject) jSource.get(i);
                String id= jNews.getString("id");
                String name= jNews.getString("name");
                String des= jNews.getString("description");
                String url= jNews.getString("url");
                String category= jNews.getString("category");
                String lang= jNews.getString("language");

                String country= jNews.getString("country");

                newsList.add(
                        new News(id,name,des,url,category,lang,country));

            }
            return newsList;



            } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
