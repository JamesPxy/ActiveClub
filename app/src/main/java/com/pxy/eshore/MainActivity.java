package com.pxy.eshore;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.pxy.eshore.ui.HotMovieActivity;
import com.pxy.eshore.ui.TopMovieActivity;
import com.pxy.eshore.ui.WelfareActivity;
import com.pxy.eshore.ui.fragment.HomeFragment;
import com.pxy.recyclerbaner.RecyclerBannerBase;
import com.pxy.recyclerbaner.RecyclerBannerStereo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private Context context;

    private RecyclerBannerStereo bannerStereo;
    private BottomNavigationView bottomNavigationView;
    private LinearLayout fragmentContainer;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    showFragment(new HomeFragment("Home"));
                    return true;
                case R.id.navigation_dashboard:
                    showFragment(new HomeFragment("Dashboard"));
                    return true;
                case R.id.navigation_notifications:
                    showFragment(new HomeFragment("Notifications"));
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        bannerStereo = findViewById(R.id.banner1);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fragmentContainer = findViewById(R.id.fragment_container);
//        setSupportActionBar(toolbar);
        // 设置透明状态栏
//        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorTheme), 0);
//        StatusBarUtil.setTranslucent(this);
        setToolBar();
        context = this;
        setDrawerLayout();
        setBanner();
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        showFragment(new HomeFragment("首页"));
    }

    /**
     * 显示所需的Fragment  可个性化定制
     *
     * @param home
     */
    private void showFragment(HomeFragment home) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, home);
        transaction.commit();
    }


    private void setDrawerLayout() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * 设置titlebar
     */
    protected void setToolBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //去除默认Title显示
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    /**
     * 设置首页轮播图
     */
    private void setBanner() {
        List<String> list = new ArrayList<>();
        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1510833032476&di=3998d3aaa2e006c1120c8534311e9a81&imgtype=0&src=http%3A%2F%2Fe.hiphotos.baidu.com%2Fzhidao%2Fpic%2Fitem%2F810a19d8bc3eb135275b10f1ae1ea8d3fc1f44df.jpg");
        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1510833032475&di=8ceb2f76c9bed8a2ebaf5ba85efd4440&imgtype=0&src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2Fb%2F591953d9ce3cb.jpg");
        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1510833032475&di=4cf3e1192a9e58529440d213de48a95a&imgtype=0&src=http%3A%2F%2Fimg.7xz.com%2Fimg%2Fpicimg%2F201607%2F20160715161145_327a1d30f651dab8932.jpg");
        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1510833032474&di=3723ba473a9c92dc420e2538a9aa584e&imgtype=0&src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2Fc%2F57450b9a295f5.jpg");
//        list.add("http://ojyz0c8un.bkt.clouddn.com/home_two_08.png");
//        list.add("http://ojyz0c8un.bkt.clouddn.com/home_two_07.png");
//        list.add("http://ojyz0c8un.bkt.clouddn.com/home_two_09.png");

        bannerStereo.initBannerImageView(list, new RecyclerBannerBase.OnBannerItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(MainActivity.this, "clicked:" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            startActivity(new Intent(context, TopMovieActivity.class));
        } else if (id == R.id.nav_gallery) {
            startActivity(new Intent(context, WelfareActivity.class));
        } else if (id == R.id.nav_slideshow) {
            startActivity(new Intent(context, HotMovieActivity.class));
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
