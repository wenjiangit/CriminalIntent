package com.wenjian.criminalintent.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.wenjian.criminalintent.R;
import com.wenjian.criminalintent.activity.CrimePagerActivity;
import com.wenjian.criminalintent.model.Crime;
import com.wenjian.criminalintent.model.CrimeLab;
import com.wenjian.criminalintent.util.TimeUtil;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class CrimeListFragment extends ListFragment {

    public static final String TAG = CrimeListFragment.class.getSimpleName();

    private ArrayList<Crime> mCrimes;

    private boolean mSubtitleVisible;
    private CrimeAdapter mAdapter;

    public CrimeListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.crimes_title);
        mCrimes = CrimeLab.get(getActivity()).getCrimes();
        mAdapter = new CrimeAdapter(mCrimes);
        setRetainInstance(true);
        mSubtitleVisible = false;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mSubtitleVisible){
            ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(R.string.subtitle);
        }
        View view = inflater.inflate(R.layout.fragment_crime_list, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        ListView listview = (ListView) view.findViewById(android.R.id.list);
//        registerForContextMenu(listview);
        listview.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        listview.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                MenuInflater inflater = actionMode.getMenuInflater();
                inflater.inflate(R.menu.crime_list_item_context,menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.menu_item_delete_crime:
                        CrimeLab crimeLab = CrimeLab.get(getActivity());
                        for (int i = mAdapter.getCount() - 1; i >= 0 ; i--) {
                            if (getListView().isItemChecked(i)){
                                crimeLab.deleteCrime(mAdapter.getItem(i));
                            }
                        }
                        actionMode.finish();
                        mAdapter.notifyDataSetChanged();
                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }
        });

        Button bt_new_crime = (Button) view.findViewById(R.id.bt_new_crime);
        bt_new_crime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crime c = new Crime();
                CrimeLab.get(getActivity()).addCrime(c);
                Intent intent = new Intent(getActivity(),CrimePagerActivity.class);
                intent.putExtra(CrimeFragment.EXTRA_CRIME_ID,c.getId());
                startActivityForResult(intent,0);
            }
        });

        listview.setAdapter(mAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
//        super.onListItemClick(l, v, position, id);
//        Crime c = (Crime) getListAdapter().getItem(position);
        Crime c = mAdapter.getItem(position);
        Log.d(TAG,c.getTitle() + "was clicked");

        Intent intent= new Intent(getContext(),CrimePagerActivity.class);
        intent.putExtra(CrimeFragment.EXTRA_CRIME_ID,c.getId());
        startActivity(intent);
    }


    private class CrimeAdapter extends ArrayAdapter<Crime>{


        public CrimeAdapter(ArrayList<Crime> list)  {
            super(getActivity(), 0,list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.crime_list_item,null);
            }
            Crime crime = getItem(position);
            TextView data = (TextView) convertView.findViewById(R.id.crime_list_item_dataTextView);
            data.setText(TimeUtil.date2String(crime.getDate()));
            TextView title = (TextView) convertView.findViewById(R.id.crime_list_item_titleTextView);
            title.setText(crime.getTitle());
            CheckBox solved = (CheckBox) convertView.findViewById(R.id.crime_list_item_solvedCheckBox);
            solved.setChecked(crime.isSovled());
            return convertView;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list,menu);
        MenuItem showSubtitle = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible && showSubtitle != null){
            showSubtitle.setTitle(R.string.hide_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_new_crime:
                Crime c = new Crime();
                CrimeLab.get(getActivity()).addCrime(c);
                Intent intent = new Intent(getActivity(),CrimePagerActivity.class);
                intent.putExtra(CrimeFragment.EXTRA_CRIME_ID,c.getId());
                startActivityForResult(intent,0);
                return true;
            case R.id.menu_item_show_subtitle:
                ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
                if (actionBar.getSubtitle() == null){
                    item.setTitle(R.string.hide_subtitle);
                    actionBar.setSubtitle(R.string.subtitle);
                    mSubtitleVisible = true;
                }else {
                    actionBar.setSubtitle(null);
                    item.setTitle(R.string.show_subtitle);
                    mSubtitleVisible = false;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.crime_list_item_context,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int i = info.position;
        Crime crime = mAdapter.getItem(i);
        switch (item.getItemId()){
            case R.id.menu_item_delete_crime:
                CrimeLab.get(getActivity()).deleteCrime(crime);
                mAdapter.notifyDataSetChanged();
                return true;
        }
        return super.onContextItemSelected(item);
    }
}
