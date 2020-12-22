package com.kangtech.tauonmusicremote.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.kangtech.tauonmusicremote.view.fragment.TestFragment;

public class MainTabAdapter extends FragmentStateAdapter {
    public MainTabAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment(int)
        Fragment fragment = new TestFragment();
        Bundle args = new Bundle();
        // Our object is just an integer :-P
        args.putInt(TestFragment.ARG_OBJECT, position + 1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 8;
    }
}
