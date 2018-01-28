package com.prestonbui.glancesocial.view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.like.LikeButton;
import com.prestonbui.glancesocial.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by phuongbui on 11/16/17.
 */

public class PostViewHolder extends RecyclerView.ViewHolder {

    public CircleImageView mRankingImage;
    public TextView mHandleName, mTimeDelta, mCaption, mRanking;
    public ImageView mImage;
    public LikeButton mUpVote, mDownVote;
    public ImageButton mComment;
    public TextView mCountUpVote, mCountDownVote, mCountComment;
    public ImageView postOption;

    public PostViewHolder(View itemView) {

        super(itemView);

        mRankingImage = itemView.findViewById(R.id.ranking_image);
        mHandleName = itemView.findViewById(R.id.handle_name);
        mTimeDelta = itemView.findViewById(R.id.time_stamp);
        mCaption = itemView.findViewById(R.id.post_caption);
//        mRanking = itemView.findViewById(R.id.ranking_name);
        mImage = itemView.findViewById(R.id.post_image);
        mUpVote = itemView.findViewById(R.id.up_vote_button);
//        mDownVote = itemView.findViewById(R.id.down_vote_button);
        mComment = itemView.findViewById(R.id.comment_button);
        mCountUpVote = itemView.findViewById(R.id.up_vote_count);
//        mCountDownVote = itemView.findViewById(R.id.down_vote_count);
        mCountComment = itemView.findViewById(R.id.comment_count);
        postOption = itemView.findViewById(R.id.post_option);
    }

}
