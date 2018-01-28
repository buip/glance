package com.prestonbui.glancesocial.view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.prestonbui.glancesocial.R;
import com.prestonbui.glancesocial.models.Comment;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by phuongbui on 11/16/17.
 */

public class CommentViewHolder extends RecyclerView.ViewHolder {

    public TextView caption;
    public CircleImageView rankingImage;
    public TextView handleName;
    public TextView timestamp;

    public CommentViewHolder(View itemView) {
        super(itemView);

        caption = itemView.findViewById(R.id.caption_comment);
        rankingImage = itemView.findViewById(R.id.ranking_image);
        handleName = itemView.findViewById(R.id.handle_name);
        timestamp = itemView.findViewById(R.id.time_stamp);
    }
}
