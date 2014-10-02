package com.shitsuma.videomobileclient.http;


import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.shitsuma.videomobileclient.model.VideoInfo;
import com.shitsuma.videomobileclient.parsers.XhamsterParser;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.List;

public class ServerUtils {
    private static final String XHAMSTER_URL = "http://xhamster.com";
    private static final String NEXT_PAGE_URL_FORMAT = "/new/%s.html";
    private static ServerUtils instance;

    private AsyncHttpClient httpClient;

    private XhamsterParser parser;

    private ServerUtils(){
        httpClient = new AsyncHttpClient();

        parser = new XhamsterParser();
    }

    public static ServerUtils getInstance(){
        if(instance == null){
            instance = new ServerUtils();
        }

        return instance;
    }

    public void downloadHtml(String pageUrl, HttpResponseHandler handler){
        httpClient.get(pageUrl, handler);
    }

    public void downloadVideosInfo(HttpResponseHandler handler){
        downloadHtml(XHAMSTER_URL, handler);
    }

    public List<VideoInfo> syncDownloadVideosInfo(int pageNumber) throws IOException {

        String nextPageUrl = String.format(NEXT_PAGE_URL_FORMAT, String.valueOf(pageNumber));
        Log.d("url debuging", ServerUtils.XHAMSTER_URL + nextPageUrl);

        String response = Jsoup.connect(ServerUtils.XHAMSTER_URL + nextPageUrl)
                .execute()
                .body();

        return parser.parseVideosList(response);
    }
}
