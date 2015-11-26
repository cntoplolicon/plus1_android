package com.oneplusapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

import org.joda.time.DateTime;

import java.util.List;

@Table(name = "Notifications")
public class Notification extends Model implements Parcelable {

    public static final String TYPE_COMMENT = "comment";
    public static final String TYPE_RECOMMEND = "recommend";

    @Column
    @Expose
    private int userId;
    @Column
    @Expose
    private String type;
    @Column
    @Expose
    private DateTime publishTime;
    @Column
    @Expose
    private DateTime receiveTime;
    @Column
    @Expose
    private String content;

    public Notification() {
    }

    public Notification(Parcel in) {
        userId = in.readInt();
        type = in.readString();
        publishTime = new DateTime().withMillis(in.readLong());
        receiveTime = new DateTime().withMillis(in.readLong());
        content = in.readString();
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DateTime getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(DateTime publishTime) {
        this.publishTime = publishTime;
    }

    public DateTime getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(DateTime receiveTime) {
        this.receiveTime = receiveTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static List<Notification> getAll() {
        return new Select().from(Notification.class).execute();
    }

    public static List<Notification> getMyNotifications(int userId) {
        return new Select().from(Notification.class).where("userId = ?", userId).orderBy("receiveTime DESC").execute();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(userId);
        parcel.writeString(type);
        parcel.writeLong(publishTime.getMillis());
        parcel.writeLong(receiveTime.getMillis());
        parcel.writeString(content);
    }

    public static final Parcelable.Creator<Notification> CREATOR
            = new Parcelable.Creator<Notification>() {
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }

        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };
}
