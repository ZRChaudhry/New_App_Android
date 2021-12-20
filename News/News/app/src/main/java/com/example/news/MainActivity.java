package com.example.news;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity  {

    private static final String TAG = "MainActivity";
    private final HashMap<String, HashSet<News>> topicData = new HashMap<>();
    private final HashMap<String, HashSet<News>> langData = new HashMap<>();
    private final HashMap<String, HashSet<News>> countriesData = new HashMap<>();

    private final ArrayList<News> subMenuDisplayed = new ArrayList<News>();
    private final HashSet<News> newList = new HashSet<>();

    HashMap<String, String> languageMap = new HashMap<>();
    HashMap<String, String> countryMap = new HashMap<>();

    private Menu opt_menu;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private ArrayAdapter<News> arrayAdapter;
    private NewsAdapter newsAdapter;

    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the drawer toggle
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.left_drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);

        mDrawerList.setOnItemClickListener((parent, view, position, id) ->
        {
            selectItem(position);
            mDrawerLayout.closeDrawer(mDrawerList);
        });

        loadLanguageFile();
        loadCountryFile();
        Log.d(TAG, "onCreate: " +languageMap.size()+languageMap.values());


        // Loads Threads
        doDownload();

    }

    @SuppressLint("NotifyDataSetChanged")
    private void selectItem(int position) {

        News selectedNews = subMenuDisplayed.get(position);

        String Sid = selectedNews.getId();

        if (selectedNews == null) {
            Toast.makeText(this, MessageFormat.format("No News Source found for {0}", selectedNews), Toast.LENGTH_LONG).show();
            return;
        }
        SourceRunnable sourceloaderTaskRunnable = new SourceRunnable(this, Sid);
        new Thread(sourceloaderTaskRunnable).start();
        setTitle(selectedNews.toString());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.opt_menu = menu;
        return true;
    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item);
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else if (item.hasSubMenu())
            return true;

        else {
            int id = item.getGroupId();
            String title = (String) item.getTitle();

            if (id == 0) {
                //subMenuDisplayed.clear();
                HashSet<News> lst = topicData.get(item.getTitle().toString());
                if (lst != null ) {
                    subMenuDisplayed.retainAll(lst);
                    //subMenuDisplayed.addAll(lst);
                }
                this.setTitle(String.format(Locale.getDefault(),
                        "News Gateway (%d)", subMenuDisplayed.size()));
                arrayAdapter.notifyDataSetChanged();
            }
            if (id == 1) {
                //subMenuDisplayed.clear();
                Log.d(TAG, "onOptionsItemSelected: "+item.getTitle());
                HashSet<News> llst = langData.get(item.getTitle().toString());
                if (llst != null) {
                    subMenuDisplayed.retainAll(llst);
                }
                this.setTitle(String.format(Locale.getDefault(),
                        "News Gateway (%d)", subMenuDisplayed.size()));
                arrayAdapter.notifyDataSetChanged();
            }
            if (id == 2) {
                //subMenuDisplayed.clear();
                HashSet<News> clst = countriesData.get(item.getTitle().toString());
                if (clst != null) {
                    subMenuDisplayed.retainAll(clst);
                }
                this.setTitle(String.format(Locale.getDefault(),
                        "News Gateway (%d)", subMenuDisplayed.size()));
                arrayAdapter.notifyDataSetChanged();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    public void updateData(ArrayList<News> w) {
        for (News c : w) {
            //////////////////////// TOPICS HASHMAP/////////////////////
            if (!topicData.containsKey(c.getCategory())) {
                topicData.put(c.getCategory(), new HashSet<>());
            }
            Objects.requireNonNull(topicData.get(c.getCategory())).add(c);

            //////////////////////// LANGUAGES HASHMAP//////////////////
            if (!langData.containsKey(c.getLang())) {
                langData.put(languageMap.get(c.getLang()), new HashSet<>());
            }
            Objects.requireNonNull(langData.get(languageMap.get(c.getLang()))).add(c);

            //////////////////////// COUNTRIES HASHMAP//////////////////
            if (!countriesData.containsKey(c.getCountry())) {
                countriesData.put(countryMap.get(c.getCountry()), new HashSet<>());
            }
            Objects.requireNonNull(countriesData.get(countryMap.get(c.getCountry()))).add(c);
        }
        //////////////////////// TOPICS SUBMENUS/////////////////////
        
        ArrayList<String> topicList = new ArrayList<>(topicData.keySet());
        Collections.sort(topicList);
        SubMenu topicsMenu = opt_menu.addSubMenu("Topics");
        int i=0;
        for (String s : topicList) {
            topicsMenu.add(0, i, i, s);
            i++;
        }

        //////////////////////// LANGUAGES SUBMENUS//////////////////
        ArrayList<String> langList = new ArrayList<>(langData.keySet());
        Collections.sort(langList);
        SubMenu langMenu = opt_menu.addSubMenu("Languages");
        int j=0;
        for (String s : langList) {
            Log.d(TAG, "updateData: " + languageMap.get(s) + s);
            langMenu.add(1, j, j, s);
            j++;
        }

        //////////////////////// COUNTRIES SUBMENUS//////////////////
        ArrayList<String> counList = new ArrayList<>(countriesData.keySet());
        Collections.sort(counList);
        SubMenu counMenu = opt_menu.addSubMenu("Countries");
        int k=0;
        for (String s : counList) {
            counMenu.add(2, k, k, s);
            k++;
        }

        //////////////////////// POPULATE DRAWER //////////////////
        subMenuDisplayed.addAll(w);

        arrayAdapter = new ArrayAdapter(this, R.layout.drawer_item, subMenuDisplayed);
        mDrawerList.setAdapter(arrayAdapter);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    //////////////// RUNNABLE THREAD FOR NEWS SOURCES /////////////////
    private void doDownload() {
        NewsRunnable loaderTaskRunnable = new NewsRunnable(this, languageMap);
        new Thread(loaderTaskRunnable).start();
    }

    public void handleError(String finalMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Data Problem").setMessage(finalMsg)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                }).create().show();
    }

    public void downloadFailed() {
        Log.d(TAG, "downloadFailed: ");
    }

    public void sourceviewer(ArrayList<Source> sourceList) {


        newsAdapter = new NewsAdapter(this, sourceList);

        viewPager = findViewById(R.id.viewpager);

        viewPager.setAdapter(newsAdapter);

        newsAdapter.notifyDataSetChanged();

        viewPager.setCurrentItem(0);

        mDrawerLayout.closeDrawer(mDrawerList);
        viewPager.setBackground(null);


    }

    public void loadLanguageFile() {

        Log.d(TAG,"loadFile: Loading JSON codes");
        try {
            InputStream is = getResources().openRawResource((R.raw.language_codes));
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            JSONObject json0bject = new JSONObject(sb.toString());
            JSONArray jsonArray = json0bject.getJSONArray("languages");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jLanguage = (JSONObject) jsonArray.get(i);
                String code = jLanguage.getString("code");
                String name = jLanguage.getString("name");
                languageMap.put(code.toLowerCase(Locale.ROOT), name);
                Log.d(TAG, "loadLanguageFile: "+languageMap.get(code) + code);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void loadCountryFile() {

        Log.d(TAG,"loadFile: Loading JSON codes");
        try {
            InputStream is = getResources().openRawResource((R.raw.country_codes));
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            JSONObject json0bject = new JSONObject(sb.toString());
            JSONArray jsonArray = json0bject.getJSONArray("countries");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jLanguage = (JSONObject) jsonArray.get(i);
                String code = jLanguage.getString("code");
                String name = jLanguage.getString("name");
                countryMap.put(code.toLowerCase(Locale.ROOT), name);
                Log.d(TAG, "loadLanguageFile: "+countryMap.get(code) + code);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }






}
