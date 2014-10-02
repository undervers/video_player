package com.shitsuma.videomobileclient.parsers;


import com.shitsuma.videomobileclient.model.VideoInfo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class XhamsterParser implements Parser{

    @Override
    public List<VideoInfo> parseVideosList(String html) {

        List <VideoInfo> videos = new ArrayList<VideoInfo>();

        Document document = Jsoup.parse(new String(html));
        Elements videosElems = document.getElementsByTag("div");

        for(Element video : videosElems){

            if(video.attr("class").equals("video")) {

                String videoPageUrl =  video.getElementsByTag("a").first().attr("href");

                Element imgElem = video.getElementsByTag("img").first();
                String imageUrl = imgElem.attr("src");
                String info = imgElem.attr("alt");

                videos.add(new VideoInfo(info, imageUrl, videoPageUrl));
            }
        }

        return videos;
    }

    @Override
    public String parseVideoUrl(String html) {

        Document htmlDoc = Jsoup.parse(html);
        return htmlDoc.getElementsByTag("video").attr("file");
    }
}
