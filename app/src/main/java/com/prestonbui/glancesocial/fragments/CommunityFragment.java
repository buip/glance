package com.prestonbui.glancesocial.fragments;

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
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.prestonbui.glancesocial.R;
import com.prestonbui.glancesocial.adapter.CommunityAdapter;
import com.prestonbui.glancesocial.adapter.TrendingViewAdapter;
import com.prestonbui.glancesocial.models.Post;
import com.prestonbui.glancesocial.models.School;

import java.util.ArrayList;
import java.util.Collections;


/**
 * Created by phuongbui on 10/14/17.
 */

public class CommunityFragment extends Fragment{

    private static final String TAG = "CommunityFragment";

    private View view;
    private ArrayList<School> mSchools;
    private CommunityAdapter adapter;


    //widgets
    private ListView listView;

    public static CommunityFragment create() {
        return new CommunityFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mSchools = new ArrayList<>();
        view = inflater.inflate(getLayoutResId(), container, false);

        listView = view.findViewById(R.id.listView);

        mSchools.clear();
        getSchools();

        return view;
    }

    private void getSchools() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child("schools");

        mSchools.clear();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                    School school = new School();

                    school.setColor(singleSnapshot.getValue(School.class).getColor());
                    school.setName(singleSnapshot.getValue(School.class).getName());
                    school.setNum_members(singleSnapshot.getValue(School.class).getNum_members());
                    school.setPoints(singleSnapshot.getValue(School.class).getPoints());
                    school.setImage_path(singleSnapshot.getValue(School.class).getImage_path());

                    mSchools.add(school);
                }

                displaySchools();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private void displaySchools() {
        if (mSchools != null) {
            try {
                adapter = new CommunityAdapter(getActivity(), R.layout.layout_community_list_items, mSchools);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } catch (NullPointerException e) {
                Log.e(TAG, "displayPhotos: NullPointerException: " + e.getMessage());
            } catch (IndexOutOfBoundsException e) {
                Log.e(TAG, "displayPhotos: IndexOutOfBoundsException: " + e.getMessage());
            }
        }
    }


    public int getLayoutResId() {
        return R.layout.fragment_community;
    }

    public void inOnCreateView(View root, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    }
}
