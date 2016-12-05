package com.wenjian.criminalintent.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.wenjian.criminalintent.R;
import com.wenjian.criminalintent.model.Crime;
import com.wenjian.criminalintent.model.CrimeLab;

import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

import butterknife.BindView;


/**
 * A simple {@link Fragment} subclass.
 */
public class CrimeFragment extends Fragment {

    public static final String EXTRA_CRIME_ID = "EXTRA_CRIME_ID";

    public static final String DIALOG_DATE = "date";

    public static final int REQUEST_DATE = 0;


    @BindView(R.id.crime_title)
    EditText mCrimeTitle;
    @BindView(R.id.crime_data)
    Button mDataButton;
    @BindView(R.id.crime_solved)
    CheckBox mSolvedCheckBox;
    private Crime mCrime;


    public CrimeFragment() {
        // Required empty public constructor
    }

    public static CrimeFragment newInstance(UUID crimeId){
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_ID,crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        UUID id = (UUID) getArguments().getSerializable(EXTRA_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(id);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_crime2, container, false);
//        ButterKnife.bind(this, inflate);
        initView(inflate);
        return inflate;
    }

    private void initView(View inflate) {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mDataButton = (Button) inflate.findViewById(R.id.crime_data);
        mCrimeTitle = (EditText) inflate.findViewById(R.id.crime_title);
        mSolvedCheckBox = (CheckBox) inflate.findViewById(R.id.crime_solved);
        mCrimeTitle.setText(mCrime.getTitle());
        mDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DialogFragment dialogFragment = DatePickerFragment.newInstance(mCrime.getDate());
                dialogFragment.setTargetFragment(CrimeFragment.this,REQUEST_DATE);
                dialogFragment.show(fm,DIALOG_DATE);
            }
        });
        mCrimeTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        String format = DateFormat.getDateInstance(DateFormat.DEFAULT).format(mCrime.getDate());
        mDataButton.setText(format);
//        mDataButton.setEnabled(false);

        mSolvedCheckBox.setChecked(mCrime.isSovled());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSovled(isChecked);
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).saveCrimes();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK)return;

        if (requestCode == REQUEST_DATE){
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateData();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
             /*   if (NavUtils.getParentActivityName(getActivity())!=null){
                    NavUtils.navigateUpFromSameTask(getActivity());
                }*/
                getActivity().finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateData(){
        mDataButton.setText(mCrime.getDate().toString());
    }
}
