package com.gooddelivery.user.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Cursines {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
//    @SerializedName("pivot")
//    @Expose
//    private Pivot pivot;
    @SerializedName("image")
    @Expose
    private String image;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public Pivot getPivot() {
//        return pivot;
//    }
//
//    public void setPivot(Pivot pivot) {
//        this.pivot = pivot;
//    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
