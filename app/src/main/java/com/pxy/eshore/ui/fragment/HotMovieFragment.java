package com.pxy.eshore.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.pxy.eshore.R;
import com.pxy.eshore.adapter.HotMovieAdapter;
import com.pxy.eshore.base.BaseFragment;
import com.pxy.eshore.base.Constants;
import com.pxy.eshore.bean.HotMovieBean;
import com.pxy.eshore.bean.moviechild.SubjectsBean;
import com.pxy.eshore.databinding.FragmentHotMovieBinding;
import com.pxy.eshore.http.HttpClient;
import com.pxy.eshore.http.MySubscriber;
import com.pxy.eshore.ui.TopMovieActivity;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author JamesPxy
 * @date 2017/12/20  16:19
 * @Description 热映电影对应fragment
 */

public class HotMovieFragment extends BaseFragment<FragmentHotMovieBinding> {

    private String TAG = "HotMovieFragment";
    private HotMovieAdapter hotMovieAdapter;
    private List<SubjectsBean> data;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bindingView.llMovieTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, TopMovieActivity.class));
            }
        });
        onRefresh();
    }

    @Override
    protected void loadData() {
        Log.i(TAG, "loadData has  excuated");
    }

    @Override
    public int setContent() {
        return R.layout.fragment_hot_movie;
    }

    @Override
    protected void onRefresh() {
        if (null != hotMovieAdapter && hotMovieAdapter.getItemCount() > 0) return;
        getHotMovie();
    }

    private void setAdapter() {
        if (null == hotMovieAdapter) {
            hotMovieAdapter = new HotMovieAdapter(mContext, data);
            bindingView.recyclerview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
            bindingView.recyclerview.setAdapter(hotMovieAdapter);
            showContentView();
        } else {
            hotMovieAdapter.notifyDataSetChanged();
        }
    }

    private void getHotMovie() {
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
                            Toast.makeText(mContext, "返回数据为空", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        addSubscription(subscription);
    }
}
