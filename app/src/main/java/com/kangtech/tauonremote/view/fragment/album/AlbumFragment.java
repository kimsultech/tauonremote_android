package com.kangtech.tauonremote.view.fragment.album;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kangtech.tauonremote.R;
import com.kangtech.tauonremote.adapter.AlbumListAdapter;
import com.kangtech.tauonremote.adapter.TrackListAdapter;
import com.kangtech.tauonremote.api.ApiServiceInterface;
import com.kangtech.tauonremote.model.album.AlbumListModel;
import com.kangtech.tauonremote.util.Server;
import com.kangtech.tauonremote.util.SharedPreferencesUtils;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AlbumFragment extends Fragment {

    private static ApiServiceInterface apiServiceInterface;
    private static AlbumListAdapter adapter;
    private static AlbumListModel albumListModels;
    private static String PlaylistID;
    private static MenuItem searchItem;
    private static SearchView searchView;

    private static RecyclerView recyclerView;

    private static Context context;
    private static AlbumFragment frag;

    public AlbumFragment() {
        setHasOptionsMenu(true);
    }

    public static AlbumFragment newInstance(String param1, String param2) {
        AlbumFragment fragment = new AlbumFragment();
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

    public static void reload() {
        AlbumInit(PlaylistID);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        apiServiceInterface = Server.getApiServiceInterface();

        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);

        if (!SharedPreferencesUtils.getBoolean("is_stream_mode", true)) {
            if (SharedPreferencesUtils.getString("playlistID", "-1").equals("-1")) {
                int delaythis = 3000;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        PlaylistID = SharedPreferencesUtils.getString("playlistID", "0");
                        AlbumInit(SharedPreferencesUtils.getString("playlistID", "0"));
                    }
                }, delaythis);
            } else {
                PlaylistID = SharedPreferencesUtils.getString("playlistID", "0");
                AlbumInit(SharedPreferencesUtils.getString("playlistID", "0"));
            }
        } else {
            PlaylistID = SharedPreferencesUtils.getString("playlistID", "0");
            AlbumInit(SharedPreferencesUtils.getString("playlistID", "0"));
        }


    }

    private static void AlbumInit(String playlist) {
        apiServiceInterface.getAlbum(playlist)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AlbumListModel>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull AlbumListModel albumListModel) {
                        albumListModels = albumListModel;
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        recyclerViewInit();
                    }
                });
    }

    private static void recyclerViewInit() {
        if (frag.isAdded()) {
            recyclerView = frag.requireActivity().findViewById(R.id.rv_albumlist);

            adapter = new AlbumListAdapter(frag.getContext(), albumListModels, PlaylistID);

            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(frag.requireContext(), 2);

            recyclerView.setLayoutManager(layoutManager);

            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_album, container, false);

        frag = this;

        FloatingActionButton mFab = v.findViewById(R.id.fab_album_search);
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
    public void onCreateOptionsMenu(@androidx.annotation.NonNull Menu menu, @androidx.annotation.NonNull MenuInflater inflater) {
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
                    AlbumInit(PlaylistID);
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
    public boolean onOptionsItemSelected(@androidx.annotation.NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(@androidx.annotation.NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }


    @Override
    public void onResume() {
        super.onResume();
        AlbumInit(PlaylistID);
        Log.e("onResume", "Fragment Album");
    }

}