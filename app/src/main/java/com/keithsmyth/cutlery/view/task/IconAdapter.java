package com.keithsmyth.cutlery.view.task;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.keithsmyth.cutlery.R;
import com.keithsmyth.cutlery.model.Icon;

import java.util.ArrayList;
import java.util.List;

public class IconAdapter extends RecyclerView.Adapter<IconAdapter.ViewHolder> {

    private final List<Icon> icons;

    private IconClickListener iconClickListener;

    public IconAdapter() {
        icons = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_icon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Icon icon = icons.get(position);
        holder.iconImage.setImageResource(icon.resId);
        holder.iconImage.setColorFilter(Icon.getDefaultColor(holder.context));
        holder.iconImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iconClickListener != null) {
                    iconClickListener.onIconClicked(icon);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return icons.size();
    }

    public void setIcons(List<Icon> icons) {
        this.icons.clear();
        this.icons.addAll(icons);
        notifyDataSetChanged();
    }

    public void setIconClickListener(IconClickListener iconClickListener) {
        this.iconClickListener = iconClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final Context context;
        public final ImageButton iconImage;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            iconImage = (ImageButton) itemView.findViewById(R.id.icon_image);
        }
    }

    interface IconClickListener {

        void onIconClicked(Icon icon);
    }
}
