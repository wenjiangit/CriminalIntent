package com.wenjian.criminalintent.activity;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.wenjian.criminalintent.base.SingleFragmentActivity;
import com.wenjian.criminalintent.fragment.CrimeCameraFragment;

public class CrimeCameraActivity extends SingleFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Fragment createFragment() {
        return CrimeCameraFragment.newInstance();
    }

    @Override
    protected boolean needFullScreen() {
        return true;
    }
}
