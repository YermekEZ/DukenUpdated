package com.example.duken.data;

public class AddProfileData {

    private String mName, mSurname, mStateID;

    public AddProfileData() {

    }

    public AddProfileData(String mName, String mSurname, String mStateID) {
        this.mName = mName;
        this.mSurname = mSurname;
        this.mStateID = mStateID;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmSurname() {
        return mSurname;
    }

    public void setmSurname(String mSurname) {
        this.mSurname = mSurname;
    }

    public String getmStateID() {
        return mStateID;
    }

    public void setmStateID(String mStateID) {
        this.mStateID = mStateID;
    }
}
