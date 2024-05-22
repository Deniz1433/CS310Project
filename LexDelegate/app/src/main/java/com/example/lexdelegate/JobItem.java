package com.example.lexdelegate;

import android.os.Parcel;
import android.os.Parcelable;

public class JobItem implements Parcelable {
    public final String id;
    public final String username;
    public final String jobType;
    public final String jobDetail;
    public final String fee;
    public final String contact;
    public final String city;

    public JobItem(String id, String username, String jobType, String jobDetail, String fee, String contact, String city) {
        this.id = id;
        this.username = username;
        this.jobType = jobType;
        this.jobDetail = jobDetail;
        this.fee = fee;
        this.contact = contact;
        this.city = city;
    }

    protected JobItem(Parcel in) {
        id = in.readString();
        username = in.readString();
        jobType = in.readString();
        jobDetail = in.readString();
        fee = in.readString();
        contact = in.readString();
        city = in.readString();
    }

    public static final Creator<JobItem> CREATOR = new Creator<JobItem>() {
        @Override
        public JobItem createFromParcel(Parcel in) {
            return new JobItem(in);
        }

        @Override
        public JobItem[] newArray(int size) {
            return new JobItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(username);
        dest.writeString(jobType);
        dest.writeString(jobDetail);
        dest.writeString(fee);
        dest.writeString(contact);
        dest.writeString(city);
    }
}