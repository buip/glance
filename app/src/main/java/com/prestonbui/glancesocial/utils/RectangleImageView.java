package com.prestonbui.glancesocial.utils;


import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Created by phuongbui on 10/19/17.
 */


public class RectangleImageView extends AppCompatImageView {

    public RectangleImageView(Context context) {
        super(context);
    }

    public RectangleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RectangleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);

//        int width = getMeasuredWidth();

        //force a 16:9 aspect ratio
//        int height = Math.round(width * .5625f);
//        setMeasuredDimension(width, height);
    }
}
