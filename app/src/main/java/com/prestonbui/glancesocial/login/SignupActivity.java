package com.prestonbui.glancesocial.login;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prestonbui.glancesocial.BaseActivity;
import com.prestonbui.glancesocial.R;
import com.prestonbui.glancesocial.utils.FireBaseMethods;

/**
 * Created by phuongbui on 10/15/17.
 */

public class SignupActivity extends BaseActivity {

    private static final String TAG = "SignupActivity";

    private Context mContext = SignupActivity.this;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FireBaseMethods fireBaseMethods;


    private String email, handleName, password;
    private EditText mHandleName, mEmail, mPassword;
    private Button mButtonRegister;
    private TextView loadingPleaseWait;
    private ProgressBar mProgressBar;

    //firebase Database
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    boolean isExisted;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        fireBaseMethods = new FireBaseMethods(mContext);

        Log.d(TAG, "onCreate: started");

        initWidgets();

        setupFirebaseAuth();

        init();

    }

    @Override
    protected void performOnCreate(Bundle state) {

    }

    private void init() {

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleName = mHandleName.getText().toString();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(mEmail.getText().toString());
                stringBuilder.append("@beloit.edu");
                email = stringBuilder.toString();
                password = mPassword.getText().toString();

                if (validate()) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    loadingPleaseWait.setVisibility(View.VISIBLE);

                    fireBaseMethods.registerNewEmail(handleName, email, password);


                }
            }
        });
    }

    /*
    Initialize the activity widgets
     */
    private void initWidgets() {
        Log.d(TAG, "initWidgets: Initialize Widgets");

        mHandleName = findViewById(R.id.handle_name);

        mEmail = findViewById(R.id.input_email_signup);

        mPassword = findViewById(R.id.input_password_signup);

        mButtonRegister = findViewById(R.id.btn_signup);

        mProgressBar = findViewById(R.id.progressBar);

        loadingPleaseWait = findViewById(R.id.loading_signup);

        mProgressBar.setVisibility(View.GONE);

        loadingPleaseWait.setVisibility(View.GONE);
    }

    public boolean validate() {
        boolean valid = true;

        if (handleName.isEmpty() || handleName.length() < 3) {
            mHandleName.setError("at least 3 characters");
            valid = false;
        } else {
            mHandleName.setError(null);
        }

        if (email.isEmpty()) {
            mEmail.setError("enter a valid email address");
            valid = false;
        } else {
            mEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4) {
            mPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            mPassword.setError(null);
        }

        return valid;
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

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        myRef = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            //1st check: make sure handle name is not ready in use

                            if (fireBaseMethods.checkIfHandleNameAlreadyExists(handleName, dataSnapshot)) {

                                mHandleName.setError("Handle name already exists. Please enter a different handle name");

                                isExisted = true;

                            }

                            //add new user to the database
                            fireBaseMethods.addNewUser(handleName, email);

                            Toast.makeText(mContext, "Sign up sucessful. Sending verification email", Toast.LENGTH_SHORT).show();

                            mAuth.signOut();


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    finish();

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
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
