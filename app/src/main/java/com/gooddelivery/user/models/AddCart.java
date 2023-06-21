
package com.gooddelivery.user.models;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddCart {

    @SerializedName("delivery_charges")
    @Expose
    private Integer deliveryCharges;
    @SerializedName("delivery_free_minimum")
    @Expose
    private Integer deliveryFreeMinimum;
    @SerializedName("tax_percentage")
    @Expose
    private Integer taxPercentage;
    @SerializedName("carts")
    @Expose
    private List<Cart> products = new ArrayList<>();
    @SerializedName("total_price")
    @Expose
    private Double totalPrice;
    @SerializedName("shop_discount")
    @Expose
    private Double shopDiscount;
    @SerializedName("tax")
    @Expose
    private Double tax;
    @SerializedName("promocode_amount")
    @Expose
    private Double promocodeAmount;
    @SerializedName("net")
    @Expose
    private Double net;
    @SerializedName("wallet_balance")
    @Expose
    private Integer walletBalance;
    @SerializedName("payable")
    @Expose
    private Double payable;


    public Double getShopDiscount() {
        return shopDiscount;
    }

    public void setShopDiscount(Double shopDiscount) {
        this.shopDiscount = shopDiscount;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Double getTax() {
        return tax;
    }

    public void setTax(Double tax) {
        this.tax = tax;
    }

    public Double getPromocodeAmount() {
        return promocodeAmount;
    }

    public void setPromocodeAmount(Double promocodeAmount) {
        this.promocodeAmount = promocodeAmount;
    }

    public Double getNet() {
        return net;
    }

    public void setNet(Double net) {
        this.net = net;
    }

    public Integer getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(Integer walletBalance) {
        this.walletBalance = walletBalance;
    }

    public Double getPayable() {
        return payable;
    }

    public void setPayable(Double payable) {
        this.payable = payable;
    }


    public Integer getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(Integer deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }

    public Integer getDeliveryFreeMinimum() {
        return deliveryFreeMinimum;
    }

    public void setDeliveryFreeMinimum(Integer deliveryFreeMinimum) {
        this.deliveryFreeMinimum = deliveryFreeMinimum;
    }

    public Integer getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(Integer taxPercentage) {
        this.taxPercentage = taxPercentage;
    }

    public List<Cart> getProductList() {
        return products;
    }

    public void setProductList(List<Cart> products) {
        this.products = products;
    }

}
