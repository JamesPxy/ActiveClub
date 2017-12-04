package com.pxy.eshore;

import android.app.Application;

/**
 * @author JamesPxy
 * @date 2017/11/24  16:36
 * @Description Application应用入口
 */

public class MyApplication extends Application {

    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static MyApplication getInstance() {
        return instance;
    }
}
