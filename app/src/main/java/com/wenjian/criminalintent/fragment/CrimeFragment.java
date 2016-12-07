package com.wenjian.criminalintent.fragment;


import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.wenjian.criminalintent.R;
import com.wenjian.criminalintent.activity.CrimeCameraActivity;
import com.wenjian.criminalintent.model.Crime;
import com.wenjian.criminalintent.model.CrimeLab;
import com.wenjian.criminalintent.model.Photo;
import com.wenjian.criminalintent.util.PictureUtil;
import com.wenjian.criminalintent.util.TimeUtil;

import java.util.Date;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class CrimeFragment extends Fragment {

    public static final String EXTRA_CRIME_ID = "EXTRA_CRIME_ID";

    public static final String DIALOG_DATE = "date";

    public static final int REQUEST_DATE = 0;

    public static final int REQUEST_PHOTO = 1;

    private static final String TAG = "CrimeFragment";

    @BindView(R.id.crime_title)
    EditText mCrimeTitle;
    @BindView(R.id.crime_data)
    Button mDataButton;
    @BindView(R.id.crime_solved)
    CheckBox mSolvedCheckBox;
    @BindView(R.id.ib_crime)
    ImageButton mIbCrime;
    @BindView(R.id.iv_photo)
    ImageView mIvPhoto;

    private Crime mCrime;
    private Unbinder mUnbinder;
    private UUID mId;

    public CrimeFragment() {
        // Required empty public constructor
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mId = (UUID) getArguments().getSerializable(EXTRA_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(mId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_crime2, container, false);
        mUnbinder = ButterKnife.bind(this, inflate);
        initView(inflate);
        return inflate;
    }

    private void initView(View inflate) {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mCrimeTitle.setText(mCrime.getTitle());
        mDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DialogFragment dialogFragment = DatePickerFragment.newInstance(mCrime.getDate());
                dialogFragment.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialogFragment.show(fm, DIALOG_DATE);
            }
        });
        updateData();
//        mDataButton.setEnabled(false);
        mSolvedCheckBox.setChecked(mCrime.isSovled());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSovled(isChecked);
            }
        });

    }

    private void showPhoto(){
        Photo photo = mCrime.getPhoto();
        BitmapDrawable b = null;
        if (photo != null){
            String path = getActivity().getFileStreamPath(photo.getFileName()).getAbsolutePath();
            b = PictureUtil.getScaledDrawable(getActivity(),path);
        }
        mIvPhoto.setImageDrawable(b);
    }

    @Override
    public void onStart() {
        super.onStart();
        showPhoto();
    }

    @Override
    public void onPause() {
        super.onPause();
        String s = mCrimeTitle.getText().toString().trim();
        if (TextUtils.isEmpty(s)) {
            mCrime.setTitle("Blank Title");
        } else {
            mCrime.setTitle(s.toString());
        }
        CrimeLab.get(getActivity()).saveCrimes();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateData();
        } else if (requestCode == REQUEST_PHOTO) {
            String fileName = data.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
            if (fileName != null) {
//                Log.i(TAG, "onActivityResult:fileName "+fileName);
                Photo photo = new Photo(fileName);
                mCrime.setPhoto(photo);
                showPhoto();
//                Log.d(TAG, "Crime" + mCrime.getTitle() + "has a photo");
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
             /*   if (NavUtils.getParentActivityName(getActivity())!=null){
                    NavUtils.navigateUpFromSameTask(getActivity());
                }*/
                getActivity().finish();
                return true;
            case R.id.menu_item_delete_crime:
                CrimeLab.get(getActivity()).deleteCrime(mCrime);
                getActivity().finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    private void updateData() {
        mDataButton.setText(TimeUtil.date2String(mCrime.getDate()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @OnClick(R.id.ib_crime)
    public void onClick() {
        Intent intent = new Intent(getActivity(), CrimeCameraActivity.class);
//        startActivity(intent);
        startActivityForResult(intent, REQUEST_PHOTO);
    }
}
