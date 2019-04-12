package com.creation.test.dot9;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.creation.test.R;

public class Dot9View extends View {
    private final Drawable drawable;

    public Dot9View(Context context, AttributeSet attrs) {
        super(context, attrs);
        drawable = getResources().getDrawable(R.drawable.dot);
        drawable.setBounds(new Rect(0, 0, 1000, 600));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawable.draw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
