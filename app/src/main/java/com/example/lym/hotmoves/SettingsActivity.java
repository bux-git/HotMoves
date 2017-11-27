package com.example.lym.hotmoves;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

/**
 * @Description：
 * @author：Bux on 2017/11/25 17:24
 * @email: 471025316@qq.com
 */

public class SettingsActivity extends AppCompatActivity {
    public static final int REQUEST_CODE=0X11;

    public static void start(Activity context){
        Intent intent = new Intent(context,SettingsActivity.class);
        context.startActivityForResult(intent,REQUEST_CODE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.movie_setting_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction()
                .add(android.R.id.content, new SettingFragment())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
