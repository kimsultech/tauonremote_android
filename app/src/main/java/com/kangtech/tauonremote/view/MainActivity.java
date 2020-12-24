package com.kangtech.tauonremote.view;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.kangtech.tauonremote.R;
import com.kangtech.tauonremote.adapter.MainTabAdapter;
import com.kangtech.tauonremote.api.ApiServiceInterface;
import com.kangtech.tauonremote.model.status.StatusModel;
import com.kangtech.tauonremote.util.Server;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private BottomSheetBehavior bottomSheetBehavior;
    private CoordinatorLayout nowplaying_sheet;
    private LinearLayout ll_nowplayingMini;

    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    private ApiServiceInterface apiServiceInterface;

    private TextView tvNpCArtist;

    private String getArtist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiServiceInterface = Server.getApiServiceInterface();

        nowplaying_sheet = findViewById(R.id.nowplaying_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(nowplaying_sheet);
        ll_nowplayingMini = findViewById(R.id.ll_nowplaying_mini);

        viewPager = findViewById(R.id.pager);
        tabLayout = findViewById(R.id.tab_layout);

        tvNpCArtist = findViewById(R.id.tv_np_cover_artist);

        runStatus();


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
                        switch (position){
                            case 0:
                                 tab.setText("PLAYLISTS");
                                break;
                            case 1:
                                tab.setText("ALBUMS");
                                break;
                            case 2:
                                tab.setText("TRACKS");
                                break;
                        }
                    }
                }).attach();

    }

    private void runStatus() {
        int delay = 1000; // 0,5 detik
        new Handler().postDelayed(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                statusInit();

                runStatus();
            }
        },delay);
    }

    private void statusInit() {
        apiServiceInterface.getStatus()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<StatusModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull StatusModel statusModel) {
                        getArtist = statusModel.artist;
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                        tvNpCArtist.setText(getArtist);
                    }
                });
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