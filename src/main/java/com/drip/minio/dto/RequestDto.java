package com.drip.minio.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by lenovo on 2018/6/4.
 */
@XmlRootElement(name = "RequestDto")
public class RequestDto {
    private String name;

    public RequestDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
