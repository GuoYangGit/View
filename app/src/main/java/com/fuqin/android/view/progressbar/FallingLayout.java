package com.fuqin.android.view.progressbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fuqin.android.view.R;

import java.util.List;
import java.util.Random;

/**
 * Created by guoyang on 2018/1/25.
 * github https://github.com/GuoYangGit
 * QQ:352391291
 */

public class FallingLayout extends RelativeLayout {
    private int drawableId = R.mipmap.money; //红包的imgResId
    private int[] bombImgId = {R.mipmap.bong0001, R.mipmap.bong0002, R.mipmap.bong0003, R.mipmap.bong0004
            , R.mipmap.bong0005, R.mipmap.bong0006, R.mipmap.bong0007, R.mipmap.bong0008
            , R.mipmap.bong0009, R.mipmap.bong0010, R.mipmap.bong0011}; //炸弹的imgResId
    private FallingHandler mFallingHandler; //添加红包的handler
    private int mHeight, mWidth;
    private Random random = new Random(); //随机数
    private long imgDuration = 2500; //红包下落时间
    private float imgRotation = 30; //红包倾斜角度
    private float imgScale = 0.5f; //红包的缩放大小


    public FallingLayout(Context paramContext) {
        this(paramContext, null);
    }

    public FallingLayout(Context paramContext, AttributeSet paramAttributeSet) {
        this(paramContext, paramAttributeSet, 0);
    }

    public FallingLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
    }

    public void addFallingBody(FallBean fallBean) {
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
            switch (fallBean.getStatus()) {
                case 0:
                    showBombView(x, y);
                    break;
                case 1:
                    showRewardView(x, y, fallBean.getMessage(), R.mipmap.gold);
                    break;
                case 2:
                    showRewardView(x, y, fallBean.getMessage(), R.mipmap.coupons);
                    break;
                case 3:
                    showRewardView(x, y, fallBean.getMessage(), R.mipmap.vip);
                    break;
            }
        });
    }

    private void showRewardView(float x, float y, String message, int resId) {
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setGravity(Gravity.CENTER_VERTICAL);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setX(x);
        TextView textView = new TextView(getContext());
        textView.setTextSize(16);
        textView.setTextColor(Color.YELLOW);
        textView.setText(message);
        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(resId);
        addView(linearLayout);
        linearLayout.addView(textView);
        linearLayout.addView(imageView);
        showRewardAnimation(linearLayout, y);
    }

    private void showRewardAnimation(LinearLayout linearLayout, float y) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.5f, 1);
        valueAnimator.setDuration(2000);
        valueAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            linearLayout.setAlpha(value);
            linearLayout.setY(y - mHeight / 10 * value);
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                removeView(linearLayout);
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
                postDelayed(() -> removeView(imageView), 300);
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

    public void start(int totalNum, long totalTime, List<FallBean> fallList) {
        if (totalNum == 0 || mFallingHandler != null) {
            return;
        }
        mFallingHandler = new FallingHandler(this);
        mFallingHandler.addTask(totalNum, (totalTime - imgDuration) / totalNum, fallList);
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

    /**
     * dp 2 px
     *
     * @param dpVal
     */
    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }
}