package com.hackathon.pebbles.hackathonapp.models;

import com.orm.SugarRecord;

/**
 * Created by @priyankvex on 5/3/16.
 * Model to hold info about foursquare venue.
 */
public class Venue extends SugarRecord<Venue>{

    private String name;
    private String address;
    private String category;
    private String checkIns;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCheckIns() {
        return checkIns;
    }

    public void setCheckIns(String checkIns) {
        this.checkIns = checkIns;
    }
}
