package com.creation.test.measure;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.creation.test.R;

public class MeasureActivity extends Activity {
    public static final String TAG = "MeasureActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);
        Log.e(TAG, "MeasureActivity");
    }
}
