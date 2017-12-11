package com.pxy.eshore.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;

import com.pxy.eshore.R;
import com.pxy.eshore.adapter.HotMovieAdapter;
import com.pxy.eshore.base.BaseActivity;
import com.pxy.eshore.bean.HotMovieBean;
import com.pxy.eshore.bean.moviechild.SubjectsBean;
import com.pxy.eshore.databinding.ActivityHotMovieBinding;
import com.pxy.eshore.http.HttpClient;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_movie);
        setTitle("电影热映榜");
        context = this;
        getHotMovie(true);

//        bindingView.refreshLayout.setEnableLoadmore(false);
        bindingView.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getHotMovie(false);
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
    }

    @Override
    protected void onRefresh() {
        super.onRefresh();
//        getHotMovie();
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
                        showError();
                    }

                    @Override
                    public void onNext(HotMovieBean hotMovieBean) {
                        // TODO: 2017/12/11 处理缓存相关 
//                        aCache = ACache.get(getActivity());
//                        oneAdapter = new OneAdapter(activity);
//                        mHotMovieBean = (HotMovieBean) aCache.getAsObject(SyncStateContract.Constants.ONE_HOT_MOVIE);
//                        if (hotMovieBean != null) {
//                            aCache.remove(SyncStateContract.Constants.ONE_HOT_MOVIE);
//                            // 保存12个小时
//                            aCache.put(Constants.ONE_HOT_MOVIE, hotMovieBean, 43200);
//                            setAdapter(hotMovieBean);
//                            // 保存请求的日期
//                            SPUtils.putString("one_data", TimeUtil.getData());
//                            // 刷新结束
//                            mIsLoading = false;
//                        }

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


}
