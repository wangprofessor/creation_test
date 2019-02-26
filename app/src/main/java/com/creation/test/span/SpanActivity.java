package com.creation.test.span;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
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

//        test1(textView);
        test2(textView);
    }

    private void test2(TextView textView) {
        String content="预祝党的十九大完美谢慕";
        SpannableStringBuilder stringBuilder=new SpannableStringBuilder(content);
        ForegroundColorSpan foregroundColorSpan=new ForegroundColorSpan(Color.parseColor("#3FC7AA"));
        stringBuilder.setSpan(foregroundColorSpan,0,3,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                finish();
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false);
            }
        };
        stringBuilder.setSpan(clickableSpan, 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(stringBuilder);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void test1(TextView textView) {
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
