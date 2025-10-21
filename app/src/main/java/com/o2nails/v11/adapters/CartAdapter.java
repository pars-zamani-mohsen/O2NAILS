package com.o2nails.v11.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.o2nails.v11.R;
import com.o2nails.v11.models.CartItem;
import com.o2nails.v11.models.ImageItem;

import java.util.List;

public class CartAdapter extends BaseAdapter {

    private Context context;
    private List<CartItem> cartItems;
    private OnCartItemClickListener clickListener;
    private LayoutInflater inflater;

    public interface OnCartItemClickListener {
        void onQuantityChanged(CartItem cartItem, int newQuantity);

        void onRemoveItem(CartItem cartItem);
    }

    public CartAdapter(Context context, List<CartItem> cartItems, OnCartItemClickListener clickListener) {
        this.context = context;
        this.cartItems = cartItems;
        this.clickListener = clickListener;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return cartItems.size();
    }

    @Override
    public Object getItem(int position) {
        return cartItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_cart, parent, false);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.imageView);
            holder.nameTextView = convertView.findViewById(R.id.nameTextView);
            holder.typeTextView = convertView.findViewById(R.id.typeTextView);
            holder.pricePerItemTextView = convertView.findViewById(R.id.pricePerItemTextView);
            holder.quantityTextView = convertView.findViewById(R.id.quantityTextView);
            holder.totalPriceTextView = convertView.findViewById(R.id.totalPriceTextView);
            holder.decreaseButton = convertView.findViewById(R.id.decreaseButton);
            holder.increaseButton = convertView.findViewById(R.id.increaseButton);
            holder.removeButton = convertView.findViewById(R.id.removeButton);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CartItem cartItem = cartItems.get(position);
        ImageItem imageItem = cartItem.getImageItem();

        // Set image
        if (imageItem.getType() == ImageItem.TYPE_DEFAULT) {
            holder.imageView.setImageResource(imageItem.getResourceId());
        } else {
            Bitmap bitmap = BitmapFactory.decodeFile(imageItem.getFilePath());
            if (bitmap != null) {
                holder.imageView.setImageBitmap(bitmap);
            } else {
                holder.imageView.setImageResource(R.drawable.ic_image_placeholder);
            }
        }

        // Set text fields
        holder.nameTextView.setText(imageItem.getName());
        holder.typeTextView.setText(imageItem.getTypeString());
        holder.pricePerItemTextView.setText("قیمت هر عدد: " + cartItem.getFormattedPricePerItem() + " تومان");
        holder.quantityTextView.setText(String.valueOf(cartItem.getQuantity()));
        holder.totalPriceTextView.setText("مجموع: " + cartItem.getFormattedTotalPrice() + " تومان");

        // Set button listeners with final position
        final int finalPosition = position;
        holder.decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartItem currentItem = cartItems.get(finalPosition);
                int newQuantity = currentItem.getQuantity() - 1;
                if (clickListener != null) {
                    clickListener.onQuantityChanged(currentItem, newQuantity);
                }
            }
        });

        holder.increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartItem currentItem = cartItems.get(finalPosition);
                int newQuantity = currentItem.getQuantity() + 1;
                if (clickListener != null) {
                    clickListener.onQuantityChanged(currentItem, newQuantity);
                }
            }
        });

        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartItem currentItem = cartItems.get(finalPosition);
                if (clickListener != null) {
                    clickListener.onRemoveItem(currentItem);
                }
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        ImageView imageView;
        TextView nameTextView;
        TextView typeTextView;
        TextView pricePerItemTextView;
        TextView quantityTextView;
        TextView totalPriceTextView;
        Button decreaseButton;
        Button increaseButton;
        Button removeButton;
    }
}
