package com.shitsuma.videomobileclient.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;

import com.commonsware.cwac.endless.EndlessAdapter;
import com.shitsuma.R;
import com.shitsuma.videomobileclient.http.HttpResponseHandler;
import com.shitsuma.videomobileclient.http.ServerUtils;
import com.shitsuma.videomobileclient.model.VideoInfo;
import com.shitsuma.videomobileclient.parsers.Parser;
import com.shitsuma.videomobileclient.parsers.XhamsterParser;

import org.apache.http.Header;

import java.util.List;


public class MainActivity extends Activity {

    private Parser videoParser = new XhamsterParser();

    private GridView videosList;
    private VideoPlayerFragment videoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_screen);

        videosList = (GridView) findViewById(R.id.videos_list);

        videoFragment = new VideoPlayerFragment();
        getFragmentManager()
            .beginTransaction()
            .add(R.id.video_player, videoFragment)
            .hide(videoFragment)
            .commit();

        downloadVideosInfo();
    }

    private void downloadVideosInfo(){
        ServerUtils.getInstance().downloadVideosInfo(new HttpResponseHandler(this) {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                List<VideoInfo> videos = videoParser.parseVideosList(new String(bytes));
                initMainPage(videos);
            }
        });
    }

    private void downloadVideoDetail(String detailUrl){
        ServerUtils.getInstance().downloadHtml(detailUrl, new HttpResponseHandler(this) {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String videoUrl = videoParser.parseVideoUrl(new String(bytes));

                playVideo(videoUrl);
            }
        });
    }

    private void initMainPage(final List<VideoInfo> videos) {
        EndlessAdapter adapter = new VideoEndlessAdapter(this, new VideosListAdapter(this, videos));

        videosList.setAdapter(adapter);
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
            videoFragment.pauseVideo();
        }else{
            super.onBackPressed();
        }
    }
}
