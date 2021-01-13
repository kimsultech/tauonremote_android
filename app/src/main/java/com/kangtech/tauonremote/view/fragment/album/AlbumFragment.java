package com.kangtech.tauonremote.view.fragment.album;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    private ApiServiceInterface apiServiceInterface;
    private AlbumListAdapter adapter;
    private AlbumListModel albumListModels;
    private String PlaylistID;

    private static RecyclerView recyclerView;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        apiServiceInterface = Server.getApiServiceInterface();

        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);

        PlaylistID = SharedPreferencesUtils.getString("playlistID", "0");
        AlbumInit(PlaylistID);

    }

    private void AlbumInit(String playlist) {
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

    private void recyclerViewInit() {
        recyclerView = requireActivity().findViewById(R.id.rv_albumlist);

        adapter = new AlbumListAdapter(getContext(), albumListModels, PlaylistID);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_album, container, false);
    }
}