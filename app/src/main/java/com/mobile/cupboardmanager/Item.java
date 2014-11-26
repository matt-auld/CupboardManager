package com.mobile.cupboardmanager;

/**
 * Created by Sam on 26/11/14.
 */
public class Item {
    String name; //used as id in database

    // constructors
    public Item() {
    }

    public Item(String name) {
        this.name = name;
    }

    // setters
    public void setName(String name) {
        this.name = name;
    }

    // getters

    public String getName() {
        return this.name;
    }
}
