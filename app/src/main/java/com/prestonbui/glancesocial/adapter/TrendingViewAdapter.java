package com.prestonbui.glancesocial.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prestonbui.glancesocial.R;
import com.prestonbui.glancesocial.models.Post;
import com.prestonbui.glancesocial.utils.UniversalImageLoader;
import com.prestonbui.glancesocial.view.ViewPostActivity;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by phuongbui on 11/11/17.
 */

public class TrendingViewAdapter extends RecyclerView.Adapter<TrendingRecycleViewHolder> {

    private ArrayList<Post> list;

    private Context mContext;

    public TrendingViewAdapter(ArrayList<Post> Data) {
        list = Data;
    }

    @Override
    public TrendingRecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_recycle_items, parent, false);

        mContext = view.getContext();

        final TrendingRecycleViewHolder holder = new TrendingRecycleViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(TrendingRecycleViewHolder holder, final int position) {
        holder.trendingText.setText(list.get(position).getCaption());

        String imageUrl = list.get(position).getImage_path();
        if (imageUrl != null) {
            UniversalImageLoader.setImage(imageUrl, holder.trendingImage, null, "");
        } else {
            holder.trendingImage.setAlpha(0.3f);
            UniversalImageLoader.setImage(randomImage(), holder.trendingImage, null, "");
        }

        if (list.get(position).getPost_id() != null) {
            holder.trendingText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ViewPostActivity.class);
                    intent.putExtra("post_id", list.get(position).getPost_id());
                    mContext.startActivity(intent);
                }
            });

            holder.trendingImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ViewPostActivity.class);
                    intent.putExtra("post_id", list.get(position).getPost_id());
                    mContext.startActivity(intent);
                }
            });
        }
    }

    private String randomImage() {
        String imageUrl = "";

        String zero = "https://firebasestorage.googleapis.com/v0/b/glance-88491.appspot.com/o/random_color%2Frandom_one.jpg?alt=media&token=63f6aba4-939e-4033-9fc4-9a099c928427";
        String one = "https://firebasestorage.googleapis.com/v0/b/glance-88491.appspot.com/o/random_color%2Frandom_two.jpg?alt=media&token=1962b501-16af-4f46-aebf-f99103e38d21";
        String two = "https://firebasestorage.googleapis.com/v0/b/glance-88491.appspot.com/o/random_color%2Frandom_three.jpg?alt=media&token=3facf2ca-5eec-477a-a872-9c66afa18133";
        String three = "https://firebasestorage.googleapis.com/v0/b/glance-88491.appspot.com/o/random_color%2Frandom_four.jpg?alt=media&token=6dc58c12-533f-4d37-b365-017baf0641cc";
        String four = "https://firebasestorage.googleapis.com/v0/b/glance-88491.appspot.com/o/random_color%2Frandom_five.jpg?alt=media&token=6def644e-b484-4105-be5a-3b18a718951e";

        int randomNum = ThreadLocalRandom.current().nextInt(0, 4);

        if (randomNum == 0) {
            imageUrl = zero;
        } else if (randomNum == 1) {
            imageUrl = one;
        } else if (randomNum == 2) {
            imageUrl = two;

        } else if (randomNum == 3) {
            imageUrl = three;

        } else if (randomNum == 4) {
            imageUrl = four;
        }

        return imageUrl;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

