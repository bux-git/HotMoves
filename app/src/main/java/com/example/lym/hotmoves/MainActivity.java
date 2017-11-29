package com.example.lym.hotmoves;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.lym.hotmoves.adapter.MovieAdapter;
import com.example.lym.hotmoves.bean.MovieBean;
import com.example.lym.hotmoves.util.NetWorkTaskNew;
import com.example.lym.hotmoves.util.NetworkUtils;
import com.example.lym.hotmoves.util.PreferenceUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NetWorkTaskNew.NetWorkCallBack {
    private static final String TAG = "MainActivity";
    private static final int REFRESH_ID = 1;

    @BindView(R.id.rv_moves_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.pb_loading)
    ProgressBar mPbLoading;


    private MovieAdapter mMovieAdapter;
    private GridLayoutManager mLayoutManager;

    private int SPAN_COUNT = 2;
    private String mPath;
    private int mPage = 1;
    //刷新状态
    private boolean mIsRefresh = true;
    private boolean mIsNoMore = true;

    private NetWorkTaskNew mTaskNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        // 判断Android当前的屏幕是横屏还是竖屏。横竖屏判断
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            //竖屏
            SPAN_COUNT = 2;
        } else {

            SPAN_COUNT = 4;
        }

        mRecyclerView.setLayoutManager(mLayoutManager = new GridLayoutManager(this, SPAN_COUNT));
        mRecyclerView.setHasFixedSize(true);

        //获取屏幕宽度
        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();

        mRecyclerView.setAdapter(mMovieAdapter = new MovieAdapter(new MovieAdapter.OnMovieItemClickListener() {
            @Override
            public void movieItemClick(MovieBean movieBean) {

                MovieDetailActivity.start(MainActivity.this, movieBean);
            }
        }, width / SPAN_COUNT));

        initListener();

        mTaskNew = new NetWorkTaskNew(this);
        refreshData(PreferenceUtils.getSortPath(this));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_setting) {
            SettingsActivity.start(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SettingsActivity.REQUEST_CODE) {
                refreshData(PreferenceUtils.getSortPath(this));
            }
        }
    }


    private void initListener() {


        //加载更多
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem = 0;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //newState 0:屏幕停止滚动；1：屏幕正在滚动，用户手指还在屏幕上；2：用户手指离开屏幕后 惯性滑动
                //RcyclerView停止滚动 且 显示最后一项为最后一行的项目时 加载更多 且 服务器还有数据时
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem + SPAN_COUNT >= mLayoutManager.getItemCount()
                        && !mIsNoMore) {
                    loadMore();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    /**
     * 刷新
     */
    private void refreshData(String path) {
        mIsRefresh = true;
        mPath = path;
        getData();
    }

    /**
     * 加载更多
     */
    private void loadMore() {
        mIsRefresh = false;
        getData();
    }

    /**
     * 获取网络数据
     */
    public void getData() {

        if (mIsRefresh) {
            mTaskNew.initLoader(REFRESH_ID, NetworkUtils.getMovieListByType(mPath, mPage = 1));
        } else {
            mTaskNew.initLoader(REFRESH_ID, NetworkUtils.getMovieListByType(mPath, ++mPage));
        }
    }

    @Override
    public void onPreExecute() {
        mPbLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSuccess(JSONObject jsonObject) {
        JSONArray array = jsonObject.getJSONArray("results");
        ArrayList<MovieBean> list = (ArrayList<MovieBean>) array.toJavaList(MovieBean.class);

        //检测是否是最后一页
        if (jsonObject.getInteger("page").equals(jsonObject.getInteger("total_pages"))) {
            mIsNoMore = true;
        } else {
            mIsNoMore = false;
        }

        if (mIsRefresh) {
            mMovieAdapter.setList(list);
        } else {
            mMovieAdapter.addList(list);
        }
    }

    @Override
    public void onFail(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onComplete() {
        mPbLoading.setVisibility(View.GONE);
    }

}
