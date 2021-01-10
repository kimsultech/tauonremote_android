package com.kangtech.tauonremote.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.kangtech.tauonremote.R;
import com.kangtech.tauonremote.model.playlist.PlaylistData;
import com.kangtech.tauonremote.model.playlist.PlaylistModel;
import com.kangtech.tauonremote.view.MainActivity;

import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context _context;
    private List<String> _listDataHeader;
    private HashMap<String, List<PlaylistModel>> _listdataChild;

    private SharedPreferences.Editor editor;

    public ExpandableListAdapter (Context context, List<String> listDataHeader,
                                  HashMap<String, List<PlaylistModel>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listdataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this._listdataChild.get(this._listDataHeader.get(groupPosition)).get(groupPosition).playlists.get(childPosition).name;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = String.valueOf(getChild(groupPosition, childPosition));

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group_child, null);
        }

        TextView tvListChild = (TextView) convertView.findViewById(R.id.lblListItem);
        tvListChild.setText(childText);

        NavController navController = Navigation.findNavController((Activity) _context, R.id.nav_host_fragment);
        Bundle bundle = new Bundle();
        bundle.putBoolean("FROM_MENU_LIST_TRACK", true);
        bundle.putString("PlaylistID", _listdataChild.get(_listDataHeader.get(groupPosition)).get(groupPosition).playlists.get(childPosition).id);
        bundle.putString("PlaylistName", _listdataChild.get(_listDataHeader.get(groupPosition)).get(groupPosition).playlists.get(childPosition).name);

        LinearLayout ll_child = convertView.findViewById(R.id.ll_child_list);
        ll_child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.nav_track, bundle);

                MainActivity.drawer.closeDrawer(GravityCompat.START);
                if (MainActivity.bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    MainActivity.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }

                editor = _context.getSharedPreferences("tauon_remote", MODE_PRIVATE).edit();
                editor.putString("titleToolbar", _listdataChild.get(_listDataHeader.get(groupPosition)).get(groupPosition).playlists.get(childPosition).name);
                editor.apply();
            }
        });

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listdataChild.get(this._listDataHeader.get(groupPosition)).get(0).playlists.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group_header, null);
        }

        TextView ListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
        //ListHeader.setTypeface(null, Typeface.BOLD);
        ListHeader.setText(headerTitle);

        return convertView;

    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
