package com.fuqin.android.view.progressbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fuqin.android.view.R;

import java.util.Random;

/**
 * Created by guoyang on 2018/1/25.
 * github https://github.com/GuoYangGit
 * QQ:352391291
 */

public class FallingLayout extends RelativeLayout {
    private int drawableId = R.mipmap.money;
    private int[] bombImgId = {R.mipmap.bong_1, R.mipmap.bong_2, R.mipmap.bong_3, R.mipmap.bong_4, R.mipmap.bong_5};
    private FallingHandler mFallingHandler;
    private int mHeight;
    private int mWidth;
    private Random random = new Random();
    private long imgDuration = 2500;
    private float imgRotation = 30;
    private float imgScale = 0.5f;

    public FallingLayout(Context paramContext) {
        this(paramContext, null);
    }

    public FallingLayout(Context paramContext, AttributeSet paramAttributeSet) {
        this(paramContext, paramAttributeSet, 0);
    }

    public FallingLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
    }

    public void addFallingBody() {
        ImageView imageView = new ImageView(getContext());
        addView(imageView);
        imageView.setImageResource(drawableId);
        imageView.setRotation(imgRotation);
        float scale = (imgScale * 10 + random.nextInt(5)) / 10;
        imageView.setScaleX(scale);
        imageView.setScaleY(scale);
        ValueAnimator valueAnimator = getImageAnimation(imageView);
        valueAnimator.start();
        imageView.setOnClickListener(v -> {
            valueAnimator.cancel();
            float x = imageView.getX();
            float y = imageView.getY();
            if (random.nextInt(2) == 1) {
                showBombView(x, y);
            } else {
                showRewardView(x, y);
            }
        });
    }

    private void showRewardView(float x, float y) {
        TextView textView = new TextView(getContext());
        addView(textView);
        textView.setTextSize(20);
        textView.setTextColor(Color.YELLOW);
        textView.setText("你很皮+1");
        textView.setX(x);
        showRewardAnimation(textView, y);
    }

    private void showRewardAnimation(TextView textView, float y) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(2000);
        valueAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            textView.setAlpha(value);
            textView.setY(y - mHeight / 5 * value);
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                removeView(textView);
            }
        });
        valueAnimator.start();
    }

    private void showBombView(float x, float y) {
        ImageView imageView = new ImageView(getContext());
        addView(imageView);
        imageView.setX(x);
        imageView.setY(y);
        showBombAnimation(imageView);
    }


    private void showBombAnimation(ImageView imageView) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(bombImgId);
        valueAnimator.setDuration(160);
        valueAnimator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            imageView.setImageResource(value);
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                postDelayed(() -> removeView(imageView), 340);
            }
        });
        valueAnimator.start();
    }

    private PathMeasure createPathMeasure() {
        Path path = new Path();
        int startX = mWidth;
        int startY = random.nextInt(mHeight / 3);
        int endX = random.nextInt(mWidth / 2);
        int endY = mHeight;
        path.moveTo(startX, startY);
        path.lineTo(endX, endY);
        return new PathMeasure(path, false);
    }

    private ValueAnimator getImageAnimation(ImageView view) {
        PathMeasure pathMeasure = createPathMeasure();
        float length = pathMeasure.getLength();
        float[] pos = new float[2];
        float[] tan = new float[2];
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(imgDuration);
        valueAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            pathMeasure.getPosTan(length * value, pos, tan);
            view.setX(pos[0]);
            view.setY(pos[1]);
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                removeView(view);
            }
        });
        return valueAnimator;
    }

    public void start(int totalNum, long totalTime) {
        if (totalNum == 0 || mFallingHandler != null) {
            return;
        }
        mFallingHandler = new FallingHandler(this);
        mFallingHandler.addTask(totalNum, (totalTime - imgDuration) / totalNum);
        mFallingHandler.onStart();
    }

    public void clean() {
        if (mFallingHandler != null) {
            mFallingHandler.onClear();
            mFallingHandler = null;
        }
    }

    protected void onMeasure(int paramInt1, int paramInt2) {
        super.onMeasure(paramInt1, paramInt2);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }
}