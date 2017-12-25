package com.pxy.eshore.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.pxy.eshore.R;
import com.pxy.eshore.base.Constants;
import com.pxy.eshore.bean.FrontpageBean;
import com.pxy.eshore.bean.GankIoDataBean;
import com.pxy.eshore.http.HttpClient;
import com.pxy.eshore.http.MySubscriber;
import com.pxy.recyclerbaner.RecyclerBannerBase;
import com.pxy.recyclerbaner.RecyclerBannerNormal;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * @author JamesPxy
 * @time 2017/12/15  11:05
 * @Description 首页对应fragment
 */
public class NewsFragment extends Fragment {

    private String TAG = getClass().getSimpleName();
    private RecyclerBannerNormal bannerNormal;
    private TextView tvMsg;
    private Context mContext;
    private CompositeSubscription mCompositeSubscription;

    List<String> bannerList = new ArrayList<>();
    private final int MSG_SHOW_BANNER = 0X001;
    private final int MSG_GET_BANNER_ERROR = 0X003;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SHOW_BANNER:
                    setBanner(true);
                    break;
                case MSG_GET_BANNER_ERROR:
                    setBanner(false);
                    break;
            }

        }
    };

    private int page = 1;
    private int prePage = 15;


    public NewsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        bannerNormal = view.findViewById(R.id.banner1);
        mContext = getActivity();
        Log.i(TAG, "NewsFragment onCreateView");
        //获取首页轮播图
        getBannerUrls();
        //获取gankIoData
        getGankIoData();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeSubscription();
    }

    /**
     * 设置首页轮播图
     */
    private void setBanner(boolean isSuccess) {
//        bannerList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1510833032475&di=8ceb2f76c9bed8a2ebaf5ba85efd4440&imgtype=0&src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2Fb%2F591953d9ce3cb.jpg");
//        bannerList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1510833032475&di=4cf3e1192a9e58529440d213de48a95a&imgtype=0&src=http%3A%2F%2Fimg.7xz.com%2Fimg%2Fpicimg%2F201607%2F20160715161145_327a1d30f651dab8932.jpg");
//        bannerList.add("http://business.cdn.qianqian.com/qianqian/pic/bos_client_1513328061a3fff3d24ea8daae55e02d41615958cb.jpg");
        if (!isSuccess) {
            bannerList.clear();
            bannerList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1510833032476&di=3998d3aaa2e006c1120c8534311e9a81&imgtype=0&src=http%3A%2F%2Fe.hiphotos.baidu.com%2Fzhidao%2Fpic%2Fitem%2F810a19d8bc3eb135275b10f1ae1ea8d3fc1f44df.jpg");
            bannerList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1510833032474&di=3723ba473a9c92dc420e2538a9aa584e&imgtype=0&src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2Fc%2F57450b9a295f5.jpg");
            bannerList.add("http://business.cdn.qianqian.com/qianqian/pic/bos_client_1513334741efc21a1f3db87a58d73eb462a4b3fe96.jpg");
            bannerList.add("http://business.cdn.qianqian.com/qianqian/pic/bos_client_1513327830057bc1862a54edaeb8c5c4f6168d2511.jpg");
        }
        bannerNormal.initBannerImageView(bannerList, new RecyclerBannerBase.OnBannerItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(getActivity(), "clicked position=" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 获取首页轮播图url
     */
    private void getBannerUrls() {
        Log.i(TAG, "getBannerUrls: excuted");
        Subscription subscription = HttpClient.Builder.getTingServer().getFrontpage()
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new MySubscriber<FrontpageBean>(Constants.BANNER_PIC) {
                    @Override
                    public void doError(Throwable e) {
                        mHandler.sendEmptyMessage(MSG_GET_BANNER_ERROR);
                    }

                    @Override
                    public void doSuccess(FrontpageBean frontpageBean) {
                        if (null != frontpageBean) {
                            List<FrontpageBean.ResultBeanXXXXXXXXXXXXXX.FocusBean.ResultBeanX> list = frontpageBean.getResult().getFocus().getResult();
                            if (list.size() > 0) bannerList.clear();
                            for (FrontpageBean.ResultBeanXXXXXXXXXXXXXX.FocusBean.ResultBeanX resultBeanX : list) {
                                bannerList.add(resultBeanX.getRandpic());
                            }
                            mHandler.sendEmptyMessage(MSG_SHOW_BANNER);
                        }
                    }
                });
        addSubscription(subscription);
    }

    /**
     * 获取gankIo数据
     */
    private void getGankIoData() {
        Log.i(TAG, "getGankIoData: excuted");
        Subscription subscription = HttpClient.Builder.getGankIOServer().getGankIoData("Android", page, prePage)
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new MySubscriber<GankIoDataBean>(Constants.GANK_ANDROID) {
                    @Override
                    public void doError(Throwable e) {
                        Log.e(TAG, "doError: getGankIoData");

                    }

                    @Override
                    public void doSuccess(GankIoDataBean gankIoDataBean) {
                        Log.i(TAG, "doSuccess: gankIoData=\n" + gankIoDataBean.toString());
                    }
                });
        addSubscription(subscription);
    }

    private void addSubscription(Subscription subscription) {
        if (null == mCompositeSubscription) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(subscription);
    }


    public void removeSubscription() {
        if (null != mCompositeSubscription && mCompositeSubscription.hasSubscriptions()) {
            this.mCompositeSubscription.unsubscribe();
        }
    }

}
