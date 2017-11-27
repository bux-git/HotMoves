package com.example.lym.hotmoves;

import android.app.Application;
import android.preference.PreferenceManager;

/**
 * @Description：
 * @author：Bux on 2017/11/25 23:42
 * @email: 471025316@qq.com
 */

public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        //初始化首选项
        PreferenceManager.setDefaultValues(this,R.xml.setting_fragment,false);
    }
}
