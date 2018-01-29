package com.fuqin.android.view.chatview;

/**
 * Created by guoyang on 2018/1/15.
 * github https://github.com/GuoYangGit
 * QQ:352391291
 */

public class ChatData {
    String happenTime;
    float num;

    public ChatData(String happenTime, float num) {
        this.happenTime = happenTime;
        this.num = num;
    }

    public String getHappenTime() {
        return happenTime;
    }

    public void setHappenTime(String happenTime) {
        this.happenTime = happenTime;
    }

    public float getNum() {
        return num;
    }

    public void setNum(float num) {
        this.num = num;
    }
}
