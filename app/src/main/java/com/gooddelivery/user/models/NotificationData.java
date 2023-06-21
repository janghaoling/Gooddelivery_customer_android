package com.gooddelivery.user.models;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationData implements Serializable {

    @SerializedName("custom data")
    @Expose
    private List<CustomDatum> customData = null;

    public List<CustomDatum> getCustomData() {
        return customData;
    }

    public void setCustomData(List<CustomDatum> customData) {
        this.customData = customData;
    }

}
