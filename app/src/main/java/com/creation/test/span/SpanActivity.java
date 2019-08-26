package com.creation.test.span;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.MaskFilterSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

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
//        test2(textView);
        test3(textView);
    }

    private void test3(TextView textView) {
        textView.setTextSize(100f);
        textView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        SpannableString stringBuilder = new SpannableString("一一一");
        MaskFilterSpan maskFilterSpan = new MaskFilterSpan(new BlurMaskFilter(50f, BlurMaskFilter.Blur.NORMAL));
        stringBuilder.setSpan(maskFilterSpan, 0, stringBuilder.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        textView.setText(stringBuilder);
    }

    private void test2(TextView textView) {
        String content = "预祝党的十九大完美谢慕";
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(content);
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#3FC7AA"));
        stringBuilder.setSpan(foregroundColorSpan, 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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
