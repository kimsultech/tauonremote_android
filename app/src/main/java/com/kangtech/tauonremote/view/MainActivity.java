package com.kangtech.tauonremote.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.huhx0015.hxaudio.audio.HXMusic;
import com.huhx0015.hxaudio.interfaces.HXMusicListener;
import com.huhx0015.hxaudio.model.HXMusicItem;
import com.kangtech.tauonremote.BuildConfig;
import com.kangtech.tauonremote.R;
import com.kangtech.tauonremote.adapter.ExpandableListAdapter;
import com.kangtech.tauonremote.adapter.TrackListAdapter;
import com.kangtech.tauonremote.api.ApiServiceInterface;
import com.kangtech.tauonremote.model.lyrics.LyricsModel;
import com.kangtech.tauonremote.model.playlist.PlaylistModel;
import com.kangtech.tauonremote.model.status.StatusModel;
import com.kangtech.tauonremote.model.track.TrackListModel;
import com.kangtech.tauonremote.model.track.TrackModel;
import com.kangtech.tauonremote.util.Server;
import com.kangtech.tauonremote.util.SharedPreferencesUtils;
import com.kangtech.tauonremote.view.fragment.album.AlbumFragment;
import com.kangtech.tauonremote.view.fragment.track.TrackFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {

    public static BottomSheetBehavior bottomSheetBehavior;
    private CoordinatorLayout nowplaying_sheet;
    private LinearLayout ll_nowplayingMini;

    private static ApiServiceInterface apiServiceInterface;

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
    private Bitmap getBitmap;

    private SwitchMaterial smStream;

    private TrackListModel trackListModels;
    private int streamProgress;

    private int sTempPosition = -1;
    private String sTempPlaylist = "-1";
    private boolean getStreamShuffle = false;
    //all, one, off
    private String getStreamRepeat = "off";
    private List<TrackModel> temp_trackListModels;

    private static boolean getReqPrev = false;
    private static boolean getReqNext = false;
    private static boolean getReqPlayPause = false;

    private TextView tvNpIpFrom;
    private LinearLayout llNpIsStream;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        runStatus = this;

        Intent intent = getIntent();

        // If Java 8 lambdas are supported
        RxJavaPlugins.setErrorHandler(e -> { });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_album, R.id.nav_track)
                //.setDrawerLayout(drawer)
                .setOpenableLayout(drawer)
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
        smStream = findViewById(R.id.sm_stream);
        tvNpIpFrom = findViewById(R.id.tv_np_ip_from);
        llNpIsStream = findViewById(R.id.ll_np_isstream);

        expandableListView = findViewById(R.id.expandableListView);

        listDataHeader = new ArrayList<String>();
        listdataChild = new HashMap<String, List<PlaylistModel>>();

        prepareMenuData();


        runStatus();

        if (!getSharedPreferences("tauon_remote", MODE_PRIVATE).contains("is_stream_mode")) {
            editor = getSharedPreferences("tauon_remote", MODE_PRIVATE).edit();
            editor.putBoolean("is_stream_mode", false);
            editor.apply();
        }

        if (SharedPreferencesUtils.getBoolean("is_stream_mode", true)) {
            TrackListInit(SharedPreferencesUtils.getString("playlist_stream", "0"));

/*            if (HXMusic.getStatus().equals("READY")) {
                if (!HXMusic.isPlaying())
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        initRemoteClear();
                    }
            }*/
        }

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
                streamProgress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (!SharedPreferencesUtils.getBoolean("is_stream_mode", true)) {
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
                } else {
                    HXMusic.seekTo(streamProgress);
                }
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

                        if (Objects.requireNonNull(navController.getCurrentDestination()).getId() == R.id.nav_album) {
                            AlbumFragment.hideSearch();
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

        /*if (intent.getBooleanExtra("FROM_SERVICE", false)) {
            int delaythis = 500;
            new Handler().postDelayed(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void run() {

                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            },delaythis);
        }*/

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

        if (SharedPreferencesUtils.getBoolean("is_stream_mode", false)) {
            smStream.setChecked(true);
            Toast.makeText(this, "Stream Mode On", Toast.LENGTH_SHORT).show();
        } else {
            smStream.setChecked(false );
            Toast.makeText(this, "Stream Mode Off", Toast.LENGTH_SHORT).show();
        }
        smStream.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    if (SharedPreferencesUtils.getString("status", "stopped").equals("playing")) {
                        dialogStream();
                    } else {
                        editor = getSharedPreferences("tauon_remote", MODE_PRIVATE).edit();
                        editor.putBoolean("is_stream_mode", true);
                        editor.putString("playlist_stream", getPlaylistId);
                        editor.putInt("trackPosition_stream", -1);
                        editor.putInt("trackId_stream", -1);
                        editor.apply();

                        TrackListInit(SharedPreferencesUtils.getString("playlist_stream", "0"));
                        initRemoteClear();
                    }
                } else {
                    editor = getSharedPreferences("tauon_remote", MODE_PRIVATE).edit();
                    editor.putBoolean("is_stream_mode", false);
                    editor.apply();

                    HXMusic.stop();
                    HXMusic.clear();

                    trackInit(getPlaylistId, getPosition);
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

    private void initStream(int trackId) {
        HXMusic.music()
                .load("http://" + Server.BASE_URL + ":7814/api1/file/" + trackId)
                .looped(false)
                .play(this);
    }

    private void dialogStream() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        TrackListInit(SharedPreferencesUtils.getString("playlist_stream", "0"));

                        initRemoteClear();

                        requestPause();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        editor = getSharedPreferences("tauon_remote", MODE_PRIVATE).edit();
                        editor.putBoolean("is_stream_mode", true);
                        editor.putString("playlist_stream", getPlaylistId);
                        editor.putInt("trackPosition_stream", -1);
                        editor.putInt("trackId_stream", -1);
                        editor.apply();

                        TrackListInit(SharedPreferencesUtils.getString("playlist_stream", "0"));
                        initRemoteClear();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Pause currently playing on the Tauon Music Box (PC)?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).setCancelable(false).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initRemoteClear() {
        play();

        editor = getSharedPreferences("tauon_remote", MODE_PRIVATE).edit();
        editor.putBoolean("is_stream_mode", true);
        editor.putInt("trackPosition_stream", -1);
        editor.putInt("trackId_stream", -1);
        editor.apply();

        seekBar.setProgress(0);
        tvSeekBar.setText("00:00");
        progressBarMini.setProgress(0);
        ivCover.setImageResource(R.drawable.ic_round_music_note_24);
        ivCoverMini.setImageResource(R.drawable.ic_round_music_note_24);
        tvArtist.setText("");
        tvTtitle.setText("");
        tv_lyrics.setText("");

        @SuppressLint("ResourceType") String titleColor = "<font color=#" + getResources().getString(R.color.rose_text_title).substring(3) + ">" + "Select a Song from the Playlist" + "</font>";
        tvArtistMini.setText(Html.fromHtml(titleColor, Html.FROM_HTML_MODE_LEGACY));
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
                if (!SharedPreferencesUtils.getBoolean("is_stream_mode", true)) {
                    nextRequest();
                } else {
                    sNextSong();
                }

            }
        });

        // Mini
        ImageView ivNextMini = findViewById(R.id.iv_next_mini);
        ivNextMini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SharedPreferencesUtils.getBoolean("is_stream_mode", true)) {
                    nextRequest();
                } else {
                    sNextSong();
                }
            }
        });

        if (HXMusic.getStatus().equals("READY")) {
            ivNext.setEnabled(false);
            ivNextMini.setEnabled(false);
        } else {
            ivNext.setEnabled(true);
            ivNextMini.setEnabled(true);
        }
    }
    public static void nextRequest() {
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
                if (!SharedPreferencesUtils.getBoolean("is_stream_mode", true)) {
                    prevRequest();
                } else {
                    sPrevSong();
                }
            }
        });

        // Mini
        ImageView ivPrevMini = findViewById(R.id.iv_prev_mini);
        ivPrevMini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SharedPreferencesUtils.getBoolean("is_stream_mode", true)) {
                    prevRequest();
                } else {
                    sPrevSong();
                }
            }
        });

        if (HXMusic.getStatus().equals("READY")) {
            ivPrev.setEnabled(false);
            ivPrevMini.setEnabled(false);
        } else {
            ivPrev.setEnabled(true);
            ivPrevMini.setEnabled(true);
        }
    }
    public static void prevRequest() {
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
                // is Stream Mode false set REMOTE
                if (!SharedPreferencesUtils.getBoolean("is_stream_mode", true)){
                    statusInit();
                    llNpIsStream.setVisibility(View.GONE);
                } else {
                    if (HXMusic.instance() != null) {
                        streamStatusInit();
                        statusInit();

                        llNpIsStream.setVisibility(View.VISIBLE);
                        tvNpIpFrom.setText(SharedPreferencesUtils.getString("ip", "localhost"));
                    }
                }

                runStatus();
            }
        };

        handler.postDelayed(runnable, delay);
    }

    private void streamStatusInit() {

        Log.e("heh ", "int " + HXMusic.getStatus());

        switch (HXMusic.getStatus()) {
            case "PLAYING" :
            case "PAUSED" :
            case "STOPPED" :
                //set ProgressBar at Mini
                progressBarMini.setProgress((int) HXMusic.getCurrentProgress());
                //set SeekBar at full
                seekBar.setProgress((int) HXMusic.getCurrentProgress());
                @SuppressLint("DefaultLocale") String progressTime = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes((long) HXMusic.getCurrentProgress()), TimeUnit.MILLISECONDS.toSeconds((long) HXMusic.getCurrentProgress()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) HXMusic.getCurrentProgress())));
                tvSeekBar.setText(progressTime);
                break;
            /*case "READY" :
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    initRemoteClear();
                }
                break;*/
            default:
                break;
        }

        /*if (!HXMusic.isPlaying()) {
            HXMusic.resume(this);
            Toast.makeText(this, "Not Playing or Stopped", Toast.LENGTH_SHORT).show();
        }*/

        HXMusic.setListener(new HXMusicListener() {
            @Override
            public void onMusicPrepared(HXMusicItem music) {

            }

            @Override
            public void onMusicCompletion(HXMusicItem music) {
                if (SharedPreferencesUtils.getInt("trackPosition_stream", 0) == trackListModels.tracks.size() - 1) {
                    switch (getStreamRepeat) {
                        case "all":
                        case "one" :
                            sNextSong();
                            break;
                        case "off":
                            HXMusic.stop();
                            HXMusic.clear();
                            break;
                    }

                } else {
                    sNextSong();
                }
            }

            @Override
            public void onMusicBufferingUpdate(HXMusicItem music, int percent) {

            }

            @Override
            public void onMusicPause(HXMusicItem music) {

            }

            @Override
            public void onMusicStop(HXMusicItem music) {

            }
        });

        switch (HXMusic.getStatus()) {
            case "PLAYING" :
                pause();
                break;
            case "PAUSED" :
            case "STOPPED" :
            case "READY" :
                play();
                break;
            default:
                break;
        }

        if (!sTempPlaylist.equals(SharedPreferencesUtils.getString("playlist_stream", "-1"))) {

            if (trackListModels != null) {
                trackListModels.tracks.clear();
            }

            TrackListInit(SharedPreferencesUtils.getString("playlist_stream", "-1"));

            int delay1 = 500;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    sTempPlaylist = SharedPreferencesUtils.getString("playlist_stream", "-1");

                }
            },delay1);
        }

        if (sTempPosition != SharedPreferencesUtils.getInt("trackPosition_stream", -1)) {



            int delay1 = 500;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    sTempPosition = SharedPreferencesUtils.getInt("trackPosition_stream", -1);

                    trackInit(SharedPreferencesUtils.getString("playlist_stream", "0"), SharedPreferencesUtils.getInt("trackPosition_stream", -1));

                }
            },delay1);
        }

        getStreamRepeat = SharedPreferencesUtils.getString("stream_repeat", "off");
        getStreamShuffle = SharedPreferencesUtils.getBoolean("stream_shuffle", false);

        ShuffleInit();
        RepeatInit();

        next();
        prev();

        Log.e("cek ", " " + getReqPrev + " " + getReqNext);

        if (getReqPrev) {
            sPrevSong();
            getReqPrev = false;
        }

        if (getReqNext) {
            sNextSong();
            getReqNext = false;
        }

        if (getReqPlayPause) {
            switch (SharedPreferencesUtils.getString("sStatus", "ready")) {
                case "playing" :
                    sPause();
                    break;
                case "paused" :
                    sPlay();
                    break;
            }
            getReqPlayPause = false;
        }
    }

    public void sPrevSong() {
        if (HXMusic.isPlaying()) {
            HXMusic.stop();
        }

        Random random = new Random();
        int next;
        if (getStreamShuffle) {
            next = random.nextInt(trackListModels.tracks.size());
        } else {
            next = SharedPreferencesUtils.getInt("trackPosition_stream", 0) - 1;
        }

        if (next < 0) {
            //Toast.makeText(this, "Track is already at the end, back to the beginning", Toast.LENGTH_SHORT).show();
            initStream(trackListModels.tracks.get(trackListModels.tracks.size() - 1).id);
            trackInit(SharedPreferencesUtils.getString("playlist_stream", "0"), trackListModels.tracks.get(trackListModels.tracks.size() - 1).id);

            editor = getSharedPreferences("tauon_remote", MODE_PRIVATE).edit();
            editor.putInt("trackPosition_stream", trackListModels.tracks.size() - 1);
            editor.putInt("trackId_stream", trackListModels.tracks.get(trackListModels.tracks.size() - 1).id);
            editor.apply();
        } else {
            initStream(trackListModels.tracks.get(next).id);
            trackInit(SharedPreferencesUtils.getString("playlist_stream", "0"), trackListModels.tracks.get(next).position);

            editor = getSharedPreferences("tauon_remote", MODE_PRIVATE).edit();
            editor.putInt("trackPosition_stream", next);
            editor.putInt("trackId_stream", trackListModels.tracks.get(next).id);
            editor.apply();
        }

        TrackFragment.reqNotifyDataUpdate();

    }

    public void sNextSong() {
        if (HXMusic.isPlaying()) {
            HXMusic.stop();
        }

        Random random = new Random();
        int next;
        if (getStreamShuffle) {
            next = random.nextInt(trackListModels.tracks.size());
        } else {
            next = SharedPreferencesUtils.getInt("trackPosition_stream", 0) + 1;
        }

        if (next > trackListModels.tracks.size() - 1) {

            switch (getStreamRepeat) {
                case "all":
                case "one" :
                    initStream(trackListModels.tracks.get(0).id);
                    trackInit(SharedPreferencesUtils.getString("playlist_stream", "0"), trackListModels.tracks.get(0).position);

                    editor = getSharedPreferences("tauon_remote", MODE_PRIVATE).edit();
                    editor.putInt("trackPosition_stream", 0);
                    editor.putInt("trackId_stream", trackListModels.tracks.get(0).id);
                    editor.apply();

                    break;
                case "off":
                    initStream(trackListModels.tracks.get(trackListModels.tracks.size() - 1).id);
                    trackInit(SharedPreferencesUtils.getString("playlist_stream", "0"), trackListModels.tracks.get(trackListModels.tracks.size() - 1).position);

                    editor = getSharedPreferences("tauon_remote", MODE_PRIVATE).edit();
                    editor.putInt("trackPosition_stream", trackListModels.tracks.size() - 1);
                    editor.putInt("trackId_stream", trackListModels.tracks.get(trackListModels.tracks.size() - 1).id);
                    editor.apply();
                    break;
            }
        } else {
            initStream(trackListModels.tracks.get(next).id);
            trackInit(SharedPreferencesUtils.getString("playlist_stream", "0"), trackListModels.tracks.get(next).position);

            editor = getSharedPreferences("tauon_remote", MODE_PRIVATE).edit();
            editor.putInt("trackPosition_stream", next);
            editor.putInt("trackId_stream", trackListModels.tracks.get(next).id);
            editor.apply();
        }

        TrackFragment.reqNotifyDataUpdate();

    }

    public void sPlay() {
        editor = getSharedPreferences("tauon_remote", MODE_PRIVATE).edit();
        editor.putString("sStatus", "playing");
        editor.apply();

        HXMusic.resume(MainActivity.this);
    }

    public void sPause() {
        editor = getSharedPreferences("tauon_remote", MODE_PRIVATE).edit();
        editor.putString("sStatus", "paused");
        editor.apply();

        HXMusic.pause();
    }

    public void TrackListInit(String playlist) {
        apiServiceInterface.getTracklist(playlist)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TrackListModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull TrackListModel trackListModel) {
                        trackListModels = trackListModel;
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                        if (SharedPreferencesUtils.getBoolean("is_stream_mode", true)) {
                            if (HXMusic.getStatus().equals("READY")) {
                                if (!HXMusic.isPlaying())
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        initRemoteClear();
                                    }
                            } else {
                                trackInit(SharedPreferencesUtils.getString("playlist_stream", "0"), trackListModels.tracks.get(SharedPreferencesUtils.getInt("trackPosition_stream", -1)).id);
                            }
                            }
                    }
                });
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

                        int delay1 = 500;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                editor = getSharedPreferences("tauon_remote", MODE_PRIVATE).edit();
                                editor.putString("status", getStatus);
                                editor.apply();
                            }
                        },delay1);

                        // is Stream Mode false set REMOTE
                        if (!SharedPreferencesUtils.getBoolean("is_stream_mode", true)) {
                            if (SharedPreferencesUtils.getInt("TrackID", -1) != getTrackId) {
                            trackInit(getPlaylistId, getPosition);

                            if (Objects.requireNonNull(navController.getCurrentDestination()).getId() == R.id.nav_track) {
                                TrackFragment.reqUpdate(getApplicationContext(), getPosition);
                            }

                            int delay = 800;
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
                        }


                        if (SharedPreferencesUtils.getInt("Inc", -1) != getInc) {
                            //Toast.makeText(MainActivity.this, "Auto Reload", Toast.LENGTH_SHORT).show();

                            // reload Menu Playlist in Drawer
                            listDataHeader.clear();
                            prepareMenuData();

                            // clear Glide Cache
                            Glide.get(getApplicationContext()).clearMemory();

                            // is Stream Mode false set REMOTE
                            if (!SharedPreferencesUtils.getBoolean("is_stream_mode", true)) {
                                // reload Now Playing
                                trackInit(getPlaylistId, getPosition);
                            }

                            // reload Track
                            if (Objects.requireNonNull(navController.getCurrentDestination()).getId() == R.id.nav_track) {
                                Bundle bundle = new Bundle();
                                bundle.putBoolean("FROM_MENU_LIST_TRACK", false);
                                navController.navigate(R.id.nav_track, bundle);
                            }

                            // reload Album
                            if (Objects.requireNonNull(navController.getCurrentDestination()).getId() == R.id.nav_album) {
                                                        //use action for disable mutiple backstack
                                navController.navigate(R.id.action_nav_album_self);
                            }

                            int delay = 500;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    editor = getSharedPreferences("tauon_remote", MODE_PRIVATE).edit();
                                    editor.putInt("Inc", getInc);
                                    editor.apply();
                                }
                            },delay);
                        }

                        // is Stream Mode false set REMOTE
                        if (!SharedPreferencesUtils.getBoolean("is_stream_mode", true)) {
                            if (tvArtistMini.length() == 0) {
                            trackInit(getPlaylistId, getPosition);
                            }
                        }

                        if (!SharedPreferencesUtils.getBoolean("is_stream_mode", true)) {
                            //set ProgressBar at Mini
                            progressBarMini.setProgress(getProgress);
                            //set SeekBar at full
                            seekBar.setProgress(getProgress);
                            @SuppressLint("DefaultLocale") String progressTime = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes((long) getProgress), TimeUnit.MILLISECONDS.toSeconds((long) getProgress) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) getProgress)));
                            tvSeekBar.setText(progressTime);

                            switch (getStatus) {
                                case "playing":
                                    pause();
                                    break;
                                case "paused":
                                case "stopped":
                                    play();
                                    break;
                                default:
                                    break;
                            }

                            ShuffleInit();
                            RepeatInit();

                            next();
                            prev();

                            seekBarVolume.setProgress(getVolume);
                            if (getVolume == 0) {
                                ivVolume.setImageResource(R.drawable.ic_round_volume_off_24);
                            } else {
                                ivVolume.setImageResource(R.drawable.ic_round_volume_up_24);
                            }

                        }

                        boolean getLyrics_has;
                        if (!SharedPreferencesUtils.getBoolean("is_stream_mode", true)) {
                            getLyrics_has = getHasLyrics;
                        } else  {
                            getLyrics_has = trackListModels.tracks.get(SharedPreferencesUtils.getInt("trackPosition_stream", -1)).hasLyrics;
                        }

                        if (getLyrics_has) {
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

                        if (!SharedPreferencesUtils.getBoolean("is_stream_mode", true)) {
                            ivVolume.setEnabled(true);
                            ImageViewCompat.setImageTintList(ivVolume, ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.rose_icon_true)));
                        } else {
                            ivVolume.setEnabled(false);
                            ImageViewCompat.setImageTintList(ivVolume, ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.rose_icon_false)));
                        }

                    }
                });
    }

    private void RepeatInit() {
        ImageView ivRepeat = findViewById(R.id.iv_repeat);

        if (!SharedPreferencesUtils.getBoolean("is_stream_mode", true)) {
            ivRepeat.setImageResource(R.drawable.ic_round_repeat_24);
            if (getRepeat) {
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
        } else {
            switch (getStreamRepeat) {
                case "all" :
                    if (HXMusic.isPlaying())
                    if (HXMusic.isLoop())
                        HXMusic.setLoop(false);

                    ivRepeat.setImageResource(R.drawable.ic_round_repeat_24);
                    ImageViewCompat.setImageTintList(ivRepeat, ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.rose_icon_true)));
                    ivRepeat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editor = getSharedPreferences("tauon_remote", MODE_PRIVATE).edit();
                            editor.putString("stream_repeat", "one");
                            editor.apply();

                            ivRepeat.setImageResource(R.drawable.ic_round_repeat_one_24);
                            ImageViewCompat.setImageTintList(ivRepeat, ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.rose_icon_true)));

                            // set one
                            if (HXMusic.isPlaying())
                            HXMusic.setLoop(true);
                        }
                    });
                    break;
                case "one" :
                    if (HXMusic.isPlaying())
                    HXMusic.setLoop(true);
                    ivRepeat.setImageResource(R.drawable.ic_round_repeat_one_24);
                    ImageViewCompat.setImageTintList(ivRepeat, ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.rose_icon_true)));
                    ivRepeat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editor = getSharedPreferences("tauon_remote", MODE_PRIVATE).edit();
                            editor.putString("stream_repeat", "off");
                            editor.apply();

                            ivRepeat.setImageResource(R.drawable.ic_round_repeat_24);
                            ImageViewCompat.setImageTintList(ivRepeat, ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.rose_icon_false)));

                            // set one off
                            if (HXMusic.isPlaying())
                            if (HXMusic.isLoop())
                                HXMusic.setLoop(false);
                        }
                    });
                    break;
                case "off" :
                    // set one off
                    if (HXMusic.isPlaying())
                    if (HXMusic.isLoop())
                        HXMusic.setLoop(false);

                    ivRepeat.setImageResource(R.drawable.ic_round_repeat_24);
                    ImageViewCompat.setImageTintList(ivRepeat, ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.rose_icon_false)));
                    ivRepeat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editor = getSharedPreferences("tauon_remote", MODE_PRIVATE).edit();
                            editor.putString("stream_repeat", "all");
                            editor.apply();

                            ivRepeat.setImageResource(R.drawable.ic_round_repeat_24);
                            ImageViewCompat.setImageTintList(ivRepeat, ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.rose_icon_true)));


                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }

    private void ShuffleInit() {
        ImageView ivShuffle = findViewById(R.id.iv_shuffle);
        // is Stream Mode false set REMOTE
        if (!SharedPreferencesUtils.getBoolean("is_stream_mode", true)) {
            if (getShuffle) {
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
        } else {
            if (getStreamShuffle) {
                ImageViewCompat.setImageTintList(ivShuffle, ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.rose_icon_true)));
                ivShuffle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editor = getSharedPreferences("tauon_remote", MODE_PRIVATE).edit();
                        editor.putBoolean("stream_shuffle", false);
                        editor.apply();

                        ImageViewCompat.setImageTintList(ivShuffle, ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.rose_icon_false)));
                    }
                });
            } else {
                ImageViewCompat.setImageTintList(ivShuffle, ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.rose_icon_false)));
                ivShuffle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editor = getSharedPreferences("tauon_remote", MODE_PRIVATE).edit();
                        editor.putBoolean("stream_shuffle", true);
                        editor.apply();

                        ImageViewCompat.setImageTintList(ivShuffle, ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.rose_icon_true)));
                    }
                });
            }
        }
    }

    private void play() {
        ImageView ivPlay = findViewById(R.id.iv_play);
        ivPlay.setImageResource(R.drawable.ic_round_play_circle_24);

        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SharedPreferencesUtils.getBoolean("is_stream_mode", true)) {
                    requestPlay();
                } else {
                    if (HXMusic.getStatus().equals("READY")) {
                        initStream(SharedPreferencesUtils.getInt("trackPosition", -1));
                        trackInit(SharedPreferencesUtils.getString("playlist_stream", "0"), trackListModels.tracks.get(SharedPreferencesUtils.getInt("trackPosition_stream", -1)).id);
                    } else {
                        sPlay();
                    }

                }

                ivPlay.setImageResource(R.drawable.ic_round_pause_circle_24);
            }

        });

        // mini
        ImageView ivPlayMini = findViewById(R.id.iv_play_mini);
        ivPlayMini.setImageResource(R.drawable.ic_round_play_circle_24);

        ivPlayMini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SharedPreferencesUtils.getBoolean("is_stream_mode", true)) {
                    requestPlay();
                } else {
                    if (HXMusic.getStatus().equals("READY")) {
                        initStream(SharedPreferencesUtils.getInt("trackPosition", -1));
                        trackInit(SharedPreferencesUtils.getString("playlist_stream", "0"), trackListModels.tracks.get(SharedPreferencesUtils.getInt("trackPosition_stream", -1)).id);
                    } else {
                        sPlay();
                    }

                }

                ivPlayMini.setImageResource(R.drawable.ic_round_pause_circle_24);
            }
        });

        if (HXMusic.getStatus().equals("READY")) {
            ivPlay.setEnabled(false);
            ivPlayMini.setEnabled(false);
        } else {
            ivPlay.setEnabled(true);
            ivPlayMini.setEnabled(true);
        }
    }
    public static void requestPlay() {
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
                if (!SharedPreferencesUtils.getBoolean("is_stream_mode", true)) {
                    requestPause();
                } else {
                    sPause();
                }

                ivPlay.setImageResource(R.drawable.ic_round_play_circle_24);
            }
        });

        // mini
        ImageView ivPlayMini = findViewById(R.id.iv_play_mini);
        ivPlayMini.setImageResource(R.drawable.ic_round_pause_circle_24);

        ivPlayMini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SharedPreferencesUtils.getBoolean("is_stream_mode", true)) {
                    requestPause();
                } else {
                    sPause();
                }

                ivPlayMini.setImageResource(R.drawable.ic_round_play_circle_24);
            }
        });

        if (HXMusic.getStatus().equals("READY")) {
            ivPlay.setEnabled(false);
            ivPlayMini.setEnabled(false);
        } else {
            ivPlay.setEnabled(true);
            ivPlayMini.setEnabled(true);
        }
    }
    public static void requestPause() {
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


                        int TrackOrPosition = -1;
                        if (!SharedPreferencesUtils.getBoolean("is_stream_mode", true)) {
                            TrackOrPosition = getTrackId;
                        } else {
                            TrackOrPosition = trackListModels.tracks.get(SharedPreferencesUtils.getInt("trackPosition_stream", 0)).id;
                        }

                        Glide.with(getApplicationContext())
                                .load("http://" + SharedPreferencesUtils.getString("ip", "127.0.0.1") + ":7814/api1/pic/medium/" + TrackOrPosition)
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
                        int finalTrackOrPosition = TrackOrPosition;
                        Glide.with(getApplicationContext())
                                .asBitmap()
                                .load("http://" + SharedPreferencesUtils.getString("ip", "127.0.0.1") + ":7814/api1/pic/medium/" + TrackOrPosition)
                                .centerCrop()
                                .placeholder(R.drawable.ic_round_music_note_24)
                                .into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        getBitmap = resource;
                                        //notificationInit();

                                        if (SharedPreferencesUtils.getBoolean("notif_enable", false)) {
                                            Intent intent = new Intent(MainActivity.this, PlayingService.class);
                                            intent.putExtra("serviceTitle", getTitle);
                                            intent.putExtra("serviceArtist", getArtist);
                                            intent.putExtra("serviceTrackID", finalTrackOrPosition);
                                            startService(intent);
                                        }

                                        ivCover.setImageBitmap(resource);
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {

                                    }
                                });

                        lyricsInit(TrackOrPosition);



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

        if (Objects.requireNonNull(navController.getCurrentDestination()).getId() == R.id.nav_album) {
            editor = getSharedPreferences("tauon_remote", MODE_PRIVATE).edit();
            editor.putString("titleToolbar", "Album");
            editor.apply();
        }

    }

    public static void sReqPrev() {
        getReqPrev = true;
    }

    public static void sReqNext() {
        getReqNext = true;
    }

    public static void sReqPlayPause() {
        getReqPlayPause = true;
    }
}