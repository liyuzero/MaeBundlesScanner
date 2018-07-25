package com.yu.mae.bundles.scanner.manager;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by liyu20 on 2017/10/24.
 */

public class ScannerBottomItem implements Parcelable{
    public int iconRes;
    public String title;
    public boolean isShowColor;

    public ScannerBottomItem(int iconRes, String title, boolean isShowColor) {
        this.iconRes = iconRes;
        this.title = title;
        this.isShowColor = isShowColor;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.iconRes);
        dest.writeString(this.title);
        dest.writeByte(this.isShowColor ? (byte) 1 : (byte) 0);
    }

    protected ScannerBottomItem(Parcel in) {
        this.iconRes = in.readInt();
        this.title = in.readString();
        this.isShowColor = in.readByte() != 0;
    }

    public static final Creator<ScannerBottomItem> CREATOR = new Creator<ScannerBottomItem>() {
        @Override
        public ScannerBottomItem createFromParcel(Parcel source) {
            return new ScannerBottomItem(source);
        }

        @Override
        public ScannerBottomItem[] newArray(int size) {
            return new ScannerBottomItem[size];
        }
    };
}
