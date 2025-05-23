package com.bpmskm.projectgeoc;

import java.util.ArrayList;
import java.util.List;

public class User {
    private List<String> caches = new ArrayList<>();
    private String uid;
    private String username;
    private String registerDate;
    private int points;
    private int steps;

    public User(String uid, String username, String registerDate, int points, int steps, List<String> caches) {
        this.uid = uid;
        this.username = username;
        this.registerDate = registerDate;
        this.points = points;
        this.steps = steps;
        this.caches = caches;
    }

    public User(String username, int points){
        this.username = username;
        this.points = points;
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public int getPoints() {
        return points;
    }

    public int getSteps() {
        return steps;
    }

    public List<String> getCaches() {
        return caches;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public void setCaches(List<String> caches) {
        this.caches = caches;
    }
}
