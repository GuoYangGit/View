package com.fuqin.android.view.progressbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.fuqin.android.view.R;


/**
 * Created by Allen on 2017/11/30.
 * <p>
 * 产品购买进度条
 */

public class ProductProgressBar extends View {
    private ValueAnimator alphaAnimator;
    private long alphaDuration = 3000L;
    private long changeDuration = 1500L;
    private ValueAnimator changeWidthAnimator;
    private float currentProgress;
    private boolean isAlpha;
    private boolean isChangeWidth;
    private float mHeight;
    private float mProgress;
    private float progressAlpha = 0.5F;
    private ValueAnimator progressAnimator;
    private int progressColor;
    private long progressDuration = 15000L;
    private int progressEndWidth;
    private ProgressListener progressListener;
    private Paint progressPaint;
    private RectF progressRectF = new RectF();
    private int progressStartWidth;
    private int progressWidth;
    private int roundRectRadius;

    public ProductProgressBar(Context paramContext) {
        this(paramContext, null);
    }

    public ProductProgressBar(Context paramContext, @Nullable AttributeSet paramAttributeSet) {
        this(paramContext, paramAttributeSet, 0);
    }

    public ProductProgressBar(Context paramContext, @Nullable AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        init();
        initPaint();
    }

    private void init() {
        roundRectRadius = dp2px(3);
        progressColor = ContextCompat.getColor(getContext(),R.color.yellow);
    }

    private void initAnimation() {
        progressAnimator = ValueAnimator.ofFloat(0.0F, mProgress);
        progressAnimator.setDuration(progressDuration);
        progressAnimator.setInterpolator(new LinearInterpolator());
        progressAnimator.addUpdateListener(valueAnimator -> {
            float value = (float) valueAnimator.getAnimatedValue();
            currentProgress = value / 100 * mHeight;
            if ((progressDuration - valueAnimator.getCurrentPlayTime() <= alphaDuration) && !isAlpha) {
                isAlpha = true;
                startAlpha();
            }
            if (progressDuration - valueAnimator.getCurrentPlayTime() <= alphaDuration + changeDuration && !isChangeWidth) {
                isChangeWidth = true;
                startChangeWidth();
            }
            if (progressListener != null) {
                progressListener.currentProgressListener(value);
            }
            invalidate();
        });
        progressAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator paramAnonymousAnimator) {
                super.onAnimationEnd(paramAnonymousAnimator);
                if (changeWidthAnimator != null) changeWidthAnimator.cancel();
                if (alphaAnimator != null) alphaAnimator.cancel();
            }
        });
        progressAnimator.start();
    }

    private void initPaint() {
        progressPaint = new Paint(1);
        progressPaint.setStrokeWidth(progressWidth);
        progressPaint.setColor(progressColor);
        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.FILL);
    }

    private void startAlpha() {
        progressPaint.setColor(ContextCompat.getColor(getContext(),R.color.red));
        alphaAnimator = ValueAnimator.ofFloat(1.0F, 0.0F, 1.0F, 0.0F, 1.0F, 0.0F);
        alphaAnimator.setDuration(this.alphaDuration);
        alphaAnimator.setInterpolator(new LinearInterpolator());
        alphaAnimator.addUpdateListener(valueAnimator ->
                progressAlpha = (float) valueAnimator.getAnimatedValue()
        );
        alphaAnimator.start();
    }

    private void startChangeWidth() {
        changeWidthAnimator = ValueAnimator.ofInt(progressStartWidth, progressEndWidth);
        changeWidthAnimator.setDuration(this.changeDuration);
        changeWidthAnimator.addUpdateListener(valueAnimator -> {
            progressWidth = (int) valueAnimator.getAnimatedValue();
            progressAlpha = 0.5f + 0.5f * valueAnimator.getCurrentPlayTime() / changeDuration;
        });
        changeWidthAnimator.start();
    }

    protected int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    protected void onDraw(Canvas paramCanvas) {
        super.onDraw(paramCanvas);
        progressRectF.left = getPaddingLeft();
        progressRectF.top = currentProgress;
        progressRectF.right = progressWidth + getPaddingLeft();
        progressRectF.bottom = (mHeight - getPaddingBottom());
        paramCanvas.drawRoundRect(progressRectF, roundRectRadius, roundRectRadius, progressPaint);
        setAlpha(progressAlpha);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight = getMeasuredHeight();
        progressEndWidth = (getMeasuredWidth() - getPaddingLeft() - getPaddingRight());
        progressStartWidth = (progressEndWidth / 3);
    }

    public ProductProgressBar setProgress(float progress) {
        mProgress = progress;
        isAlpha = false;
        isChangeWidth = false;
        progressWidth = progressStartWidth;
        currentProgress = 0.0F;
        progressAlpha = 0.5F;
        initAnimation();
        return this;
    }

    public void clear() {
        if (changeWidthAnimator != null) {
            changeWidthAnimator.cancel();
            changeWidthAnimator = null;
        }
        if (progressAnimator != null) {
            progressAnimator.cancel();
            progressAnimator = null;
        }
        if (alphaAnimator != null) {
            alphaAnimator.cancel();
            alphaAnimator = null;
        }
    }

    public ProductProgressBar setProgressListener(ProgressListener paramProgressListener) {
        progressListener = paramProgressListener;
        return this;
    }

    public interface ProgressListener {
        void currentProgressListener(float paramFloat);
    }
}