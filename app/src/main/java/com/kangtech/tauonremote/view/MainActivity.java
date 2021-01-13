package com.kangtech.tauonremote.view;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager2.widget.ViewPager2;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
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
import com.kangtech.tauonremote.BuildConfig;
import com.kangtech.tauonremote.R;
import com.kangtech.tauonremote.adapter.ExpandableListAdapter;
import com.kangtech.tauonremote.adapter.MainTabAdapter;
import com.kangtech.tauonremote.api.ApiServiceInterface;
import com.kangtech.tauonremote.model.lyrics.LyricsModel;
import com.kangtech.tauonremote.model.playlist.PlaylistData;
import com.kangtech.tauonremote.model.playlist.PlaylistModel;
import com.kangtech.tauonremote.model.status.StatusModel;
import com.kangtech.tauonremote.model.track.TrackModel;
import com.kangtech.tauonremote.util.Server;
import com.kangtech.tauonremote.util.SharedPreferencesUtils;
import com.kangtech.tauonremote.view.fragment.track.TrackFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.zip.Deflater;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {

    public static BottomSheetBehavior bottomSheetBehavior;
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
    Toolbar toolbar;
    public static DrawerLayout drawer;

    ExpandableListAdapter expandableListAdapter;
    ExpandableListView expandableListView;
    private List<String> listDataHeader;
    private HashMap<String, List<PlaylistModel>> listdataChild;

    private LinearLayout llMenuAlbum, llMenuTrack;

    private LinearLayout llVolume;
    private ImageView ivVolume;
    private SeekBar seekBarVolume;
    private int getVolume;
    private int valueProgressVol;

    private TextView tv_lyrics;
    private ConstraintLayout cl_lyrics, cl_cover;
    private ImageView iv_lyrics;
    private Boolean getHasLyrics;
    private String getLyrics;

    FragmentManager fm = getSupportFragmentManager();
    public static NavController navController;

    private TextView tvHeaderIP, tvHeaderVersion;
    private ImageView ivHeaderSettings;
    private int getInc;

    private ImageView ivCollapsed;
    public static MainActivity runStatus;
    public static Handler handler;
    private static Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        runStatus = this;

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_album, R.id.nav_playlist, R.id.nav_track)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        View header = navigationView.getHeaderView(0);


        apiServiceInterface = Server.getApiServiceInterface();


        nowplaying_sheet = findViewById(R.id.nowplaying_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(nowplaying_sheet);
        ll_nowplayingMini = findViewById(R.id.ll_nowplaying_mini);
        tvArtist = findViewById(R.id.tv_np_artist);  tvArtist.setSelected(true);
        tvTtitle = findViewById(R.id.tv_np_title);  tvTtitle.setSelected(true);
        ivCover = findViewById(R.id.iv_cover_full);
        seekBar = findViewById(R.id.seekBar);
        tvArtistMini = findViewById(R.id.tv_artis_title_mini);  tvArtistMini.setSelected(true);
        ivCoverMini = findViewById(R.id.iv_cover_mini);
        progressBarMini = findViewById(R.id.pb_nowplaying);
        tvSeekBar = findViewById(R.id.tv_seekbar);
        llMenuAlbum = findViewById(R.id.ll_c_album);
        llMenuTrack = findViewById(R.id.ll_c_track);
        llVolume = findViewById(R.id.ll_volume);
        ivVolume = findViewById(R.id.iv_volume);
        seekBarVolume = findViewById(R.id.seekBar_vol);
        cl_lyrics = findViewById(R.id.cl_cover_lyric);
        cl_cover = findViewById(R.id.cl_cover);
        tv_lyrics = findViewById(R.id.tv_lyric);
        iv_lyrics = findViewById(R.id.iv_lyric);
        tvHeaderIP = header.findViewById(R.id.tv_header_ip);
        tvHeaderVersion = header.findViewById(R.id.tv_header_version);
        ivHeaderSettings = header.findViewById(R.id.iv_header_settings);
        ivCollapsed = findViewById(R.id.iv_collapsed);

        expandableListView = findViewById(R.id.expandableListView);

        listDataHeader = new ArrayList<String>();
        listdataChild = new HashMap<String, List<PlaylistModel>>();

        prepareMenuData();


        //trackIDtemp = SharedPreferencesUtils.getInt("trackID", -1);

        runStatus();

        next();
        prev();

        editor = getSharedPreferences("tauon_remote", MODE_PRIVATE).edit();
        editor.putString("titleToolbar", "Now Playing");
        editor.putInt("TrackID", -1);
        editor.putInt("TrackPosition", getPosition);
        editor.putInt("Inc", -1);
        editor.apply();


        tvHeaderIP.setText(SharedPreferencesUtils.getString("ip", "127.0.0.1"));
        tvHeaderVersion.setText("v" + BuildConfig.VERSION_NAME);

        ivHeaderSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });


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
                        .subscribe(new Observer<ResponseBody>() {
                            @Override
                            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                            }

                            @Override
                            public void onNext(@io.reactivex.annotations.NonNull ResponseBody responseBody) {

                            }

                            @Override
                            public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        });

        seekBarVolume.setMax(100);
        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                valueProgressVol = progress;
                seekBar.setThumb(getThumb(progress));
                if (progress == 0) {
                    ivVolume.setImageResource(R.drawable.ic_round_volume_off_24);
                } else {
                    ivVolume.setImageResource(R.drawable.ic_round_volume_up_24);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                apiServiceInterface.setvolume(valueProgressVol)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<ResponseBody>() {
                            @Override
                            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                            }

                            @Override
                            public void onNext(@io.reactivex.annotations.NonNull ResponseBody responseBody) {

                            }

                            @Override
                            public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });
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

                        if (SharedPreferencesUtils.getString("titleToolbar", "").equals("Now Playing")) {
                            saveTitleToolbar();
                        }
                        toolbar.setTitle("Now Playing");

                        if (Objects.requireNonNull(navController.getCurrentDestination()).getId() == R.id.nav_track) {
                            TrackFragment.hideSearch();
                        }

                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        // add if for fix always expand before state expanded
                        if (ll_nowplayingMini.getVisibility() != View.VISIBLE) {
                            expand_NowPlayingMini();

                            toolbar.setTitle(SharedPreferencesUtils.getString("titleToolbar", ""));
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

        ivCollapsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        ivCoverMini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        llMenuAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.nav_album);
                drawer.closeDrawer(GravityCompat.START);
                editor = getSharedPreferences("tauon_remote", MODE_PRIVATE).edit();
                editor.putString("titleToolbar", "Album");
                editor.apply();

                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        llMenuTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("FROM_MENU_LIST_TRACK", false);
                navController.navigate(R.id.nav_track, bundle);

                drawer.closeDrawer(GravityCompat.START);
                editor = getSharedPreferences("tauon_remote", MODE_PRIVATE).edit();
                editor.putString("titleToolbar", "Track");
                editor.putString("playlistID", getPlaylistId);
                editor.apply();

                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        ivVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llVolume.getVisibility() == View.VISIBLE) {
                    collapse_Volume();
                } else {
                    expand_Volume();
                }
            }
        });

    }


    public Drawable getThumb(int progress) {
        View thumbView = LayoutInflater.from(this).inflate(R.layout.seekbar_thumb, null, false);

        ((TextView) thumbView.findViewById(R.id.tv_seekbar_vol)).setText(String.valueOf(progress));

        thumbView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Bitmap bitmap = Bitmap.createBitmap(thumbView.getMeasuredWidth(), thumbView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        thumbView.layout(0, 0, thumbView.getMeasuredWidth(), thumbView.getMeasuredHeight());
        thumbView.draw(canvas);

        return new BitmapDrawable(getResources(), bitmap);
    }

    private void prepareMenuData() {
        apiServiceInterface.getPlaylist()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PlaylistModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull PlaylistModel playlistModel) {

                        listDataHeader.add("Playlist");

                        List<PlaylistModel> nowShowing = new ArrayList<>();
                        nowShowing.add(playlistModel);

                        listdataChild.put(listDataHeader.get(0), nowShowing);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                        populateExpandableList();
                    }
                });
    }

    private void populateExpandableList() {

        expandableListAdapter = new ExpandableListAdapter(this, listDataHeader, listdataChild);
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                /*if (headerList.get(groupPosition).isGroup) {
                    if (!headerList.get(groupPosition).hasChildren) {
                        onBackPressed();
                    }
                }*/

                return false;
            }
        });
        /*
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Toast.makeText(MainActivity.this, "" + childPosition, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        */
    }

    private void saveTitleToolbar() {
        editor = getSharedPreferences("tauon_remote", MODE_PRIVATE).edit();
        editor.putString("titleToolbar", toolbar.getTitle().toString());
        editor.apply();
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
                //statusInit();
            }
        });

        // Mini
        ImageView ivNextMini = findViewById(R.id.iv_next_mini);
        ivNextMini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextRequest();
                //statusInit();
            }
        });
    }
    private void nextRequest() {
        apiServiceInterface.next()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull ResponseBody responseBody) {

                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void prev() {
        ImageView ivPrev = findViewById(R.id.iv_prev);
        ivPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevRequest();
                //statusInit();
            }
        });

        // Mini
        ImageView ivPrevMini = findViewById(R.id.iv_prev_mini);
        ivPrevMini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevRequest();
                //statusInit();
            }
        });
    }
    private void prevRequest() {
        apiServiceInterface.back()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull ResponseBody responseBody) {

                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void runStatus() {
        int delay = 1000; // 0,5 detik
        handler = new Handler();
        runnable = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                statusInit();

                runStatus();
            }
        };

        handler.postDelayed(runnable, delay);
        Log.e("aa", Server.BASE_URL);
    }

    public static void stopStatus() {
        handler.removeCallbacks(runnable);
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
                        getVolume = statusModel.volume;
                        getHasLyrics = statusModel.track.hasLyrics;
                        getInc = statusModel.inc;
                    }

                    @Override
                    public void onError(Throwable e) {
                        //Toast.makeText(MainActivity.this, "Check your IP", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                        if (SharedPreferencesUtils.getInt("TrackID", -1) != getTrackId) {
                            trackInit(getPlaylistId, getPosition);

                            if (Objects.requireNonNull(navController.getCurrentDestination()).getId() == R.id.nav_track) {
                                TrackFragment.reqUpdate(getApplicationContext(), getPosition);
                            }


                            int delay = 500; // 2 detik
                            new Handler().postDelayed(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public void run() {
                                    editor = getSharedPreferences("tauon_remote", MODE_PRIVATE).edit();
                                    editor.putInt("TrackID", getTrackId);
                                    editor.putString("playlistID", getPlaylistId);
                                    editor.putInt("TrackPosition", getPosition);
                                    editor.apply();

                                }
                            },delay);
                        }

                        if (SharedPreferencesUtils.getInt("Inc", -1) != getInc) {
                            Toast.makeText(MainActivity.this, "Auto Reload", Toast.LENGTH_SHORT).show();

                            // reload Menu Playlist in Drawer
                            listDataHeader.clear();
                            prepareMenuData();

                            // clear Glide Cache
                            Glide.get(getApplicationContext()).clearMemory();

                            // reload Now Playing
                            trackInit(getPlaylistId, getPosition);

                            // reload Track
                            if (Objects.requireNonNull(navController.getCurrentDestination()).getId() == R.id.nav_track) {
                                Bundle bundle = new Bundle();
                                bundle.putBoolean("FROM_MENU_LIST_TRACK", false);
                                navController.navigate(R.id.nav_track, bundle);
                            }

                            int delay = 500; // 2 detik
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    editor = getSharedPreferences("tauon_remote", MODE_PRIVATE).edit();
                                    editor.putInt("Inc", getInc);
                                    editor.apply();
                                }
                            },delay);
                        }


                        if (tvArtistMini.length() == 0) {
                            trackInit(getPlaylistId, getPosition);
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

                        seekBarVolume.setProgress(getVolume);
                        if (getVolume == 0) {
                            ivVolume.setImageResource(R.drawable.ic_round_volume_off_24);
                        } else {
                            ivVolume.setImageResource(R.drawable.ic_round_volume_up_24);
                        }

                        if (getHasLyrics) {
                            ImageViewCompat.setImageTintList(iv_lyrics, ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.rose_icon_true)));

                            iv_lyrics.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    cl_cover.setVisibility(View.GONE);
                                    cl_lyrics.setVisibility(View.VISIBLE);
                                    iv_lyrics.setImageResource(R.drawable.ic_round_album_24);
                                }
                            });

                            if (cl_lyrics.getVisibility() == View.VISIBLE) {
                                iv_lyrics.setImageResource(R.drawable.ic_round_album_24);

                                iv_lyrics.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        cl_cover.setVisibility(View.VISIBLE);
                                        cl_lyrics.setVisibility(View.GONE);
                                        iv_lyrics.setImageResource(R.drawable.ic_round_lyric_24);
                                    }
                                });
                            }

                        } else {
                            ImageViewCompat.setImageTintList(iv_lyrics, ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.rose_icon_false)));

                            cl_cover.setVisibility(View.VISIBLE);
                            cl_lyrics.setVisibility(View.GONE);

                            iv_lyrics.setImageResource(R.drawable.ic_round_lyric_24);

                            iv_lyrics.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(MainActivity.this, "This song not have Lyrics", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }


                        if (listDataHeader.isEmpty()) {
                            //prepareMenuData();
                        }

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
                            .subscribe(new Observer<ResponseBody>() {
                                @Override
                                public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                                }

                                @Override
                                public void onNext(@io.reactivex.annotations.NonNull ResponseBody responseBody) {

                                }

                                @Override
                                public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                                }

                                @Override
                                public void onComplete() {

                                }
                            });

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
                            .subscribe(new Observer<ResponseBody>() {
                                @Override
                                public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                                }

                                @Override
                                public void onNext(@io.reactivex.annotations.NonNull ResponseBody responseBody) {

                                }

                                @Override
                                public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                                }

                                @Override
                                public void onComplete() {

                                }
                            });
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
                            .subscribe(new Observer<ResponseBody>() {
                                @Override
                                public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                                }

                                @Override
                                public void onNext(@io.reactivex.annotations.NonNull ResponseBody responseBody) {

                                }

                                @Override
                                public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                                }

                                @Override
                                public void onComplete() {

                                }
                            });

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
                            .subscribe(new Observer<ResponseBody>() {
                                @Override
                                public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                                }

                                @Override
                                public void onNext(@io.reactivex.annotations.NonNull ResponseBody responseBody) {

                                }

                                @Override
                                public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                                }

                                @Override
                                public void onComplete() {

                                }
                            });

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
                ivPlay.setImageResource(R.drawable.ic_round_pause_circle_24);
            }

        });

        // mini
        ImageView ivPlayMini = findViewById(R.id.iv_play_mini);
        ivPlayMini.setImageResource(R.drawable.ic_round_play_circle_24);

        ivPlayMini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPlay();
                ivPlayMini.setImageResource(R.drawable.ic_round_pause_circle_24);
            }
        });
    }
    private void requestPlay() {
        apiServiceInterface.play()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull ResponseBody responseBody) {

                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void pause() {
        ImageView ivPlay = findViewById(R.id.iv_play);
        ivPlay.setImageResource(R.drawable.ic_round_pause_circle_24);

        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPause();
                ivPlay.setImageResource(R.drawable.ic_round_play_circle_24);
            }
        });

        // mini
        ImageView ivPlayMini = findViewById(R.id.iv_play_mini);
        ivPlayMini.setImageResource(R.drawable.ic_round_pause_circle_24);

        ivPlayMini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPause();
                ivPlayMini.setImageResource(R.drawable.ic_round_play_circle_24);
            }
        });
    }
    private void requestPause() {
        apiServiceInterface.pause()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull ResponseBody responseBody) {

                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
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
                                .load("http://" + SharedPreferencesUtils.getString("ip", "127.0.0.1") + ":7814/api1/pic/medium/" + getTrackId)
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
                                .load("http://" + SharedPreferencesUtils.getString("ip", "127.0.0.1") + ":7814/api1/pic/medium/" + getTrackId)
                                .centerCrop()
                                .placeholder(R.drawable.ic_round_music_note_24)
                                .into(ivCover);

                        lyricsInit(getTrackId);


                    }
                });

    }

    private void lyricsInit(int trackID) {
        apiServiceInterface.getLyrics(trackID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LyricsModel>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull LyricsModel lyricsModel) {
                        getLyrics = lyricsModel.lyricsText;
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        tv_lyrics.setText(getLyrics);
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

    private void expand_Volume()
    {
        llVolume.setVisibility(View.VISIBLE);

        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        llVolume.measure(widthSpec, heightSpec);

        ValueAnimator mAnimator = slideAnimator2(0, llVolume.getMeasuredWidth());
        mAnimator.start();
    }

    private void collapse_Volume() {
        int finalHeight = llVolume.getWidth();

        ValueAnimator mAnimator = slideAnimator2(finalHeight, 0);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                //Height = 0, but it set visibility to GONE
                llVolume.setVisibility(View.GONE);
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

    private ValueAnimator slideAnimator2(int start, int end)
    {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = llVolume.getLayoutParams();
                layoutParams.width = value;
                llVolume.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
/*        if (Objects.requireNonNull(navController.getCurrentDestination()).getId() == R.id.nav_track) {
            if (MainActivity.bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                menu.findItem(R.id.menu_search_track).setVisible(false);
            } else {
                menu.findItem(R.id.menu_search_track).setVisible(true);
            }
        }*/

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

}