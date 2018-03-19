package com.czsm.Demand_Driver.model;

import java.io.Serializable;

/**
 * Created by czsm4 on 19/03/18.
 */

public class Users {
    private String phonenumber;
    private String uid;


    public Users() {

    }
    public Users(String phonenumber,String uid) {
        this.phonenumber = phonenumber;
        this.uid=uid;

//        this.profileImageURL = profileImageURL;
//        this.username        = username;

    }



    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }



//    private String profileImageURL;
//    private String username;

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }




}
