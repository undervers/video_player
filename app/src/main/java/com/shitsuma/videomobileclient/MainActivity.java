package com.shitsuma.videomobileclient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.shitsuma.videomobileclient.model.VideoInfo;

import org.apache.http.Header;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));

        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get("http://xhamster.com", new AsyncHttpResponseHandler() {
            ProgressDialog waitingDialog;

            @Override
            public void onStart(){
                waitingDialog = ProgressDialog.show(MainActivity.this, "Loading data", "Waiting please...");
            }

            @Override
            public void onFinish(){
                waitingDialog.dismiss();
            }

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                parseMainPage(new String(bytes));
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });

    }

    private void parseMainPage(String page){
        List <VideoInfo> videos = new ArrayList <VideoInfo> ();

        Document html = Jsoup.parse(new String(page));
        Elements videosElems = html.getElementsByTag("div");

        for(Element video : videosElems){

            if(video.attr("class").equals("video")) {

                String videoPageUrl =  video.getElementsByTag("a").first().attr("href");

                Element imgElem = video.getElementsByTag("img").first();
                String imageUrl = imgElem.attr("src");
                String info = imgElem.attr("alt");

                videos.add(new VideoInfo(info, imageUrl, videoPageUrl));
            }
        }

        createMainPage(videos);
    }

    private void createMainPage(final List <VideoInfo> videos){
        GridView view = new GridView(this);
        view.setNumColumns(5);
        view.setAdapter(new ArrayAdapter <VideoInfo>(this, 0, videos){

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                LinearLayout layout = new LinearLayout(MainActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                ImageView image = new ImageView(MainActivity.this);
                ImageLoader.getInstance().displayImage(getItem(position).getImageUrl(), image);

                TextView text = new TextView(MainActivity.this);
                text.setText(getItem(position).getTitle());

                layout.addView(image);
                layout.addView(text);

                return layout;
            }
        });

        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView <?> parent, View view, int position, long id) {
                VideoInfo video = videos.get(position);
                parseVideoPage(video.getVideoPageUrl());
            }
        });



        setContentView(view);
    }

    private void parseVideoPage(String url){
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(url, new AsyncHttpResponseHandler() {
            ProgressDialog waitingDialog;

            @Override
            public void onStart(){
                waitingDialog = ProgressDialog.show(MainActivity.this, "Loading data", "Waiting please...");
            }

            @Override
            public void onFinish(){
                waitingDialog.dismiss();
            }

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                parseSecondPage(new String(bytes));
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }

    private void parseSecondPage(String html){
        Document htmlDoc = Jsoup.parse(html);
        String url = htmlDoc.getElementsByTag("video").attr("file");

        VideoView video = new VideoView(this);
        video.setVideoURI(Uri.parse(url));

        setContentView(video);
        video.start();
    }
}
