package com.pxy.eshore.ui;

import android.os.Bundle;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

import com.pxy.eshore.R;
import com.pxy.eshore.adapter.WelfareAdapter;
import com.pxy.eshore.base.BaseActivity;
import com.pxy.eshore.bean.GankIoDataBean;
import com.pxy.eshore.databinding.ActivityWelfareBinding;
import com.pxy.eshore.http.HttpClient;
import com.scwang.smartrefresh.header.BezierCircleHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class WelfareActivity extends BaseActivity<ActivityWelfareBinding> {

    private String id = "福利";
    private int page = 1;
    private int prePage = 20;
    private String TAG = getClass().getSimpleName();
    private WelfareAdapter welfareAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welfare);
        setTitle("福利");
        getWelfareData();

        bindingView.refreshLayout.setRefreshHeader(new BezierCircleHeader(this));

        bindingView.refreshLayout.setRefreshFooter(new BallPulseFooter(this));

        bindingView.refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                page++;
                getWelfareData();
            }
        });

        bindingView.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                bindingView.refreshLayout.finishRefresh(2000);
            }
        });
    }

    private void setWelfareAdapter(GankIoDataBean gankIoDataBean) {
        if (null == welfareAdapter) {
            welfareAdapter = new WelfareAdapter();
            //构造器中，第一个参数表示列数或者行数，第二个参数表示滑动方向,瀑布流
            bindingView.recyclerview.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            bindingView.recyclerview.setAdapter(welfareAdapter);
        }
        welfareAdapter.addAll(gankIoDataBean.getResults());
        welfareAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onRefresh() {
        getWelfareData();
    }

    private void getWelfareData() {
        Subscription subscriptions = HttpClient.Builder.getGankIOServer().getGankIoData(id, page, prePage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GankIoDataBean>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted: ");
                        showContentView();
                        bindingView.refreshLayout.finishLoadmore();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                        showError();
                    }

                    @Override
                    public void onNext(GankIoDataBean gankIoDataBean) {
                        Log.e(TAG, "onNext: " + gankIoDataBean.toString());
                        setWelfareAdapter(gankIoDataBean);
                    }
                });
        addSubscription(subscriptions);
    }

}
