package com.pxy.eshore.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.pxy.eshore.MainActivity;
import com.pxy.eshore.R;
import com.pxy.eshore.base.ConstantsImageUrl;
import com.pxy.eshore.databinding.ActivitySplashBinding;

import java.util.Random;

/**
 * @author JamesPxy
 * @date 2017/12/12  15:41
 * @Description 闪屏页
 */
public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding bindingView;
    private Handler handler = new Handler();
    //是否已经跳转
    private boolean hasJump;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        StatusBarUtil.setTranslucent(this);
//        StatusBarUtil.setTranslucentForCoordinatorLayout(this,0);
        bindingView = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        //随机加载闪屏图
        bindingView.setUrl(ConstantsImageUrl.SPLASH_URLS[getRandomInt()]);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toMainActivity();
            }
        }, 500);

        bindingView.tvJump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMainActivity();
            }
        });

    }

    /**
     * 产生闪屏图范围内随机数
     *
     * @return
     */
    private int getRandomInt() {
        return new Random().nextInt(ConstantsImageUrl.SPLASH_URLS.length);
    }

    private void toMainActivity() {
        if (hasJump) return;
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
        hasJump = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(this);
    }
}
