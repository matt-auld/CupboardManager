package com.mobile.cupboardmanager;

/**
 * Created by Sam on 26/11/14.
 */
public class ShoppingItem {
    String name; //foreign key of item
    int quantity;

    //constructors
    ShoppingItem() {
    }

    ShoppingItem(String name) {
        this.name = name;
    }

    ShoppingItem(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    //setters
    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    //getters
    public String getName() {
        return this.name;
    }

    public int getQuantity() {
        return this.quantity;
    }
}
