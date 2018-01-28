package com.prestonbui.glancesocial.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.prestonbui.glancesocial.R;
import com.prestonbui.glancesocial.models.Post;
import com.prestonbui.glancesocial.models.User;
import com.prestonbui.glancesocial.profile.SignOutActivity;
import com.prestonbui.glancesocial.view.ViewPostActivity;
import com.prestonbui.glancesocial.view_holder.PostViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by phuongbui on 11/16/17.
 */

public class NewsFeedRecyclerViewAdapter extends RecyclerView.Adapter<PostViewHolder> {

    private static final String TAG = "NewsFeedRecyclerView";

    private List<Post> mPosts = new ArrayList<>();
    private Context mContext;
    private final String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private boolean upVotedByCurrentUser, reportedByCurrentUser;

    public NewsFeedRecyclerViewAdapter(Context context, List<Post> mPosts) {
        this.mContext = context;
        this.mPosts = mPosts;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_main_feed_list_items, parent, false);
        PostViewHolder holder = new PostViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {

        Post post = mPosts.get(position);

        setListViewClickListener(holder, post);

        getUpVote(holder, post);
        setUpVote(holder, post);

        getHandleName(holder, post);
        getTimeDifference(holder, post);
        getCaption(holder, post);
        getPostImage(holder, post);
        setCommentCount(holder, post);

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    private void getRankingImage() {

    }

    private void getHandleName(final PostViewHolder viewHolder, Post post) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child("data")
                .child("-Kxzyb5JsUPhsMQAb84X")
                .child(mContext.getString(R.string.dbname_users))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(post.getUser_id());

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

    private void getTimeDifference(PostViewHolder holder, Post post) {

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
            String result = (String) DateUtils.getRelativeTimeSpanString(System.currentTimeMillis(), post.getDate_created(), 0);
            String time = result.replace("In ", "");
            holder.mTimeDelta.setText(time);
        }
    }

    private void setCommentCount(PostViewHolder holder, Post post) {
        holder.mCountComment.setText(String.valueOf(post.getComments()));
    }

    private void getCaption(PostViewHolder holder, Post post) {
        holder.mCaption.setText(post.getCaption());
    }

    private void getPostImage(final PostViewHolder holder, Post post) {
        if (post.getImage_path() != null) {
            Glide.with(mContext).load(post.getImage_path()).into(holder.mImage);
        } else {
            Glide.with(mContext).load(null).into(holder.mImage);
        }
    }

    private void setUpVote(final PostViewHolder holder, final Post post) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        final String userId = post.getUser_id();
        final String postId = post.getPost_id();
        holder.mUpVote.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("up_votes").child(currentUserID).child(post.getPost_id()).setValue("true");
                reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("posts").child(post.getPost_id()).child("up_votes").child(currentUserID).setValue("true");
                getUpVote(holder, post);
                setUserPoints(userId);
                setPostPlusPoint(postId);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("up_votes").child(currentUserID).child(post.getPost_id()).removeValue();
                reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("posts").child(post.getPost_id()).child("up_votes").removeValue();
                getUpVote(holder, post);
                setUserPoints(userId);
                setMinusPostPoint(postId);
            }
        });

    }

    private void getUpVote(final PostViewHolder holder, Post post) {
        String postID = post.getPost_id();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference newReference = reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("posts").child(postID).child("up_votes");
        newReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.hasChild(currentUserID)) {
                    upVotedByCurrentUser = true;
                    setState(holder);
                } else {
                    upVotedByCurrentUser = false;
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

    private void getReported(final PostViewHolder holder, Post post) {
        final String postID = post.getPost_id();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference newReference = reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("posts").child(postID).child("down_votes");
        newReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.d(TAG, "onDataChange: check if lalala" + dataSnapshot.hasChild(currentUserID));
                if (dataSnapshot.hasChild(currentUserID)) {
                    reportedByCurrentUser = true;
                } else {
                    reportedByCurrentUser = false;
                }

                setDownVote(postID);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void setDownVote(final String postID) {
        Log.d(TAG, "setDownVote: reported by current user" + reportedByCurrentUser);
        if(reportedByCurrentUser) {
            Toast.makeText(mContext, "You reported this post", Toast.LENGTH_SHORT).show();
        } else {
            final DatabaseReference referenceTwo = FirebaseDatabase.getInstance().getReference();
            referenceTwo.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("down_votes").child(currentUserID).child(postID).setValue("true");
            referenceTwo.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("posts").child(postID).child("down_votes").child(currentUserID).setValue("true");
            setMinusPostPoint(postID);
        }
    }

    private void setUserPoints(final String userID) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference newReference = reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("users").child(userID);
        newReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                User user = dataSnapshot.getValue(User.class);
                int points = user.getPoints();
                if (upVotedByCurrentUser) {
                    reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("users").child(userID).child("points").setValue(points + 1);
                } else {
                    reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("users").child(userID).child("points").setValue(points - 1);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void setPostPlusPoint(final String postID) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference newReference = reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("posts").child(postID);
        newReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Post post = dataSnapshot.getValue(Post.class);
                long points = post.getPoints();
                reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("posts").child(postID).child("points").setValue(points + 1);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void setMinusPostPoint(final String postID) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference newReference = reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("posts").child(postID);
        newReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Post post = dataSnapshot.getValue(Post.class);
                long points = post.getPoints();
                reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("posts").child(postID).child("points").setValue(points - 1);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void setState(PostViewHolder holder) {
        if (upVotedByCurrentUser) {
            holder.mUpVote.setLiked(true);
        } else {
            holder.mUpVote.setLiked(false);
        }
    }

    private void setListViewClickListener(final PostViewHolder holder, final Post post) {

        holder.mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ViewPostActivity.class);
                intent.putExtra("post_id", post.getPost_id());
                mContext.startActivity(intent);
            }
        });

        holder.mCaption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ViewPostActivity.class);
                intent.putExtra("post_id", post.getPost_id());
                mContext.startActivity(intent);
            }
        });

        holder.mHandleName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ViewPostActivity.class);
                intent.putExtra("post_id", post.getPost_id());
                mContext.startActivity(intent);
            }
        });

        holder.mComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ViewPostActivity.class);
                intent.putExtra("post_id", post.getPost_id());
                mContext.startActivity(intent);
            }
        });

        holder.postOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(mContext, v);
                //inflating menu from xml resource
                popup.inflate(R.menu.post_pop_up_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.report_post:
                                getReported(holder, post);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();
            }
        });
    }
}
