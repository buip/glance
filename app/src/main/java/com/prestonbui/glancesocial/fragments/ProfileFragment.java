package com.prestonbui.glancesocial.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prestonbui.glancesocial.R;
import com.prestonbui.glancesocial.login.LoginActivity;
import com.prestonbui.glancesocial.login.SignupActivity;
import com.prestonbui.glancesocial.login.WelcomeActivity;
import com.prestonbui.glancesocial.models.User;
import com.prestonbui.glancesocial.profile.SignOutActivity;
import com.prestonbui.glancesocial.utils.FireBaseMethods;
import com.prestonbui.glancesocial.utils.UniversalImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phuongbui on 10/14/17.
 */

public class ProfileFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {

    private static final String TAG = "ProfileFragment";

    private Context mContext;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFireBaseDatabase;
    private DatabaseReference myRef;
    private FireBaseMethods mFireBaseMethods;

    //Fields for user information
    TextView mHandleName;
    TextView mPosts;
    TextView mPoints;

    private ImageView rankingImage;
    private ImageView profile_background;

    public static ProfileFragment create() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: opening Profile fragment");

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //Setup context
        mContext = getActivity();

        //Firbase
        mFireBaseMethods = new FireBaseMethods(mContext);

        //Intialize  image
        rankingImage = view.findViewById(R.id.ranking_image);
        profile_background = view.findViewById(R.id.college_background_profile);


        //Fields for user information
        mHandleName = view.findViewById(R.id.handle_name);
        mPosts = view.findViewById(R.id.tvPosts);
        mPoints = view.findViewById(R.id.tvPoints);


        //Setup account setting button
        ImageButton imageButton = view.findViewById(R.id.image_setting_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(v);
            }
        });


        // Setting ViewPager for each Tabs
        ViewPager viewPager = view.findViewById(R.id.profile_viewpager);
        setupViewPager(viewPager);

        // Set Tabs inside Toolbar
        TabLayout tabs = view.findViewById(R.id.tab_layout_profile);
        tabs.setupWithViewPager(viewPager);

        setImage();

        setupFirebaseAuth();

        return view;

    }

    private void setProfileWigets(User user) {

        mHandleName.setText(user.getHandlename());
        mPosts.setText(String.valueOf(user.getPosts()));
        mPoints.setText(String.valueOf(user.getPoints()));

    }

    private void setImage() {

        Log.d(TAG, "setRankingImage: setting ranking image");

        String imgURL = "https://yt3.ggpht.com/-Cbp1OF_NviQ/AAAAAAAAAAI/AAAAAAAAAAA/Sq7fexyvxrw/s900-c-k-no-mo-rj-c0xffffff/photo.jpg";
        String beloitImgUrl = "https://www.beloit.edu/images/main_profile_images/MPI15.jpg";
        UniversalImageLoader.setImage(imgURL, rankingImage, null, "");
        UniversalImageLoader.setImage(beloitImgUrl, profile_background, null, "");
    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(mContext, v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.pop_up_menu);
        popup.show();
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out:
                Intent intent = new Intent(mContext, SignOutActivity.class);
                startActivity(intent);
                return true;
            default:
                return false;
        }
    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new PostsFragment(), "Post");
        adapter.addFragment(new LitFragment(), "LIT");
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

       /*
    ------------------------------------- Firebase ---------------------------------------------------
     */


    /**
     * Set up firebase auth object
     */
    private void setupFirebaseAuth() {

        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth");

        mAuth = FirebaseAuth.getInstance();

        mFireBaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFireBaseDatabase.getReference().child("data").child("-Kxzyb5JsUPhsMQAb84X");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //retrieve user info from the database
                setProfileWigets(mFireBaseMethods.getUserAccountInformation(dataSnapshot));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


}


