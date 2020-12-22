package com.kangtech.tauonmusicremote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.kangtech.tauonmusicremote.adapter.MainTabAdapter;

public class MainActivity extends AppCompatActivity {

    private BottomSheetBehavior bottomSheetBehavior;
    private CoordinatorLayout nowplaying_sheet;
    private LinearLayout ll_nowplayingMini;

    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nowplaying_sheet = findViewById(R.id.nowplaying_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(nowplaying_sheet);
        ll_nowplayingMini = findViewById(R.id.ll_nowplaying_mini);

        viewPager = findViewById(R.id.pager);
        tabLayout = findViewById(R.id.tab_layout);


        // callback for do something
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        collapse_NowPlayingMini();
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        // add if for fix always expand before state expanded
                        if (ll_nowplayingMini.getVisibility() != View.VISIBLE) {
                            expand_NowPlayingMini();
                        }
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        viewPager.setAdapter(createMainTabAdapter());
        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setText("Tab " + (position + 1));
                    }
                }).attach();

    }

    private MainTabAdapter createMainTabAdapter() {
        MainTabAdapter adapter = new MainTabAdapter(this);
        return adapter;
    }

    private void expand_NowPlayingMini()
    {
        ll_nowplayingMini.setVisibility(View.VISIBLE);

        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        ll_nowplayingMini.measure(widthSpec, heightSpec);

        ValueAnimator mAnimator = slideAnimator(0, ll_nowplayingMini.getMeasuredHeight());
        mAnimator.start();
    }

    private void collapse_NowPlayingMini() {
        int finalHeight = ll_nowplayingMini.getHeight();

        ValueAnimator mAnimator = slideAnimator(finalHeight, 0);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                //Height = 0, but it set visibility to GONE
                ll_nowplayingMini.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

        });
        mAnimator.start();
    }

    private ValueAnimator slideAnimator(int start, int end)
    {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = ll_nowplayingMini.getLayoutParams();
                layoutParams.height = value;
                ll_nowplayingMini.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }
}