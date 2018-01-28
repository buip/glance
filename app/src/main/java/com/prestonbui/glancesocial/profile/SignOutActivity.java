package com.prestonbui.glancesocial.profile;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.prestonbui.glancesocial.BaseActivity;
import com.prestonbui.glancesocial.R;
import com.prestonbui.glancesocial.login.LoginActivity;
import com.prestonbui.glancesocial.login.WelcomeActivity;

public class SignOutActivity extends BaseActivity {

    private static final String TAG = "SignOutActivity";

    private Context mContext;

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;

    private TextView tvSignout;

    private Button btnConfirmSignout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_out);

        mContext = SignOutActivity.this;

        tvSignout = findViewById(R.id.tvConfirmSignout);

        btnConfirmSignout = findViewById(R.id.btnConfirmSignout);

        setupFirebaseAuth();

        btnConfirmSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to sign out.");

                FirebaseAuth.getInstance().signOut();

                finish();
            }
        });


    }

    @Override
    protected void performOnCreate(Bundle state) {

    }

   /*
    ------------------------------------ Firebase ---------------------------------------------
     */


    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();

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

                    Log.d(TAG, "onAuthStateChanged: navigating back to login screen.");

                    Intent intent = new Intent(mContext, WelcomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                // ...
            }
        };
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
