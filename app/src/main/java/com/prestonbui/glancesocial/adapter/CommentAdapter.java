package com.prestonbui.glancesocial.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.prestonbui.glancesocial.R;
import com.prestonbui.glancesocial.models.Comment;
import com.prestonbui.glancesocial.models.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by phuongbui on 11/12/17.
 */

public class CommentAdapter extends ArrayAdapter<Comment> {

    private static final String TAG = "CommentAdapter";

    //widgets

    private LayoutInflater inflater;
    private int layoutResource;
    private Context mContext;

    //vars


    public CommentAdapter(@NonNull Context context, int resource, @NonNull List<Comment> objects) {
        super(context, resource, objects);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        layoutResource = resource;
    }

    private static class ViewHolder {
        TextView caption;
        CircleImageView rankingImage;
        TextView handleName;
        TextView timestamp;
        Comment comment;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

         ViewHolder viewHolder;

        if (convertView == null) {

            convertView = inflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.caption = convertView.findViewById(R.id.caption_comment);
            viewHolder.rankingImage = convertView.findViewById(R.id.ranking_image);
            viewHolder.handleName = convertView.findViewById(R.id.handle_name);
            viewHolder.timestamp = convertView.findViewById(R.id.time_stamp);
            viewHolder.comment = getItem(position);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();

        }

        getCaption(position, viewHolder);

        getHandleName(viewHolder);

        getTimeDifference(viewHolder, viewHolder.comment);

        return convertView;
    }

    private void getCaption(int position, ViewHolder viewHolder) {
        viewHolder.caption.setText(getItem(position).getCaption());
    }

    private void getTimeDifference(ViewHolder viewHolder, Comment comment) {
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

    private void getHandleName(final ViewHolder viewHolder) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Log.d(TAG, "getHandleName: checking comment userID" + viewHolder.comment.getUser_id());
        Query query = reference
                .child("data")
                .child("-Kxzyb5JsUPhsMQAb84X")
                .child("users")
                .orderByChild("user_id")
                .equalTo(viewHolder.comment.getUser_id());

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
