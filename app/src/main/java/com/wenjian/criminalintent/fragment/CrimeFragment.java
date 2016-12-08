package com.wenjian.criminalintent.fragment;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.ContextMenu;
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
import android.widget.Toast;

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

    public static final String DIALOG_PHOTO = "photo";

    public static final int REQUEST_DATE = 0;

    public static final int REQUEST_PHOTO = 1;

    public static final int REQUEST_CONTACT = 2;

    public static final int REQUEST_PERMIS_CALL_PHONE = 3;

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
    @BindView(R.id.bt_crime_suspect)
    Button mBtCrimeSuspect;
    @BindView(R.id.bt_crime_report)
    Button mBtCrimeReport;
    @BindView(R.id.bt_call_suspect)
    Button mBtCallSuspect;

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
        //注册上下文菜单
        registerForContextMenu(mIvPhoto);
        mCrimeTitle.setText(mCrime.getTitle());
        updateData();
        mSolvedCheckBox.setChecked(mCrime.isSovled());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSovled(isChecked);
            }
        });
        if (mCrime.getSuspect() != null) {
            mBtCrimeSuspect.setText(mCrime.getSuspect());
        }

    }

    private void showPhoto() {
        Photo photo = mCrime.getPhoto();
        BitmapDrawable b = null;
        if (photo != null) {
            String path = getActivity().getFileStreamPath(photo.getFileName()).getAbsolutePath();
            b = PictureUtil.getScaledDrawable(getActivity(), path);
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
        } else if (requestCode == REQUEST_CONTACT) {
            Uri contactUri = data.getData();
            String[] queryFiled = new String[]{ContactsContract.Contacts.DISPLAY_NAME};
            Cursor cursor = getActivity().getContentResolver().query(contactUri, queryFiled, null, null, null);

            //cursor用完后记得关闭
            if (cursor.getCount() == 0) {
                cursor.close();
                return;
            }
            cursor.moveToFirst();
            String suspect = cursor.getString(0);
            mCrime.setSuspect(suspect);
            mBtCrimeSuspect.setText(suspect);
            cursor.close();
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.crime_item_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_photo:
                if (mCrime.getPhoto() == null) {
                    Toast.makeText(getActivity(), R.string.no_photo_to_delete, Toast.LENGTH_SHORT).show();
                    return true;
                }
                PictureUtil.clearImageView(mIvPhoto);
                Crime crime = CrimeLab.get(getActivity()).getCrime(mId);
                crime.setPhoto(null);
                return true;

        }
        return super.onContextItemSelected(item);
    }

    private void updateData() {
        mDataButton.setText(TimeUtil.date2String(mCrime.getDate()));
    }

    @Override
    public void onStop() {
        super.onStop();
        PictureUtil.clearImageView(mIvPhoto);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @OnClick({R.id.ib_crime, R.id.crime_data, R.id.iv_photo, R.id.bt_crime_report,
            R.id.bt_crime_suspect,R.id.bt_call_suspect})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_crime:
                Intent intent = new Intent(getActivity(), CrimeCameraActivity.class);
                startActivityForResult(intent, REQUEST_PHOTO);
                break;
            case R.id.crime_data:
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DialogFragment dialogFragment = DatePickerFragment.newInstance(mCrime.getDate());
                dialogFragment.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialogFragment.show(fm, DIALOG_DATE);
                break;
            case R.id.iv_photo:
                Photo photo = mCrime.getPhoto();
                if (photo == null) {
                    return;
                }
                FragmentManager fm1 = getActivity().getSupportFragmentManager();
                String path = getActivity().getFileStreamPath(photo.getFileName()).getAbsolutePath();
                ImageFragment.newInstance(path).show(fm1, DIALOG_PHOTO);
                break;
            case R.id.bt_crime_report:
                Intent intent1 = new Intent(Intent.ACTION_SEND);
                intent1.setType("text/plain");
                intent1.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                intent1.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                startActivity(Intent.createChooser(intent1, getString(R.string.send_report)));
                break;
            case R.id.bt_crime_suspect:
                Intent intent2 = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent2, REQUEST_CONTACT);
                break;
            case R.id.bt_call_suspect:
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CALL_PHONE
                    },REQUEST_PERMIS_CALL_PHONE);
                }else {
                    Uri number = Uri.parse("tel:10086");
                    Intent intent3 = new Intent(Intent.ACTION_CALL,number);
                    startActivity(intent3);
                }
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_PERMIS_CALL_PHONE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Uri number = Uri.parse("tel:10086");
                    Intent intent3 = new Intent(Intent.ACTION_CALL,number);
                    startActivity(intent3);
                }
                break;
        }
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSovled()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "MMM dd,EEE";
        String dateStr = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_noSuspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report, mCrime.getTitle(),
                dateStr, solvedString, suspect);
        return report;
    }
}
