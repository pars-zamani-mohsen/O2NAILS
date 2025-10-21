package com.o2nails.v11.models;

import java.io.Serializable;

public class ImageItem implements Serializable {

    public static final int TYPE_DEFAULT = 0;
    public static final int TYPE_GALLERY = 1;
    public static final int TYPE_CAMERA = 2;
    public static final int TYPE_FAVORITE = 3;

    private String name;
    private String filePath;
    private int type;
    private int resourceId;
    private boolean isFavorite;
    private long timestamp;

    public ImageItem() {
        this.timestamp = System.currentTimeMillis();
        this.isFavorite = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTypeString() {
        switch (type) {
            case TYPE_DEFAULT:
                return "پیش‌فرض";
            case TYPE_GALLERY:
                return "گالری";
            case TYPE_CAMERA:
                return "دوربین";
            case TYPE_FAVORITE:
                return "محبوب";
            default:
                return "نامشخص";
        }
    }

    @Override
    public String toString() {
        return "ImageItem{" +
                "name='" + name + '\'' +
                ", type=" + getTypeString() +
                ", isFavorite=" + isFavorite +
                '}';
    }
}
