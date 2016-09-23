package com.wangxiandeng.saviortest;

import android.app.Application;

import com.wangxiandeng.savior.PatchLoader;


/**
 * Created by xingzhu on 2016/9/23.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PatchLoader.getInstance().init(this, "com.wangxiandeng.saviortest.PatchBox");
    }


}
