package com.pxy.eshore.http;

import com.pxy.eshore.bean.HotMovieBean;
import com.pxy.eshore.bean.MovieDetailBean;

import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import rx.Observable;

/**
 * @author JamesPxy
 * @date 2017/12/19  15:35
 * @Description
 */

public interface MyApi {

    /**
     * 豆瓣热映电影，每日更新
     */
    @Headers("Cache-Control : public, max-age = 3600")
    @GET("v2/movie/in_theaters")
    Observable<HotMovieBean> getHotMovie();

    /**
     * 获取电影详情
     *
     * @param id 电影bean里的id
     */
    @Headers("Cache-Control : public, max-age = 3600")
    @GET("v2/movie/subject/{id}")
    Observable<MovieDetailBean> getMovieDetail(@Path("id") String id);

}
