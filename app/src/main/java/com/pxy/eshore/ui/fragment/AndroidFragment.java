package com.pxy.eshore.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;

import com.pxy.eshore.R;
import com.pxy.eshore.adapter.AndroidAdapter;
import com.pxy.eshore.base.BaseFragment;
import com.pxy.eshore.base.Constants;
import com.pxy.eshore.bean.GankIoDataBean;
import com.pxy.eshore.databinding.FragmentAndroidBinding;
import com.pxy.eshore.http.HttpUtils;
import com.pxy.eshore.http.network.RequestImpl;
import com.pxy.eshore.http.network.cache.ACache;
import com.pxy.eshore.model.GankModel;
import com.pxy.eshore.utils.DebugUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import rx.Subscription;

/**
 * 安卓对应 fragment
 */
public class AndroidFragment extends BaseFragment<FragmentAndroidBinding> {

    private static final String TAG = "AndroidFragment";
    private static final String TYPE = "mType";
    private String mType = "Android";
    private int mPage = 1;
    private boolean mIsPrepared;
    private boolean mIsFirst = true;
    private AndroidAdapter mAndroidAdapter;
    private ACache mACache;
    private GankIoDataBean mAndroidBean;
    private GankModel mModel;

    public static AndroidFragment newInstance(String type) {
        AndroidFragment fragment = new AndroidFragment();
        Bundle args = new Bundle();
        args.putString(TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getString(TYPE);
        }
    }

    @Override
    public int setContent() {
        return R.layout.fragment_android;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mACache = ACache.get(getContext());
        mModel = new GankModel();
//        mAndroidBean = (GankIoDataBean) mACache.getAsObject(Constants.GANK_ANDROID);
        DebugUtil.error(TAG + "AndroidFragment  onActivityCreated");
        mAndroidAdapter = new AndroidAdapter();
        bindingView.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mPage = 1;
                loadAndroidData();
            }
        });
        bindingView.refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                mPage++;
                loadAndroidData();
            }
        });
        // 准备就绪
        mIsPrepared = true;
    }

    @Override
    protected void loadData() {
        if (!mIsPrepared || !mIsVisible || !mIsFirst) {
            return;
        }

        if (mAndroidBean != null
                && mAndroidBean.getResults() != null
                && mAndroidBean.getResults().size() > 0) {
            showContentView();
            mAndroidBean = (GankIoDataBean) mACache.getAsObject(Constants.GANK_ANDROID);
            setAdapter(mAndroidBean);
        } else {
            loadAndroidData();
        }
    }

    private void loadAndroidData() {
        DebugUtil.debug("loadAndroidData  page=" + mPage);
        mModel.setData(mType, mPage, HttpUtils.per_page_more);
        mModel.getGankIoData(new RequestImpl() {
            @Override
            public void loadSuccess(Object object) {
                showContentView();
                GankIoDataBean gankIoDataBean = (GankIoDataBean) object;
                if (mPage == 1) {
                    if (gankIoDataBean != null && gankIoDataBean.getResults() != null && gankIoDataBean.getResults().size() > 0) {
                        setAdapter(gankIoDataBean);

                        mACache.remove(Constants.GANK_ANDROID);
                        // 缓存50分钟
                        mACache.put(Constants.GANK_ANDROID, gankIoDataBean, 30000);
                    }
                } else {
                    if (gankIoDataBean != null && gankIoDataBean.getResults() != null && gankIoDataBean.getResults().size() > 0) {
                        bindingView.refreshLayout.finishRefresh();
                        mAndroidAdapter.addAll(gankIoDataBean.getResults());
                        mAndroidAdapter.notifyDataSetChanged();
                    } else {
                        bindingView.refreshLayout.finishLoadmore();
                    }
                }
            }

            @Override
            public void loadFailed() {
                bindingView.refreshLayout.finishRefresh();
                // 注意：这里不能写成 mPage == 1，否则会一直显示错误页面
                if (mAndroidAdapter.getItemCount() == 0) {
                    showError();
                }
                if (mPage > 1) {
                    mPage--;
                }
            }

            @Override
            public void addSubscription(Subscription subscription) {
                AndroidFragment.this.addSubscription(subscription);
            }
        });
    }

    /**
     * 设置adapter
     */
    private void setAdapter(GankIoDataBean mAndroidBean) {
//        mAndroidAdapter.clear();
        mAndroidAdapter.addAll(mAndroidBean.getResults());
        bindingView.recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        bindingView.recyclerview.setAdapter(mAndroidAdapter);
        mAndroidAdapter.notifyDataSetChanged();
        bindingView.refreshLayout.finishRefresh();
        bindingView.refreshLayout.finishLoadmore();

        mIsFirst = false;
    }

    /**
     * 加载失败后点击后的操作
     */
    @Override
    protected void onRefresh() {
        loadAndroidData();
    }
}