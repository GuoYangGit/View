package com.fuqin.android.view.progressbar;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fuqin.android.view.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProgressBarActivity extends AppCompatActivity {
    private ProductProgressBar mProgressBar;
    private ProgressView mProgressView;
    private FallingLayout mFallingLayout;
    private RelativeLayout mRelativeLayout;
    private DownTimeLayout mDownTimeLayout;
    private LinearLayout mLoadingLayout;
    private LinearLayout mLoadFailLayout;
    private Button mLoadFailButton;
    private List<FallBean> mFallBeanList;
    private Random mRandom = new Random();
    private FallBean[] mFallBeans = {new FallBean(0, "bom"), new FallBean(1, "+1000")
            , new FallBean(2, "+0.5%"), new FallBean(3, "爱奇艺VIP")};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_bar);
        initView();
        initData();
        mProgressView.setProgress(100);
        mProgressView.setProgressListener(currentProgress -> {
            if (currentProgress == 100) {
                mRelativeLayout.removeView(mLoadingLayout);
                mDownTimeLayout.startAnimation(0);
                mDownTimeLayout.setAnimatorEndListener(() -> {
                    mProgressBar.setProgress(100);
                    mFallingLayout.start(50, 15000, mFallBeanList);
                });
            }
        });
        new Handler().postDelayed(() -> {
            mProgressView.clear();
            mLoadingLayout.setVisibility(View.GONE);
            mLoadFailLayout.setVisibility(View.VISIBLE);
        }, 3000);
    }

    private void initData() {
        mFallBeanList = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            mFallBeanList.add(mFallBeans[mRandom.nextInt(3)]);
        }
    }

    private void initView() {
        mProgressBar = findViewById(R.id.progressbar);
        mFallingLayout = findViewById(R.id.fallinglayout);
        mProgressView = findViewById(R.id.progressview);
        mRelativeLayout = findViewById(R.id.relativeLayout);
        mDownTimeLayout = findViewById(R.id.downTimeLayout);
        mLoadingLayout = findViewById(R.id.loading_layout);
        mLoadFailLayout = findViewById(R.id.load_fail_layout);
        mLoadFailButton = findViewById(R.id.load_fail_button);
        mLoadFailButton.setOnClickListener(v -> {
            mLoadingLayout.setVisibility(View.VISIBLE);
            mLoadFailLayout.setVisibility(View.GONE);
            mProgressView.setProgress(100);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFallingLayout.clean();
        mProgressBar.clear();
        mProgressView.clear();
    }
}
