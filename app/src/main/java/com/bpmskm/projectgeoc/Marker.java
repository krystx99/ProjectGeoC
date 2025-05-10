package com.bpmskm.projectgeoc;

import com.google.android.gms.maps.model.LatLng;

public class Marker {
    public LatLng latLng;
    public String title;
    public String snippet;
    public String user;
    public int userid;
    public int found_count;
    public String markerId;

    public Marker(LatLng latLng, String title, String snippet, String user, int userid, int found_count, String markerId) {
        this.latLng = latLng;
        this.title = title;
        this.snippet = snippet;
        this.user = user;
        this.userid = userid;
        this.found_count = found_count;
        this.markerId = markerId;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getFound_count() {
        return found_count;
    }

    public void setFound_count(int found_count) {
        this.found_count = found_count;
    }

    public String getMarkerId() {
        return markerId;
    }

    public void setMarkerId(String markerId) {
        this.markerId = markerId;
    }
}
