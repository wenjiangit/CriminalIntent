package com.wenjian.criminalintent.model;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by wenjian on 2016/12/4.
 */
public class CriminalIntentJsonSerializer {

    private Context mContext;
    private String mFileName;

    public CriminalIntentJsonSerializer(Context context, String fileName) {
        mContext = context;
        mFileName = fileName;
    }

    public void saveCrimes(ArrayList<Crime> crimes) throws JSONException, IOException {
        JSONArray array = new JSONArray();
        for(Crime c:crimes){
            array.put(c.toJSON());
        }
        Writer writer = null;
        try {
            FileOutputStream out = mContext.openFileOutput(mFileName, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(array.toString());
        }finally {
            if (writer != null){
                writer.close();
            }
        }
    }

    public ArrayList<Crime> loadCrimes() throws IOException, JSONException {
        ArrayList<Crime> crimes = new ArrayList<>();
        BufferedReader reader = null;

        try {
            FileInputStream in = mContext.openFileInput(mFileName);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null){
                jsonString.append(line);
            }
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            for(int i = 0;i < array.length(); i++){
                crimes.add(new Crime(array.getJSONObject(i)));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if (reader != null){
                reader.close();
            }
        }
        return crimes;
    }


}
