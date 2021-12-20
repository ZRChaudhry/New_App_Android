package com.example.news;

public class Source {


    private String sourceID;
    private String sourceName;
    private String sourceAuthor;
    private String sourceTitle;
    private String sourceDes;
    private String sourceURL;
    private String sourceImage;
    private String sourceDate;
    private String sourceContent;
    private int total;


    Source(String sourceID, String sourceName, String sourceAuthor, String sourceTitle,
           String sourceDes, String sourceURL, String sourceImage, String sourceDate,
           String sourceContent, int total) {
        this.sourceID=sourceID;
        this.sourceName=sourceName;
        this.sourceAuthor=sourceAuthor;
        this.sourceTitle=sourceTitle;
        this.sourceDes=sourceDes;
        this.sourceURL=sourceURL;
        this.sourceImage=sourceImage;
        this.sourceDate=sourceDate;
        this.sourceContent=sourceContent;
        this.total=total;

    }

    String getSourceID() { return sourceID; }
    String getSourceName() { return sourceName; }
    String getSourceAuthor() {
        return sourceAuthor;
    }
    String getSourceTitle() {
        return sourceTitle;
    }
    String getSourceDes() {
        return sourceDes;
    }
    String getSourceURL() {
        return sourceURL;
    }
    String getSourceImage() {
        return sourceImage;
    }
    String getSourceDate() {
        return sourceDate;
    }
    String getSourceContent() {
        return sourceContent;
    }
    int getTotal(){return total;}











}
