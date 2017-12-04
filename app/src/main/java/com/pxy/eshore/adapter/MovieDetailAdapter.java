package com.pxy.eshore.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.pxy.eshore.R;
import com.pxy.eshore.base.baseadapter.BaseRecyclerViewAdapter;
import com.pxy.eshore.base.baseadapter.BaseRecyclerViewHolder;
import com.pxy.eshore.bean.moviechild.PersonBean;
import com.pxy.eshore.databinding.ItemMovieDetailPersonBinding;
import com.pxy.eshore.utils.PerfectClickListener;

/**
 * 电影详情适配器
 */

public class MovieDetailAdapter extends BaseRecyclerViewAdapter<PersonBean> {
    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_movie_detail_person);
    }

    private class ViewHolder extends BaseRecyclerViewHolder<PersonBean, ItemMovieDetailPersonBinding> {

        ViewHolder(ViewGroup parent, int layout) {
            super(parent, layout);
        }

        @Override
        public void onBindViewHolder(final PersonBean bean, int position) {
            binding.setPersonBean(bean);
            binding.llItem.setOnClickListener(new PerfectClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    if (bean != null && !TextUtils.isEmpty(bean.getAlt())) {
                        // TODO: 2017/12/1   调转至演员详情 
//                        WebViewActivity.loadUrl(v.getContext(), bean.getAlt(), bean.getName());
                    }
                }
            });
        }
    }
}
