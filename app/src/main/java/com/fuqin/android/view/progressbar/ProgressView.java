package com.fuqin.android.view.progressbar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.print.PrintJob;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.fuqin.android.view.R;

/**
 * Created by guoyang on 2018/1/26.
 * github https://github.com/GuoYangGit
 * QQ:352391291
 */

public class ProgressView extends View {
    private Paint bgPaint;
    private Paint progressPaint;
    private Paint textPaint;

    private int mWidth;
    private int mHeight;

    private int[] redImgId = {R.mipmap.hongbao0001, R.mipmap.hongbao0002, R.mipmap.hongbao0003
            , R.mipmap.hongbao0004, R.mipmap.hongbao0005, R.mipmap.hongbao0006, R.mipmap.hongbao0007
            , R.mipmap.hongbao0008, R.mipmap.hongbao0009, R.mipmap.hongbao0010, R.mipmap.hongbao0011
            , R.mipmap.hongbao0012, R.mipmap.hongbao0013, R.mipmap.hongbao0014, R.mipmap.hongbao0015
            , R.mipmap.hongbao0016, R.mipmap.hongbao0017, R.mipmap.hongbao0018, R.mipmap.hongbao0019
            , R.mipmap.hongbao0020, R.mipmap.hongbao0021, R.mipmap.hongbao0022, R.mipmap.hongbao0023
            , R.mipmap.hongbao0024, R.mipmap.hongbao0025, R.mipmap.hongbao0026, R.mipmap.hongbao0027
            , R.mipmap.hongbao0028, R.mipmap.hongbao0029}; //炸弹的imgResId
    private int index = 0;
    private Paint mRedPaint;
    private long currentTime;

    private float mProgress;
    private float textHeight;
    private float textWidth;

    private int roundRectRadius;
    private int progressHeight;
    private float moveDis;
    private float currentProgress;
    private ValueAnimator progressAnimator;

    private int duration = 5000;
    private Rect textRect = new Rect();
    private RectF bgRectF = new RectF();
    private RectF progressRectF = new RectF();

    private String textString = "0%";
    private int textPaintSize;
    private float animationValue;

    private int bgColor;
    private int progressColor;

    private ProgressListener progressListener;

    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initPaint();
        initTextPaint();
        initRedPaint();
    }

    private void initRedPaint() {
        mRedPaint = new Paint();
        mRedPaint.setAntiAlias(true);
    }

    /**
     * 初始化画笔宽度及view大小
     */
    private void init() {
        bgColor = ContextCompat.getColor(getContext(), R.color.red_dark);
        progressColor = ContextCompat.getColor(getContext(), R.color.yellow);
        progressHeight = dp2px(10);
        roundRectRadius = dp2px(9);
        textPaintSize = sp2px(10);
        textHeight = dp2px(10);
    }


    private void initPaint() {
        bgPaint = getPaint(progressHeight, bgColor, Paint.Style.FILL);
        progressPaint = getPaint(progressHeight, progressColor, Paint.Style.FILL);
    }

    /**
     * 初始化文字画笔
     */
    private void initTextPaint() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textPaintSize);
        textPaint.setColor(progressColor);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
    }


    /**
     * 统一处理paint
     *
     * @param strokeWidth 画笔宽度
     * @param color       颜色
     * @param style       风格
     * @return paint
     */
    private Paint getPaint(int strokeWidth, int color, Paint.Style style) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setStyle(style);
        return paint;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        mHeight = getHeight() - getPaddingTop() - getPaddingBottom();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制文字
        drawText(canvas, textString);
        //背景
        drawBgProgress(canvas);
        //进度条
        drawProgress(canvas);

        drawRedPackage(canvas);
    }

    private void drawRedPackage(Canvas canvas) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), redImgId[index]);
        if (progressRectF.right - getPaddingLeft() - bitmap.getWidth() < 0) return;
        canvas.drawBitmap(bitmap, progressRectF.right - bitmap.getWidth(), progressRectF.bottom - bitmap.getHeight(), mRedPaint);
    }

    private void drawBgProgress(Canvas canvas) {
        bgRectF.left = getPaddingLeft();
        bgRectF.top = getPaddingTop();
        bgRectF.right = bgRectF.left + mWidth;
        bgRectF.bottom = bgRectF.top + progressHeight;
        canvas.drawRoundRect(bgRectF, roundRectRadius, roundRectRadius, bgPaint);
    }

    private void drawProgress(Canvas canvas) {
        progressRectF.left = getPaddingLeft();
        progressRectF.top = getPaddingTop();
        progressRectF.right = progressRectF.left + currentProgress;
        progressRectF.bottom = progressRectF.top + progressHeight;
        canvas.drawRoundRect(progressRectF, roundRectRadius, roundRectRadius, progressPaint);
    }


    /**
     * 绘制文字
     *
     * @param canvas 画布
     */
    private void drawText(Canvas canvas, String textString) {
        textRect.left = (int) moveDis + getPaddingLeft();
        textRect.top = getPaddingTop() + progressHeight;
        textRect.right = (int) (textPaint.measureText(textString) + moveDis + getPaddingLeft());
        textRect.bottom = (int) (getPaddingTop() + progressHeight + textHeight);
        //文字绘制到整个布局的中心位置
        canvas.drawText(textString, textRect.centerX(), textRect.bottom, textPaint);
    }


    /**
     * 进度移动动画  通过插值的方式改变移动的距离
     */
    private void initAnimation() {
        progressAnimator = ValueAnimator.ofFloat(0, mProgress);
        progressAnimator.setDuration(duration);
        progressAnimator.setInterpolator(new AccelerateInterpolator());
        progressAnimator.addUpdateListener(valueAnimator -> {
            float value = (float) valueAnimator.getAnimatedValue();
            if (value - animationValue >= 6 || value == mProgress) {
                textString = (int) value + "%";
                textWidth = textPaint.measureText(textString);
                animationValue = value;
            }
            currentProgress = value * mWidth / 100;
            if (progressListener != null) {
                progressListener.currentProgressListener(value);
            }
            if (valueAnimator.getCurrentPlayTime() - currentTime >= 40) {
                currentTime = valueAnimator.getCurrentPlayTime();
                index++;
                if (index == 29) index = 0;
            }
            //移动百分比提示框，只有当前进度到提示框中间位置之后开始移动，当进度框移动到最右边的时候停止移动，但是进度条还可以继续移动
            if (currentProgress >= textWidth && currentProgress <= mWidth) {
                moveDis = currentProgress - textWidth;
            }
            invalidate();
        });
        if (!progressAnimator.isStarted()) {
            progressAnimator.start();
        }
    }

    /**
     * 回调接口
     */
    public interface ProgressListener {
        void currentProgressListener(float currentProgress);
    }

    /**
     * 回调监听事件
     *
     * @param listener
     * @return
     */
    public ProgressView setProgressListener(ProgressListener listener) {
        progressListener = listener;
        return this;
    }

    public ProgressView setProgress(int progress) {
        mProgress = progress;
        textString = "0%";
        currentProgress = 0;
        animationValue = 0;
        moveDis = 0;
        index = 0;
        currentTime = 0;
        textWidth = textPaint.measureText(textString);
        initAnimation();
        return this;
    }

    public ProgressView clear() {
        if (progressAnimator.isRunning()) {
            progressAnimator.cancel();
            progressAnimator = null;
        }
        return this;
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

    /**
     * sp 2 px
     *
     * @param spVal
     * @return
     */
    protected int sp2px(int spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, getResources().getDisplayMetrics());
    }
}
