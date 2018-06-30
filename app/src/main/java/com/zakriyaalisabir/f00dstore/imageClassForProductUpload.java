package com.zakriyaalisabir.f00dstore;

/**
 * Created by Zakriya Ali Sabir on 3/25/2018.
 */

public class imageClassForProductUpload {

    String url;
    String productName;
    String productPrice;
    String rating;
    imageClassForProductUpload(){

    }

    public imageClassForProductUpload(String url, String productName, String productPrice, String rating) {
        this.url = url;
        this.productName = productName;
        this.productPrice = productPrice;
        this.rating = rating;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }


    public String getProductName() {
        return productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public String getUrl() {
        return url;
    }
}
