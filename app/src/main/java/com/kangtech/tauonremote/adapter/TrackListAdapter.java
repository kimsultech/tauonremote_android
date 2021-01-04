package com.kangtech.tauonremote.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kangtech.tauonremote.R;
import com.kangtech.tauonremote.model.track.TrackListModel;
import com.kangtech.tauonremote.model.track.TrackModel;

import java.util.ArrayList;
import java.util.List;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.TrackListViewHolder> implements Filterable {

    private final TrackListModel trackListModels;
    private final TrackListModel getTrackListModelsFiltered;
    private final Context context;

    public TrackListAdapter(Context context, TrackListModel trackListModels) {
        this.context = context;
        this.trackListModels = trackListModels;
        this.getTrackListModelsFiltered = trackListModels;
    }

    @NonNull
    @Override
    public TrackListAdapter.TrackListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_trackall, parent, false);
        return new TrackListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackListAdapter.TrackListViewHolder holder, int position) {
        holder.title.setText(trackListModels.tracks.get(position).title);
        holder.artist.setText(trackListModels.tracks.get(position).artist);

        Glide.with(context)
                .load("http://192.168.43.151:7814/api1/pic/small/" + trackListModels.tracks.get(position).id)
                .centerCrop()
                .placeholder(R.drawable.ic_round_music_note_24)
                .dontAnimate()
                .into(holder.ivCover);
    }

    @Override
    public int getItemCount() {
        return trackListModels.tracks.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();

                if(charSequence == null | charSequence.length() == 0){
                    filterResults.count = getTrackListModelsFiltered.tracks.size();
                    filterResults.values = getTrackListModelsFiltered.tracks;

                    Toast.makeText(context, "Fix Soon, data not showing", Toast.LENGTH_SHORT).show();

                }else{
                    String searchChr = charSequence.toString().toLowerCase();

                    List<TrackModel> resultData = new ArrayList<>();

                    for(TrackModel userModel: getTrackListModelsFiltered.tracks){
                        if(userModel.title.toLowerCase().contains(searchChr) || userModel.artist.toLowerCase().contains(searchChr)){
                            resultData.add(userModel);
                        }
                    }
                    filterResults.count = resultData.size();
                    filterResults.values = resultData;

                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

                trackListModels.tracks = (List<TrackModel>) filterResults.values;
                notifyDataSetChanged();

            }
        };
    }


    public static class TrackListViewHolder extends RecyclerView.ViewHolder {
        private TextView title, artist;
        private ImageView ivCover;

        public TrackListViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_tracklist_title);
            artist = itemView.findViewById(R.id.tv_tracklist_artist);
            ivCover = itemView.findViewById(R.id.iv_tracklist_cover);
        }
    }

}
