package com.creation.test.view_pager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.creation.test.R;

import java.util.ArrayList;

public class ViewPagerActivity extends AppCompatActivity {
    private static final String TAG = "ViewPagerActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        final ViewPager viewPager = findViewById(R.id.view_pager);

        final ArrayList<FragmentText> fragmentList = new ArrayList<>();
        fragmentList.add(new FragmentText());
        fragmentList.add(new FragmentText());
        fragmentList.add(new FragmentText());
        fragmentList.get(0).resource = R.drawable.view_pager_image;
        fragmentList.get(1).resource = R.drawable.view_pager_image;
        fragmentList.get(2).resource = R.drawable.view_pager_image;

        final SingleFragmentPagerAdapter adapter = new SingleFragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return fragmentList.size();
            }

            @Override
            protected Fragment getItem(int position) {
                return fragmentList.get(position);
            }
        };
        viewPager.setAdapter(adapter);
    }

    public static class FragmentText extends Fragment {
        public int resource;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            ImageView textView = new ImageView(container.getContext());
            textView.setImageResource(resource);
            return textView;
        }
    }
}
