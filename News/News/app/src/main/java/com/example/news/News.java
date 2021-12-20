package com.example.news;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class News  implements Comparable<News>, Serializable {

    private String id;
    private String name;
    private String des;
    private String url;
    private String category;
    private String lang;
    private String country;

    News(String id, String name, String des, String url, String category, String lang, String country) {
        this.id=id;
        this.name=name;
        this.des=des;
        this.url=url;
        this.category=category;
        this.lang=lang;
        this.country=country;
    }

    String getId() {
        return id;
    }
    String getNewsName() {
        return name;
    }
    String getDescription() {
        return des;
    }
    String getUrl() {
        return url;
    }
    String getCategory() {
        return category;
    }
    String getLang() {
        return lang;
    }
    String getCountry() {
        return country;
    }
    @NonNull
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(News n) { return name.compareTo(n.name);}

}
