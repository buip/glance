package com.prestonbui.glancesocial.view;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;


import com.prestonbui.glancesocial.R;

/**
 * Created by phuongbui on 10/15/17.
 */

public class BottomTabsView extends FrameLayout implements ViewPager.OnPageChangeListener {

    private ImageView mStartImage;
    private ImageView mCenterImage;
    private ImageView mEndImage;

    private View mIndicator;

    private ArgbEvaluator mArgbEvaluator;
    private int mCenterColor;
    private int mSideColor;

    private int mEndViewTranslationX;
    private int mIndicatorTranslationX;


    public BottomTabsView(@NonNull Context context) {
        this(context, null);
    }

    public BottomTabsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomTabsView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setUpWithViewPager (final ViewPager viewPager) {
        viewPager.addOnPageChangeListener(this);

        mStartImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewPager.getCurrentItem() != 0) {
                    viewPager.setCurrentItem(0);
                }
            }
        });

        mCenterImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewPager.getCurrentItem() != 1) {
                    viewPager.setCurrentItem(1);
                }
            }
        });

        mEndImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewPager.getCurrentItem() != 2) {
                    viewPager.setCurrentItem(2);
                }
            }
        });
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.tabs_view_bottom, this, true);

        mStartImage = findViewById(R.id.bottom_profile_button);
        mCenterImage = findViewById(R.id.bottom_feeds_buttom);
        mEndImage = findViewById(R.id.bottom_community_button);
        mIndicator = findViewById(R.id.bottom_indicator);

        mCenterColor = ContextCompat.getColor(getContext(), R.color.dark_blue);
        mSideColor = ContextCompat.getColor(getContext(), R.color.dark_blue);

        mArgbEvaluator = new ArgbEvaluator();

        mIndicatorTranslationX = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());

        mCenterImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mEndViewTranslationX = (int) ((mCenterImage.getX() - mStartImage.getX()) - mIndicatorTranslationX);

                mCenterImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }

        });

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (position == 0) {
            setColor(1 - positionOffset);
            moveViews(1 - positionOffset);
            mIndicator.setTranslationX((positionOffset - 1) * mIndicatorTranslationX);
        } else if (position == 1) {
            setColor(positionOffset);
            moveViews(positionOffset);
            mIndicator.setTranslationX(positionOffset * mIndicatorTranslationX);
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void moveViews(float fractionFromCenter) {
        mStartImage.setTranslationX(fractionFromCenter * mEndViewTranslationX);
        mEndImage.setTranslationX(-fractionFromCenter * mEndViewTranslationX);

    }

    private void setColor(float fractionFromCenter) {
        int color = (int) mArgbEvaluator.evaluate(fractionFromCenter, mCenterColor, mSideColor);

        mStartImage.setColorFilter(color);
        mCenterImage.setColorFilter(color);
        mEndImage.setColorFilter(color);

    }

}
