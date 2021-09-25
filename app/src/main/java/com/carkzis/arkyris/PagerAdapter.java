package com.carkzis.arkyris;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.carkzis.arkyris.entries.ArkeFragment;
import com.carkzis.arkyris.entries.IrisFragment;

import org.jetbrains.annotations.NotNull;

/**
 * Class for attaching fragments to the viewpager.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {

    private final int mNumOfTabs;

    public PagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        this.mNumOfTabs = 2;
    }

    /**
     * Returns a different fragment depending on the users position in the viewpager.
     */
    @NotNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ArkeFragment();
            case 1:
                return new IrisFragment();
            default: throw new IllegalArgumentException(
                    "Something went wrong with the PagerAdapter.");
        }
    }

    /**
     * Returns the number of tabs.
     */
    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
