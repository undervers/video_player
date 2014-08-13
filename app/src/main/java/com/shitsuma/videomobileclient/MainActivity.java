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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shitsuma.videomobileclient.http.HttpResponseHandler;
import com.shitsuma.videomobileclient.model.ServerConst;
import com.shitsuma.videomobileclient.model.VideoInfo;
import com.shitsuma.videomobileclient.parsers.Parser;
import com.shitsuma.videomobileclient.parsers.XhamsterParser;

import org.apache.http.Header;
import java.util.List;


public class MainActivity extends Activity {

    private Parser videoParser = new XhamsterParser();
    private AsyncHttpClient httpClient = new AsyncHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        downloadVideosInfo(ServerConst.XHAMSTER_URL);
    }

    private void downloadVideosInfo(String pageUrl){
        httpClient.get(pageUrl, new HttpResponseHandler(this) {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                List<VideoInfo> videos = videoParser.parseVideosList(new String(bytes));
                createMainPage(videos);
            }
        });
    }

    private void downloadVideoDetail(String detailUrl){
        httpClient.get(detailUrl, new HttpResponseHandler(this) {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String videoUrl = videoParser.parseVideoUrl(new String(bytes));

                playVideo(videoUrl);
            }
        });
    }

    private void createMainPage(final List<VideoInfo> videos) {
        GridView view = new GridView(this);
        view.setNumColumns(5);
        view.setAdapter(new ArrayAdapter<VideoInfo>(this, 0, videos) {

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
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                VideoInfo video = videos.get(position);
                downloadVideoDetail(video.getVideoPageUrl());
            }
        });

        setContentView(view);
    }


    private void playVideo(String videoUrl) {

        VideoView video = new VideoView(this);
        video.setVideoURI(Uri.parse(videoUrl));

        setContentView(video);

        video.start();
    }
}
