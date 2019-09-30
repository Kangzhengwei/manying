package com.kzw.manying;

import java.io.Serializable;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * author: kang4
 * Date: 2019/9/23
 * Description:
 */
public class SearchItem implements Serializable {
    //0:ok资源网，1：最大资源网，2：永久资源网 3:酷哈资源
    public String name;
    public String url;
    public int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<VideoUrl> list;

    public SearchItem() {

    }

    public SearchItem(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<VideoUrl> getList() {
        return list;
    }

    public void setList(List<VideoUrl> list) {
        this.list = list;
    }

    public static class VideoUrl {
        public int type;

        public String url;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        SearchItem item = (SearchItem) obj;
        if (name.equals(item.getName())) {
            VideoUrl videoUrl = new VideoUrl();
            videoUrl.setType(item.type);
            videoUrl.setUrl(item.url);
            list.add(videoUrl);
            return true;
        } else {
            VideoUrl videoUrl = new VideoUrl();
            videoUrl.setType(type);
            videoUrl.setUrl(url);
            list.add(videoUrl);
            return false;
        }
    }
}
