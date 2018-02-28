package com.fuqin.android.view.progressbar;


import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by guoyang on 2018/1/26.
 * github https://github.com/GuoYangGit
 * QQ:352391291
 */

public class FallingHandler extends Handler {
    WeakReference<FallingLayout> mFallingLayout;
    private int totalNum;
    private long delayTime;
    private List<FallBean> fallList;
    private int index = 0;
    private FallingThread mFallingThread;

    FallingHandler(FallingLayout fallingLayout) {
        mFallingLayout = new WeakReference(fallingLayout);
    }

    public void handleMessage(Message paramMessage) {
        super.handleMessage(paramMessage);
        FallingLayout localFallingLayout = mFallingLayout.get();
        if (localFallingLayout == null) {
            return;
        }
        switch (paramMessage.what) {
            case 0:
                localFallingLayout.addFallingBody(fallList.get(index));
                index++;
                break;
        }
    }

    void addTask(int totalNum, long delayTime, List<FallBean> fallList) {
        this.totalNum = totalNum;
        this.delayTime = delayTime;
        this.fallList = fallList;
    }

    class FallingThread implements Runnable {

        public void run() {
            if (totalNum < 1) return;
            sendEmptyMessage(0);
            totalNum--;
            postDelayed(this, delayTime);
        }
    }

    void onStart() {
        mFallingThread = new FallingThread();
        post(mFallingThread);
    }

    public void onClear() {
        if (mFallingLayout != null) {
            mFallingLayout = null;
        }
        if (mFallingThread != null) {
            removeCallbacks(mFallingThread);
            mFallingThread = null;
        }
    }
}
