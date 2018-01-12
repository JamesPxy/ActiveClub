package com.pxy.eshore.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.pxy.eshore.R;
import com.pxy.eshore.adapter.MovieDetailAdapter;
import com.pxy.eshore.base.BaseHeaderActivity;
import com.pxy.eshore.base.Constants;
import com.pxy.eshore.bean.MovieDetailBean;
import com.pxy.eshore.bean.moviechild.SubjectsBean;
import com.pxy.eshore.databinding.ActivityMovieDetailBinding;
import com.pxy.eshore.databinding.HeaderSlideShapeBinding;
import com.pxy.eshore.http.HttpClient;
import com.pxy.eshore.http.MySubscriber;
import com.pxy.eshore.utils.CommonUtils;
import com.pxy.eshore.utils.StringFormatUtil;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author JamesPxy
 * @date 2017/12/1  09:49
 * @Description 电影详情页
 */

public class MovieDetailActivity extends BaseHeaderActivity<HeaderSlideShapeBinding, ActivityMovieDetailBinding> {

    private SubjectsBean subjectsBean;
    private String mMoreUrl;
    private String mMovieName;
    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        if (getIntent() != null) {
            subjectsBean = (SubjectsBean) getIntent().getSerializableExtra("bean");
        }

        initSlideShapeTheme(setHeaderImgUrl(), setHeaderImageView());

        setTitle(subjectsBean.getTitle());
        setSubTitle(String.format("主演：%s", StringFormatUtil.formatName(subjectsBean.getCasts())));
//        ImgLoadUtil.showImg(bindingHeaderView.ivOnePhoto,subjectsBean.getImages().getLarge());
        bindingHeaderView.setSubjectsBean(subjectsBean);
        bindingHeaderView.executePendingBindings();

//        loadMovieDetail();
        loadMovieDetailNew();
    }

    @Override
    protected void setTitleClickMore() {
//        WebViewActivity.loadUrl(MovieDetailActivity.this, mMoreUrl, mMovieName);
        Toast.makeText(MovieDetailActivity.this, "待开发", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected int setHeaderLayout() {
        return R.layout.header_slide_shape;
    }

    @Override
    protected String setHeaderImgUrl() {
        if (subjectsBean == null) {
            return "";
        }
        return subjectsBean.getImages().getMedium();
    }

    @Override
    protected ImageView setHeaderImageView() {
        return bindingHeaderView.imgItemBg;
    }

    private void loadMovieDetail() {
        Subscription get = HttpClient.Builder.getDouBanService().getMovieDetail(subjectsBean.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MovieDetailBean>() {
                    @Override
                    public void onCompleted() {
                        showContentView();
                        Log.d(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                        showError();
                    }

                    @Override
                    public void onNext(final MovieDetailBean movieDetailBean) {
                        Log.d(TAG, "onNext: " + movieDetailBean.toString());
                        // 上映日期
                        bindingHeaderView.tvOneDay.setText(String.format("上映日期：%s", movieDetailBean.getYear()));
                        // 制片国家
                        bindingHeaderView.tvOneCity.setText(String.format("制片国家/地区：%s",
                                StringFormatUtil.formatGenres(movieDetailBean.getCountries())));
                        bindingHeaderView.setMovieDetailBean(movieDetailBean);
                        bindingContentView.setBean(movieDetailBean);
                        bindingContentView.executePendingBindings();

                        mMoreUrl = movieDetailBean.getAlt();
                        mMovieName = movieDetailBean.getTitle();

                        transformData(movieDetailBean);
                    }
                });
        addSubscription(get);
    }

    private void loadMovieDetailNew() {
        String id = subjectsBean.getId();
        String key = Constants.DOUBAN_MOVIE_DETAIL + id;
        Subscription subscription = HttpClient.Builder.getDouBanService().getMovieDetail(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MySubscriber<MovieDetailBean>(key) {
                    @Override
                    public void onCompleted() {
                        showContentView();
                    }

                    @Override
                    public void doError(Throwable e) {
                        showError();
                    }

                    @Override
                    public void doSuccess(MovieDetailBean movieDetailBean) {
                        Log.d(TAG, "onNext: " + movieDetailBean.toString());
                        // 上映日期
                        bindingHeaderView.tvOneDay.setText(String.format("上映日期：%s", movieDetailBean.getYear()));
                        // 制片国家
                        bindingHeaderView.tvOneCity.setText(String.format("制片国家/地区：%s",
                                StringFormatUtil.formatGenres(movieDetailBean.getCountries())));
                        bindingHeaderView.setMovieDetailBean(movieDetailBean);
                        bindingContentView.setBean(movieDetailBean);
                        bindingContentView.executePendingBindings();

                        mMoreUrl = movieDetailBean.getAlt();
                        mMovieName = movieDetailBean.getTitle();

                        transformData(movieDetailBean);
                    }
                });
        addSubscription(subscription);
    }

    /**
     * 异步线程转换数据
     */
    private void transformData(final MovieDetailBean movieDetailBean) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < movieDetailBean.getDirectors().size(); i++) {
                    movieDetailBean.getDirectors().get(i).setType("导演");
                }
                for (int i = 0; i < movieDetailBean.getCasts().size(); i++) {
                    movieDetailBean.getCasts().get(i).setType("演员");
                }

                MovieDetailActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setAdapter(movieDetailBean);
                    }
                });
            }
        }).start();
    }

    /**
     * 设置导演&演员adapter
     */
    private void setAdapter(MovieDetailBean movieDetailBean) {
        bindingContentView.xrvCast.setVisibility(View.VISIBLE);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(MovieDetailActivity.this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        bindingContentView.xrvCast.setLayoutManager(mLayoutManager);
        // 需加，不然滑动不流畅
        bindingContentView.xrvCast.setNestedScrollingEnabled(false);
        bindingContentView.xrvCast.setHasFixedSize(false);

        MovieDetailAdapter mAdapter = new MovieDetailAdapter();
        mAdapter.addAll(movieDetailBean.getDirectors());
        mAdapter.addAll(movieDetailBean.getCasts());
        bindingContentView.xrvCast.setAdapter(mAdapter);
    }

    @Override
    protected void onRefresh() {
//        loadMovieDetail();
        loadMovieDetail();
    }

    /**
     * @param context      activity
     * @param positionData bean
     * @param imageView    imageView
     */
    public static void start(Activity context, SubjectsBean positionData, ImageView imageView) {
        Intent intent = new Intent(context, MovieDetailActivity.class);
        intent.putExtra("bean", positionData);
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(context,
                        imageView, CommonUtils.getString(R.string.transition_movie_img));//与xml文件对应
        ActivityCompat.startActivity(context, intent, options.toBundle());
    }

}
