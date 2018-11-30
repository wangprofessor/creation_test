package com.creation.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
                final Data data = list.get(position);
                TextView textView = (TextView) viewHolder.itemView;
                textView.setText((position + 1) + "." + data.text);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, data.clazz);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public int getItemCount() {
                return list.size();
            }
        });
    }

    private void initList(ArrayList<Data> list) {
        list.add(new Data("main", MainActivity.class));
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
