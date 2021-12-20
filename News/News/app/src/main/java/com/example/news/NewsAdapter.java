package com.example.news;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.nfc.Tag;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

public class NewsAdapter extends RecyclerView.Adapter<NewsViewHolder> {

    private final MainActivity mainActivity;
    private final ArrayList<Source> sList;
    private static final String TAG = "New Adapter";

    private  NewsViewHolder vh;
    Picasso p;

    public NewsAdapter(MainActivity mainActivity, ArrayList<Source> sList) {
        this.mainActivity = mainActivity;
        this.sList = sList;
        this.p=Picasso.get();
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.news_entry, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {


        Source s=sList.get(position);


        holder.title.setText(s.getSourceTitle().toUpperCase(Locale.ROOT));
        holder.author.setText(s.getSourceAuthor().toUpperCase(Locale.ROOT));
        holder.date.setText(s.getSourceDate());
        holder.content.setText(s.getSourceContent());
        Log.d(TAG, s.getSourceImage());
        loadRemoteImage(s.getSourceImage(),holder.storyphoto);
        holder.total.setText(position+1+ " of " + s.getTotal());

        String t=s.getSourceURL();

        holder.title.setOnClickListener(v -> clickArticle(t));
        holder.storyphoto.setOnClickListener(v -> clickArticle(t));
        holder.content.setOnClickListener(v -> clickArticle(t));



    }

    private void clickArticle(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        mainActivity.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return sList.size();
    }

    private void loadRemoteImage(String imageURL, ImageView storyphoto) {
        // Needs gradle  implementation 'com.squareup.picasso:picasso:2.71828'

        long millisS = System.currentTimeMillis();

        //if (imageURL==null){
        //    Picasso.
        //}


        p.load(imageURL)
                .error(R.drawable.brokenimage)
                .placeholder(R.drawable.loading)
                .into(storyphoto, new Callback() {
                    @Override
                    public void onSuccess() {
                        long millisE = System.currentTimeMillis();
                        Log.d(TAG, "loadRemoteImage: Duration: " +
                                (millisE - millisS) + " ms");
                    }

                    @Override
                    public void onError(Exception e) {
                        long millisE = System.currentTimeMillis();
                        Log.d(TAG, "loadRemoteImage: Duration: " +
                                (millisE - millisS) + " ms");
                    }
                });

    }

}
