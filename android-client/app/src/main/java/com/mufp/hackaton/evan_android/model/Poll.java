package com.mufp.hackaton.evan_android.model;

import java.io.Serializable;

/**
 * Created by milan on 23.11.14..
 */
public class Poll implements Serializable{
    private int number;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
