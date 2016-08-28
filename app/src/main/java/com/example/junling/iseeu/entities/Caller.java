package com.example.junling.iseeu.entities;

/**
 * Created by Junling on 26/8/16.
 */

import java.io.Serializable;
import java.util.UUID;

public class Caller implements Serializable{

    private String id;
    private String tabletNum;
    private String callerName;

    public Caller() {
        this.id = "Caller " + UUID.randomUUID().toString();
    }

    public Caller(String tabletNum, String callerName) {
        this.id = "Caller " + UUID.randomUUID().toString();
        this.tabletNum = tabletNum;
        this.callerName = callerName;
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

    public String getCallerName(){
        return callerName;
    }

    public void setCallerName(String callerName){
        this.callerName = callerName;
    }

}
