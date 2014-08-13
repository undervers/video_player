package com.shitsuma.videomobileclient.parsers;

import com.shitsuma.videomobileclient.model.VideoInfo;

import java.util.List;

public interface Parser {

    List<VideoInfo> parseVideosList(String html);

    String parseVideoUrl(String html);
}
