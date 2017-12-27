package com.pxy.eshore.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.pxy.eshore.R;
import com.pxy.eshore.ui.fragment.AndroidFragment;
import com.pxy.eshore.ui.fragment.HomeFragment;
import com.pxy.eshore.ui.fragment.HotMovieFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ViewPager.OnPageChangeListener {

    private Toolbar toolbar;
    private Context context;
    private DrawerLayout drawerLayout;

    private FragmentTabHost tabHost;
    private ViewPager viewPager;
    private Class fragmentArray[] = {HotMovieFragment.class, HomeFragment.class, HomeFragment.class};
    private int imageViewArray[] = {R.drawable.selector_tab_home, R.drawable.selector_tab_music, R.drawable.selector_tab_home};
    private String textViewArray[] = {"电影", "音乐", "我的"};
    private List<Fragment> fragmentList = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        tabHost = findViewById(android.R.id.tabhost);
        viewPager = findViewById(R.id.viewpager);
//        setSupportActionBar(toolbar);
        // 设置透明状态栏
//        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorTheme), 0);
//        StatusBarUtil.setTranslucent(this);
//        StatusBarUtil.setColorNoTranslucentForDrawerLayout(MainActivity.this, drawerLayout,
//                CommonUtils.getColor(R.color.colorTheme));
        context = this;
        setToolBar();
        setTabHost();
        setViewPager();
        setDrawerLayout();
    }

    private void setDrawerLayout() {
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
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

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 设置底部tabHost
     */
    private void setTabHost() {
        tabHost.setup(this, getSupportFragmentManager(), R.id.viewpager);//绑定viewpager
        //设置分割线
//        tabHost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        tabHost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
        /*实现setOnTabChangedListener接口,目的是为监听界面切换），然后实现TabHost里面图片文字的选中状态切换*/
        /*简单来说,是为了当点击下面菜单时,上面的ViewPager能滑动到对应的Fragment*/
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                Log.i("???", "onTabChanged: tabid=" + tabId);
                viewPager.setCurrentItem(tabHost.getCurrentTab());//把选中的Tab的位置赋给适配器，让它控制页面切换
            }
        });

        /*新建Tabspec选项卡并设置Tab菜单栏的内容和绑定对应的Fragment*/
        for (int i = 0; i < textViewArray.length; i++) {
            // 给每个Tab按钮设置标签、图标和文字
            TabHost.TabSpec tabSpec = tabHost.newTabSpec(textViewArray[i])
                    .setIndicator(getTabItemView(i));
            // 将Tab按钮添加进Tab选项卡中，并绑定Fragment
            tabHost.addTab(tabSpec, fragmentArray[i], null);
            tabHost.setTag(i);
//            tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.select_one_header);//设置Tab被选中的时候颜色改变
        }
    }

    /*初始化Fragment*/
    private void setViewPager() {
        fragmentList.add(new HotMovieFragment());
//        fragmentList.add(AndroidFragment.newInstance("all"));
        fragmentList.add(AndroidFragment.newInstance("Android"));
        fragmentList.add(new HomeFragment());
        //绑定Fragment适配器
        viewPager.setAdapter(new MyFragmentAdapter(getSupportFragmentManager()));
        //设置预加载个数 默认为1
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(this);
    }

    private View getTabItemView(int i) {
        //将xml布局转换为view对象
        View view = LayoutInflater.from(context).inflate(R.layout.item_bottom_tab, null);
        //利用view对象，找到布局中的组件,并设置内容，然后返回视图
        ImageView mImageView = (ImageView) view.findViewById(R.id.tab_imageview);
        TextView mTextView = (TextView) view.findViewById(R.id.tab_textview);
        mImageView.setBackgroundResource(imageViewArray[i]);
        mTextView.setText(textViewArray[i]);
        return view;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        TabWidget widget = tabHost.getTabWidget();
        int oldFocusability = widget.getDescendantFocusability();
        widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);//设置View覆盖子类控件而直接获得焦点
        tabHost.setCurrentTab(position);//根据位置Postion设置当前的Tab
        widget.setDescendantFocusability(oldFocusability);//设置取消分割线
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * fragment对应适配器
     */
    private class MyFragmentAdapter extends FragmentPagerAdapter {

        public MyFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }
}
