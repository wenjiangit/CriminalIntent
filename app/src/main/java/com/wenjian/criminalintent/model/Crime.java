package com.wenjian.criminalintent.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * Created by wenjian on 2016/11/29.
 */
public class Crime {

    private UUID mId;
    private String mTitle;

    private Date mDate;
    private boolean mSovled;

    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_SOLVED = "solved";
    private static final String JSON_DATE = "date";

    public Crime() {
        //生成唯一标识符
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    public Crime(JSONObject json) throws JSONException {
        mId = UUID.fromString(json.getString(JSON_ID));
        if (json.has(JSON_TITLE)){
            mTitle = json.getString(JSON_TITLE);
        }
        mDate = new Date(json.getLong(JSON_DATE));
        mSovled = json.getBoolean(JSON_SOLVED);
    }



    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSON_ID,mId.toString());
        jsonObject.put(JSON_SOLVED,mSovled);
        jsonObject.put(JSON_TITLE,mTitle);
        jsonObject.put(JSON_DATE,mDate.getTime());
        return jsonObject;
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSovled() {
        return mSovled;
    }

    public void setSovled(boolean sovled) {
        mSovled = sovled;
    }

    @Override
    public String toString() {
        return mTitle;
    }
}
