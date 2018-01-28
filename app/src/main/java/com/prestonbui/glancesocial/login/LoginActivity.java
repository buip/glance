package com.prestonbui.glancesocial.login;

import android.content.Context;
import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.prestonbui.glancesocial.BaseActivity;
import com.prestonbui.glancesocial.MainActivity;
import com.prestonbui.glancesocial.R;

import org.w3c.dom.Text;

/**
 * Created by phuongbui on 10/15/17.
 */

public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;

    private Context mContext = LoginActivity.this;

    private ProgressBar mProgressBar;
    private EditText mEmail, mPassword;
    private TextView mPleaseWait;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mProgressBar = findViewById(R.id.progressBar);

        mPleaseWait = findViewById(R.id.loading_signin);

        mEmail = findViewById(R.id.input_email_login);

        mPassword = findViewById(R.id.input_password_login);

        TextView mForgotPassword = findViewById(R.id.link_forgot_password);

        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });

        Log.d(TAG, "onCreate: started");

        mProgressBar.setVisibility(View.GONE);
        mPleaseWait.setVisibility(View.GONE);

        setupFirebaseAuth();

        init();

    }

    @Override
    protected void performOnCreate(Bundle state) {

    }

    public boolean validate() {

        boolean valid = true;

        String emailValidate = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        if (emailValidate.isEmpty()) {
            mEmail.setError("enter a valid email address");
            valid = false;
        } else {
            mEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4) {
            mPassword.setError("minimum 4 characters");
            valid = false;
        } else {
            mPassword.setError(null);
        }

        return valid;

    }



        /*
    ------------------------------------- Firebase ---------------------------------------------------
     */

    private void init() {
        // initialize the button for logging in

        Button btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to login");

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(mEmail.getText().toString());
                stringBuilder.append("@beloit.edu");
                String email = stringBuilder.toString();

                String password = mPassword.getText().toString();

                if (!validate()) {

                    Toast.makeText(mContext, "Login failed", Toast.LENGTH_LONG).show();

                } else {

                    mProgressBar.setVisibility(View.VISIBLE);
                    mPleaseWait.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                                    FirebaseUser user = mAuth.getCurrentUser();

                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        Log.w(TAG, "signInWithEmail:failed", task.getException());
                                        Toast.makeText(mContext, task.getException().toString(),
                                                Toast.LENGTH_SHORT).show();

                                        mProgressBar.setVisibility(View.GONE);
                                        mPleaseWait.setVisibility(View.GONE);

                                    } else {

                                        try {
                                            if (user.isEmailVerified()) {
                                                Log.d(TAG, "onComplete: success.email is verified");
                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(mContext, "Email is not verified. Please check your mailbox", Toast.LENGTH_SHORT).show();
                                                mProgressBar.setVisibility(View.GONE);
                                                mPleaseWait.setVisibility(View.GONE);
                                                mAuth.signOut();
                                            }
                                        } catch (NullPointerException e) {
                                            Log.e(TAG, "onComplete: NullPointerException" + e.getMessage());
                                        }
                                    }

                                }
                            });

                }
            }
        });

        /*
        If the user is logged in then navigate to MainActivity
         */
        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Set up firebase auth object
     */
    private void setupFirebaseAuth() {

        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth");

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
