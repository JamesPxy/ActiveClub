package com.pxy.recyclerbaner.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.pxy.recyclerbaner.R;
import com.pxy.recyclerbaner.RecyclerBannerBase;

import java.util.List;


/**
 * 3D立体轮播页适配器
 */
public class StereoBannerAdapter extends BaseBannerAdapter<StereoBannerAdapter.MyViewHolder> {

    private RecyclerBannerBase.OnBannerItemClickListener onBannerItemClickListener;

    public StereoBannerAdapter(Context context, List<String> urlList, RecyclerBannerBase.OnBannerItemClickListener onBannerItemClickListener) {
        super(context, urlList);
        this.onBannerItemClickListener = onBannerItemClickListener;
    }

    @Override
    protected MyViewHolder createCustomViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false));
    }

    @Override
    public void bindCustomViewHolder(MyViewHolder holder, final int position) {
        if (urlList == null || urlList.isEmpty())
            return;
        String url = urlList.get(position % urlList.size());
        ImageView img = (ImageView) holder.imageView;
        Glide.with(context).load(url).into(img);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onBannerItemClickListener != null) {
                    onBannerItemClickListener.onItemClick(position % urlList.size());
                }
            }
        });
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
        }
    }

}
