package com.example.lym.hotmoves.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Loader;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.net.URL;

/**
 * @Description：
 * @author：Bux on 2017/11/29 11:18
 * @email: 471025316@qq.com
 */

public class NetWorkTaskNew implements LoaderManager.LoaderCallbacks<String> {

    private static final String URL_EXTRA = "URL_EXTRA";

    private Activity mContext;
    private NetWorkCallBack mCallBack;

    public NetWorkTaskNew(Activity context) {
        mContext = context;
        if (mContext instanceof NetWorkCallBack) {
            mCallBack = (NetWorkCallBack) mContext;
        }


    }

    /**
     * 初始化加载器
     *
     * @param id
     */
    public void initLoader(int id, URL url) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(URL_EXTRA, url);

        mContext.getLoaderManager().initLoader(id, bundle, this);
    }

    /**
     * 重启加载器
     *
     * @param id
     */
    public void restartLoader(int id, URL url) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(URL_EXTRA, url);
        mContext.getLoaderManager().restartLoader(id, bundle, this);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {

        return new AsyncTaskLoader<String>(mContext) {
            private boolean hasResults;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();

                if (args == null || hasResults) {
                    return;
                }
                mCallBack.onPreExecute();
                forceLoad();
            }

            @Override
            public String loadInBackground() {
                URL url = (URL) args.getSerializable(URL_EXTRA);
                String result = null;
                try {
                    result = NetworkUtils.getResponseFromHttpUrl(url);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }

                return result;
            }

            @Override
            public void deliverResult(String data) {
                if (!TextUtils.isEmpty(data)) {
                    hasResults = true;
                } else {
                    hasResults = false;
                }
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        if (TextUtils.isEmpty(data)) {
            mCallBack.onFail("获取失败");
        } else {
            try {
                JSONObject jsonObject = JSONObject.parseObject(data);
                String success = jsonObject.getString("success");
                //失败
                if (!TextUtils.isEmpty(success) && "false".equals(success)) {

                    mCallBack.onFail(jsonObject.getString("status_message"));
                } else {
                    mCallBack.onSuccess(jsonObject);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                mCallBack.onFail("获取失败");
            }
        }
        mCallBack.onComplete();
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }


    public interface NetWorkCallBack {
        /**
         * 执行网络任务前
         */
        void onPreExecute();

        /**
         * 成功
         *
         * @param jsonObject
         */
        void onSuccess(JSONObject jsonObject);

        /**
         * 失败
         *
         * @param msg
         */
        void onFail(String msg);

        /**
         * 完成
         */
        void onComplete();
    }
}
