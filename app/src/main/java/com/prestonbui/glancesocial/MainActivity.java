package com.prestonbui.glancesocial;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.prestonbui.glancesocial.adapter.MainPagerAdapter;
import com.prestonbui.glancesocial.adapter.NewsFeedListAdapter;
import com.prestonbui.glancesocial.fragments.FeedsFragment;
import com.prestonbui.glancesocial.login.LoginActivity;
import com.prestonbui.glancesocial.login.WelcomeActivity;
import com.prestonbui.glancesocial.utils.UniversalImageLoader;
import com.prestonbui.glancesocial.view.BottomTabsView;

public class MainActivity extends BaseActivity{

    private static final String TAG = "MainActivity";

    private Context mContext = MainActivity.this;

    private FirebaseAuth mAuth;

    private ViewPager viewPager;

    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setupFirebaseAuth();

        initImageLoader();

        viewPager = (ViewPager) findViewById(R.id.main_view_pager);

        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(adapter);

        BottomTabsView bottomTabsView = findViewById(R.id.bottom_tabs);

        bottomTabsView.setUpWithViewPager(viewPager);

        viewPager.setCurrentItem(1);

        TypefaceProvider.registerDefaultIconSets();

    }

    @Override
    protected void performOnCreate(Bundle state) {

    }

    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }


    /*
    ------------------------------------- Firebase ---------------------------------------------------
     */

    /**
     * Check if to see if @param 'user' is logged in
     * @param user
     */
    private void checkCurrentUser(FirebaseUser user) {
        Log.d(TAG, "checkCurrentUser: checking if user is logged in");

        if (user == null) {
            Intent intent = new Intent(mContext, WelcomeActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Set up firebase auth object
     */
    private void setupFirebaseAuth() {
        FirebaseApp.initializeApp(this);

        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //check if user is logged in
                checkCurrentUser(user);

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
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        checkCurrentUser(mAuth.getCurrentUser());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
