package com.prestonbui.glancesocial.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.prestonbui.glancesocial.R;

/**
 * Created by phuongbui on 11/11/17.
 */

public class TrendingRecycleViewHolder extends RecyclerView.ViewHolder {

    public TextView trendingText;
    public ImageView trendingImage;

    public TrendingRecycleViewHolder(View v) {
        super(v);
        trendingText =  v.findViewById(R.id.trending_text);
        trendingImage =  v.findViewById(R.id.trending_image);
    }
}
