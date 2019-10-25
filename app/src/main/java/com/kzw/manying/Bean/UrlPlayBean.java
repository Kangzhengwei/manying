package com.kzw.manying.Bean;

import java.io.Serializable;
import java.util.List;

/**
 * author: kang4
 * Date: 2019/9/23
 * Description:
 */
public class UrlPlayBean implements Serializable {

    public String urlType;
    public List<Url> list;

    public String getUrlType() {
        return urlType;
    }

    public void setUrlType(String urlType) {
        this.urlType = urlType;
    }

    public List<Url> getList() {
        return list;
    }

    public void setList(List<Url> list) {
        this.list = list;
    }

    public static class Url{
        public String videoSeries;
        public String videoUrl;

        public String getVideoSeries() {
            return videoSeries;
        }

        public void setVideoSeries(String videoSeries) {
            this.videoSeries = videoSeries;
        }

        public String getVideoUrl() {
            return videoUrl;
        }

        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }
    }
}
