package com.artqueen.snappy;

import java.io.Serializable;

/**
 * Created by shaikmdashiq on 27/1/15.
 */
public class PhotoDb implements Serializable {

    public PhotoDb(int id, String name, String desc, Double latitude, Double longitude) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    int id;
    String name;
    String desc;
    Double latitude;
    Double longitude;

}
