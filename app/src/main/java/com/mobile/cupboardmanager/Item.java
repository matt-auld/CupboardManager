package com.mobile.cupboardmanager;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sam on 26/11/14.
 */
public class Item implements Parcelable {
    String name; //used as id in database

    // constructors
    public Item() {
    }

    public Item(Parcel in) {
        readFromParcel(in);
    }

    public static final Parcelable.Creator CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel source) {
            return new Item(source);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[0];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }

    public void readFromParcel(Parcel in) {
        name = in.readString();
    }
}
