package com.gooddelivery.user.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CustomDatum implements Serializable {

    @SerializedName("page")
    @Expose
    private String page;
    @SerializedName("order_id")
    @Expose
    private Integer orderId;
    @SerializedName("dispute_status")
    @Expose
    private String disputeStatus;
    @SerializedName("status")
    @Expose
    private String status;

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Object getDisputeStatus() {
        return disputeStatus;
    }

    public void setDisputeStatus(String disputeStatus) {
        this.disputeStatus = disputeStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}

