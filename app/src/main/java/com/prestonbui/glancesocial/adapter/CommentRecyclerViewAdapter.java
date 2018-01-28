package com.prestonbui.glancesocial.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.prestonbui.glancesocial.R;
import com.prestonbui.glancesocial.models.Comment;
import com.prestonbui.glancesocial.models.User;
import com.prestonbui.glancesocial.view_holder.CommentViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phuongbui on 11/16/17.
 */

public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentViewHolder> {

    private static final String TAG = "CommentRecyclerViewAdap";

    private Context context;

    private List<Comment> mComments = new ArrayList<>();

    public CommentRecyclerViewAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.mComments = mComments;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_comments, parent, false);
        CommentViewHolder holder = new CommentViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {

        Comment comment = mComments.get(position);

        getCaption(holder, comment);
        getHandleName(holder, comment);
        getTimeDifference(holder, comment);
    }

    @Override
    public int getItemCount() {
        return mComments.size();
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

    private void getCaption(CommentViewHolder viewHolder, Comment comment) {
        viewHolder.caption.setText(comment.getCaption());
    }

    private void getTimeDifference(CommentViewHolder viewHolder, Comment comment) {
        long timeCurrent = System.currentTimeMillis() / 1000;
        long timePost = comment.getDate_created() / 1000;
        long timeDifference = timeCurrent - timePost;

        if (timeDifference < 60) {
            String time = timeDifference + "s";
            viewHolder.timestamp.setText(time);
        } else if (timeDifference < 3600) {
            String time = timeDifference / 60 + "m";
            viewHolder.timestamp.setText(time);
        } else if (timeDifference < 86400) {
            String time = timeDifference / 3600 + "h";
            viewHolder.timestamp.setText(time);
        } else if (timeDifference < 604800) {
            String time = timeDifference / 86400 + "d";
            viewHolder.timestamp.setText(time);
        } else {
            String result = (String) DateUtils.getRelativeTimeSpanString(System.currentTimeMillis(), comment.getDate_created(), 0);
            String time = result.replace("In ", "");
            viewHolder.timestamp.setText(time);
        }
    }

    private void getHandleName(final CommentViewHolder viewHolder, Comment comment) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Log.d(TAG, "getHandleName: checking comment userID" + comment.getUser_id());
        Query query = reference
                .child("data")
                .child("-Kxzyb5JsUPhsMQAb84X")
                .child("users")
                .orderByChild("user_id")
                .equalTo(comment.getUser_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    viewHolder.handleName.setText(singleSnapshot.getValue(User.class).getHandlename());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
