package com.shitsuma.videomobileclient.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.commonsware.cwac.endless.EndlessAdapter;
import com.shitsuma.videomobileclient.R;
import com.shitsuma.videomobileclient.http.HttpResponseHandler;
import com.shitsuma.videomobileclient.http.ServerUtils;
import com.shitsuma.videomobileclient.model.VideoInfo;
import com.shitsuma.videomobileclient.parsers.Parser;
import com.shitsuma.videomobileclient.parsers.XhamsterParser;

import org.apache.http.Header;

import java.util.List;


public class MainActivity extends Activity implements TextView.OnEditorActionListener, View.OnClickListener {

    private Parser videoParser;

    private View videosListLayout;
    private GridView videosList;

    private View searchBtn;
    private EditText searchWord;

    private VideoPlayerFragment videoFragment;

    private NewVideosHandler videosListHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_screen);

        videoParser = new XhamsterParser();

        videosListHandler = new NewVideosHandler();

        videosListLayout = findViewById(R.id.videos_list_layout);
        videosList = (GridView) videosListLayout.findViewById(R.id.videos_list);

        searchBtn = videosListLayout.findViewById(R.id.search_button);
        searchBtn.setOnClickListener(this);
        searchWord = (EditText) videosListLayout.findViewById(R.id.search_word_input);
        searchWord.setOnEditorActionListener(this);

        videoFragment = new VideoPlayerFragment();
        getFragmentManager()
            .beginTransaction()
            .add(R.id.video_player, videoFragment)
            .hide(videoFragment)
            .commit();

        downloadVideosInfo();
    }

    private void downloadVideosInfo(){
        ServerUtils.getInstance().downloadVideosInfo(videosListHandler);
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

    private void showNewVideos(final List<VideoInfo> videos) {
        EndlessAdapter adapter = new VideoEndlessAdapter(this, new VideosListAdapter(this, videos));
        initMainPage(adapter);
    }

    private void showSearchPage(final List<VideoInfo> videos, String searchPhrase) {
        EndlessAdapter adapter = new VideoEndlessAdapter(this, new VideosListAdapter(this, videos),  searchPhrase);
        initMainPage(adapter);
    }

    private void initMainPage(EndlessAdapter adapter){
        videosList.setAdapter(adapter);
        videosList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                VideoInfo video = (VideoInfo) parent.getItemAtPosition(position);
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
            videosListLayout.setVisibility(View.GONE);
        }else{
            getFragmentManager()
                .beginTransaction()
                .hide(videoFragment)
                .commit();
            videosListLayout.setVisibility(View.VISIBLE);
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


    private void searchVideo(){
        searchVideo(searchWord.getText().toString());
    }

    private void searchVideo(String searchPhrase) {
        if (searchPhrase.length() > 0) {
            hideKeyboard();

            ServerUtils.getInstance().makeSearchRequest(searchPhrase, new SearchVideosHandler(searchPhrase));
        }
    }

    private void hideKeyboard(){
        InputMethodManager input = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        input.hideSoftInputFromWindow(searchWord.getWindowToken(), 0);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                && event.getAction() == KeyEvent.ACTION_DOWN){
            searchVideo();
            return true;
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        searchVideo();
    }

    private class NewVideosHandler extends HttpResponseHandler {

        public NewVideosHandler() {
            super(MainActivity.this);
        }

        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            List<VideoInfo> videos = videoParser.parseVideosList(new String(bytes));
            Log.d("url debuging", "" + videos.size());

            showNewVideos(videos);
        }
    }

    private class SearchVideosHandler extends HttpResponseHandler {

        private String searchPhrase;

        public SearchVideosHandler(String searchPhrase) {
            super(MainActivity.this);

            this.searchPhrase = searchPhrase;
        }

        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            List<VideoInfo> videos = videoParser.parseVideosList(new String(bytes));

            showSearchPage(videos, searchPhrase);
        }
    }
}
