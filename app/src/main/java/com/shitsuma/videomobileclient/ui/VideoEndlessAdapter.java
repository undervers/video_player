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

    public VideoEndlessAdapter(Context context, ArrayAdapter <VideoInfo> wrapped) {
        super(context, wrapped, 0);
        this.wrapped = wrapped;

        waitingDialog = new ProgressBar(context);
    }



    @Override
    protected boolean cacheInBackground() throws Exception {
        ///!!!!!!Fix this
        final int videosOnPage = 46;
        cached = ServerUtils.getInstance().syncDownloadVideosInfo(wrapped.getCount() / videosOnPage);

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
