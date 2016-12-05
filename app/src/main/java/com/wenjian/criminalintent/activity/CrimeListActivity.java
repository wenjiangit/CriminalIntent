package com.wenjian.criminalintent.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.wenjian.criminalintent.fragment.CrimeListFragment;
import com.wenjian.criminalintent.base.SingleFragmentActivity;

public class CrimeListActivity extends SingleFragmentActivity{

    public static final String TAG = CrimeListActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
    }

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
