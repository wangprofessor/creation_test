package com.creation.test.clip;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.creation.test.R;

public class ClipActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip);

        final ClipFrameLayout clipFrameLayout = findViewById(R.id.clip_layout);

        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clipFrameLayout.isAnimating = true;
                ValueAnimator valueAnimator = clipFrameLayout.animateClip(false, 0);
                valueAnimator.start();
            }
        });
        findViewById(R.id.reverse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clipFrameLayout.isAnimating = true;
                ValueAnimator valueAnimator = clipFrameLayout.animateClip(true, 0);
                valueAnimator.start();
            }
        });
    }
}
