package com.pxy.eshore.utils;

import android.widget.Toast;

import com.pxy.eshore.MyApplication;

/**
 * 单例Toast  统一吐司处理入口
 */

public class ToastUtil {

    private static Toast mToast;

    public static void showToast(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(MyApplication.getInstance(), text, Toast.LENGTH_SHORT);
        }
        mToast.setText(text);
        mToast.show();
    }
}
