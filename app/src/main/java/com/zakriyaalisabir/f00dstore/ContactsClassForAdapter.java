package com.zakriyaalisabir.f00dstore;

/**
 * Created by Zakriya Ali Sabir on 3/26/2018.
 */

public class ContactsClassForAdapter {
    private String name;
    private String type;
    private String email;
    ContactsClassForAdapter(){

    }

    public ContactsClassForAdapter(String name, String type, String email) {
        this.name = name;
        this.type = type;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}