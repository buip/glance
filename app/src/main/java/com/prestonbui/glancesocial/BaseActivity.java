package com.prestonbui.glancesocial;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by phuongbui on 11/3/17.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        performOnCreate(state);
    }

    protected abstract void performOnCreate(Bundle state);

}
