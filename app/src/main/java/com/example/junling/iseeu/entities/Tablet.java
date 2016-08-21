package com.example.junling.iseeu.entities;

/**
 * Created by Junling on 19/8/16.
 */

import java.io.Serializable;
import java.util.UUID;

public class Tablet implements Serializable{

    private String id;
    private String tabletNum;
    private String patientName;

    public Tablet() {
        this.id = "Tablet " + UUID.randomUUID().toString();
    }

    public Tablet(String tabletNum, String patientName) {
        this.id = "Tablet " + UUID.randomUUID().toString();
        this.tabletNum = tabletNum;
        this.patientName = patientName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public String getTabletNum(){
        return tabletNum;
    }

    public void setTabletNum(String tabletNum){
        this.tabletNum = tabletNum;
    }

    public String getPatientName(){
        return patientName;
    }

    public void setPatientName(String patientName){
        this.patientName = patientName;
    }
}
