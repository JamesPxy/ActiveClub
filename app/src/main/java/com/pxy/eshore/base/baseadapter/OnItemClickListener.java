package com.pxy.eshore.base.baseadapter;

import com.pxy.eshore.bean.GankIoDataBean;

public interface OnItemClickListener<T> {
    void onClick(GankIoDataBean.ResultBean t, int position);
}
