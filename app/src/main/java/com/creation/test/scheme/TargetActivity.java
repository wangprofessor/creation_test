package com.creation.test.scheme;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.creation.test.R;

import java.util.List;

public class TargetActivity extends Activity {
    public static final String TAG = "TargetActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_target);

        Intent intent = getIntent();
        Uri uri = intent.getData();

        // 完整的url信息
        String url = uri.toString();
        Log.e(TAG, "url:" + uri);

        // scheme部分
        String scheme = uri.getScheme();
        Log.e(TAG, "scheme:" + scheme);

        // host部分
        String host = uri.getHost();
        Log.e(TAG, "host:" + host);

        // port部分
        int port = uri.getPort();
        Log.e(TAG, "port:" + port);

        // 访问路劲
        String path = uri.getPath();
        Log.e(TAG, "path:" + path);

        List<String> pathSegments = uri.getPathSegments();

        // Query部分
        String query = uri.getQuery();
        Log.e(TAG, "query:" + query);

        //获取指定参数值
        String type = uri.getQueryParameter("external_msg_type");
        Log.e(TAG, "external_msg_type:" + type);
        String content = uri.getQueryParameter("external_msg_content");
        Log.e(TAG, "external_msg_content:" + content);
    }
}
