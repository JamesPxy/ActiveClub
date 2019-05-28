package com.pxy.eshore.bean.book;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 测试 Parcelable
 */
public class UserInfo implements Parcelable {

    public String name;
    public int age;

    public UserInfo(Parcel source) {
        name = source.readString();
        age = source.readInt();
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel source) {
            return new UserInfo(source);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(age);
    }
}
