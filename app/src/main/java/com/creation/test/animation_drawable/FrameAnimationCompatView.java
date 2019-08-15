package com.creation.test.animation_drawable;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class FrameAnimationCompatView extends ImageView {
    private boolean isAnimating = false;

    public FrameAnimationCompatView(Context context) {
        super(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        Drawable drawable = getDrawable();
        if (!(drawable instanceof AnimationDrawable)) {
            return;
        }
        if (!isAnimating) {
            ((AnimationDrawable) drawable).stop();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        Drawable drawable = getDrawable();
        if (!(drawable instanceof AnimationDrawable)) {
            return;
        }
        isAnimating = ((AnimationDrawable) drawable).isRunning();

        super.onDetachedFromWindow();
    }
}
