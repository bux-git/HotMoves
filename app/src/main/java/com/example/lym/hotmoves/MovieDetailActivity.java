package com.example.lym.hotmoves;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.example.lym.hotmoves.bean.MovieDetailBean;
import com.example.lym.hotmoves.util.NetWorkTask;
import com.example.lym.hotmoves.util.NetworkUtils;

/**
 * @Description：
 * @author：Bux on 2017/11/23 16:35
 * @email: 471025316@qq.com
 */

public class MovieDetailActivity extends AppCompatActivity implements NetWorkTask.NetWorkCallBack {
    private static final String MOVIE_ID_EXTRA = "movieId";

    //海报
    private ImageView mIvPoster;
    //原名
    private TextView mTvOriginalTitle;
    //上映日期
    private TextView mTvReleaseDate;
    //时长
    private TextView mTvRuntime;
    //人气
    private TextView mTvPopularity;


    //评分
    private TextView mTvVoteAverAge;
    //投票人数
    private TextView mTvVoteCount;

    private TextView mTvIntroduction;
    //简介
    private TextView mTvOverView;

    private ProgressBar mPbLoading;


    private int mMovieId;

    public static void start(Context context, int movieId) {
        Intent intent = new Intent(context, MovieDetailActivity.class);
        intent.putExtra(MOVIE_ID_EXTRA, movieId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_movie_detail_layout);
            setTitle(R.string.movie_detail_title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            mIvPoster = findViewById(R.id.iv_poster);
            mTvOriginalTitle = findViewById(R.id.tv_original_title);
            mTvReleaseDate = findViewById(R.id.tv_release_date);
            mTvRuntime = findViewById(R.id.tv_runtime);
            mTvPopularity = findViewById(R.id.tv_popularity);

            mTvVoteAverAge = findViewById(R.id.tv_vote_average);
            mTvVoteCount = findViewById(R.id.tv_vote_count);
            mTvIntroduction = findViewById(R.id.tv_introduction);
            mTvOverView = findViewById(R.id.tv_overview);
            mPbLoading = findViewById(R.id.pb_loading);

            Intent intent = getIntent();
            if (intent.hasExtra(MOVIE_ID_EXTRA)) {
                mMovieId = intent.getIntExtra(MOVIE_ID_EXTRA, 0);
                getData();
            } else {
                finish();
            }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPreExecute() {
        mPbLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSuccess(JSONObject jsonObject) {
        MovieDetailBean bean = jsonObject.toJavaObject(MovieDetailBean.class);
        Glide.with(this).load(NetworkUtils.getImagePath(bean.getPoster_path())).into(mIvPoster);

        mTvOriginalTitle.setText(bean.getOriginal_title());
        mTvReleaseDate.setText(getString(R.string.release_date_format, bean.getRelease_date()));
        mTvRuntime.setText(getString(R.string.runtime_format,String.valueOf(bean.getRuntime())));
        mTvPopularity.setText(getString(R.string.popularity_format,String.valueOf(bean.getPopularity())));
        mTvVoteAverAge.setText(getString(R.string.vote_average_format,String.valueOf(bean.getVote_average())));

        mTvVoteCount.setText(getString(R.string.vote_count_format,String.valueOf(bean.getVote_count())));
        mTvIntroduction.setText(R.string.introducation);
        mTvOverView.setText(bean.getOverview());
    }

    @Override
    public void onFail(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onComplete() {
        mPbLoading.setVisibility(View.GONE);
    }

    public void getData() {
        new NetWorkTask(this).execute(NetworkUtils.getMovieDetail(mMovieId));
    }
}
