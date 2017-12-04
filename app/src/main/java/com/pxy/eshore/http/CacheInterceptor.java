package com.pxy.eshore.http;


import android.util.Log;

import com.pxy.eshore.utils.CookieDbUtil;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;


/**
 * gson持久化截取保存数据
 * 自主实现拦截器  进行缓存拦截
 */

public class CacheInterceptor implements Interceptor {

    private final String TAG = "CacheInterceptor";

    private CookieDbUtil dbUtil;
    /*是否缓存标识*/
    private boolean cache;

    public CacheInterceptor(boolean cache) {
        dbUtil = CookieDbUtil.getInstance();
        this.cache = cache;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        if (cache) {
            ResponseBody body = response.body();
            BufferedSource source = body.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();
            Charset charset = Charset.defaultCharset();
            MediaType contentType = body.contentType();
            if (contentType != null) {
                charset = contentType.charset(charset);
            }
            String bodyString = buffer.clone().readString(charset);
            String url = request.url().toString().split("\\?")[0];
            String array[] = url.split("\\?");
            Log.d(TAG, "request url-----" + array[0]);
//            Log.d(TAG, "intercept data---F" + bodyString);
//            if(null!=array[0]){
//                url=array[0];
//            }
            CookieResult result = dbUtil.queryCookieBy(url);
            long time = System.currentTimeMillis();
            /*保存和更新本地数据*/
            if (result == null) {
                result = new CookieResult(url, bodyString, time);
                dbUtil.saveCookie(result);
                Log.d(TAG, "saved data  success" + result.getTime());
            } else {
                result.setResult(bodyString);
                result.setTime(time);
                dbUtil.updateCookie(result);
                Log.d(TAG, "updated data success" + result.getTime());
            }
        }
        return response;
    }
}
