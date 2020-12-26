package com.kangtech.tauonremote.view;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager2.widget.ViewPager2;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.kangtech.tauonremote.R;
import com.kangtech.tauonremote.adapter.MainTabAdapter;
import com.kangtech.tauonremote.api.ApiServiceInterface;
import com.kangtech.tauonremote.model.status.StatusModel;
import com.kangtech.tauonremote.model.track.TrackModel;
import com.kangtech.tauonremote.util.Server;
import com.kangtech.tauonremote.util.SharedPreferencesUtils;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private BottomSheetBehavior bottomSheetBehavior;
    private CoordinatorLayout nowplaying_sheet;
    private LinearLayout ll_nowplayingMini;

    private ApiServiceInterface apiServiceInterface;

    private TextView tvArtist, tvTtitle, tvArtistMini;

    private ImageView ivCoverMini, ivCover;

    private String getArtist, getPlaylistId, getTitle;
    private int getTrackId, getPosition, getProgress;
    private int getTrackIdTemp = 0;

    private SharedPreferences.Editor editor;
    private int trackIDtemp;

    private ProgressBar progressBarMini;
    private int getDuration;

    private SeekBar seekBar;
    private TextView tvSeekBar;
    private String getStatus;
    private Boolean getShuffle;
    private Boolean getRepeat;
    private int valueProgress;

    private AppBarConfiguration mAppBarConfiguration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_album, R.id.nav_playlist, R.id.nav_track)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        apiServiceInterface = Server.getApiServiceInterface();

        nowplaying_sheet = findViewById(R.id.nowplaying_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(nowplaying_sheet);
        ll_nowplayingMini = findViewById(R.id.ll_nowplaying_mini);


        tvArtist = findViewById(R.id.tv_np_artist); /*for runninf text*/ tvArtist.setSelected(true);
        tvTtitle = findViewById(R.id.tv_np_title); /*for runninf text*/ tvTtitle.setSelected(true);
        ivCover = findViewById(R.id.iv_cover_full);
        seekBar = findViewById(R.id.seekBar);

        tvArtistMini = findViewById(R.id.tv_artis_title_mini); /*for runninf text*/ tvArtistMini.setSelected(true);
        ivCoverMini = findViewById(R.id.iv_cover_mini);
        progressBarMini = findViewById(R.id.pb_nowplaying);
        tvSeekBar = findViewById(R.id.tv_seekbar);


        //trackIDtemp = SharedPreferencesUtils.getInt("trackID", -1);

        runStatus();

        next();
        prev();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                valueProgress = (int) (progress / (double) getDuration * 1000);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                apiServiceInterface.seek1k(valueProgress)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();
            }
        });

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

        ivCoverMini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void next() {
        ImageView ivNext = findViewById(R.id.iv_next);
        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextRequest();
                statusInit();
            }
        });

        // Mini
        ImageView ivNextMini = findViewById(R.id.iv_next_mini);
        ivNextMini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextRequest();
                statusInit();
            }
        });
    }
    private void nextRequest() {
        apiServiceInterface.next()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    private void prev() {
        ImageView ivPrev = findViewById(R.id.iv_prev);
        ivPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevRequest();
                statusInit();
            }
        });

        // Mini
        ImageView ivPrevMini = findViewById(R.id.iv_prev_mini);
        ivPrevMini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevRequest();
                statusInit();
            }
        });
    }
    private void prevRequest() {
        apiServiceInterface.back()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
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
                        getStatus = statusModel.status;
                        getTrackId = statusModel.id;
                        getProgress = statusModel.progress;
                        getPlaylistId = statusModel.playlist;
                        getPosition = statusModel.position;
                        getShuffle = statusModel.shuffle;
                        getRepeat = statusModel.repeat;
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                        if (getProgress <= 0) {
                            trackInit(getPlaylistId, getPosition);
                        }
                        if (tvArtistMini.length() == 0) {
                            trackInit(getPlaylistId, getPosition);
                            Log.e("TextView", String.valueOf(tvArtistMini.length()));
                        }
                        //set ProgressBar at Mini
                        progressBarMini.setProgress(getProgress);
                        //set SeekBar at full
                        seekBar.setProgress(getProgress);
                        @SuppressLint("DefaultLocale") String progressTime = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes((long) getProgress), TimeUnit.MILLISECONDS.toSeconds((long) getProgress) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) getProgress)));
                        tvSeekBar.setText(progressTime);

                        switch (getStatus) {
                            case "playing" :
                                pause();
                                break;
                            case "paused" :
                            case "stopped" :
                                play();
                                break;
                            default:
                                break;
                        }

                        ShuffleInit();
                        RepeatInit();

                    }
                });
    }

    private void RepeatInit() {
        if (getRepeat) {
            ImageView ivRepeat = findViewById(R.id.iv_repeat);
            ImageViewCompat.setImageTintList(ivRepeat, ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.rose_icon_true)));
            ivRepeat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    apiServiceInterface.repeat()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();
                    ImageViewCompat.setImageTintList(ivRepeat, ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.rose_icon_false)));
                }
            });
        } else {
            ImageView ivRepeat = findViewById(R.id.iv_repeat);
            ImageViewCompat.setImageTintList(ivRepeat, ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.rose_icon_false)));
            ivRepeat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    apiServiceInterface.repeat()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();
                    ImageViewCompat.setImageTintList(ivRepeat, ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.rose_icon_true)));
                }
            });
        }
    }

    private void ShuffleInit() {
        if (getShuffle) {
            ImageView ivShuffle = findViewById(R.id.iv_shuffle);
            ImageViewCompat.setImageTintList(ivShuffle, ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.rose_icon_true)));
            ivShuffle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    apiServiceInterface.shuffle()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();
                    ImageViewCompat.setImageTintList(ivShuffle, ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.rose_icon_false)));
                }
            });
        } else {
            ImageView ivShuffle = findViewById(R.id.iv_shuffle);
            ImageViewCompat.setImageTintList(ivShuffle, ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.rose_icon_false)));
            ivShuffle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    apiServiceInterface.shuffle()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();
                    ImageViewCompat.setImageTintList(ivShuffle, ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.rose_icon_true)));
                }
            });
        }
    }

    private void play() {
        ImageView ivPlay = findViewById(R.id.iv_play);
        ivPlay.setImageResource(R.drawable.ic_round_play_circle_24);

        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPlay();
            }

        });

        // mini
        ImageView ivPlayMini = findViewById(R.id.iv_play_mini);
        ivPlayMini.setImageResource(R.drawable.ic_round_play_circle_24);

        ivPlayMini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPlay();
            }
        });
    }
    private void requestPlay() {
        apiServiceInterface.play()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    private void pause() {
        ImageView ivPlay = findViewById(R.id.iv_play);
        ivPlay.setImageResource(R.drawable.ic_round_pause_circle_24);

        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPause();
            }
        });

        // mini
        ImageView ivPlayMini = findViewById(R.id.iv_play_mini);
        ivPlayMini.setImageResource(R.drawable.ic_round_pause_circle_24);

        ivPlayMini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPause();
            }
        });
    }
    private void requestPause() {
        apiServiceInterface.pause()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }


    private void trackInit(String playlist, int position) {
        apiServiceInterface.getTrack(playlist, position)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TrackModel>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull TrackModel trackModel) {
                        getArtist = trackModel.artist;
                        getTitle = trackModel.title;
                        getDuration = trackModel.duration;
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete() {
                        @SuppressLint("ResourceType") String artistColor = "<font color=#" + getResources().getString(R.color.rose_text_artist).substring(3) + ">" + getArtist + "</font>";
                        @SuppressLint("ResourceType") String titleColor = "<font color=#" + getResources().getString(R.color.rose_text_title).substring(3) + ">" + getTitle + "</font>";
                        tvArtistMini.setText(Html.fromHtml(artistColor + " " + titleColor, Html.FROM_HTML_MODE_LEGACY));

                        Glide.with(getApplicationContext())
                                .load("http://192.168.43.150:7814/api1/pic/medium/" + getTrackId)
                                .centerCrop()
                                .placeholder(R.drawable.ic_round_music_note_24)
                                .into(ivCoverMini);

                        // set Max progressbar
                        progressBarMini.setMax(getDuration);
                        // set Max seekbar
                        seekBar.setMax(getDuration);

                        // set at Full Now Playing
                        tvArtist.setText(getArtist);
                        tvTtitle.setText(getTitle);
                        Glide.with(getApplicationContext())
                                .load("http://192.168.43.150:7814/api1/pic/medium/" + getTrackId)
                                .centerCrop()
                                .placeholder(R.drawable.ic_round_music_note_24)
                                .into(ivCover);
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