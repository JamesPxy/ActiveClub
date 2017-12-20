package com.pxy.eshore.ui.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.pxy.eshore.R;
import com.pxy.eshore.bean.HotMovieBean;
import com.pxy.eshore.http.MyApi;
import com.pxy.recyclerbaner.RecyclerBannerBase;
import com.pxy.recyclerbaner.RecyclerBannerNormal;
import com.tamic.novate.BaseSubscriber;
import com.tamic.novate.Novate;
import com.tamic.novate.Throwable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JamesPxy
 * @time 2017/12/15  11:05
 * @Description 首页对应fragment
 */
public class HomeFragment extends Fragment {

    private RecyclerBannerNormal bannerNormal;
    private TextView tvMsg;
    private String message;
    private Context mContext;

    public HomeFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public HomeFragment(String message) {
        this.message = message;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        tvMsg = view.findViewById(R.id.message);
        bannerNormal = view.findViewById(R.id.banner1);
        tvMsg.setText(message);
        mContext = getActivity();
        setBanner();
//        getTop250();
        return view;
    }

    /**
     * 设置首页轮播图
     */
    private void setBanner() {
        List<String> list = new ArrayList<>();
//        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1510833032475&di=8ceb2f76c9bed8a2ebaf5ba85efd4440&imgtype=0&src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2Fb%2F591953d9ce3cb.jpg");
//        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1510833032475&di=4cf3e1192a9e58529440d213de48a95a&imgtype=0&src=http%3A%2F%2Fimg.7xz.com%2Fimg%2Fpicimg%2F201607%2F20160715161145_327a1d30f651dab8932.jpg");
//        list.add("http://business.cdn.qianqian.com/qianqian/pic/bos_client_1513328061a3fff3d24ea8daae55e02d41615958cb.jpg");

        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1510833032476&di=3998d3aaa2e006c1120c8534311e9a81&imgtype=0&src=http%3A%2F%2Fe.hiphotos.baidu.com%2Fzhidao%2Fpic%2Fitem%2F810a19d8bc3eb135275b10f1ae1ea8d3fc1f44df.jpg");
        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1510833032474&di=3723ba473a9c92dc420e2538a9aa584e&imgtype=0&src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2Fc%2F57450b9a295f5.jpg");
        list.add("http://business.cdn.qianqian.com/qianqian/pic/bos_client_1513334741efc21a1f3db87a58d73eb462a4b3fe96.jpg");
        list.add("http://business.cdn.qianqian.com/qianqian/pic/bos_client_151357145737a47da3ebd5b57c79b36739e63afd15.jpg");
        list.add("http://business.cdn.qianqian.com/qianqian/pic/bos_client_1513327830057bc1862a54edaeb8c5c4f6168d2511.jpg");
        bannerNormal.initBannerImageView(list, new RecyclerBannerBase.OnBannerItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(getActivity(), "clicked:" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTop250() {
        Novate novate = new Novate.Builder(getActivity())
                .baseUrl("http://api.douban.com/")
                .addLog(true)
                .addCache(true)
                .build();

        MyApi myAPI = novate.create(MyApi.class);
//        novate.call(myAPI.getHotMovie(), new RxStringCallback() {
//            @Override
//            public void onNext(Object tag, String response) {
//                Toast.makeText(mContext, "onNext---" + response, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onError(Object tag, Throwable e) {
//                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onCancel(Object tag, Throwable e) {
//                Toast.makeText(mContext, "onCancel", Toast.LENGTH_SHORT).show();
//            }
//        });

        //法2：
        novate.schedulersMain(myAPI.getHotMovie())
                .subscribe(new BaseSubscriber<HotMovieBean>(mContext) {
                    @Override
                    public void onNext(HotMovieBean hotMovieBean) {
                        Toast.makeText(mContext, hotMovieBean.toString(), Toast.LENGTH_SHORT).show();
                        Log.i("666", "onNext: hotmoviebean=" + hotMovieBean.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.i("666", "onError: " + e.getMessage());
                    }
                });

    }


}
