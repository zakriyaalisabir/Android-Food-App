package com.zakriyaalisabir.f00dstore;

/**
 * Created by Zakriya Ali Sabir on 3/21/2018.
 */

public class TitleForNavSettings {
    String title;
    String email;
    String userId;

    TitleForNavSettings(){

    }

    public TitleForNavSettings(String title, String email, String userId) {
        this.title = title;
        this.email = email;
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public String getEmail() {
        return email;
    }

    public String getUserId() {
        return userId;
    }
}
