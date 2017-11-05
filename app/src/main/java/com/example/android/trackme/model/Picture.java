package com.example.android.trackme.model;

/**
 * Created by tanujanuj on 29/10/17.
 */

public class Picture {
    private String url;
    private String caption;

    public String getDesc() {
        return desc;
    }

    private String desc;

    public Picture(String url, String caption,String desc) {
        this.url = url;
        this.caption = caption;
        this.desc=desc;

    }

    public Picture() {
    }

    public String getUrl() {
        return url;
    }

    public String getCaption() {
        return caption;
    }
}
