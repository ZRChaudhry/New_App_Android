package com.example.news;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.LongDef;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

public class SourceRunnable implements Runnable {
    private static final String TAG = "SourceRunnable";

    private String Sid="";


    private final MainActivity mainActivity;

    //Api Call
    private static final String newsSource= "https://newsapi.org/v2/top-headlines";
    private static final String APIKey = "6d3f2382b77f46699932c1028398c9c2";
            //"5f44a7f1a0e643ac967b129e65c0d8f8";

    SourceRunnable(MainActivity mainActivity, String Sid) {
        this.Sid=Sid.toString();
        this.mainActivity=mainActivity;

    }


    @Override
    public void run() {

        //Builder
        Uri.Builder buildURL = Uri.parse(newsSource).buildUpon();
        buildURL.appendQueryParameter("apiKey", APIKey);
        buildURL.appendQueryParameter("sources", Sid);



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
        String finalMsg = String.format("%s (%s)", msg);
        mainActivity.runOnUiThread(() -> mainActivity.handleError(finalMsg));
    }

    //Gets the information from the API call then returns to Main Activity
    public void handleResults(final String jsonString) {
        if (jsonString == null) {
            Log.d(TAG, "handleResults: Failure in data download");
            mainActivity.runOnUiThread(mainActivity::downloadFailed);
            return;
        }

        final ArrayList<Source> sourceList = parseSourceJSON(jsonString);
        if (sourceList == null) {
            mainActivity.runOnUiThread(mainActivity::downloadFailed);
            return;
        }

        mainActivity.runOnUiThread(() -> mainActivity.sourceviewer(sourceList));
    }

    // Gets the Data from API call
    private ArrayList<Source> parseSourceJSON(String s) {

        Log.d(TAG, "JSON OBJ"+s);
        ArrayList<Source> sourceList = new ArrayList<>();



        try {
            JSONObject jObjMain = new JSONObject(s);
            int total = jObjMain.getInt("totalResults");
            JSONArray articles = jObjMain.getJSONArray("articles");
            for (int i = 0; i < total; i++) {
                JSONObject source = articles.getJSONObject(i);
                JSONObject news = source.getJSONObject("source");
                String sourceID = news.getString("id");
                String sourceName = news.getString("name");

                String sourceAuthor = source.getString("author");
                if (sourceAuthor=="null" || sourceAuthor=="")
                    sourceAuthor="";
                String sourceTitle = source.getString("title");
                String sourceContent = source.getString("description");
                String sourceURL = source.getString("url");
                String sourceImage = source.getString("urlToImage");
                String d = source.getString("publishedAt");

                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                Date date = formatter.parse(d);
                String sourceDate = date.toString();

                String sourceDes = source.getString("content");


/*
                //   String myString = content;
                // String [] arr = myString.split("\\s+");
                int N = 30;


                Log.d(TAG, "parseSourceJSON: "+ sourceContent.length());
                //Log.d(TAG, "parseSourceJSON: "+ arr.length);


                if (sourceContent.length() > 30) {
                    sourceContent = "";

                   // for (int j = 0; j < N; j++) {
                    //    sourceContent = sourceContent + " " + arr[j];

                sourceContent = sourceContent.substring(0, 30) + ". . .";
            }

 */


                sourceList.add(
                        new Source(sourceID,sourceName,sourceAuthor,sourceTitle,
                                sourceDes,sourceURL,sourceImage,sourceDate,sourceContent,total));

            }
            return sourceList;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



}

