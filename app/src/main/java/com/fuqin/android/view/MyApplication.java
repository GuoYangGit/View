package com.fuqin.android.view;

import android.app.Application;

/**
 * Created by guoyang on 2018/1/16.
 * github https://github.com/GuoYangGit
 * QQ:352391291
 */

public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        new RudenessScreenHelper(this).activate();
    }
}
