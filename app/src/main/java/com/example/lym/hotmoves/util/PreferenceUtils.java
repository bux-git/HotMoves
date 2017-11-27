package com.example.lym.hotmoves.util;

import android.content.Context;
import android.preference.PreferenceManager;

import com.example.lym.hotmoves.R;

/**
 * @Description：
 * @author：Bux on 2017/11/27 15:35
 * @email: 471025316@qq.com
 */

public class PreferenceUtils {
    public static final int SORT_KEY= R.string.sort_value_key;


    /**
     * 获取排序方式path
     * @param context
     * @return
     */
    public static String getSortPath(Context context){

        String path=PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.getString(SORT_KEY),"");

        return path;
    }

}
