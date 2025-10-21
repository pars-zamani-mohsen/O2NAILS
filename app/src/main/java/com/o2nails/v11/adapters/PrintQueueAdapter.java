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
import com.o2nails.v11.models.CartItem;
import com.o2nails.v11.models.ImageItem;

import java.util.List;

public class PrintQueueAdapter extends BaseAdapter {

    private Context context;
    private List<CartItem> cartItems;
    private LayoutInflater inflater;
    private int currentPrintingIndex = -1;
    private int currentCopyIndex = 0;

    public PrintQueueAdapter(Context context, List<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
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
            convertView = inflater.inflate(R.layout.item_print_queue, parent, false);
            holder = new ViewHolder();
            holder.queuePositionTextView = convertView.findViewById(R.id.queuePositionTextView);
            holder.imageView = convertView.findViewById(R.id.imageView);
            holder.nameTextView = convertView.findViewById(R.id.nameTextView);
            holder.quantityTextView = convertView.findViewById(R.id.quantityTextView);
            holder.statusTextView = convertView.findViewById(R.id.statusTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CartItem cartItem = cartItems.get(position);
        ImageItem imageItem = cartItem.getImageItem();

        // Set queue position
        holder.queuePositionTextView.setText(String.valueOf(position + 1));

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

        // Set name
        holder.nameTextView.setText(imageItem.getName());

        // Set quantity
        holder.quantityTextView.setText("تعداد: " + cartItem.getQuantity());

        // Set status
        if (position < currentPrintingIndex) {
            // Completed
            holder.statusTextView.setText("تکمیل شد");
            holder.statusTextView.setBackgroundColor(context.getResources().getColor(R.color.md_theme_light_primary));
            holder.statusTextView.setTextColor(context.getResources().getColor(R.color.md_theme_light_onPrimary));
        } else if (position == currentPrintingIndex) {
            // Currently printing
            holder.statusTextView.setText("در حال پرینت");
            holder.statusTextView.setBackgroundColor(context.getResources().getColor(R.color.md_theme_light_secondary));
            holder.statusTextView.setTextColor(context.getResources().getColor(R.color.md_theme_light_onSecondary));
        } else {
            // Waiting
            holder.statusTextView.setText("در انتظار");
            holder.statusTextView
                    .setBackgroundColor(context.getResources().getColor(R.color.md_theme_light_surfaceContainer));
            holder.statusTextView
                    .setTextColor(context.getResources().getColor(R.color.md_theme_light_onSurfaceVariant));
        }

        return convertView;
    }

    public void updatePrintingStatus(int itemIndex, int copyIndex) {
        this.currentPrintingIndex = itemIndex;
        this.currentCopyIndex = copyIndex;
        notifyDataSetChanged();
    }

    public void setCurrentPrintingIndex(int index) {
        this.currentPrintingIndex = index;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView queuePositionTextView;
        ImageView imageView;
        TextView nameTextView;
        TextView quantityTextView;
        TextView statusTextView;
    }
}
