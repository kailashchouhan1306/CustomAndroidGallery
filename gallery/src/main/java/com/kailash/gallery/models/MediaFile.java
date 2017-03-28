package com.kailash.gallery.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Kailash Chouhan
 * @creationDate 17-Feb-2017
 */
public class MediaFile implements Parcelable {
    public long id;
    public String name;
    public String path;
    public boolean isSelected;
    public String type;
    public int videoDuration;
    public long imageSize;

    public MediaFile(long id, String name, String path, boolean isSelected, String type, long imageSize) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.isSelected = isSelected;
        this.type = type;
        this.imageSize = imageSize;
    }

    public MediaFile(long id, String name, String path, boolean isSelected, String type, int videoDuration, long imageSize) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.isSelected = isSelected;
        this.type = type;
        this.videoDuration = videoDuration;
        this.imageSize = imageSize;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(path);
        dest.writeString(type);
        dest.writeInt(videoDuration);
        dest.writeLong(imageSize);
    }

    public static final Creator<MediaFile> CREATOR = new Creator<MediaFile>() {
        @Override
        public MediaFile createFromParcel(Parcel source) {
            return new MediaFile(source);
        }

        @Override
        public MediaFile[] newArray(int size) {
            return new MediaFile[size];
        }
    };

    private MediaFile(Parcel in) {
        id = in.readLong();
        name = in.readString();
        path = in.readString();
        type = in.readString();
        videoDuration = in.readInt();
        imageSize = in.readLong();
    }
}
