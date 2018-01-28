package com.prestonbui.glancesocial.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.prestonbui.glancesocial.BaseActivity;
import com.prestonbui.glancesocial.R;

import static com.prestonbui.glancesocial.R.id.progressBar;

/**
 * Created by phuongbui on 10/21/17.
 */

public class ResetPasswordActivity extends BaseActivity {


    private EditText inputEmail;

    private Button btnReset;

    private FirebaseAuth auth;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reset_password);

        inputEmail = findViewById(R.id.email);

        btnReset = findViewById(R.id.btn_reset_password);

        auth = FirebaseAuth.getInstance();


        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplication(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.sendPasswordResetEmail(email)

                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ResetPasswordActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);

                                    startActivity(intent);

                                } else {
                                    Toast.makeText(ResetPasswordActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            }
        });


    }

    @Override
    protected void performOnCreate(Bundle state) {

    }
}
