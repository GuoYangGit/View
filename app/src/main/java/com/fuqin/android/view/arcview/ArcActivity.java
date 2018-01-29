package com.fuqin.android.view.arcview;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.fuqin.android.view.R;

public class ArcActivity extends AppCompatActivity {
    private ArcView mArcView;
    private int[] mColors = {Color.GREEN, Color.BLUE, Color.RED};
    private double[] mNums = {23.89, 43.89, 65.23};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arc);
        mArcView = findViewById(R.id.arc_view);

    }

    public void onclick(View view) {
        mArcView.setColors(mColors)
                .setText("测试")
                .setNums(mNums)
                .start();
    }
}
