package com.mobile.cupboardmanager;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sam on 26/11/14.
 */
public class CupboardItem implements Parcelable {
    String name; //foreign key of item
    int quantity;
    int expiry_time_ms;
    int notification_id;

    //constructors
    CupboardItem() {
    }

    CupboardItem(Parcel in) {
        readFromParcel(in);
    }

    CupboardItem(String name) {
        this.name = name;
    }

    CupboardItem(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    CupboardItem(String name, int quantity, int expiry_time_ms) {
        this.name = name;
        this.quantity = quantity;
        this.expiry_time_ms = expiry_time_ms;
    }

    CupboardItem(String name, int quantity, int expiry_time_ms, int notification_id) {
        this.name = name;
        this.quantity = quantity;
        this.expiry_time_ms = expiry_time_ms;
        this.notification_id = notification_id;
    }

    public static final Parcelable.Creator CREATOR = new Creator<CupboardItem>() {
        @Override
        public CupboardItem createFromParcel(Parcel source) {
            return new CupboardItem(source);
        }

        @Override
        public CupboardItem[] newArray(int size) {
            return new CupboardItem[0];
        }
    };

    //setters
    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setExpiry_time_ms(int expiry_time_ms) {
        this.expiry_time_ms = expiry_time_ms;
    }

    public void setNotification_id(int notification_id) {
        this.notification_id = notification_id;
    }

    //getters
    public String getName() {
        return this.name;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public int getExpiry_time_ms() {
        return expiry_time_ms;
    }

    public int getNotification_id() {
        return notification_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(quantity);
        dest.writeInt(expiry_time_ms);
        dest.writeInt(notification_id);
    }

    public void readFromParcel(Parcel source) {
        name = source.readString();
        quantity = source.readInt();
        expiry_time_ms = source.readInt();
        notification_id = source.readInt();
    }
}
