package com.creation.test.clip;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class ClipFrameLayout extends FrameLayout {
    public boolean isAnimating = false;

    private Rect rect = new Rect();

    private Path mPath = new Path();
    private RectF rectF = new RectF();
    private float[] cornerArray = new float[8];

    public ClipFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
    }

    @Override
    public void draw(Canvas canvas) {
        if (isAnimating) {
            canvas.clipPath(mPath);
        }
        super.draw(canvas);
    }

    public ValueAnimator animateClip(final boolean reverse, int clipHeight) {
        float fraction = 0.5f;
        clipHeight = (int) (getHeight() * fraction);
        ValueAnimator animator = ValueAnimator.ofInt(reverse ? clipHeight : 0, reverse ? 0 : clipHeight);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int top = (int) animation.getAnimatedValue();

                float fraction = animation.getAnimatedFraction();
                float corner = (reverse ? 1 - fraction : fraction) * 40;

                rect.set(0, top, getWidth(), getHeight());

                mPath.reset();
                rectF.set(0, top, getWidth(), getHeight());
                cornerArray[0] = corner;
                cornerArray[1] = corner;
                cornerArray[2] = corner;
                cornerArray[3] = corner;
                mPath.addRoundRect(rectF, cornerArray, Path.Direction.CW);

                invalidate();
            }
        });
        animator.setDuration(300);
        return animator;
    }
}
