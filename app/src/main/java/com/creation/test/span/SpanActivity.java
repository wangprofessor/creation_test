package com.creation.test.span;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.widget.TextView;

import com.creation.test.R;

import cn.iwgang.simplifyspan.SimplifySpanBuild;
import cn.iwgang.simplifyspan.other.SpecialGravity;
import cn.iwgang.simplifyspan.unit.SpecialImageUnit;
import cn.iwgang.simplifyspan.unit.SpecialTextUnit;

public class SpanActivity extends Activity {
    public static final String TAG = "SpanActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "SpanActivity");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_span);
        TextView textView = findViewById(R.id.text);

        SimplifySpanBuild textBuilder = new SimplifySpanBuild("");
        Bitmap bitmap = getBitmapFromVectorResource(R.drawable.shape);
        textBuilder.append(new SpecialImageUnit(this, "error", bitmap, 40, 40, SpecialGravity.TOP));
        textBuilder.append("a123");
        SpannableStringBuilder build = textBuilder.build();
        textView.setText(build);
    }

    private Bitmap getBitmapFromVectorResource(int resourceId) {
        Drawable drawable = obtainDrawableFromVectorResource(resourceId);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
        drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private Drawable obtainDrawableFromVectorResource(int resourceId) {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return VectorDrawableCompat.create(
                    getResources(),
                    resourceId,
                    getTheme()
            );
        } else {
            return getResources().getDrawable(resourceId, getTheme());
        }
    }
}
