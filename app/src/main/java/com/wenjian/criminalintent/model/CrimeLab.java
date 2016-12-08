package com.wenjian.criminalintent.model;

import android.content.Context;
import android.util.Log;

import com.wenjian.criminalintent.util.JLog;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by wenjian on 2016/11/30.
 */
public class CrimeLab {

    private static final String TAG = "CrimeLab";
    private static final String FILENAME = "crimes.json";
    private static CrimeLab sCrimeLab;
    private Context mAppContext;

    private ArrayList<Crime> mCrimes;
    private CriminalIntentJsonSerializer mSerializer;

    private CrimeLab(Context appContext){
        mAppContext = appContext;
//        mCrimes = new ArrayList<>();
        mSerializer = new CriminalIntentJsonSerializer(mAppContext,FILENAME);

        try {
            mCrimes = mSerializer.loadCrimes();
        } catch (Exception e) {
            mCrimes = new ArrayList<>();
            JLog.e(TAG,"Error loading crimes: ",e);
        }

    }

    public static CrimeLab get(Context c){
        if (sCrimeLab == null){
            sCrimeLab = new CrimeLab(c.getApplicationContext());
        }
        return sCrimeLab;
    }

    public void addCrime(Crime c){
        mCrimes.add(c);
    }

    public ArrayList<Crime> getCrimes() {
        return mCrimes;
    }

    public Crime getCrime(UUID id){
        for (Crime c:mCrimes) {
            if (c.getId().equals(id)){
                return c;
            }
        }
        return null;
    }

    public boolean saveCrimes(){
        try {
            mSerializer.saveCrimes(mCrimes);
            JLog.d(TAG,"crimes saved to file");
            return true;
        } catch (Exception e) {
            JLog.e(TAG,"Error saving crimes:",e);
            return false;
        }
    }

    public void deleteCrime(Crime c){
        mCrimes.remove(c);
    }
}
