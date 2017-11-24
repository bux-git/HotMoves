/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.lym.hotmoves.util;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the weather servers.
 */
public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    //developers.themoviedb.org 秘钥
    private static final String APP_KEY = "";

    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    //图片加载路径
    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185";

    //电影path
    private static final String API_TYPE_MOVIE_PATH = "movie";
    public static final String TOP_RATED_PATH = "top_rated";
    public static final String POPULAR_PATH = "popular";


    private static final String LANGUAGE = "zn-CN";

    private static final String API_KEY_PARAM = "api_key";
    private static final String LANGUAGE_PARAM = "language";
    private static final String PAGE_PARAM = "page";


    /**
     * 根据排序类型获取电影列表
     *
     * @param path 排序类型 热门评分 等路径
     * @return
     */
    public static URL getMovieListByType(String path, int page) {

        Map<String,String> map = new HashMap();
        map.put(PAGE_PARAM, String.valueOf(page));

        URL url = buildUrl(map,API_TYPE_MOVIE_PATH,path);

        return url;
    }

    /**
     * 电影详情
     * @param movieId
     * @return
     */
    public static URL getMovieDetail(int movieId){
        URL url = buildUrl(null, API_TYPE_MOVIE_PATH,String.valueOf(movieId));
        return url;
    }


    private static URL buildUrl(Map<String, String> query, String... path) {
        Uri.Builder builder = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, APP_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, LANGUAGE);

        for(String s:path){
            builder.appendPath(s);
        }
        if(query!=null) {
            for (Map.Entry<String, String> kv : query.entrySet()) {
                builder.appendQueryParameter(kv.getKey(), kv.getValue());
            }
        }
        URL url=null;

        try {
            url = new URL(builder.build().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * 获取图片Path
     *
     * @param imgName
     * @return
     */
    public static String getImagePath(String imgName) {
        return IMAGE_BASE_URL + imgName;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(30*1000);
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}