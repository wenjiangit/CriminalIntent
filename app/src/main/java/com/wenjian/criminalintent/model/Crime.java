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
    private Photo mPhoto;

    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_SOLVED = "solved";
    private static final String JSON_DATE = "date";
    private static final String JSON_PHOTO = "photo";

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
        if (json.has(JSON_PHOTO)){
            mPhoto = new Photo(json.getJSONObject(JSON_PHOTO));
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
        jsonObject.put(JSON_PHOTO,mPhoto.toJson());
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

    public Photo getPhoto() {
        return mPhoto;
    }

    public void setPhoto(Photo photo) {
        mPhoto = photo;
    }

    @Override
    public String toString() {
        return mTitle;
    }
}
