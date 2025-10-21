package com.o2nails.v11.models;

import java.io.Serializable;

public class CartItem implements Serializable {

    private ImageItem imageItem;
    private int quantity;
    private int pricePerItem;
    private long timestamp;

    public CartItem() {
        this.timestamp = System.currentTimeMillis();
        this.quantity = 1;
        this.pricePerItem = 5000; // Default price - will be updated from preferences
    }

    public CartItem(ImageItem imageItem, int quantity) {
        this();
        this.imageItem = imageItem;
        this.quantity = quantity;
    }

    public ImageItem getImageItem() {
        return imageItem;
    }

    public void setImageItem(ImageItem imageItem) {
        this.imageItem = imageItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPricePerItem() {
        return pricePerItem;
    }

    public void setPricePerItem(int pricePerItem) {
        this.pricePerItem = pricePerItem;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getTotalPrice() {
        return quantity * pricePerItem;
    }

    public String getFormattedTotalPrice() {
        return String.format("%,d", getTotalPrice());
    }

    public String getFormattedPricePerItem() {
        return String.format("%,d", pricePerItem);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        CartItem cartItem = (CartItem) obj;

        // Compare based on image item properties
        if (imageItem == null || cartItem.imageItem == null)
            return false;

        if (imageItem.getType() != cartItem.imageItem.getType())
            return false;

        if (imageItem.getType() == ImageItem.TYPE_DEFAULT) {
            return imageItem.getResourceId() == cartItem.imageItem.getResourceId();
        } else {
            return imageItem.getFilePath() != null &&
                    imageItem.getFilePath().equals(cartItem.imageItem.getFilePath());
        }
    }

    @Override
    public int hashCode() {
        if (imageItem == null)
            return 0;

        if (imageItem.getType() == ImageItem.TYPE_DEFAULT) {
            return imageItem.getResourceId();
        } else {
            return imageItem.getFilePath() != null ? imageItem.getFilePath().hashCode() : 0;
        }
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "imageName='" + (imageItem != null ? imageItem.getName() : "null") + '\'' +
                ", quantity=" + quantity +
                ", pricePerItem=" + pricePerItem +
                ", totalPrice=" + getTotalPrice() +
                '}';
    }
}
