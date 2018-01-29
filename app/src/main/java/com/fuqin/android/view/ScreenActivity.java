package com.fuqin.android.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class ScreenActivity extends AppCompatActivity {
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);
        mTextView = findViewById(R.id.textview);
        Log.i("debug", "Window: height:" + getResources().getDisplayMetrics().heightPixels + "   with:" + getResources().getDisplayMetrics().widthPixels);
        mTextView.post(() -> {
            Log.i("debug", "mTextView: height:" + mTextView.getHeight() + "   with:" + mTextView.getWidth());
        });
    }
}
