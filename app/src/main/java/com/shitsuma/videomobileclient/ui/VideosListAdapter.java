package com.shitsuma.videomobileclient.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.shitsuma.R;
import com.shitsuma.videomobileclient.model.VideoInfo;

import java.util.List;

public class VideosListAdapter extends ArrayAdapter <VideoInfo> {

    private LayoutInflater inflater;
    private ImageLoader imageLoader;

    public VideosListAdapter(Context context, List<VideoInfo> objects) {
        super(context, 0, objects);

        inflater = LayoutInflater.from(context);
        imageLoader = ImageLoader.getInstance();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View layout;
        final ViewHolder holder;

        if(convertView == null){
            layout = inflater.inflate(R.layout.videos_list_item, null);

            holder = new ViewHolder();
            holder.image = (ImageView) layout.findViewById(R.id.image);
            holder.image.setVisibility(View.INVISIBLE);

            holder.waiting = layout.findViewById(R.id.waiting);
            holder.waiting.setVisibility(View.VISIBLE);

            holder.titleView = (TextView) layout.findViewById(R.id.title);

            layout.setTag(holder);
        }else{
            layout = convertView;
            holder = (ViewHolder) convertView.getTag();
            holder.image.setVisibility(View.INVISIBLE);
            holder.waiting.setVisibility(View.VISIBLE);
        }

        imageLoader.displayImage(getItem(position).getImageUrl(), holder.image, new SimpleImageLoadingListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                holder.image.setVisibility(View.VISIBLE);
                holder.waiting.setVisibility(View.INVISIBLE);
            }
        });

        holder.titleView.setText(getItem(position).getTitle());

        return layout;
    }

    private class ViewHolder{
        View waiting;
        ImageView image;
        TextView titleView;
    }
}
