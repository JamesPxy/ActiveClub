package com.pxy.eshore.http;


import android.util.Log;

import com.pxy.eshore.MyApplication;
import com.pxy.eshore.base.Constants;
import com.pxy.eshore.http.network.cache.ACache;
import com.pxy.eshore.utils.CheckNetwork;

import java.io.Serializable;

import rx.Subscriber;

/**
 * @author JamesPxy
 * @date 2017/12/20  10:21
 * @Description
 */

public abstract class MySubscriber<T> extends Subscriber<T> {

    private boolean isFromCache;
    private String key;
    private ACache aCache;

    public MySubscriber(String key) {
        this.key = key;
        aCache = ACache.get(MyApplication.getInstance());
    }

    public abstract void doError(Throwable e);

    public abstract void doSuccess(T t);

    @Override
    public void onStart() {
        //没网络  配置需要缓存则优先取缓存数据
        if (HttpClient.isNeedCache && !CheckNetwork.isNetworkConnected(MyApplication.getInstance())) {
            T model = (T) aCache.getAsObject(key);
            if (null == model) return;
            Log.i("6666", "onStart: get  cache  success=" + model.toString());
            doSuccess(model);
            isFromCache = true;
            onCompleted();
        }
    }

    @Override
    public void onCompleted() {
        Log.i("6666", "onCompleted in  baseSubscriber");
    }

    @Override
    public void onError(Throwable e) {
        Log.i("6666", "onError in  baseSubscriber");
        if (isFromCache) return;
        doError(e);
    }

    @Override
    public void onNext(T t) {
        //需要缓存则每次网络请求后 更新缓存
        if (HttpClient.isNeedCache) {
            Log.i("6666", "onNext: save cache  data");
            //处理缓存相关
            if (null != t) {
                aCache.remove(key);
                // 保存12个小时
                aCache.put(key, (Serializable) t, Constants.CACHE_TIME);
            }
        }
        doSuccess(t);
    }
}
