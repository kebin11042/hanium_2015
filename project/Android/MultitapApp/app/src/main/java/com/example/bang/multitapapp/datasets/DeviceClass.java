package com.example.bang.multitapapp.datasets;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by BANG on 2015-11-24.
 */
public class DeviceClass implements Serializable {

    private String id;
    private String device_id;
    private String name;
    private String mac;
    private ArrayList<PortClass> arrPortClases;

    public DeviceClass() {
        arrPortClases = new ArrayList<PortClass>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public ArrayList<PortClass> getArrPortClases() {
        return arrPortClases;
    }

    public void setArrPortClases(ArrayList<PortClass> arrPortClases) {
        this.arrPortClases = arrPortClases;
    }
}
