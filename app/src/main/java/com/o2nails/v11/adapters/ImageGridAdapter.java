package com.o2nails.v11.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.o2nails.v11.R;
import com.o2nails.v11.models.ImageItem;
import com.o2nails.v11.utils.CartManager;

import java.util.List;

public class ImageGridAdapter extends BaseAdapter {

    private Context context;
    private List<ImageItem> imageList;
    private OnImageClickListener clickListener;
    private LayoutInflater inflater;
    private CartManager cartManager;

    public interface OnImageClickListener {
        void onImageClick(ImageItem imageItem);
    }

    public ImageGridAdapter(Context context, List<ImageItem> imageList, OnImageClickListener clickListener) {
        this.context = context;
        this.imageList = imageList;
        this.clickListener = clickListener;
        this.inflater = LayoutInflater.from(context);
        this.cartManager = CartManager.getInstance();
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public Object getItem(int position) {
        return imageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_image_grid, parent, false);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.imageView);
            holder.nameTextView = convertView.findViewById(R.id.nameTextView);
            holder.typeTextView = convertView.findViewById(R.id.typeTextView);
            holder.favoriteIcon = convertView.findViewById(R.id.favoriteIcon);
            holder.selectionOverlay = convertView.findViewById(R.id.selectionOverlay);
            holder.quantityBadge = convertView.findViewById(R.id.quantityBadge);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ImageItem imageItem = imageList.get(position);

        // Set image
        if (imageItem.getType() == ImageItem.TYPE_DEFAULT) {
            holder.imageView.setImageResource(imageItem.getResourceId());
        } else {
            // Load from file path
            Bitmap bitmap = BitmapFactory.decodeFile(imageItem.getFilePath());
            if (bitmap != null) {
                holder.imageView.setImageBitmap(bitmap);
            } else {
                holder.imageView.setImageResource(R.drawable.ic_image_placeholder);
            }
        }

        // Set name
        holder.nameTextView.setText(imageItem.getName());

        // Set type
        holder.typeTextView.setText(imageItem.getTypeString());

        // Set favorite icon visibility
        holder.favoriteIcon.setVisibility(imageItem.isFavorite() ? View.VISIBLE : View.GONE);

        // Set selection state
        boolean isInCart = cartManager.containsItem(imageItem);
        int quantity = cartManager.getItemQuantity(imageItem);

        if (isInCart) {
            holder.selectionOverlay.setVisibility(View.VISIBLE);
            holder.quantityBadge.setVisibility(View.VISIBLE);
            holder.quantityBadge.setText(String.valueOf(quantity));
        } else {
            holder.selectionOverlay.setVisibility(View.GONE);
            holder.quantityBadge.setVisibility(View.GONE);
        }

        // Set click listener
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onImageClick(imageItem);
                }
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        ImageView imageView;
        TextView nameTextView;
        TextView typeTextView;
        ImageView favoriteIcon;
        View selectionOverlay;
        TextView quantityBadge;
    }
}
