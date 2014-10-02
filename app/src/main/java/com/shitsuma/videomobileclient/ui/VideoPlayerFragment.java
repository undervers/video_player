package com.shitsuma.videomobileclient.ui;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import com.shitsuma.R;


public class VideoPlayerFragment extends Fragment {

    private VideoView video;
    private View waiting;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video_player_fragment, container, false);

        video = (VideoView) view.findViewById(R.id.video_view);
        waiting = view.findViewById(R.id.waiting_bar);
        initVideoPlayer(video);

        return view;
    }

    private void initVideoPlayer(final VideoView video){
        video.setMediaController(new MediaController(getActivity()));
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnInfoListener(new ChangeInfoListener(waiting));
            }
        });
    }

    public void playVideo(String videoUrl){
        if(!videoUrl.equals(video.getTag())) {
            waiting.setVisibility(View.VISIBLE);

            video.setVideoURI(Uri.parse(videoUrl));
            video.setTag(videoUrl);
            video.start();
        }else{
            video.resume();
        }
    }

    public void pauseVideo(){
        video.pause();
    }

    @Override
    public void onStop(){
        super.onStop();

        pauseVideo();
    }

    @Override
    public void onDestroy() {
        super.onStop();

        video.suspend();
    }

    private class ChangeInfoListener implements MediaPlayer.OnInfoListener{

        private View waiting;

        ChangeInfoListener(View waiting){
            this.waiting = waiting;
        }

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            //!!!!
            //Find which value is 703 and 3
            if(what == MediaPlayer.MEDIA_INFO_BUFFERING_END || what == 3){
                waiting.setVisibility(View.GONE);
                return true;
            }

            if(what == MediaPlayer.MEDIA_INFO_BUFFERING_START || what == 703){
                waiting.setVisibility(View.VISIBLE);
                return true;
            }

            return false;
        }
    }
}
