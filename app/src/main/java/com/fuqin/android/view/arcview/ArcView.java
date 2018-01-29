package com.fuqin.android.view.arcview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.fuqin.android.view.R;

/**
 * Created by guoyang on 2018/1/4.
 * github https://github.com/GuoYangGit
 * QQ:352391291
 */

public class ArcView extends View {
    //设置圆弧的宽度
    private float mStrokeWidth;
    //圆弧的画笔
    private Paint mArcPaint;
    //文字画笔
    private Paint mTextPaint;
    //圆弧的角度
    private float mArcAngle = 270;
    //数据的颜色
    private int[] mColors;
    //数据的角度
    private float[] mArcAngles;
    //数据
    private double[] mNums;
    //总数据
    private double mTotalNum;
    //默认圆弧的颜色
    private int mPaintColor;
    //文字
    private String mText;
    //文字颜色
    private int mTextColor;
    //文字大小
    private int mTextSize;
    //动画的监听器
    private ValueAnimator mValueAnimator;

    public ArcView(Context context) {
        this(context, null);
    }

    public ArcView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ArcView);
        mPaintColor = array.getColor(R.styleable.ArcView_arcview_norColor, ContextCompat.getColor(context, R.color.gray_line));
        mStrokeWidth = array.getDimension(R.styleable.ArcView_arcview_borderWidth, dip2px(context, 35));
        mText = array.getString(R.styleable.ArcView_arcview_text);
        mTextColor = array.getColor(R.styleable.ArcView_arcview_textColor, ContextCompat.getColor(context, R.color.gray_text));
        mTextSize = array.getDimensionPixelSize(R.styleable.ArcView_arcview_textSize, dip2px(context, 15));
        array.recycle();
        //初始化圆弧画笔
        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(mStrokeWidth);
        mArcPaint.setColor(mPaintColor);
        //初始化文字画笔
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        //初始化动画监听器
        mValueAnimator = ValueAnimator.ofFloat(0, 360);
        mValueAnimator.setInterpolator(new DecelerateInterpolator());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //圆弧画布
        RectF rectF = new RectF(mStrokeWidth, mStrokeWidth, getWidth() - mStrokeWidth, getHeight() - mStrokeWidth);
        //判断是否有数据
        if (mNums != null && mNums.length > 0) {
            float currentStartAngle = 0;
            for (int i = 0; i < mNums.length; i++) {
                mArcPaint.setColor(mColors[i]);
                if (mArcAngle > mArcAngles[i]) {
                    canvas.drawArc(rectF, currentStartAngle, mArcAngles[i], false, mArcPaint);
                } else {
                    canvas.drawArc(rectF, currentStartAngle, mArcAngle, false, mArcPaint);
                }
                mArcAngle = mArcAngle - mArcAngles[i];
                if (mArcAngle <= 0) {
                    mArcAngle = 360;
                    break;
                }
                currentStartAngle += mArcAngles[i];
            }
        } else {
            canvas.drawArc(rectF, 0, 0, false, mArcPaint);
        }
        //判断是否需要画中间文字
        if (!TextUtils.isEmpty(mText)) {
            Rect textBounds = new Rect();
            mTextPaint.getTextBounds(mText, 0, mText.length(), textBounds);
            int dx = getWidth() / 2 - textBounds.width() / 2;
            //基线 baseLine
            Paint.FontMetricsInt fontMetricsInt = mTextPaint.getFontMetricsInt();
            int dy = (fontMetricsInt.bottom - fontMetricsInt.top) / 2 - fontMetricsInt.bottom;
            int baseLine = getHeight() / 2 + dy;
            canvas.drawText(mText, dx, baseLine, mTextPaint);
        }
    }

    /**
     * 设置数据的颜色
     *
     * @param colors
     * @return
     */
    public ArcView setColors(int... colors) {
        mColors = colors;
        return this;
    }

    /**
     * 设置数据
     *
     * @param nums
     * @return
     */
    public ArcView setNums(double... nums) {
        mNums = nums;
        return this;
    }

    /**
     * 设置文字
     *
     * @param text
     * @return
     */
    public ArcView setText(String text) {
        mText = text;
        return this;
    }

    /**
     * 开启动画
     */
    public void start() {
        if (mNums != null && mNums.length > 0) getTotalNum();
        mValueAnimator.setDuration(3000);
        mValueAnimator.addUpdateListener(animation -> {
            mArcAngle = (float) animation.getAnimatedValue();
            invalidate();
        });
        mValueAnimator.start();
    }

    /**
     * 获取总数据并计算各个数据占比
     */
    private void getTotalNum() {
        for (double mNum : mNums) {
            mTotalNum += mNum;
        }
        mArcAngles = new float[mNums.length];
        for (int i = 0; i < mNums.length; i++) {
            mArcAngles[i] = (float) (mNums[i] * 360 / mTotalNum);
        }
    }

    private int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
