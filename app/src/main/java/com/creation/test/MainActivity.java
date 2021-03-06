package com.creation.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creation.test.animation.AnimationActivity;
import com.creation.test.animation_drawable.AnimationDrawableActivity;
import com.creation.test.appbar.AppbarActivity;
import com.creation.test.clip.ClipActivity;
import com.creation.test.dispatch.DispatchActivity;
import com.creation.test.dot9.Dot9Activity;
import com.creation.test.flutter.MyFlutterActivity;
import com.creation.test.measure.MeasureActivity;
import com.creation.test.measure.MeasureService;
import com.creation.test.memory.MemoryActivity;
import com.creation.test.proc.ProcActivity;
import com.creation.test.scheme.SchemeActivity;
import com.creation.test.span.SpanActivity;
import com.creation.test.touch.TouchActivity;
import com.creation.test.view_pager.ViewPagerActivity;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "MainActivity");

        MeasureService.Companion.instance().init(this, new Function0<Unit>() {
            @Override
            public Unit invoke() {
                return null;
            }
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.list);
        LinearLayoutManager layout = new LinearLayoutManager(this);
        layout.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layout);

        final ArrayList<Data> list = new ArrayList<>();
        initList(list);

        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                TextView textView = new TextView(MainActivity.this);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
                return new RecyclerView.ViewHolder(textView) {};
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
                final Data data = list.get(position);
                TextView textView = (TextView) viewHolder.itemView;
                textView.setText((position + 1) + "." + data.text);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startPosition(list, position);
                    }
                });
            }

            @Override
            public int getItemCount() {
                return list.size();
            }
        });
    }

    private void startPosition(List<Data> list, int position) {
        Data data = list.get(position);
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, data.clazz);
        startActivity(intent);
    }

    private void initList(ArrayList<Data> list) {
        list.add(new Data("span", SpanActivity.class));
        list.add(new Data("touch", TouchActivity.class));
        list.add(new Data("scheme", SchemeActivity.class));
        list.add(new Data("clip", ClipActivity.class));
        list.add(new Data("animation", AnimationActivity.class));
        list.add(new Data("measure", MeasureActivity.class));
        list.add(new Data("dispatch", DispatchActivity.class));
        list.add(new Data("view_pager", ViewPagerActivity.class));
        list.add(new Data("memory", MemoryActivity.class));
        list.add(new Data("dot9", Dot9Activity.class));
        list.add(new Data("proc", ProcActivity.class));
        list.add(new Data("appbar", AppbarActivity.class));
        list.add(new Data("animation_drawable", AnimationDrawableActivity.class));
        list.add(new Data("flutter", MyFlutterActivity.class));
    }

    private static class Data {
        private final String text;
        private final Class<?> clazz;

        private Data(String text, Class<?> clazz) {
            this.text = text;
            this.clazz = clazz;
        }
    }
}
