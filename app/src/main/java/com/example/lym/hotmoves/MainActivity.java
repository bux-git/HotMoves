package com.example.lym.hotmoves;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.lym.hotmoves.bean.MovieBean;
import com.example.lym.hotmoves.util.NetWorkTask;
import com.example.lym.hotmoves.util.NetworkUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NetWorkTask.NetWorkCallBack {
    private static final String TAG = "MainActivity";

    private RecyclerView mRecyclerView;
    private ProgressBar mPbLoading;
    private RadioGroup mRadioGroup;

    private MovieAdapter mMovieAdapter;
    private GridLayoutManager mLayoutManager;

    private static final int SPAN_COUNT = 2;
    private String mPath;
    private int mPage = 1;
    //刷新状态
    private boolean mIsRefresh = true;
    private boolean mIsNoMore = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            mRecyclerView = findViewById(R.id.rv_moves_list);
            mPbLoading = findViewById(R.id.pb_loading);
            mRadioGroup = findViewById(R.id.rg_group);

            mRecyclerView.setLayoutManager(mLayoutManager = new GridLayoutManager(this, 2));
            mRecyclerView.setHasFixedSize(true);

            //获取屏幕宽度
            WindowManager wm = this.getWindowManager();
            int width = wm.getDefaultDisplay().getWidth();

            mRecyclerView.setAdapter(mMovieAdapter = new MovieAdapter(new MovieAdapter.OnMovieItemClickListener() {
                @Override
                public void movieItemClick(MovieBean movieBean) {

                    MovieDetailActivity.start(MainActivity.this, movieBean.getId());
                }
            }, width / SPAN_COUNT));

            initListener();


            refreshData(NetworkUtils.POPULAR_PATH);

    }

    private void initListener() {
        //监听排序类型
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //热门
                if (checkedId == R.id.rb_popular) {
                    refreshData(NetworkUtils.POPULAR_PATH);

                } else if (checkedId == R.id.rb_top_rated) {
                    //评分排行
                    refreshData(NetworkUtils.TOP_RATED_PATH);
                }
            }
        });

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
        new NetWorkTask(this).execute(NetworkUtils.getMovieListByType(mPath, mIsRefresh ? mPage = 1 : ++mPage));
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
        }else{
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
