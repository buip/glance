package com.prestonbui.glancesocial.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.module.AppGlideModule;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.prestonbui.glancesocial.R;
import com.prestonbui.glancesocial.models.Post;
import com.prestonbui.glancesocial.models.User;
import com.prestonbui.glancesocial.utils.UniversalImageLoader;
import com.prestonbui.glancesocial.view.ViewPostActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by phuongbui on 10/31/17.
 */

public class NewsFeedListAdapter extends ArrayAdapter<Post> {

    private static final String TAG = "NewsFeedListAdapter";

    private Context mContext;
    private int mResource;
    private LayoutInflater mInflater;

    //vars

    //Firebase
    private DatabaseReference mReference;

    public NewsFeedListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Post> objects) {
        super(context, resource, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResource = resource;
        this.mContext = context;
        mReference = FirebaseDatabase.getInstance().getReference();
    }


    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        //Widgets
        CircleImageView mRankingImage;
        TextView mHandleName, mTimeDelta, mCaption, mRanking;
        ImageView mImage;
        LikeButton mUpVote, mDownVote;
        ImageButton mComment;
        TextView mCountUpVote, mCountDownVote, mCountComment;

        //vars
        String currentUserID;
        Post post;
        boolean upVotedByCurrentUser, downVotedByCurrentUser;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        //ViewHolder object
        final ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(mResource, parent, false);
            holder = new ViewHolder();

            //widgets
            holder.mRankingImage = convertView.findViewById(R.id.ranking_image);
            holder.mHandleName = convertView.findViewById(R.id.handle_name);
            holder.mTimeDelta = convertView.findViewById(R.id.time_stamp);
            holder.mCaption = convertView.findViewById(R.id.post_caption);
//            holder.mRanking = convertView.findViewById(R.id.ranking_name);
            holder.mImage = convertView.findViewById(R.id.post_image);
            holder.mUpVote = convertView.findViewById(R.id.up_vote_button);
//            holder.mDownVote = convertView.findViewById(R.id.down_vote_button);
            holder.mComment = convertView.findViewById(R.id.comment_button);
            holder.mCountUpVote = convertView.findViewById(R.id.up_vote_count);
//            holder.mCountDownVote = convertView.findViewById(R.id.down_vote_count);
            holder.mCountComment = convertView.findViewById(R.id.comment_count);
            holder.currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.post = getItem(position);

        setListViewClickListener(holder);

        init(holder);

        return convertView;
    }

    private void init(ViewHolder holder) {
        getUpVote(holder);
        getDownVote(holder);
        setUpVote(holder);
        setDownVote(holder);

        getHandleName(holder);
        getTimeDifference(holder, holder.post);
        getCaption(holder);
        getPostImage(holder);
        setCommentCount(holder);
    }

    private void getRankingImage() {

    }

    private void getHandleName(final ViewHolder viewHolder) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mContext.getString(R.string.dbname_users))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(viewHolder.post.getUser_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    viewHolder.mHandleName.setText(singleSnapshot.getValue(User.class).getHandlename());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getTimeDifference(ViewHolder holder, Post post) {

        long timeCurrent = System.currentTimeMillis() / 1000;
        long timePost = post.getDate_created() / 1000;
        long timeDifference = timeCurrent - timePost;

        if (timeDifference < 60) {
            String time = timeDifference + "s";
            holder.mTimeDelta.setText(time);
        } else if (timeDifference < 3600) {
            String time = timeDifference / 60 + "m";
            holder.mTimeDelta.setText(time);
        } else if (timeDifference < 86400) {
            String time = timeDifference / 3600 + "h";
            holder.mTimeDelta.setText(time);
        } else if (timeDifference < 604800) {
            String time = timeDifference / 86400 + "d";
            holder.mTimeDelta.setText(time);
        } else {
            String result = (String) DateUtils.getRelativeTimeSpanString(System.currentTimeMillis(), holder.post.getDate_created(), 0);
            String time = result.replace("In ", "");
            holder.mTimeDelta.setText(time);
        }
    }

    private void setCommentCount(ViewHolder holder) {
        holder.mCountComment.setText(String.valueOf(holder.post.getComments()));
    }

    private void getCaption(ViewHolder holder) {
        holder.mCaption.setText(holder.post.getCaption());
    }


    private void getPostImage(final ViewHolder holder) {
        if (holder.post.getImage_path() != null) {
            Glide.with(mContext).load(holder.post.getImage_path()).into(holder.mImage);
        } else {
            holder.mImage.setVisibility(View.GONE);
        }
    }

    private void setUpVote(final ViewHolder holder) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        final String userId = holder.post.getUser_id();
        final String postId = holder.post.getPost_id();
        holder.mUpVote.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                reference.child("up_votes").child(holder.currentUserID).child(holder.post.getPost_id()).setValue("true");
                reference.child("posts").child(holder.post.getPost_id()).child("up_votes").child(holder.currentUserID).setValue("true");
                getUpVote(holder);
                holder.mDownVote.setEnabled(false);
                setUserPoints(userId, holder);
                setPostPoints(postId, holder);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                reference.child("up_votes").child(holder.currentUserID).child(holder.post.getPost_id()).removeValue();
                reference.child("posts").child(holder.post.getPost_id()).child("up_votes").removeValue();
                getUpVote(holder);
                holder.mDownVote.setEnabled(true);
                setUserPoints(userId, holder);
                setPostPoints(postId, holder);
            }
        });

    }

    private void getUpVote(final ViewHolder holder) {
        String postID = holder.post.getPost_id();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference newReference = reference.child("posts").child(postID).child("up_votes");
        newReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.hasChild(holder.currentUserID)) {
                    holder.upVotedByCurrentUser = true;
                    setState(holder);
                } else {
                    holder.upVotedByCurrentUser = false;
                }
                long numUpVotes = dataSnapshot.getChildrenCount();

                holder.mCountUpVote.setText(String.valueOf(numUpVotes));
            }


            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void setDownVote(final ViewHolder holder) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        final String postId = holder.post.getPost_id();
        holder.mDownVote.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                reference.child("down_votes").child(holder.currentUserID).child(holder.post.getPost_id()).setValue("true");
                reference.child("posts").child(holder.post.getPost_id()).child("down_votes").child(holder.currentUserID).setValue("true");
                getDownVote(holder);
                holder.mUpVote.setEnabled(false);
                setPostPoints(postId, holder);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                reference.child("down_votes").child(holder.currentUserID).child(holder.post.getPost_id()).removeValue();
                reference.child("posts").child(holder.post.getPost_id()).child("down_votes").removeValue();
                getDownVote(holder);
                holder.mUpVote.setEnabled(true);
                setPostPoints(postId, holder);
            }
        });

    }

    private void getDownVote(final ViewHolder holder) {
        String postID = holder.post.getPost_id();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference newReference = reference.child("posts").child(postID).child("down_votes");
        newReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.hasChild(holder.currentUserID)) {
                    holder.downVotedByCurrentUser = true;
                    setState(holder);
                } else {
                    holder.downVotedByCurrentUser = false;
                }
                long numDownVotes = dataSnapshot.getChildrenCount();

                holder.mCountDownVote.setText(String.valueOf(numDownVotes));
            }


            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }


    private void setUserPoints(final String userID, final ViewHolder holder) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference newReference = reference.child("users").child(userID);
        newReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                User user = dataSnapshot.getValue(User.class);
                int points = user.getPoints();
                if (holder.upVotedByCurrentUser) {
                    reference.child("users").child(userID).child("points").setValue(points + 1);
                } else {
                    reference.child("users").child(userID).child("points").setValue(points - 1);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void setPostPoints(final String postID, final ViewHolder holder) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference newReference = reference.child("posts").child(postID);
        newReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Post post = dataSnapshot.getValue(Post.class);
                long points = post.getPoints();
                Log.d(TAG, "onDataChange: check like: " + holder.upVotedByCurrentUser);
                Log.d(TAG, "onDataChange: check unlike: " + holder.downVotedByCurrentUser);

                if (holder.upVotedByCurrentUser && !holder.downVotedByCurrentUser) {
                    reference.child("posts").child(postID).child("points").setValue(points + 1);
                } else if (!holder.upVotedByCurrentUser && !holder.downVotedByCurrentUser) {
                    reference.child("posts").child(postID).child("points").setValue(points - 1);
                } else if (!holder.upVotedByCurrentUser && holder.downVotedByCurrentUser) {
                    reference.child("posts").child(postID).child("points").setValue(points - 1);
                } else if (!holder.upVotedByCurrentUser && !holder.downVotedByCurrentUser) {
                    reference.child("posts").child(postID).child("points").setValue(points + 1);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void setState(ViewHolder holder) {
        if (holder.upVotedByCurrentUser) {
            holder.mUpVote.setLiked(true);
        }

        if (holder.downVotedByCurrentUser) {
            holder.mDownVote.setLiked(true);
        }
    }

    private void setListViewClickListener(final ViewHolder holder) {

        holder.mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ViewPostActivity.class);
                intent.putExtra("post_id", holder.post.getPost_id());
                mContext.startActivity(intent);
            }
        });

        holder.mCaption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ViewPostActivity.class);
                intent.putExtra("post_id", holder.post.getPost_id());
                mContext.startActivity(intent);
            }
        });

        holder.mHandleName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ViewPostActivity.class);
                intent.putExtra("post_id", holder.post.getPost_id());
                mContext.startActivity(intent);
            }
        });

        holder.mComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ViewPostActivity.class);
                intent.putExtra("post_id", holder.post.getPost_id());
                mContext.startActivity(intent);
            }
        });
    }
}