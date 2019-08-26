package com.creation.test.view_pager;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;

import java.util.HashSet;
import java.util.Set;

public abstract class SingleFragmentPagerAdapter extends PagerAdapter {
    private final FragmentManager mFragmentManager;
    private final Set<Fragment> mVisibilitySet = new HashSet<>();
    private Fragment mCurrentPrimaryItem = null;

    public SingleFragmentPagerAdapter(FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        String name = makeFragmentName(container.getId(), position);
        Fragment fragment = mFragmentManager.findFragmentByTag(name);

        if (fragment == null) {
            fragment = getItem(position);
        }

        if (fragment != mCurrentPrimaryItem) {
            fragment.setMenuVisibility(false);
            fragment.setUserVisibleHint(false);
        }

        return fragment;
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        Fragment fragment = (Fragment) object;
        if (fragment != mCurrentPrimaryItem) {
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            if (mVisibilitySet.contains(fragment)) {
                fragmentTransaction.attach(fragment);
            } else {
                mVisibilitySet.add(fragment);
                String name = makeFragmentName(container.getId(), position);
                fragmentTransaction.add(container.getId(), fragment, name);
            }

            if (mCurrentPrimaryItem != null) {
                fragmentTransaction.detach(mCurrentPrimaryItem);
                mCurrentPrimaryItem.setMenuVisibility(false);
                mCurrentPrimaryItem.setUserVisibleHint(false);
            }

            fragmentTransaction.commit();

            fragment.setMenuVisibility(true);
            fragment.setUserVisibleHint(true);
            mCurrentPrimaryItem = fragment;
        }
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return ((Fragment) object).getView() == view;
    }

    protected abstract Fragment getItem(int position);

    private static String makeFragmentName(int viewId, int position) {
        return "android:switcher:" + viewId + ":" + position;
    }
}
