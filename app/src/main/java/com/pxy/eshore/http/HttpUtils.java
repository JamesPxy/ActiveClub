package com.pxy.eshore.http;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pxy.eshore.BuildConfig;
import com.pxy.eshore.MyApplication;
import com.pxy.eshore.utils.CheckNetwork;

import java.io.IOException;
import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 网络请求工具类
 * <p>
 * 豆瓣api:
 * 问题：API限制为每分钟40次，一不小心就超了，马上KEY就被封,用不带KEY的API，每分钟只有可怜的10次。
 * 返回：code:112（rate_limit_exceeded2 IP 访问速度限制）
 * 解决：1.使用每分钟访问次数限制（客户端）2.更换ip (更换wifi)
 * 豆瓣开发者服务使用条款: https://developers.douban.com/wiki/?title=terms
 * 使用时请在"CloudReaderApplication"下初始化。
 */

public class HttpUtils {
    private static HttpUtils instance;
    private Gson gson;
    private Context context;
    private Object gankHttps;
    private Object doubanHttps;
    private Object dongtingHttps;
    private IpmlTokenGetListener listener;
    private boolean debug;
    // gankio、豆瓣、（轮播图）
    private final static String API_GANKIO = "https://gank.io/api/";
    private final static String API_DOUBAN = "https://api.douban.com/";
    private final static String API_TING = "https://tingapi.ting.baidu.com/v1/restserver/";
    /**
     * 分页数据，每页的数量
     */
    public static int per_page = 10;
    public static int per_page_more = 20;

    public static HttpUtils getInstance() {
        if (instance == null) {
            synchronized (HttpUtils.class) {
                if (instance == null) {
                    instance = new HttpUtils();
                }
            }
        }
        return instance;
    }

    public void init(Context context, boolean debug) {
        this.context = context;
        this.debug = debug;
        HttpHead.init(context);
    }

    public <T> T getGankIOServer(Class<T> a) {
        if (gankHttps == null) {
            synchronized (HttpUtils.class) {
                if (gankHttps == null) {
                    gankHttps = getBuilder(API_GANKIO).build().create(a);
                }
            }
        }
        return (T) gankHttps;
    }

    public <T> T getDouBanServer(Class<T> a) {
        if (doubanHttps == null) {
            synchronized (HttpUtils.class) {
                if (doubanHttps == null) {
                    doubanHttps = getBuilder(API_DOUBAN).build().create(a);
                }
            }
        }
        return (T) doubanHttps;
    }

    public <T> T getTingServer(Class<T> a) {
        if (dongtingHttps == null) {
            synchronized (HttpUtils.class) {
                if (dongtingHttps == null) {
                    dongtingHttps = getBuilder(API_TING).build().create(a);
                }
            }
        }
        return (T) dongtingHttps;
    }

    private Retrofit.Builder getBuilder(String apiUrl) {
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.client(getOkClient());
        builder.baseUrl(apiUrl);//设置远程地址
        builder.addConverterFactory(new NullOnEmptyConverterFactory());
        builder.addConverterFactory(GsonConverterFactory.create(getGson()));
        builder.addCallAdapterFactory(RxJavaCallAdapterFactory.create());

        return builder;
    }


    private Gson getGson() {
        if (gson == null) {
            GsonBuilder builder = new GsonBuilder();
            builder.setLenient();
            builder.setFieldNamingStrategy(new AnnotateNaming());
            builder.serializeNulls();
            gson = builder.create();
        }
        return gson;
    }


    private static class AnnotateNaming implements FieldNamingStrategy {
        @Override
        public String translateName(Field field) {
            ParamNames a = field.getAnnotation(ParamNames.class);
            return a != null ? a.value() : FieldNamingPolicy.IDENTITY.translateName(field);
        }
    }

    public OkHttpClient getUnsafeOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[]{};
                }
            }};
            // Install the all-trusting trust manager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();
            okBuilder.readTimeout(20, TimeUnit.SECONDS);
            okBuilder.connectTimeout(10, TimeUnit.SECONDS);
            okBuilder.writeTimeout(20, TimeUnit.SECONDS);
            okBuilder.cache(new Cache(MyApplication.getInstance().getCacheDir(), 10 * 1024 * 1024));//缓存位置和大小 10M
//            okBuilder.addInterceptor(new CacheInterceptor(true));
//            okBuilder.addNetworkInterceptor(new CacheInterceptor(true));
//
            okBuilder.addInterceptor(getInterceptor());
            okBuilder.addNetworkInterceptor(getInterceptor());

//            okBuilder.addInterceptor(new BaseInterceptor(null, MyApplication.getInstance()));
//            okBuilder.addNetworkInterceptor(new BaseInterceptor(null, MyApplication.getInstance()));

            okBuilder.sslSocketFactory(sslSocketFactory);
            okBuilder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
//                    Log.d("HttpUtils", "==come");
                    return true;
                }
            });

            return okBuilder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public OkHttpClient getOkClient() {
        OkHttpClient client1;
        client1 = getUnsafeOkHttpClient();
        return client1;
    }

    public void setTokenListener(IpmlTokenGetListener listener) {
        this.listener = listener;
    }

    private HttpLoggingInterceptor getInterceptor() {
        HttpLoggingInterceptor interceptor;
        //如果不是在正式包，添加拦截 打印响应json
        if (BuildConfig.DEBUG) {
            interceptor = new HttpLoggingInterceptor(
                    new HttpLoggingInterceptor.Logger() {
                        @Override
                        public void log(String message) {
                            if (TextUtils.isEmpty(message)) return;
                            Log.e("NetWork  return  data", "return  data===" + message);
                        }
                    });
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE); // 正式环境不打印日志
        }
        return interceptor;
    }

    /**
     * BaseInterceptor
     * Created by Tamic on 2016-7-15.
     */
    public class BaseInterceptor implements Interceptor {
        private Map<String, String> headers;
        private Context context;

        public BaseInterceptor(Map<String, String> headers, Context context) {
            this.headers = headers;
            this.context = context;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {

            Request.Builder builder = chain.request()
                    .newBuilder();
            builder.cacheControl(CacheControl.FORCE_CACHE).url(chain.request().url())
                    .build();

            if (!CheckNetwork.isNetworkConnected(context)) {

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "当前无网络!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            if (headers != null && headers.size() > 0) {
                Set<String> keys = headers.keySet();
                for (String headerKey : keys) {
                    builder.addHeader(headerKey, headers.get(headerKey)).build();
                }
            }

            if (CheckNetwork.isNetworkConnected(context)) {
                int maxAge = 60; // read from cache for 60 s
                builder
                        .removeHeader("Pragma")
                        .addHeader("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {
                int maxStale = 60 * 60 * 24 * 14; // tolerate 2-weeks stale
                builder
                        .removeHeader("Pragma")
                        .addHeader("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
            return chain.proceed(builder.build());
        }
    }
}
