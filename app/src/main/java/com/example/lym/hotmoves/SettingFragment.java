package com.example.lym.hotmoves;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.example.lym.hotmoves.util.PreferenceUtils;

/**
 * @Description：
 * @author：Bux on 2017/11/25 17:01
 * @email: 471025316@qq.com
 */

public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    ListPreference mSortPref;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting_fragment);
        mSortPref = (ListPreference) findPreference(getString(PreferenceUtils.SORT_KEY));
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(PreferenceUtils.SORT_KEY))) {
            getActivity().setResult(Activity.RESULT_OK);
        }
    }
}
