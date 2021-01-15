package com.kangtech.tauonremote.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kangtech.tauonremote.R;
import com.kangtech.tauonremote.model.album.AlbumListModel;
import com.kangtech.tauonremote.model.album.AlbumModel;
import com.kangtech.tauonremote.model.track.TrackModel;
import com.kangtech.tauonremote.util.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.AlbumListViewHolder> implements Filterable {

    private final Context context;
    private AlbumListModel albumListModel;
    private AlbumListModel albumListModelFiltered;
    private final String playlistID;

    private SharedPreferences.Editor editor;

    public AlbumListAdapter(Context context, AlbumListModel albumListModel, String playlistID) {
        this.context = context;
        this.albumListModel = albumListModel;
        this.playlistID = playlistID;
        this.albumListModelFiltered = albumListModel;
    }

    @NonNull
    @Override
    public AlbumListAdapter.AlbumListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_album, parent, false);
        return new AlbumListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumListAdapter.AlbumListViewHolder holder, int position) {
        holder.album.setText(albumListModel.albums.get(position).album);
        holder.artist.setText(albumListModel.albums.get(position).artist);

        Glide.with(context)
                .load("http://" + SharedPreferencesUtils.getString("ip", "127.0.0.1") + ":7814/api1/pic/medium/" + albumListModel.albums.get(position).id)
                .centerCrop()
                .placeholder(R.drawable.ic_round_music_note_24)
                .dontAnimate()
                .into(holder.ivCover);

        NavController navController = Navigation.findNavController((Activity) context, R.id.nav_host_fragment);
        Bundle bundle = new Bundle();
        bundle.putBoolean("FROM_ALBUM_LIST", true);
        bundle.putBoolean("FROM_MENU_LIST_TRACK", false);
        bundle.putInt("AlbumID", albumListModel.albums.get(position).albumId);
        bundle.putString("AlbumName", albumListModel.albums.get(position).album);

        holder.llAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.nav_track, bundle);

                editor = context.getSharedPreferences("tauon_remote", MODE_PRIVATE).edit();
                editor.putString("titleToolbar", albumListModel.albums.get(position).album);
                editor.apply();
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumListModel.albums.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                if(charSequence.toString().isEmpty()){
                    albumListModelFiltered.albums = albumListModel.albums;

                }else{
                    String searchChr = charSequence.toString().toLowerCase();

                    List<AlbumModel> resultData = new ArrayList<>();

                    for(AlbumModel userModel: albumListModel.albums){
                        if(userModel.album.toLowerCase().contains(searchChr) || userModel.artist.toLowerCase().contains(searchChr)){
                            resultData.add(userModel);
                        }
                    }
                    albumListModelFiltered.albums = resultData;

                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = albumListModelFiltered.albums;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                albumListModelFiltered.albums = (List<AlbumModel>) filterResults.values;
                notifyDataSetChanged();

            }
        };
    }

    public static class AlbumListViewHolder extends RecyclerView.ViewHolder {
        private TextView album, artist;
        private ImageView ivCover;
        private ConstraintLayout llAlbum;


        public AlbumListViewHolder(@NonNull View itemView) {
            super(itemView);

            album = itemView.findViewById(R.id.tv_album_name);
            artist = itemView.findViewById(R.id.tv_album_artist);
            ivCover = itemView.findViewById(R.id.iv_album);

            llAlbum = itemView.findViewById(R.id.cl_albumlist);

        }
    }


}
