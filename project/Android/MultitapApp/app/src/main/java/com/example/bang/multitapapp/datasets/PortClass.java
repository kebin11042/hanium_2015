package com.example.bang.multitapapp.datasets;

import java.io.Serializable;

/**
 * Created by BANG on 2015-11-24.
 */
public class PortClass implements Serializable {

    private String id;
    private String device_id;
    private String port_num;
    private String state;

    public PortClass() {

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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPort_num() {
        return port_num;
    }

    public void setPort_num(String port_num) {
        this.port_num = port_num;
    }
}
