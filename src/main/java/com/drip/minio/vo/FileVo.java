package com.drip.minio.vo;

import java.io.Serializable;

/**
 * Created by lenovo on 2018/6/5.
 */
public class FileVo implements Serializable{

    private String name;

    private String suffix;

    private String size;

    private String url;

    private String desc;

    public FileVo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
