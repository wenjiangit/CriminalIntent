package com.wenjian.criminalintent.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by douliu on 2016/12/7.
 */
public class Photo {

    public static final String JSON_FILENAME = "fileName";

    private String mFileName;

    public Photo(String fileName) {
        mFileName = fileName;
    }

    public Photo(JSONObject json) throws JSONException {
        mFileName = json.getString(JSON_FILENAME);
    }

    public JSONObject toJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSON_FILENAME,mFileName);
        return jsonObject;
    }

    public String getFileName() {
        return mFileName;
    }


}
