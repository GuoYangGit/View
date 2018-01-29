package com.fuqin.android.view.neterrorview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.fuqin.android.view.util.XChartCalc;

/**
 * Created by guoyang on 2018/1/8.
 * github https://github.com/GuoYangGit
 * QQ:352391291
 */

public class NetErrorBottom extends View {
    private String mText = "刷新";
    //圆弧画笔
    private Paint mArcPaint;
    //文字画笔
    private Paint mTextPaint;
    //三角箭头画笔
    private Paint mTrianglePaint;

    //圆弧起始角度
    private int mStartAngle = 30;
    //圆弧的旋转角度
    private int mSweepAngle = 300;
    //圆弧的长径
    private int mArcWith;
    //文字的宽度
    private int mTextWith;
    //圆弧与文字之间的间隙
    private int mPanding;
    //圆弧x距离中心x的距离
    private int mArcPandingCenter;
    //文字起始点x距离空间rightX的距离
    private int mTextPandingRight;
    //圆弧移动的距离
    private int mArcMove;
    //文字移动的距离
    private float mTextMove;
    //文字颜色
    private int mTextColor = Color.WHITE;
    //文字大小
    private int mTextSize;
    //圆弧的颜色
    private int mArcColor = Color.WHITE;
    //计算圆弧某角度的x,y坐标点工具类
    private XChartCalc mXChartCalc;

    public NetErrorBottom(Context context) {
        this(context, null);
    }

    public NetErrorBottom(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NetErrorBottom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mArcWith = dip2px(context, 15);
        mTextSize = dip2px(context, 17);
        mPanding = dip2px(context, 7);

        //初始化工具类
        mXChartCalc = new XChartCalc();

        //初始化三角箭头画笔
        mTrianglePaint = new Paint();
        mTrianglePaint.setAntiAlias(true);
        mTrianglePaint.setStrokeCap(Paint.Cap.ROUND);
        mTrianglePaint.setStyle(Paint.Style.STROKE);
        mTrianglePaint.setStrokeWidth(dip2px(getContext(), 2));
        mTrianglePaint.setColor(mArcColor);

        //初始化圆弧画笔
        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStrokeCap(Paint.Cap.ROUND);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(dip2px(getContext(), 2));
        mArcPaint.setColor(mArcColor);

        //初始化文字画笔
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //获取文字的宽度
        Rect textBounds = new Rect();
        mTextPaint.getTextBounds(mText, 0, mText.length(), textBounds);
        mTextWith = textBounds.width();

        //计算画圆弧的起始点
        int arcX = getWidth() / 2 - (mArcWith + mTextWith + mPanding) / 2;
        int arcY = getHeight() / 2 - mArcWith / 2;

        //计算圆弧距离中心点X的位置
        mArcPandingCenter = getWidth() / 2 - arcX - mArcWith / 2;

        //画圆弧
        RectF rectF = new RectF(arcX + mArcMove, arcY, arcX + mArcWith + mArcMove, arcY + mArcWith);
        canvas.drawArc(rectF, mStartAngle, mSweepAngle, false, mArcPaint);

        //获取圆弧的结束角度点的X,Y
        int arccenterX = arcX + mArcMove + mArcWith / 2;
        int arccenterY = arcY + mArcWith / 2;
        mXChartCalc.CalcArcEndPointXY(arccenterX, arccenterY, mArcWith / 2, mStartAngle + mSweepAngle);
        //画三角箭头
        float triandleX = mXChartCalc.getPosX();
        float triandleY = mXChartCalc.getPosY();
        mXChartCalc.CalcArcEndPointXY(triandleX, triandleY, dip2px(getContext(), 4), mStartAngle + 145);
        canvas.drawLine(mXChartCalc.getPosX(), mXChartCalc.getPosY(), triandleX, triandleY, mTrianglePaint);
        mXChartCalc.CalcArcEndPointXY(triandleX, triandleY, dip2px(getContext(), 4), mStartAngle + 240);
        canvas.drawLine(mXChartCalc.getPosX(), mXChartCalc.getPosY(), triandleX, triandleY, mTrianglePaint);

        //画文字
        int dx = arcX + mArcWith + mPanding;
        mTextPandingRight = getWidth() - dx;
        //基线 baseLine
        Paint.FontMetricsInt fontMetricsInt = mTextPaint.getFontMetricsInt();
        int dy = (fontMetricsInt.bottom - fontMetricsInt.top) / 2 - fontMetricsInt.bottom;
        int baseLine = getHeight() / 2 + dy;
        canvas.drawText(mText, dx + mTextMove, baseLine, mTextPaint);
    }

    private int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 开启动画
     */
    public void startAnimation() {
        startArcAnimation();
        startOutAnimation();
    }

    /**
     * 开启旋转动画
     */
    private void startArcAnimation() {
        ValueAnimator animator = ObjectAnimator.ofInt(30, 1110);
        animator.setDuration(2000);
        animator.addUpdateListener(animation -> {
            int angle = (int) animation.getAnimatedValue();
            mStartAngle = angle % 360;
            if (mStartAngle >= 60) {
                mStartAngle = -(360 - mStartAngle);
            }
            invalidate();
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                startInputAnimation();
            }
        });
        animator.start();
    }

    /**
     * 开启textview退出动画
     */
    private void startOutAnimation() {
        ValueAnimator animator = ObjectAnimator.ofInt(0, mArcPandingCenter);
        animator.setDuration(450);
        animator.addUpdateListener(animation -> {
            int move = (int) animator.getAnimatedValue();
            mArcMove = move;
            mTextMove = ((float) mTextPandingRight / mArcPandingCenter) * move;
        });
        animator.start();
    }

    /**
     * 开启textview进入动画
     */
    private void startInputAnimation() {
        ValueAnimator animator = ObjectAnimator.ofInt(mArcPandingCenter, 0);
        animator.setDuration(450);
        animator.addUpdateListener(animation -> {
            int move = (int) animator.getAnimatedValue();
            mArcMove = move;
            mTextMove = ((float) mTextPandingRight / mArcPandingCenter) * move;
            invalidate();
        });
        animator.start();
    }
}
