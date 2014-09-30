package com.shitsuma.videomobileclient.ui;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import com.shitsuma.R;


public class VideoPlayerFragment extends Fragment {

    private VideoView video;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video_player_fragment, container, false);

        video = (VideoView) view.findViewById(R.id.video_view);
        View waiting = view.findViewById(R.id.waiting_bar);
        initVideoPlayer(video, waiting);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        video.pause();
    }

    private void initVideoPlayer(final VideoView video, final View waiting){
        video.setMediaController(new MediaController(getActivity()));
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                waiting.setVisibility(View.GONE);
                video.start();

                mp.setOnInfoListener(new ChangeInfoListener(waiting));
            }
        });
    }

    public void playVideo(String videoUrl){
        video.setVideoURI(Uri.parse(videoUrl));
        video.start();
    }

    private class ChangeInfoListener implements MediaPlayer.OnInfoListener{

        private View waiting;

        ChangeInfoListener(View waiting){
            this.waiting = waiting;
        }

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            if(what == MediaPlayer.MEDIA_INFO_BUFFERING_END){
                waiting.setVisibility(View.GONE);
                return true;
            }

            if(what == MediaPlayer.MEDIA_INFO_BUFFERING_START){
                waiting.setVisibility(View.VISIBLE);
                return true;
            }

            return false;
        }
    }
}
