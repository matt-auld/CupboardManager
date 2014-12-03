package com.mobile.cupboardmanager;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sam on 26/11/14.
 */
public class ShoppingItem implements Parcelable {
    String name; //foreign key of item
    int quantity;

    //constructors
    ShoppingItem() {
    }

    ShoppingItem(Parcel in) {
        readFromParcel(in);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator<ShoppingItem>() {
        @Override
        public ShoppingItem createFromParcel(Parcel source) {
            return new ShoppingItem(source);
        }

        @Override
        public ShoppingItem[] newArray(int size) {
            return new ShoppingItem[0];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(quantity);
    }

    public void readFromParcel(Parcel source) {
        name = source.readString();
        quantity = source.readInt();
    }
}
