package com.example.duken.data;

public class AddProductData {

    private String mProductName, mBarcodeNumber, mPrice, mPieces;

    public AddProductData() {

    }

    public AddProductData(String mProductName, String mBarcodeNumber, String mPrice, String mPieces) {
        this.mProductName = mProductName;
        this.mBarcodeNumber = mBarcodeNumber;
        this.mPrice = mPrice;
        this.mPieces = mPieces;
    }

    public String getmProductName() {
        return mProductName;
    }

    public void setmProductName(String mProductName) {
        this.mProductName = mProductName;
    }

    public String getmBarcodeNumber() {
        return mBarcodeNumber;
    }

    public void setmBarcodeNumber(String mBarcodeNumber) {
        this.mBarcodeNumber = mBarcodeNumber;
    }

    public String getmPrice() {
        return mPrice;
    }

    public void setmPrice(String mPrice) {
        this.mPrice = mPrice;
    }

    public String getmPieces() {
        return mPieces;
    }

    public void setmPieces(String mPieces) {
        this.mPieces = mPieces;
    }

}
