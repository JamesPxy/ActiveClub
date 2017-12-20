package com.pxy.eshore.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

import com.pxy.eshore.R;
import com.pxy.eshore.adapter.DouBanTopAdapter;
import com.pxy.eshore.base.BaseActivity;
import com.pxy.eshore.base.Constants;
import com.pxy.eshore.bean.GankIoDataBean;
import com.pxy.eshore.bean.HotMovieBean;
import com.pxy.eshore.bean.moviechild.SubjectsBean;
import com.pxy.eshore.databinding.ActivityTopMovieBinding;
import com.pxy.eshore.http.HttpClient;
import com.pxy.eshore.http.MySubscriber;
import com.pxy.eshore.http.network.cache.ACache;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;

import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TopMovieActivity extends BaseActivity<ActivityTopMovieBinding> {

    private String TAG = getClass().getSimpleName();
    private int mStart = 0;
    private int mCount = 20;
    private DouBanTopAdapter mDouBanTopAdapter;
    private ACache aCache;
    private boolean isFirst = true;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_movie);
        setTitle("豆瓣电影Top250");
//        StatusBarUtil.setColor(this, R.color.colorPrimary);
        aCache = ACache.get(this);

        //请求获取豆瓣TOP250电影数据
//        getTop250Movie();
        getDouBanTop250();

        bindingView.refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                isFirst = false;
                mStart += mCount;
//                getTop250Movie();
                getDouBanTop250();
            }
        });
    }

    public void setAdapter(List<SubjectsBean> data) {
        if (null == mDouBanTopAdapter) {
            mDouBanTopAdapter = new DouBanTopAdapter(this);
            mDouBanTopAdapter.addAll(data);
            bindingView.recyclerview.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
            bindingView.recyclerview.setAdapter(mDouBanTopAdapter);
        } else {
//            mDouBanTopAdapter.clear();
            mDouBanTopAdapter.addAll(data);
            //构造器中，第一个参数表示列数或者行数，第二个参数表示滑动方向,瀑布流
            mDouBanTopAdapter.notifyDataSetChanged();
        }
        if (isFirst) {
            showContentView();
        } else {
            bindingView.refreshLayout.finishLoadmore();
        }
    }


    @Override
    protected void onRefresh() {
//        getTop250Movie();
        //为了测试加载效果加此限制
        mStart = 0;
        getDouBanTop250();
    }

    public void getDouBanTop250() {
        Subscription subscription = HttpClient.Builder.getDouBanService().getMovieTop250(mStart, mCount)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MySubscriber<HotMovieBean>(Constants.DOUBAN_TOP_MOVIE + mStart + mCount) {

                    @Override
                    public void doError(Throwable e) {
                        if (mDouBanTopAdapter.getData().size() > 0) return;
                        if (!isFirst) bindingView.refreshLayout.finishLoadmore();
                        showError();
                    }

                    @Override
                    public void doSuccess(HotMovieBean hotMovieBean) {
                        if (hotMovieBean != null && hotMovieBean.getSubjects() != null && hotMovieBean.getSubjects().size() > 0) {
                            setAdapter(hotMovieBean.getSubjects());
                        } else {
                            showError();
                        }
                    }
                });
        addSubscription(subscription);
    }


    private void getTop250Movie() {
        final Subscription subscription = HttpClient.Builder.getDouBanService().getMovieTop250(mStart, mCount)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HotMovieBean>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted: ");
                        showContentView();
                    }

                    @Override
                    public void onError(Throwable e) {
                        bindingView.refreshLayout.finishLoadmore();
                        HotMovieBean bean = (HotMovieBean) aCache.getAsObject(Constants.DOUBAN_TOP_MOVIE);
                        if (null != bean) {
                            setAdapter(bean.getSubjects());
                            showContentView();
                        } else {
                            showError();
                        }
//                        if (mDouBanTopAdapter.getItemCount() == 0) {
//                            showError();
//                        }
                    }

                    @Override
                    public void onNext(HotMovieBean hotMovieBean) {
                        Log.d(TAG, "onNext: " + hotMovieBean.toString());
                        if (null != hotMovieBean) {
                            aCache.remove(Constants.DOUBAN_TOP_MOVIE);
                            aCache.put(Constants.DOUBAN_TOP_MOVIE, hotMovieBean, Constants.CACHE_TIME);
                        }
                        if (hotMovieBean != null && hotMovieBean.getSubjects() != null && hotMovieBean.getSubjects().size() > 0) {
                            setAdapter(hotMovieBean.getSubjects());
                        } else {
                            showError();
                        }
//                        if (mStart == 0) {
//                            if (hotMovieBean != null && hotMovieBean.getSubjects() != null && hotMovieBean.getSubjects().size() > 0) {
//                                mDouBanTopAdapter.clear();
//                                mDouBanTopAdapter.addAll(hotMovieBean.getSubjects());
//                                //构造器中，第一个参数表示列数或者行数，第二个参数表示滑动方向,瀑布流
//                                bindingView.recyclerview.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
//                                bindingView.recyclerview.setAdapter(mDouBanTopAdapter);
//                                mDouBanTopAdapter.notifyDataSetChanged();
//                            } else {
//                                showError();
//                            }
//                        } else {
//                            if (hotMovieBean != null && hotMovieBean.getSubjects() != null && hotMovieBean.getSubjects().size() > 0) {
//                                bindingView.refreshLayout.finishLoadmore();
//                                mDouBanTopAdapter.addAll(hotMovieBean.getSubjects());
//                                mDouBanTopAdapter.notifyDataSetChanged();
//                            } else {
//                                bindingView.refreshLayout.finishLoadmore();
//                            }
//                        }
                    }
                });
        addSubscription(subscription);
    }

    public void getGankIoData() {
        Subscription subscription = HttpClient.Builder.getGankIOServer().getGankIoData("Android", 1, 20)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GankIoDataBean>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onNext(GankIoDataBean gankIoDataBean) {
                        Log.d(TAG, "onNext: " + gankIoDataBean.toString());
                    }
                });
        addSubscription(subscription);
    }

}
