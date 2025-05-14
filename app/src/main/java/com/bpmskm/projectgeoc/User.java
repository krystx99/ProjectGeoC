package com.bpmskm.projectgeoc;

public class User {
    private String uid;
    private String username;
    private String registerDate;
    private int points;
    private int steps;

    public User(String uid, String username, String registerDate, int points, int steps) {
        this.uid = uid;
        this.username = username;
        this.registerDate = registerDate;
        this.points = points;
        this.steps = steps;
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
}
