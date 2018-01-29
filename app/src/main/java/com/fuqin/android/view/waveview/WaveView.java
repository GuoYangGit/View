package com.fuqin.android.view.waveview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;

import com.fuqin.android.view.R;

import java.util.ArrayList;
import java.util.List;

import static android.view.animation.Animation.INFINITE;

/**
 * Created by guoyang on 2018/1/4.
 * github https://github.com/GuoYangGit
 * QQ:352391291
 */

public class WaveView extends View {
    private Context mContext;

    private Paint mPaint;
    private Paint wavePaint;

    private Path wavePath;

    private Path shadPath;

    private int WAVE_COLOR = Color.BLUE;
    private int BG_COLOR = Color.WHITE;

    private float VIEW_WIDTH = 0f;
    private float VIEW_HEIGHT = 0f;

    private float WAVE_WIDTH = 0f;
    private float WAVE_HEIGHT = 0f;

    private List<Point> pointList = new ArrayList<>();
    private List<Point> shadpointList = new ArrayList<>();

    private boolean isInitPoint = true;
    private boolean isStartAnimation = false;
    private boolean isDone = false;
    private boolean isMeasure = false;
    private boolean isCompleteLayout = false;
    boolean isHasWindowFocus = false;

    private float dy = 0;
    private float old_dy = 0;
    private float sum_dy = 0;
    private float beforDy = 0;

    private float dx = 0;
    private float shd_dx = 0;
    private float runRatio = 1.5f;

    private float speed = 50f;

    private long progress = 0;
    private long curProgress = 0;
    private long max = 100;
    private float progressRatio = 0f;

    private waveProgressListener progressListener;

    private ValueAnimator reiseAnimator;
    private ObjectAnimator flowingAnimato;

    public WaveView(Context context) {
        super(context);
        init(context, null);
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WaveView);

            int bgColor = typedArray.getColor(R.styleable.WaveView_backgroudColor, BG_COLOR);
            int pColor = typedArray.getColor(R.styleable.WaveView_progressColor, WAVE_COLOR);
            int aMax = typedArray.getInt(R.styleable.WaveView_max, (int) max);
            int aP = typedArray.getInteger(R.styleable.WaveView_progress, (int) progress);

            BG_COLOR = bgColor;
            WAVE_COLOR = pColor;
            max = aMax;
            progress = aP;

            typedArray.recycle();
        }

        VIEW_WIDTH = dip2px(context, 300);
        VIEW_HEIGHT = dip2px(context, 300);

        wavePath = new Path();
        shadPath = new Path();
        wavePath.setFillType(Path.FillType.EVEN_ODD);

        this.mContext = context;

        mPaint = new Paint();
        mPaint.setColor(BG_COLOR);
        mPaint.setStrokeWidth(1);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        wavePaint = new Paint();
        wavePaint.setColor(BG_COLOR);
        wavePaint.setStrokeWidth(1);
        wavePaint.setStyle(Paint.Style.FILL);
        wavePaint.setAntiAlias(true);

        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                setSpeed(speed);
                if (isHasWindowFocus) {

                    isCompleteLayout = true;

                    if (max >= progress) {
                        progressRatio = progress / (float) max;
                        dy = updateDyData();
                        getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        isMeasure = true;
                    }
                }
            }
        });

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!isMeasure) {
            getRealWidthMeasureSpec(widthMeasureSpec);
            getRealHeightMeasureSpec(heightMeasureSpec);
            if (VIEW_HEIGHT > VIEW_WIDTH) {
                VIEW_WIDTH = VIEW_HEIGHT;
            } else {
                VIEW_HEIGHT = VIEW_WIDTH;
            }
            setMeasuredDimension((int) VIEW_WIDTH, (int) VIEW_HEIGHT);
        }
        initPoint();
    }

    private void initPoint() {
        if (isInitPoint) {
            isInitPoint = false;
            pointList.clear();
            shadpointList.clear();

            WAVE_WIDTH = (float) (VIEW_WIDTH / 2.5);
            WAVE_HEIGHT = VIEW_HEIGHT / speed;

            dy = VIEW_HEIGHT;
            int n = Math.round(VIEW_WIDTH / WAVE_WIDTH);
            int startX = 0;
            for (int i = 0; i < 4 * n + 1; i++) {
                Point point = new Point();
                point.y = (int) dy;
                if (i == 0) {
                    point.x = startX;
                } else {
                    startX += WAVE_WIDTH;
                    point.x = startX;
                }
                pointList.add(point);
            }
            startX = (int) VIEW_WIDTH;
            for (int i = 0; i < 4 * n + 1; i++) {
                Point point = new Point();
                point.y = (int) dy;
                if (i == 0) {
                    point.x = startX;
                } else {
                    startX -= WAVE_WIDTH;
                    point.x = startX;
                }
                shadpointList.add(point);
            }

            speed = (px2dip(mContext, VIEW_WIDTH) / 20);
        }
    }

    public void setProgressListener(waveProgressListener progressListener) {
        this.progressListener = progressListener;
        isDone = false;
    }


    public void setSpeed(float speed) {
        this.speed = speed;
        dx = 0;
        shd_dx = 0;
    }

    public void setMax(long max) {
        this.max = max;
        isDone = false;
    }

    public void setbgColor(int color) {
        this.BG_COLOR = color;
    }

    public void setWaveColor(int color) {
        this.WAVE_COLOR = color;
    }

    public void setProgress(long progress) {
        mPaint.setColor(BG_COLOR);
        mPaint.setAlpha(255);

        isDone = false;
        if (progress > max) {
            if (this.progress < max) progress = max;
            else return;
        }

        if (flowingAnimato == null) flowingAnimation();

        if (reiseAnimator != null && reiseAnimator.isRunning()) {
            reiseAnimator.end();
        }

        this.progress = progress;
        if (progress == 0) {
            resetWave();
        }
        if (!isCompleteLayout) {
            return;
        }

        long cP = max - progress;
        if (max >= progress) {
            progressRatio = cP / (float) max;
            updateProgress();
        }
    }

    public long getProgress() {
        return progress;
    }

    public long getMax() {
        return max;
    }

    private void rerefreshPoints() {
        pointList.clear();
        shadpointList.clear();

        WAVE_HEIGHT = VIEW_HEIGHT / speed;

        //计算最多能支持多少点 非控制点
        int n = Math.round(VIEW_WIDTH / WAVE_WIDTH);
        //起始点
        int startX = (int) -dx;
        for (int i = 0; i < 4 * n + 1; i++) {
            Point point = new Point();
            point.y = (int) dy;
            if (i == 0) {
                point.x = startX;
            } else {
                startX += WAVE_WIDTH;
                point.x = startX;
            }
            pointList.add(point);
        }

        startX = (int) VIEW_WIDTH;
        for (int i = 0; i < 4 * n + 1; i++) {
            Point point = new Point();
            point.y = (int) dy;
            if (i == 0) {
                point.x = startX;
            } else {
                startX -= WAVE_WIDTH;
                point.x = startX;
            }
            shadpointList.add(point);
        }
    }

    public void resetWave() {
        isDone = false;
        dy = VIEW_HEIGHT;
        beforDy = 0;
    }

    private int updateDyData() {
        if (sum_dy == 0 && isHasWindowFocus) {
            sum_dy = VIEW_HEIGHT;
        }

        old_dy = dy;
        int offsetDy = (int) (sum_dy - sum_dy * progressRatio - beforDy);
        beforDy = sum_dy - sum_dy * progressRatio;
        return offsetDy;
    }

    private void updateProgress() {
        riseAnimation();
    }

    private int getRealWidthMeasureSpec(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            VIEW_WIDTH = widthSize;
        }
        return (int) VIEW_WIDTH;
    }

    private int getRealHeightMeasureSpec(int heightMeasureSpec) {

        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            VIEW_HEIGHT = heightSize;
        }
        if (!isHasWindowFocus) {
            updateDyData();
        } else {
            dy = VIEW_HEIGHT;//为了防止多次测量，必须重新更新初始高度
            old_dy = dy;
            sum_dy = dy;
        }

        return (int) VIEW_HEIGHT;
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onDraw(Canvas canvas) {
        wavePath.reset();
        shadPath.reset();

        float radius = VIEW_WIDTH / 2f;

        int saveFlags = Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG;
        canvas.saveLayer(0, 0, VIEW_WIDTH, VIEW_HEIGHT, null, saveFlags);

        canvas.drawCircle(VIEW_WIDTH / 2f, VIEW_HEIGHT / 2f, radius, mPaint);

        wavePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));

        if (!isDone) {
            wavePaint.setColor(WAVE_COLOR);
            wavePaint.setAlpha(100);

            float end1 = 0;
            for (int i = 0; i < pointList.size(); i++) {
                int j = i + 1;
                if (pointList.size() > i) {
                    float start1 = pointList.get(i).x;
                    wavePath.moveTo(start1, dy);//+dy
                    if (j % 2 == 0 && j >= 2) {
                        end1 = start1;
                        wavePath.quadTo(start1 + WAVE_WIDTH / 2, dy + WAVE_HEIGHT, start1 + WAVE_WIDTH, dy);//+dy
                    } else {
                        end1 = start1;
                        wavePath.quadTo(start1 + WAVE_WIDTH / 2, dy - WAVE_HEIGHT, start1 + WAVE_WIDTH, dy);
                    }
                }
            }

            if (end1 >= VIEW_WIDTH) {
                wavePath.lineTo(VIEW_WIDTH, VIEW_HEIGHT);
                wavePath.lineTo(0, VIEW_HEIGHT);
                wavePath.lineTo(0, dy);
                wavePath.close();
                canvas.drawPath(wavePath, wavePaint);
            }

            wavePaint.setAlpha(200);
            for (int i = 0; i < shadpointList.size(); i++) {
                int j = i + 1;
                if (shadpointList.size() > i) {
                    float start1 = shadpointList.get(i).x + shd_dx;
                    shadPath.moveTo(start1, dy);//+dy
                    if (j % 2 == 0 && j >= 2) {
                        end1 = start1;
                        shadPath.quadTo(start1 - WAVE_WIDTH / 2, (float) (dy + WAVE_HEIGHT * runRatio), start1 - WAVE_WIDTH, dy);//+dy
                    } else {
                        end1 = start1;
                        shadPath.quadTo(start1 - WAVE_WIDTH / 2, (float) (dy - WAVE_HEIGHT * runRatio), start1 - WAVE_WIDTH, dy);
                    }
                }
            }
            if (end1 <= -VIEW_WIDTH) {
                shadPath.lineTo(0, VIEW_HEIGHT);
                shadPath.lineTo(VIEW_WIDTH, VIEW_HEIGHT);
                shadPath.lineTo(VIEW_WIDTH, dy);
                shadPath.close();
                canvas.drawPath(shadPath, wavePaint);
            }

            wavePaint.setXfermode(null);
            canvas.restore();
            if (this.progressListener != null) {
                if (!isDone && curProgress != this.progress) {
                    this.progressListener.onPorgress(this.progress == this.max, this.progress, this.max);
                    curProgress = this.progress;
                }
                if (this.progress == this.max) {
                    isDone = true;
                }
            }

            if (isDone) doneAnimation();

        }

    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        isHasWindowFocus = hasWindowFocus;
        if (!isDone) {
            if (!isStartAnimation) {
                isStartAnimation = true;
                flowingAnimation();
            }
            if (!hasWindowFocus) {
                if (flowingAnimato != null)
                    flowingAnimato.cancel();
                if (reiseAnimator != null)
                    reiseAnimator.end();
                isMeasure = false;
            } else {
                if (flowingAnimato != null && !flowingAnimato.isRunning()) {
                    flowingAnimation();
                }
                if (reiseAnimator != null && !reiseAnimator.isRunning()) {
                    setProgress(this.progress);
                }
            }
        } else {
            if (isHasWindowFocus) {
                doneAnimation();
            }
        }

    }

    @SuppressLint("ObjectAnimatorBinding")
    private void flowingAnimation() {
        flowingAnimato = ObjectAnimator.ofFloat(this, "wave", 0, 100)
                .setDuration(100);
        flowingAnimato.setRepeatCount(INFINITE);
        flowingAnimato.addUpdateListener(valueAnimator -> {
            dx = dx + speed;
            shd_dx = shd_dx + speed / 2;

            if (shd_dx >= WAVE_WIDTH * 2) {
                shd_dx = 0;
            }

            if (dx >= WAVE_WIDTH * 2) {
                dx = 0;
            }
            rerefreshPoints();
            postInvalidate();
        })
        ;
        flowingAnimato.start();
    }

    private void riseAnimation() {
        if (!isHasWindowFocus) {
            return;
        }
        isMeasure = true;
        if (dy > 0) {
            float offset = updateDyData();
            reiseAnimator = ValueAnimator.ofFloat(0, offset)
                    .setDuration(500);
            reiseAnimator.setInterpolator(new LinearInterpolator());
            reiseAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    dy = sum_dy - beforDy;
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
            reiseAnimator.addUpdateListener(valueAnimator -> {
                float m = (float) valueAnimator.getAnimatedValue();
                float s = old_dy - m;
                dy = s;
            });


            reiseAnimator.start();
        }
    }

    private void doneAnimation() {
        if (reiseAnimator != null) {
            reiseAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    justDone();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            reiseAnimator.end();
        } else {
            justDone();
        }

    }

    private void justDone() {
        mPaint.setColor(WAVE_COLOR);
        if (flowingAnimato != null && flowingAnimato.isRunning()) {
            flowingAnimato.end();
            flowingAnimato = null;
        } else invalidate();
    }

    public interface waveProgressListener {
        void onPorgress(boolean isDone, long progress, long max);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    private int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
