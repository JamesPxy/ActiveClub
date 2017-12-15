package com.pxy.recyclerbaner;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.pxy.recyclerbaner.adapter.StereoBannerAdapter;
import com.pxy.recyclerbaner.layoutmanager.BannerLayoutManager;

import java.util.List;

/**
 * @author JamesPxy
 * @time 2017/12/14
 * @Description 3D立体轮播组件
 */
public class RecyclerBannerStereo extends RecyclerBannerBase<BannerLayoutManager, StereoBannerAdapter> {


    public RecyclerBannerStereo(Context context) {
        this(context, null);
    }

    public RecyclerBannerStereo(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerBannerStereo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onBannerScrolled(RecyclerView recyclerView, int dx, int dy) {

    }

    @Override
    protected void onBannerScrollStateChanged(RecyclerView recyclerView, int newState) {
        int first = mLayoutManager.getCurrentPosition();
        if (currentIndex != first) {
            currentIndex = first;
            refreshIndicator();
        }
    }

    @Override
    protected BannerLayoutManager getLayoutManager(Context context, int orientation) {
        return new BannerLayoutManager(orientation, itemSpace);
    }

    @Override
    protected StereoBannerAdapter getAdapter(Context context, List<String> list, OnBannerItemClickListener onBannerItemClickListener) {
        return new StereoBannerAdapter(context, list, onBannerItemClickListener);
    }


}