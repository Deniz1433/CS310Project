package com.example.lexdelegate;

import android.os.Parcel;
import android.os.Parcelable;

public class nameContract implements Parcelable {
    public final String name;
    public final String contact;

    public nameContract(String name, String contact) {
        this.name = name;
        this.contact = contact;
    }

    protected nameContract(Parcel in) {
        name = in.readString();
        contact = in.readString();
    }

    public static final Creator<nameContract> CREATOR = new Creator<nameContract>() {
        @Override
        public nameContract createFromParcel(Parcel in) {
            return new nameContract(in);
        }

        @Override
        public nameContract[] newArray(int size) {
            return new nameContract[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(contact);
    }
}