package com.creation.test.view_pager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        fragmentList.get(0).textString = "000000000000000000000000000000000000000000000";
        fragmentList.get(1).textString = "111111111111111111111111111111111111111111111";
        fragmentList.get(2).textString = "222222222222222222222222222222222222222222222";

        final OneFragmentPagerAdapter adapter = new OneFragmentPagerAdapter(getSupportFragmentManager()) {
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
        public String textString;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            TextView textView = new TextView(container.getContext());
            textView.setText(textString);
            return textView;
        }
    }
}
