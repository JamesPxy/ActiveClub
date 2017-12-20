package com.pxy.eshore.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.widget.Toast;

import com.pxy.eshore.R;
import com.pxy.eshore.adapter.HotMovieAdapter;
import com.pxy.eshore.base.BaseActivity;
import com.pxy.eshore.base.Constants;
import com.pxy.eshore.bean.HotMovieBean;
import com.pxy.eshore.bean.moviechild.SubjectsBean;
import com.pxy.eshore.databinding.ActivityHotMovieBinding;
import com.pxy.eshore.http.MySubscriber;
import com.pxy.eshore.http.HttpClient;
import com.pxy.eshore.http.network.cache.ACache;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HotMovieActivity extends BaseActivity<ActivityHotMovieBinding> {

    private HotMovieAdapter hotMovieAdapter;
    private List<SubjectsBean> data;
    private Context context;
    private ACache aCache;
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_movie);
        setTitle("电影热映榜");
        context = this;
        aCache = ACache.get(context);
        getHotMovieNew();

//        bindingView.refreshLayout.setEnableLoadmore(false);
        bindingView.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                isFirst = false;
                getHotMovieNew();
            }
        });

    }

    private void setAdapter() {
        if (null == hotMovieAdapter) {
            hotMovieAdapter = new HotMovieAdapter(data);
            bindingView.recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            bindingView.recyclerview.setAdapter(hotMovieAdapter);
        }
        hotMovieAdapter.notifyDataSetChanged();
        if (isFirst) {
            showContentView();
        } else {
            bindingView.refreshLayout.finishRefresh();
        }
    }

    @Override
    protected void onRefresh() {
        super.onRefresh();
        getHotMovieNew();
    }

    private void getHotMovie(final boolean isFirst) {
        Subscription subscriptions = HttpClient.Builder.getDouBanService().getHotMovie()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HotMovieBean>() {
                    @Override
                    public void onCompleted() {
                        showContentView();
                        if (!isFirst) {
                            bindingView.refreshLayout.finishRefresh();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        HotMovieBean cache = (HotMovieBean) aCache.getAsObject(Constants.DOUBAN_HOT_MOVIE);
                        //有缓存则使用缓存
                        if (cache != null) {
                            data = cache.getSubjects();
                            setAdapter();
                            showContentView();
                        } else {
                            showError();
                        }
                    }

                    @Override
                    public void onNext(HotMovieBean hotMovieBean) {
                        //处理缓存相关
                        if (hotMovieBean != null) {
                            aCache.remove(Constants.DOUBAN_HOT_MOVIE);
                            // 保存12个小时
                            aCache.put(Constants.DOUBAN_HOT_MOVIE, hotMovieBean, Constants.CACHE_TIME);
                        }
                        data = hotMovieBean.getSubjects();
                        if (null != data) {
                            setAdapter();
                        } else {
                            onError(new Throwable("data is null"));
                            Toast.makeText(context, "返回数据为空", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        addSubscription(subscriptions);
    }

    private void getHotMovieNew() {
        Subscription subscription = HttpClient.Builder.getDouBanService().getHotMovie()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MySubscriber<HotMovieBean>(Constants.DOUBAN_HOT_MOVIE) {
                    @Override
                    public void doError(Throwable e) {
                        showError();
                        Log.i("6666", "doError: in  hot  movie");
                    }

                    @Override
                    public void doSuccess(HotMovieBean hotMovieBean) {
                        Log.i("6666", "doSuccess: in  hot  movie");
                        data = hotMovieBean.getSubjects();
                        if (null != data) {
                            setAdapter();
                        } else {
                            doError(new Throwable("data is null"));
                            Toast.makeText(context, "返回数据为空", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        addSubscription(subscription);
    }


}
