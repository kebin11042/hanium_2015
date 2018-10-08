package com.example.bang.multitapapp.datasets;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by BANG on 2015-11-24.
 */
public class UserClass implements Serializable {

    private String id;
    private String user_id;
    private String password;
    private ArrayList<DeviceClass> arrDevice;

    public UserClass() {
        arrDevice = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<DeviceClass> getArrDevice() {
        return arrDevice;
    }

    public void setArrDevice(ArrayList<DeviceClass> arrDevice) {
        this.arrDevice = arrDevice;
    }
}
