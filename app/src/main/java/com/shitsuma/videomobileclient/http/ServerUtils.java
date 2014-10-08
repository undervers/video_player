package com.shitsuma.videomobileclient.http;


import android.net.Uri;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.shitsuma.videomobileclient.model.VideoInfo;
import com.shitsuma.videomobileclient.parsers.XhamsterParser;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.List;

public class ServerUtils {
    private static final String XHAMSTER_URL = "http://xhamster.com";
    private static final String NEW_VIDEOS_URL_FORMAT = "/new/%s.html";
    private static final String SEARCH_REQUEST_FORMAT = "/search.php?q=%s&qcat=video&page=%s";
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
        downloadVideosInfo(handler, 1);
    }

    public void downloadVideosInfo(HttpResponseHandler handler, int pageNumber){
        String url = createNewVideosUrl(pageNumber);
        downloadHtml(url, handler);
    }

    public void makeSearchRequest(String searchPhrase, HttpResponseHandler handler){
        makeSearchRequest(searchPhrase, handler, 1);
    }

    public void makeSearchRequest(String searchPhrase, HttpResponseHandler handler, int pageNumber){
        String searchUrl = createSearchUrl(searchPhrase, pageNumber);
        downloadHtml(searchUrl, handler);
    }

    private String createSearchUrl(String searchPhrase, int pageNumber){
        return XHAMSTER_URL + String.format(SEARCH_REQUEST_FORMAT, Uri.encode(searchPhrase), pageNumber);
    }

    private String createNewVideosUrl(int pageNumber){
        return XHAMSTER_URL + String.format(NEW_VIDEOS_URL_FORMAT, pageNumber);
    }

    public List<VideoInfo> syncMakeSearchRequest(String searchPhrase, int pageNumber) throws IOException {
        return syncDownloadVideos(createSearchUrl(searchPhrase, pageNumber));
    }

    public List<VideoInfo> syncGetNewVideos(int pageNumber) throws IOException {
        return syncDownloadVideos(createNewVideosUrl(pageNumber));
    }

    private List<VideoInfo> syncDownloadVideos(String url) throws IOException {
        String response = Jsoup.connect(url)
                .execute()
                .body();

        Log.d("url debuging", url);

        return parser.parseVideosList(response);
    }
}
