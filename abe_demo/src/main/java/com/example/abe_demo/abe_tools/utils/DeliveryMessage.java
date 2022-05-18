package com.example.abe_demo.abe_tools.utils;

import androidx.annotation.NonNull;

public class DeliveryMessage {
    private String personName;
    private String phoneNumber;
    private String aheadAddress;
    private String behindAddress;

    public DeliveryMessage(String personName, String phoneNumber, String aheadAddress, String behindAddress) {
        this.personName = personName;
        this.phoneNumber = phoneNumber;
        this.aheadAddress = aheadAddress;
        this.behindAddress = behindAddress;
    }

    public DeliveryMessage() {
    }

    public String getPersonName() {
        return personName;
    }


    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAheadAddress() {
        return aheadAddress;
    }

    public void setAheadAddress(String aheadAddress) {
        this.aheadAddress = aheadAddress;
    }

    public String getBehindAddress() {
        return behindAddress;
    }

    public void setBehindAddress(String behindAddress) {
        this.behindAddress = behindAddress;
    }

    @NonNull
    @Override
    public String toString() {
        return "DeliveryMessage{" +
                "personName='" + personName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", aheadAddress='" + aheadAddress + '\'' +
                ", behindAddress='" + behindAddress + '\'' +
                '}';
    }
}
