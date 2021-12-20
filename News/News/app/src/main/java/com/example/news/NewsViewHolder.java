package com.example.news;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NewsViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "ViewHolder";

    TextView title;
    TextView date;
    TextView author;
    TextView content;
    ImageView storyphoto;
    MainActivity mainActivity;
    TextView total;


    public NewsViewHolder(@NonNull View itemView) {
        super(itemView);



        storyphoto=itemView.findViewById(R.id.storyphoto);
        title=itemView.findViewById(R.id.title);
        author=itemView.findViewById(R.id.author);
        date=itemView.findViewById(R.id.date);
        content=itemView.findViewById(R.id.content);
        total=itemView.findViewById(R.id.total);

    }



    private void loadRemoteImage(String imageURL) {
        // Needs gradle  implementation 'com.squareup.picasso:picasso:2.71828'

        long millisS = System.currentTimeMillis();

        Picasso.get().load(imageURL)
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
