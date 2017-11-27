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
import com.example.lym.hotmoves.bean.MovieBean;
import com.example.lym.hotmoves.bean.MovieDetailBean;
import com.example.lym.hotmoves.util.NetWorkTask;
import com.example.lym.hotmoves.util.NetworkUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Description：
 * @author：Bux on 2017/11/23 16:35
 * @email: 471025316@qq.com
 */

public class MovieDetailActivity extends AppCompatActivity implements NetWorkTask.NetWorkCallBack {
    private static final String MOVIE_ID_EXTRA = "movie";

    //海报
    @BindView(R.id.iv_poster)
    ImageView mIvPoster;
    //原名
    @BindView(R.id.tv_original_title)
    TextView mTvOriginalTitle;
    //上映日期
    @BindView(R.id.tv_release_date)
    TextView mTvReleaseDate;
    //时长
    @BindView(R.id.tv_runtime)
    TextView mTvRuntime;
    //人气
    @BindView(R.id.tv_popularity)
    TextView mTvPopularity;


    //评分
    @BindView(R.id.tv_vote_average)
    TextView mTvVoteAverAge;
    //投票人数
    @BindView(R.id.tv_vote_count)
    TextView mTvVoteCount;

    @BindView(R.id.tv_introduction)
    TextView mTvIntroduction;
    //简介
    @BindView(R.id.tv_overview)
    TextView mTvOverView;
    @BindView(R.id.pb_loading)
    ProgressBar mPbLoading;


    private MovieBean mMovieBean;

    public static void start(Context context, MovieBean movie) {
        Intent intent = new Intent(context, MovieDetailActivity.class);
        intent.putExtra(MOVIE_ID_EXTRA, movie);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail_layout);
        ButterKnife.bind(this);

        setTitle(R.string.movie_detail_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        if (intent.hasExtra(MOVIE_ID_EXTRA)) {
            mMovieBean = intent.getParcelableExtra(MOVIE_ID_EXTRA);
            setUpDataView(mMovieBean.getOriginal_title(), mMovieBean.getRelease_date(), ""
                    ,String.valueOf(mMovieBean.getPopularity()), String.valueOf(mMovieBean.getVote_average()), String.valueOf(mMovieBean.getVote_count())
                    , mMovieBean.getOverview(),mMovieBean.getPoster_path());
           // getData();
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


        setUpDataView(bean.getOriginal_title(), bean.getRelease_date(), String.valueOf(bean.getRuntime())
                , String.valueOf(bean.getPopularity()), String.valueOf(bean.getVote_average()), String.valueOf(bean.getVote_count())
                , bean.getOverview(),bean.getPoster_path());

    }

    private void setUpDataView(String originalTitle, String releaseDate, String runTime, String popularity, String voteAverage
            , String voteCount, String overView,String path) {
        mTvOriginalTitle.setText(originalTitle);
        mTvReleaseDate.setText(getString(R.string.release_date_format, releaseDate));
        mTvRuntime.setText(getString(R.string.runtime_format, runTime));
        mTvPopularity.setText(getString(R.string.popularity_format, popularity));
        mTvVoteAverAge.setText(getString(R.string.vote_average_format, voteAverage));

        mTvVoteCount.setText(getString(R.string.vote_count_format, voteCount));
        mTvIntroduction.setText(R.string.introducation);

        mTvOverView.setText(overView);

        Glide.with(this).load(NetworkUtils.getImagePath(path)).into(mIvPoster);
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
        new NetWorkTask(this).execute(NetworkUtils.getMovieDetail(mMovieBean.getId()));
    }
}
