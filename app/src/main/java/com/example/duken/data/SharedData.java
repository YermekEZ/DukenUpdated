package com.example.duken.data;

public class SharedData {

    static String phoneNumber, barcodeNumber, name, surname, stateId;
    static boolean registered = false;
    static int maxCount;

    public SharedData(){

    }

    public static String getPhoneNumber() {
        return phoneNumber;
    }

    public static boolean isRegistered() {
        return registered;
    }

    public static void setPhoneNumber(String phoneNumber) {
        SharedData.phoneNumber = phoneNumber;
    }

    public static void setRegistered(boolean registered) {
        SharedData.registered = registered;
    }

    public static String getBarcodeNumber() {
        return barcodeNumber;
    }

    public static void setBarcodeNumber(String barcodeNumber) {
        SharedData.barcodeNumber = barcodeNumber;
    }

    public static int getMaxCount() {
        return maxCount;
    }

    public static void setMaxCount(int maxCount) {
        SharedData.maxCount = maxCount;
    }

    public static String getName() {
        return name;
    }

    public static String getSurname() {
        return surname;
    }

    public static void setName(String name) {
        SharedData.name = name;
    }

    public static void setSurname(String surname) {
        SharedData.surname = surname;
    }

    public static String getStateId() {
        return stateId;
    }

    public static void setStateId(String stateId) {
        SharedData.stateId = stateId;
    }
}
