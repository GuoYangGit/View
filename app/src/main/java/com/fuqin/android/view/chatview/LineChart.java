package com.fuqin.android.view.chatview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.fuqin.android.view.R;

import java.util.List;

/**
 * Created by guoyang on 2018/1/15.
 * github https://github.com/GuoYangGit
 * QQ:352391291
 */

public class LineChart extends View {
    private Context mContext;
    private Paint mPaint;//画笔
    private int mXDown, mLastX;
    int minMove = 0;//最短滑动距离
    private float startX, startY;
    private float lastStartX = 0;//抬起手指后，当前控件最左边X的坐标
    private int cellCountW;//一个屏幕的宽度会显示的格子数
    private int cellCountH;//整个控件的高度会显示的格子数
    private float leftTextLength;//左边字体的宽度
    private int textSize;//字体大小
    private int mLineColor = R.color.FEE8E2;
    private int mTextColor = R.color.ACACAC;
    private float cellH, cellW;
    private float maxNum, animationY;
    private PathEffect mEffect = new CornerPathEffect(20);//平滑过渡的角度
    private int state = -100;
    private int lineWidth;
    List<ChatData> data;
    private String[] items;
    private boolean isAnimation;

    public void setData(List<ChatData> data, String[] items) {
        for (int start = 0, end = items.length - 1; start < end; start++, end--) {
            String temp = items[end];
            items[end] = items[start];
            items[start] = temp;
        }
        maxNum = data.get(0).getNum();
        for (int i = 0; i < data.size(); i++) {
            if (maxNum < data.get(i).getNum()) {
                maxNum = data.get(i).getNum();
            }
        }
        this.items = items;
        this.data = data;
        state = -100;
        postInvalidate();
    }

    public LineChart(Context context) {
        this(context, null);
    }

    public LineChart(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public LineChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setClickable(true);
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint();
        lineWidth = dip2px(mContext, 1);
        minMove = dip2px(mContext, ViewConfiguration.get(mContext).getScaledDoubleTapSlop());
        mPaint.setAntiAlias(true);
        textSize = dip2px(mContext, 6);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (data == null || data.size() == 0 || items.length == 0) {
            return;
        }
        cellCountH = items.length;
        cellCountW = data.size();
        startX = getPaddingLeft();
        startY = getPaddingTop() + getHeight() / cellCountH / 2;
        cellH = (getHeight() - getPaddingTop() - getPaddingBottom()) / cellCountH;

        DrawAbscissa(canvas);
        DrawOrdinate(canvas);
        //------------到此背景结束---------------

        canvas.saveLayer(0, 0, getWidth(), getHeight(), mPaint, Canvas.ALL_SAVE_FLAG);
        DrawDataBackground(canvas);
        canvas.restore();

        canvas.saveLayer(0, 0, getWidth(), getHeight(), mPaint, Canvas.ALL_SAVE_FLAG);
        DrawDataLine(canvas);
        canvas.restore();

        showPop(canvas);

        if (state == -100) {
            gotoEnd();
        }
    }

    //画横坐标
    private void DrawOrdinate(Canvas canvas) {
        mPaint.reset();
        int i = 1;
        for (ChatData tmp : data) {
            mPaint.setColor(ContextCompat.getColor(getContext(), mTextColor));
            mPaint.setTextSize(textSize);
            ChatData tmp2 = getDataByX(mLastX);
            //选中的那一项需要加深
            if (tmp2 != null && tmp2.getHappenTime().equals(tmp.getHappenTime()) && state == MotionEvent.ACTION_UP && Math.abs(mLastX - mXDown) < minMove) {
                mPaint.setColor(ContextCompat.getColor(getContext(), mLineColor));
            } else {
                mPaint.setColor(ContextCompat.getColor(getContext(), mTextColor));
            }
            String str = tmp.getHappenTime();
            canvas.drawText(str,
                    leftTextLength + cellW * i - mPaint.measureText(str),
                    getHeight() - getPaddingBottom(),
                    mPaint);
            mPaint.setTextSize(textSize);
            //画背景竖线
            mPaint.setColor(ContextCompat.getColor(getContext(), mTextColor));
            canvas.drawLine(leftTextLength + cellW * i,
                    startY + cellH * (cellCountH - 1),
                    leftTextLength + cellW * i,
                    startY + cellH * (cellCountH - 1) + cellH / 4,
                    mPaint);
            i++;
        }
        mPaint.setColor(ContextCompat.getColor(getContext(), mTextColor));
        mPaint.setTextSize(textSize);
    }

    //画纵坐标
    private void DrawAbscissa(Canvas canvas) {
        mPaint.reset();
        mPaint.setColor(ContextCompat.getColor(getContext(), mTextColor));
        mPaint.setTextSize(textSize);
        leftTextLength = mPaint.measureText(items[0]) + startX;
        cellW = (getWidth() - leftTextLength - getPaddingRight()) / cellCountW;
        for (int i = 0; i < items.length; i++) {
            canvas.drawText(items[i],
                    startX,
                    startY + cellH * i - (mPaint.ascent() + mPaint.descent()) / 2,
                    mPaint);
            canvas.drawLine(leftTextLength + 0.5f,
                    startY + cellH * i,
                    getWidth(),
                    startY + cellH * i,
                    mPaint);
        }
    }

    //画渐变背景
    private void DrawDataBackground(Canvas canvas) {
        if (data == null || data.size() == 0) {
            return;
        }
        LinearGradient lg = new LinearGradient(getWidth() / 2, startY, getWidth() / 2, startY + cellH * (cellCountH - 1), ContextCompat.getColor(mContext, R.color.FEE8E2), ContextCompat.getColor(mContext, R.color.FEE8E1), Shader.TileMode.CLAMP);
        mPaint.setShader(lg);

        int i = 1;
        Path path = new Path();

        //起点和终点要多画2次，防止圆角出现
        path.moveTo(leftTextLength, startY + cellH * (cellCountH - 1));
        path.lineTo(leftTextLength, startY + cellH * (cellCountH - 1));
        path.lineTo(leftTextLength, getHByValue(data.get(0).getNum()));
        for (ChatData tmp : data) {
            path.lineTo(leftTextLength + cellW * i, getHByValue(tmp.getNum()));
            i++;
        }
        path.lineTo(leftTextLength + cellW * (i - 1), getHByValue(data.get(cellCountW - 1).getNum()));
        path.lineTo(leftTextLength + cellW * (i - 1), startY + cellH * (cellCountH - 1));
        path.lineTo(leftTextLength + cellW * (i - 1), startY + cellH * (cellCountH - 1));
        path.close();
        mPaint.setPathEffect(mEffect);
        canvas.drawPath(path, mPaint);
    }

    //画数据线
    private void DrawDataLine(Canvas canvas) {
        int i = 1;
        mPaint.reset();
        mPaint.setStrokeWidth(lineWidth);
        mPaint.setColor(ContextCompat.getColor(getContext(), mLineColor));

        Path path = new Path();
        path.moveTo(leftTextLength, getHByValue(data.get(0).getNum()));
        path.lineTo(leftTextLength, getHByValue(data.get(0).getNum()));
        for (ChatData tmp : data) {
            path.lineTo(leftTextLength + cellW * i, getHByValue(tmp.getNum()));
            i++;
        }
        path.lineTo(leftTextLength + cellW * (i - 1), getHByValue(data.get(cellCountW - 1).getNum()));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setPathEffect(mEffect);
        canvas.drawPath(path, mPaint);
    }

    //显示数据气泡
    private void showPop(Canvas canvas) {
        //点击了
        if (state == MotionEvent.ACTION_UP && Math.abs(mLastX - mXDown) < minMove) {
            ChatData data = getDataByX(mLastX);
            if (data == null) {
                return;
            }
            initPaint();
            // 选中的线
            mPaint.setColor(ContextCompat.getColor(getContext(), mLineColor));
            canvas.drawLine(getXBykey(data.getHappenTime()), getHByValue(data.getNum()), getXBykey(data.getHappenTime()), startY + cellH * (cellCountH - 1) + cellH / 4, mPaint);
            //画气泡背景
            mPaint.setColor(ContextCompat.getColor(getContext(), mLineColor));
            mPaint.setTextSize(textSize);
            Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
            RectF r;

            //气泡距离顶点有0.5个格子高度的距离，气泡的高度是文字高度的1.5倍。宽度是文字宽度的1.6倍（0.8+0.8）
            float left = getXBykey(data.getHappenTime()) - mPaint.measureText(data.getNum() + "%") * 0.8f;
            if (left < 0) {
                left = 0;
            }
            float right = left + 2 * mPaint.measureText(data.getNum() + "%") * 0.8f;
            if (data.getNum() >= 40) {
                r = new RectF(left,
                        getHByValue(data.getNum()) + cellH / 2,
                        right,
                        getHByValue(data.getNum()) + cellH / 2 + 1.5f * (fontMetrics.bottom - fontMetrics.top));
            } else {
                r = new RectF(left,
                        getHByValue(data.getNum()) - cellH / 2 - 1.5f * (fontMetrics.bottom - fontMetrics.top),
                        right,
                        getHByValue(data.getNum()) - cellH / 2);
            }
            //画气泡上的文字
            canvas.drawRoundRect(r, 90, 90, mPaint);
            mPaint.setColor(Color.BLACK);

            float baseline = (r.bottom + r.top - fontMetrics.bottom - fontMetrics.top) / 2;

            canvas.drawText(data.getNum() / 10 + "%",
                    (r.left + r.right) / 2 - mPaint.measureText(data.getNum() + "%") / 2,
                    baseline, mPaint);

            //画线上的圆
            mPaint.setStrokeWidth(lineWidth * 2);
            mPaint.setColor(ContextCompat.getColor(getContext(), mLineColor));
            canvas.drawCircle(getXBykey(data.getHappenTime()), getHByValue(data.getNum()), lineWidth * 5, mPaint);
            mPaint.setColor(Color.WHITE);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(getXBykey(data.getHappenTime()), getHByValue(data.getNum()), lineWidth * 5, mPaint);


            mPaint.setStrokeWidth(lineWidth);

        }
    }

    //触摸处理
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (data == null || data.size() == 0) {
            return super.onTouchEvent(event);
        }
        final int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 按下
                mXDown = (int) event.getRawX();
                state = MotionEvent.ACTION_DOWN;
                break;

            case MotionEvent.ACTION_MOVE:
                // 移动
                mLastX = (int) event.getRawX();

                if (Math.abs(lastStartX - mXDown) < minMove) {
                    break;
                }

                //滑动限制
                if (lastStartX + mLastX - mXDown > 0.5f * cellW || lastStartX + mLastX - mXDown + cellW * (data.size() + 0.5f) < cellW * (cellCountW - 1)) {
                    break;
                }
                state = MotionEvent.ACTION_MOVE;
                startX = lastStartX + mLastX - mXDown;
                postInvalidate();
                break;

            case MotionEvent.ACTION_UP:
                // 抬起
                lastStartX = startX;
                state = MotionEvent.ACTION_UP;
                postInvalidate();
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    //通过坐标，获得附近的点
    private ChatData getDataByX(int pointX) {
        int i = 1;
        ChatData result = null;
        for (ChatData tmp : data) {
            float x = leftTextLength + cellW * i;
            if (Math.abs(x - pointX) < cellW / 2) {
                result = tmp;
                return result;
            }
            i++;
        }
        return result;
    }

    private float getHByValue(float value) {
        if (value > animationY && isAnimation) {
            return cellH * cellCountH + startY - cellH * cellCountH * animationY / 100;
        } else {
            return cellH * cellCountH + startY - cellH * cellCountH * value / 100;
        }
    }

    public void startAnimation() {
        isAnimation = true;
        ValueAnimator animator = ValueAnimator.ofFloat(20, maxNum);
        animator.setDuration(3000);
        animator.addUpdateListener(animation -> {
            animationY = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    //通过横坐标文字获取该点的X坐标
    private float getXBykey(String key) {
        int i = 1;
        for (ChatData tmp : data) {
            if (tmp.getHappenTime().equals(key)) {
                return leftTextLength + cellW * i;
            }
            i++;
        }
        return 0;
    }


    //显示最左边的最新数据
    private void gotoEnd() {
        if (data == null || data.size() == 0) {
            return;
        }
        if (data.size() < cellCountW - 1) {
            postInvalidate();
            return;
        }
        postInvalidate();
    }

    private int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}