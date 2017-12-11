package com.pxy.eshore.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.pxy.eshore.R;
import com.pxy.eshore.bean.moviechild.SubjectsBean;
import com.pxy.eshore.databinding.ItemHotMovieBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JamesPxy
 * @date 2017/12/11  16:06
 * @Description  热映电影adapter
 */

public class HotMovieAdapter extends RecyclerView.Adapter<HotMovieAdapter.MyViewHolder> {

    List<SubjectsBean> data = new ArrayList<>();

    public HotMovieAdapter(List<SubjectsBean> data) {
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(parent, R.layout.item_hot_movie);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.binding.setSubjectsBean(data.get(position));
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        private final ItemHotMovieBinding binding;

        public MyViewHolder(ViewGroup parent, int layoutId) {
            // 注意要依附 viewGroup，不然显示item不全!!
            super(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), layoutId, parent, false).getRoot());
            // 得到这个View绑定的Binding
            binding = DataBindingUtil.getBinding(this.itemView);
        }
    }
}
