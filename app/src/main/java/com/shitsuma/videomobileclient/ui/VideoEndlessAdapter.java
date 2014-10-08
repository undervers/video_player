package com.shitsuma.videomobileclient.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.commonsware.cwac.endless.EndlessAdapter;
import com.shitsuma.videomobileclient.http.ServerUtils;
import com.shitsuma.videomobileclient.model.VideoInfo;

import java.util.List;

public class VideoEndlessAdapter extends EndlessAdapter {

    private View waitingDialog;

    private ArrayAdapter <VideoInfo> wrapped;
    private List <VideoInfo> cached;

    private ServerUtils utils;

    private final String searchPhrase;

    public VideoEndlessAdapter(Context context, ArrayAdapter <VideoInfo> wrapped) {
        this(context, wrapped, "");
    }

    public VideoEndlessAdapter(Context context, ArrayAdapter <VideoInfo> wrapped, String searchPhrase) {
        super(context, wrapped, 0);
        this.wrapped = wrapped;

        this.searchPhrase = searchPhrase;

        utils = ServerUtils.getInstance();

        waitingDialog = new ProgressBar(context);
    }

    @Override
    protected boolean cacheInBackground() throws Exception {
        ///!!!!!!Fix this
        final int videosOnPage;


        if(searchPhrase.length() > 0){
            videosOnPage = 28;
            cached = utils.syncMakeSearchRequest(searchPhrase, wrapped.getCount() / videosOnPage + 1);
        } else {
            videosOnPage = 46;
            cached = utils.syncGetNewVideos(wrapped.getCount() / videosOnPage + 1);
        }


        return (cached != null && cached.size() > 0);
    }

    @Override
    protected void appendCachedData() {
        wrapped.addAll(cached);

        cached = null;
    }

    @Override
    protected View getPendingView(ViewGroup parent) {
        return waitingDialog;
    }
}
