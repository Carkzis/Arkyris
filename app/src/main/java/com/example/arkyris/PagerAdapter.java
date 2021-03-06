package com.example.arkyris;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.arkyris.entries.ArkeFragment;
import com.example.arkyris.entries.IrisFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;

    public PagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        this.mNumOfTabs = 2;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ArkeFragment();
            case 1:
                return new IrisFragment();
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
