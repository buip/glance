package com.prestonbui.glancesocial.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prestonbui.glancesocial.R;
import com.prestonbui.glancesocial.models.Post;
import com.prestonbui.glancesocial.models.School;
import com.prestonbui.glancesocial.utils.UniversalImageLoader;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by phuongbui on 11/14/17.
 */

public class CommunityAdapter extends ArrayAdapter<School> {

    private static final String TAG = "PersonListAdapter";

    private Context mContext;
    private int mResource;

    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        ImageView logo;
        TextView collegeName;
        TextView textTrending;
        TextView textComingSoon;
        TextView numPoints, textPoints;
        TextView numMembers, textMembers;
        RecyclerView recyclerView;
        ArrayList<Post> mPosts;
        ArrayList<Post> mTemporary;
        School school;
    }

    /**
     * Default constructor for the PersonListAdapter
     *
     * @param context
     * @param resource
     * @param objects
     */
    public CommunityAdapter(Context context, int resource, ArrayList<School> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //ViewHolder object
        final ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();

            holder.logo = convertView.findViewById(R.id.college_logo);
            holder.collegeName = convertView.findViewById(R.id.college_name);
            holder.numPoints = convertView.findViewById(R.id.tv_points);
            holder.textPoints = convertView.findViewById(R.id.text_points);
            holder.numMembers = convertView.findViewById(R.id.tv_members);
            holder.textMembers = convertView.findViewById(R.id.text_members);
            holder.recyclerView = convertView.findViewById(R.id.cardView);
            holder.textTrending = convertView.findViewById(R.id.text_trending);
            holder.textComingSoon = convertView.findViewById(R.id.text_coming_soon);
            holder.mPosts = new ArrayList<>();
            holder.mTemporary = new ArrayList<>();

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.school = getItem(position);

        holder.textComingSoon.setVisibility(View.GONE);
        holder.textTrending.setVisibility(View.VISIBLE);

        //setup temp post for school
        setUpTempPost(holder);

        //setup school information
        setUpSchoolInfo(holder);

        getPost(holder);

        return convertView;
    }

    private void setUpSchoolInfo(ViewHolder holder) {
        holder.collegeName.setText(holder.school.getName());
        holder.collegeName.setTextColor(Color.parseColor(holder.school.getColor()));
        holder.numPoints.setText(String.valueOf(holder.school.getPoints()));
        holder.numPoints.setTextColor(Color.parseColor(holder.school.getColor()));
        holder.numMembers.setText(String.valueOf(holder.school.getNum_members()));
        holder.numMembers.setTextColor(Color.parseColor(holder.school.getColor()));
        String imagePath = holder.school.getImage_path();
        UniversalImageLoader.setImage(imagePath, holder.logo, null, "");
    }

    private void setUpTempPost(ViewHolder holder) {

        Post post1 = new Post("Comming Soon", "", null, null, null, 0, 0, 0);
        Post post2 = new Post("Comming Soon", "", null, null, null, 0, 0, 0);

        holder.mTemporary.add(post1);
        holder.mTemporary.add(post2);
    }

    private void setRecycleView(ViewHolder viewHolder) {
        viewHolder.recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        if (viewHolder.mPosts.size() > 0 & viewHolder.recyclerView != null) {

            TrendingViewAdapter adapter = null;
            if (viewHolder.school.getName().equals("Beloit College")) {
                adapter = new TrendingViewAdapter(viewHolder.mPosts);
            } else {
                viewHolder.textComingSoon.setVisibility(View.VISIBLE);
                viewHolder.textTrending.setVisibility(View.GONE);

            }
            viewHolder.recyclerView.setAdapter(adapter);
        }
        viewHolder.recyclerView.setLayoutManager(layoutManager);
    }

    private void getPost(final ViewHolder viewHolder) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("data")
                .child("-Kxzyb5JsUPhsMQAb84X")
                .child("posts");
        reference.orderByChild("points").limitToLast(5).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                viewHolder.mPosts.clear();
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Post post = singleSnapshot.getValue(Post.class);
                    post.setCaption(post.getCaption());
                    post.setImage_path(post.getImage_path());
                    viewHolder.mPosts.add(post);
                }

                Collections.reverse(viewHolder.mPosts);
                setRecycleView(viewHolder);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    /**
     * Required for setting up the Universal Image loader Library
     */

}
