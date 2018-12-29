package com.creation.test.animation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.creation.test.R;

public class AnimationActivity extends Activity {
    public static final String TAG = "AnimationActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);

        final View text = findViewById(R.id.text);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(text, "translationY", 0f, 500f);
        translationY.setDuration(3000);
        translationY.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.e(TAG, "onAnimationStart");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.e(TAG, "onAnimationEnd");
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.e(TAG, "onAnimationCancel");
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                Log.e(TAG, "onAnimationRepeat");
            }
        });
        translationY.addPauseListener(new Animator.AnimatorPauseListener() {
            @Override
            public void onAnimationPause(Animator animation) {
                Log.e(TAG, "onAnimationPause");
            }

            @Override
            public void onAnimationResume(Animator animation) {
                Log.e(TAG, "onAnimationResume");
            }
        });
        translationY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float y = (float) animation.getAnimatedValue();
                text.setPadding((int) y, text.getPaddingTop(), text.getPaddingRight(), text.getPaddingBottom());
            }
        });
        translationY.start();
    }
}
