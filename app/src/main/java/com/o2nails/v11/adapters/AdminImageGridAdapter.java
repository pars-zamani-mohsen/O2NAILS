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
import com.o2nails.v11.models.ImageItem;

import java.util.List;

public class AdminImageGridAdapter extends BaseAdapter {

    private Context context;
    private List<ImageItem> imageList;
    private LayoutInflater inflater;
    private OnImageDeleteListener deleteListener;

    public interface OnImageDeleteListener {
        void onImageDelete(ImageItem imageItem, int position);
    }

    public AdminImageGridAdapter(Context context, List<ImageItem> imageList) {
        this.context = context;
        this.imageList = imageList;
        this.inflater = LayoutInflater.from(context);
    }

    public void setDeleteListener(OnImageDeleteListener listener) {
        this.deleteListener = listener;
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
            convertView = inflater.inflate(R.layout.item_admin_image_grid, parent, false);
            holder = new ViewHolder();
            holder.imagePreview = convertView.findViewById(R.id.imagePreview);
            holder.imageNameTextView = convertView.findViewById(R.id.imageNameTextView);
            holder.deleteButton = convertView.findViewById(R.id.deleteButton);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ImageItem imageItem = imageList.get(position);

        // Set image
        if (imageItem.getType() == ImageItem.TYPE_DEFAULT) {
            holder.imagePreview.setImageResource(imageItem.getResourceId());
        } else {
            // Load from file path
            Bitmap bitmap = BitmapFactory.decodeFile(imageItem.getFilePath());
            if (bitmap != null) {
                holder.imagePreview.setImageBitmap(bitmap);
            } else {
                holder.imagePreview.setImageResource(R.drawable.ic_image_placeholder);
            }
        }

        // Set name
        holder.imageNameTextView.setText(imageItem.getName());

        // Set delete button click listener
        final int finalPosition = position;
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteListener != null) {
                    deleteListener.onImageDelete(imageList.get(finalPosition), finalPosition);
                }
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        ImageView imagePreview;
        TextView imageNameTextView;
        Button deleteButton;
    }
}
