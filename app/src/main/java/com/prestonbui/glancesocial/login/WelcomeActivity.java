package com.prestonbui.glancesocial.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.prestonbui.glancesocial.BaseActivity;
import com.prestonbui.glancesocial.R;

/**
 * Created by phuongbui on 10/15/17.
 */

public class WelcomeActivity extends BaseActivity {

    private static final String TAG = "WelcomeActivity";

    private Button btnSignUp;

    private TextView linkSignIn;

    private Context mContext = WelcomeActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Log.d(TAG, "onCreate: started");

        btnSignUp = findViewById(R.id.btn_signup_welcome);

        linkSignIn = findViewById(R.id.link_login);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Sign Up");

                Intent intent = new Intent(mContext, SignupActivity.class);

                startActivity(intent);
            }
        });

        linkSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Sign Up");

                Intent intent = new Intent(mContext, LoginActivity.class);

                startActivity(intent);
            }
        });


    }

    @Override
    protected void performOnCreate(Bundle state) {

    }
}
