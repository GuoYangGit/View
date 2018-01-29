package com.fuqin.android.view.progressbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.fuqin.android.view.R;

/**
 * Created by guoyang on 2018/1/26.
 * github https://github.com/GuoYangGit
 * QQ:352391291
 */

public class DownTimeLayout extends RelativeLayout {
    private int[] mImgIds = {R.mipmap.three, R.mipmap.two, R.mipmap.one};
    private float[] scaleValues = {2.8f, 1.8f, 2.0f, 2.0f, 2.0f, 2.0f};
    private float[] alphaValues = {0.6f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f};
    private int mImgIdInDex = 0;
    private AnimatorEndListener mAnimatorEndListener;

    public DownTimeLayout(Context context) {
        super(context, null);
    }

    public DownTimeLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public DownTimeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void startAnimation(int index) {
        mImgIdInDex = index + 1;
        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(mImgIds[index]);
        ValueAnimator alphaAnimator = ValueAnimator.ofFloat(alphaValues);
        alphaAnimator.setDuration(1200);
        alphaAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            if (value == imageView.getAlpha()) return;
            imageView.setAlpha(value);
        });
        alphaAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                removeView(imageView);
            }
        });
        ValueAnimator scaleAnimator = ValueAnimator.ofFloat(scaleValues);
        scaleAnimator.setDuration(1000);
        scaleAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            if (imageView.getScaleX() == value) return;
            imageView.setScaleX(value);
            imageView.setScaleY(value);
        });
        scaleAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mImgIdInDex >= mImgIds.length) {
                    if (mAnimatorEndListener != null) mAnimatorEndListener.animatorend();
                    return;
                }
                ;
                startAnimation(mImgIdInDex);
            }
        });
        addView(imageView);
        AnimatorSet animationSet = new AnimatorSet();
        animationSet.setInterpolator(new LinearInterpolator());
        animationSet.play(alphaAnimator).with(scaleAnimator);
        animationSet.start();
    }

    public void setAnimatorEndListener(AnimatorEndListener animatorEndListener) {
        mAnimatorEndListener = animatorEndListener;
    }

    interface AnimatorEndListener {
        void animatorend();
    }
}
