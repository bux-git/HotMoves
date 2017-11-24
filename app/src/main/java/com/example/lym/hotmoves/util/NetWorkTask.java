package com.example.lym.hotmoves.util;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.net.URL;

/**
 * @Description：
 * @author：Bux on 2017/11/24 9:52
 * @email: 471025316@qq.com
 */

public class NetWorkTask extends AsyncTask<URL,Void,String> {

    private NetWorkCallBack mCallBack;

    public NetWorkTask(NetWorkCallBack callBack) {
        mCallBack = callBack;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mCallBack.onPreExecute();
    }

    @Override
    protected String doInBackground(URL... urls) {
        String result = null;
        try {
            result = NetworkUtils.getResponseFromHttpUrl(urls[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (TextUtils.isEmpty(s)) {
            mCallBack.onFail("获取失败");
        } else {
            try {
                JSONObject jsonObject = JSONObject.parseObject(s);
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

    public interface NetWorkCallBack{
        /**
         * 执行网络任务前
         */
        void onPreExecute();

        /**
         * 成功
         * @param jsonObject
         */
        void onSuccess(JSONObject jsonObject);

        /**
         * 失败
         * @param msg
         */
        void onFail(String msg);

        /**
         *完成
         */
        void onComplete();
    }
}
