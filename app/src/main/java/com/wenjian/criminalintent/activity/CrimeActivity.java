package com.wenjian.criminalintent.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.wenjian.criminalintent.fragment.CrimeFragment;
import com.wenjian.criminalintent.base.SingleFragmentActivity;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity {

    private UUID mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected Fragment createFragment() {
        mId = (UUID) getIntent().getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);
        return CrimeFragment.newInstance(mId);
    }
}
