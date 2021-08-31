package com.kangtech.tauonremote.view.fragment.track;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kangtech.tauonremote.R;
import com.kangtech.tauonremote.adapter.TrackListAdapter;
import com.kangtech.tauonremote.api.ApiServiceInterface;
import com.kangtech.tauonremote.model.track.TrackListModel;
import com.kangtech.tauonremote.util.Server;
import com.kangtech.tauonremote.util.SharedPreferencesUtils;
import com.kangtech.tauonremote.view.MainActivity;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class TrackFragment extends Fragment {

    private ApiServiceInterface apiServiceInterface;

    private static RecyclerView recyclerView;
    static TrackListAdapter adapter;
    private TrackListModel trackListModels;
    private String PlaylistID;
    private static MenuItem searchItem;
    private static SearchView searchView;
    private Toolbar toolbar;

    private SharedPreferences.Editor editor;


    public TrackFragment() {
        setHasOptionsMenu(true);
    }

    public static TrackFragment newInstance(String param1, String param2) {
        TrackFragment fragment = new TrackFragment();
        Bundle args = new Bundle();
        /*args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);*/
        fragment.setArguments(args);
        return fragment;
    }

    public static void hideSearch() {
        if (!searchView.isIconified()) {
            searchView.clearFocus();
            searchView.setQuery("", false);
            searchItem.setVisible(false);
        }
    }

    public static void reqNotifyDataUpdate() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        apiServiceInterface = Server.getApiServiceInterface();

        toolbar = requireActivity().findViewById(R.id.toolbar);

        if (requireArguments().getBoolean("FROM_MENU_LIST_TRACK")) {
            PlaylistID = requireArguments().getString("PlaylistID");
            TrackListInit(PlaylistID);
            toolbar.setTitle(requireArguments().getString("PlaylistName"));
        } else if (requireArguments().getBoolean("FROM_ALBUM_LIST")) {
            PlaylistID = SharedPreferencesUtils.getString("playlistID", "0");
            TrackListAlbumInit(PlaylistID, requireArguments().getInt("AlbumID"));
            toolbar.setTitle(requireArguments().getString("AlbumName"));
        } else {
            PlaylistID = SharedPreferencesUtils.getString("playlistID", "0");
            TrackListInit(PlaylistID);
            if (MainActivity.bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                toolbar.setTitle("Now Playing");
            }
        }


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
                        recyclerViewInit();

                        if (PlaylistID.equals(SharedPreferencesUtils.getString("playlistID", "0"))) {
                            recyclerView.scrollToPosition(SharedPreferencesUtils.getInt("TrackPosition", -1));
                        }
                    }
                });
    }

    public void TrackListAlbumInit(String playlist, int album) {
        apiServiceInterface.getTrackAlbum(playlist, album)
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
                        recyclerViewInit();

                        if (PlaylistID.equals(SharedPreferencesUtils.getString("playlistID", "0"))) {
                            recyclerView.scrollToPosition(SharedPreferencesUtils.getInt("TrackPosition", -1));
                        }
                    }
                });
    }

    private void recyclerViewInit() {
        recyclerView = requireActivity().findViewById(R.id.rv_tracklist);

        adapter = new TrackListAdapter(getContext(), trackListModels, PlaylistID);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_track, container, false);

        FloatingActionButton mFab = v.findViewById(R.id.fab_track_search);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchItem.setVisible(true);
                searchView.setIconified(false);
            }
        });


        return v;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.track_menu, menu);

        searchItem = menu.findItem(R.id.menu_search_track);
        searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                if (newText.isEmpty()) {
                    TrackListInit(PlaylistID);
                }
                return true;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchView.clearFocus();

                searchItem.setVisible(false);
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }


    public static void reqUpdate(Context context, int getTrackId) {
        if (getTrackId == -1) {
            Toast.makeText(context, "Kosong", Toast.LENGTH_SHORT).show();
        } else {
            recyclerView.scrollToPosition(getTrackId);
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (requireArguments().getBoolean("FROM_MENU_LIST_TRACK")) {
            PlaylistID = requireArguments().getString("PlaylistID");
            TrackListInit(PlaylistID);
            toolbar.setTitle(requireArguments().getString("PlaylistName"));
        } else if (requireArguments().getBoolean("FROM_ALBUM_LIST")) {
            PlaylistID = SharedPreferencesUtils.getString("playlistID", "0");
            TrackListAlbumInit(PlaylistID, requireArguments().getInt("AlbumID"));
            toolbar.setTitle(requireArguments().getString("AlbumName"));
        } else {
            PlaylistID = SharedPreferencesUtils.getString("playlistID", "0");
            TrackListInit(PlaylistID);
            if (MainActivity.bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                toolbar.setTitle("Now Playing");
            }
        }

        int delay = 800;
        new Handler().postDelayed(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                if (isAdded()) {
                    editor = requireContext().getSharedPreferences("tauon_remote", MODE_PRIVATE).edit();
                    editor.putString("titleToolbar", toolbar.getTitle().toString());
                    editor.apply();
                }
            }
        },delay);
        Log.e("onResume", "Fragment Track");
    }


}