package com.creation.test.view_pager;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashSet;
import java.util.Set;

/**
 * 懒加载fragment的adapter，只有滑动到某一个页面才会add对应的fragment，并且永远不会移除
 */
public abstract class LazyFragmentPagerAdapter extends PagerAdapter {
    private final FragmentManager mFragmentManager;
    private final Set<Fragment> mVisibilitySet = new HashSet<>();
    private Fragment mCurrentPrimaryItem = null;

    public LazyFragmentPagerAdapter(FragmentManager fragmentManager) {
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
            if (!mVisibilitySet.contains(fragment)) {
                mVisibilitySet.add(fragment);
                String name = makeFragmentName(container.getId(), position);
                fragmentTransaction.add(container.getId(), fragment, name);
            }

            if (mCurrentPrimaryItem != null) {
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
