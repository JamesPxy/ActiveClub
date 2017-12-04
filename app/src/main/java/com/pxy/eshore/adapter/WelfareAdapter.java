package com.pxy.eshore.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.pxy.eshore.R;
import com.pxy.eshore.base.baseadapter.BaseRecyclerViewAdapter;
import com.pxy.eshore.base.baseadapter.BaseRecyclerViewHolder;
import com.pxy.eshore.bean.GankIoDataBean;
import com.pxy.eshore.databinding.ItemWelfareBinding;

/**
 * @author JamesPxy
 * @date 2017/12/4  16:48
 * @Description
 */

public class WelfareAdapter extends BaseRecyclerViewAdapter<GankIoDataBean.ResultBean> {

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_welfare);
    }

    private class ViewHolder extends BaseRecyclerViewHolder<GankIoDataBean.ResultBean, ItemWelfareBinding> {

        public ViewHolder(ViewGroup viewGroup, int layoutId) {
            super(viewGroup, layoutId);
        }

        @Override
        public void onBindViewHolder(final GankIoDataBean.ResultBean resultsBean, final int position) {
            /**
             * 注意：DensityUtil.setViewMargin(itemView,true,5,3,5,0);
             * 如果这样使用，则每个item的左右边距是不一样的，
             * 这样item不能复用，所以下拉刷新成功后显示会闪一下
             * 换成每个item设置上下左右边距是一样的话，系统就会复用，就消除了图片不能复用 闪跳的情况
             */
//            if (position % 2 == 0) {
//                DensityUtil.setViewMargin(itemView, false, 12, 6, 12, 0);
//            } else {
//                DensityUtil.setViewMargin(itemView, false, 6, 12, 12, 0);
//            }
            binding.setBean(resultsBean);
            // 仿抖动
            binding.executePendingBindings();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClick(resultsBean, position);
                    }
                }
            });
        }
    }


}
