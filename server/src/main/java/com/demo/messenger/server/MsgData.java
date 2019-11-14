package com.demo.messenger.server;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by guoxiaodong on 2019-11-14 13:09
 */
public class MsgData implements Parcelable {
    public static final Creator<MsgData> CREATOR = new Creator<MsgData>() {
        @Override
        public MsgData createFromParcel(Parcel source) {
            return new MsgData(source);
        }

        @Override
        public MsgData[] newArray(int size) {
            return new MsgData[size];
        }
    };
    public String msg;

    public MsgData() {
    }

    public MsgData(String msg) {
        this.msg = msg;
    }

    protected MsgData(Parcel in) {
        this.msg = in.readString();
    }

    @Override
    public String toString() {
        return msg;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.msg);
    }
}