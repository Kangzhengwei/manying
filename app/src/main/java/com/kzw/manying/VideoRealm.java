package com.kzw.manying;

import java.io.Serializable;
import java.util.List;

import io.realm.RealmObject;

/**
 * author: kang4
 * Date: 2019/9/29
 * Description:
 */
public class VideoRealm extends RealmObject implements Serializable {

    public String name;
    public String url;
    public int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public VideoRealm() {

    }

    public VideoRealm(String name, String url) {
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


}
