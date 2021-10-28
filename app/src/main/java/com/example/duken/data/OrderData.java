package com.example.duken.data;

public class OrderData {

    String mName, mPrice, mPieces, mDate, mTotalPrice;

    public OrderData() {
    }

    public OrderData(String mName, String mPrice, String mPieces, String mDate, String mTotalPrice) {
        this.mName = mName;
        this.mPrice = mPrice;
        this.mPieces = mPieces;
        this.mDate = mDate;
        this.mTotalPrice = mTotalPrice;
    }

    public String getmPieces() {
        return mPieces;
    }

    public String getmPrice() {
        return mPrice;
    }

    public String getmName() {
        return mName;
    }

    public void setmPieces(String mPieces) {
        this.mPieces = mPieces;
    }

    public void setmPrice(String mPrice) {
        this.mPrice = mPrice;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmDate() {
        return mDate;
    }

    public String getmTotalPrice() {
        return mTotalPrice;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public void setmTotalPrice(String mTotalPrice) {
        this.mTotalPrice = mTotalPrice;
    }
}
