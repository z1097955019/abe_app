package com.example.abe_demo.sqlite3.bean;

public class Place extends BasePlace{
    public Place(String name, int code, int upper_id) {
        super(name, code);
        this.upper_id = upper_id;
    }

    public int getUpper_id() {
        return upper_id;
    }

    public void setUpper_id(int upper_id) {
        this.upper_id = upper_id;
    }

    @Override
    public String toString() {
        return "Province{" +
                "upper_id=" + upper_id +
                "} " + super.toString();
    }

    private int upper_id;
}
