package com.shitsuma.videomobileclient.model;

public class VideoInfo {

    private String title;
    private String imageUrl;
    private String videoPageUrl;

    public VideoInfo(String title, String imageUrl, String videoPageUrl) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.videoPageUrl = videoPageUrl;
    }


    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getVideoPageUrl() {
        return videoPageUrl;
    }
}



