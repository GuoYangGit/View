package com.fuqin.android.view.progressbar;

/**
 * Created by guoyang on 2018/1/30.
 * github https://github.com/GuoYangGit
 * QQ:352391291
 */

public class FallBean {
    private int status; //判断是否得奖或者炸弹 0.炸弹 1.体验金 2.加息券 3.电子券
    private String message; //判断奖品内容

    public FallBean(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
