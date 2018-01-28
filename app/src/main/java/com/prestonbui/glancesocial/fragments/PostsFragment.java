package com.prestonbui.glancesocial.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.prestonbui.glancesocial.R;
import com.prestonbui.glancesocial.adapter.NewsFeedListAdapter;
import com.prestonbui.glancesocial.adapter.NewsFeedRecyclerViewAdapter;
import com.prestonbui.glancesocial.login.WelcomeActivity;
import com.prestonbui.glancesocial.models.Post;
import com.prestonbui.glancesocial.share.ShareActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by phuongbui on 10/15/17.
 */

public class PostsFragment extends Fragment{

    private static final String TAG = "PostsFragment";

    //widgets
    private RecyclerView recyclerView;

    //vars
    private List<Post> mPosts = new ArrayList<>();
    private List<String> mPostIds = new ArrayList<>();
    private NewsFeedRecyclerViewAdapter adapter;
    private String currentUserID;

    public static PostsFragment create() {
        return new PostsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutResId(), container, false);

        recyclerView = view.findViewById(R.id.recyclerView);

        mPosts = new ArrayList<>();

        mPosts.clear();

        getPost();

        if (mPosts != null) {
            try {
                adapter = new NewsFeedRecyclerViewAdapter(getActivity(), mPosts);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            } catch (NullPointerException e) {
                Log.e(TAG, "displayPhotos: NullPointerException: " + e.getMessage());
            } catch (IndexOutOfBoundsException e) {
                Log.e(TAG, "displayPhotos: IndexOutOfBoundsException: " + e.getMessage());
            }
        }

        return view;
    }

    private void getPost() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(getActivity(), WelcomeActivity.class);
            startActivity(intent);
        }
        else{
            currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference
                .child("data")
                .child("-Kxzyb5JsUPhsMQAb84X")
                .child("posts")
                .orderByChild("user_id")
                .equalTo(currentUserID);

        mPosts.clear();
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // A new comment has been added, add it to the displayed list
                Post post = dataSnapshot.getValue(Post.class);

                // Update RecyclerView
                mPostIds.add(dataSnapshot.getKey());
                mPosts.add(post);
                adapter.notifyDataSetChanged();

                Collections.sort(mPosts, new Comparator<Post>() {
                    @Override
                    public int compare(Post o1, Post o2) {
                        String dateOne = String.valueOf(o1.getDate_created());
                        String dateTwo = String.valueOf(o2.getDate_created());
                        return dateTwo.compareTo(dateOne);
                    }
                });

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

//                Post newPost = dataSnapshot.getValue(Post.class);
//                String commentKey = dataSnapshot.getKey();
//
//                int postIndex = mPostIds.indexOf(commentKey);
//                if (postIndex > -1) {
//                    // Replace with the new data
//                    mPosts.set(postIndex, newPost);
//
//                    // Update the RecyclerView
//                    adapter.notifyItemChanged(postIndex);
//                } else {
//                    Log.w(TAG, "onChildChanged:unknown_child:" + commentKey);
//                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public int getLayoutResId() {
        return R.layout.fragment_post;
    }

    public void inOnCreateView(View root, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    }

}
