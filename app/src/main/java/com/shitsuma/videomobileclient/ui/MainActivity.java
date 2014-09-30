package com.shitsuma.videomobileclient.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shitsuma.R;
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

    private GridView videosList;
    private VideoPlayerFragment videoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_screen);

        videosList = (GridView) findViewById(R.id.videos_list);

        videoFragment = new VideoPlayerFragment();
        getFragmentManager()
                .beginTransaction()
                .add(R.id.video_player, videoFragment)
                .hide(videoFragment)
                .commit();

        downloadVideosInfo(ServerConst.XHAMSTER_URL);
    }

    private void downloadVideosInfo(String pageUrl){
        httpClient.get(pageUrl, new HttpResponseHandler(this) {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                List<VideoInfo> videos = videoParser.parseVideosList(new String(bytes));
                initMainPage(videos);
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

    private void initMainPage(final List<VideoInfo> videos) {
        videosList.setAdapter(new VideosListAdapter(this, videos));
        videosList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                VideoInfo video = videos.get(position);
                downloadVideoDetail(video.getVideoPageUrl());
            }
        });
    }

    private void playVideo(String url){
        showVideoPlayer(true);
        videoFragment.playVideo(url);
    }

    private void showVideoPlayer(boolean show){
        if(show){
            getFragmentManager()
                .beginTransaction()
                .show(videoFragment)
                .commit();
            videosList.setVisibility(View.GONE);
        }else{
            getFragmentManager()
                .beginTransaction()
                .hide(videoFragment)
                .commit();
            videosList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if(videoFragment.isVisible()){
            showVideoPlayer(false);
        }else{
            super.onBackPressed();
        }
    }
}
