package com.bpmskm.projectgeoc;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

public class Cache {
    private Timestamp additionDate;
    private String description;
    private GeoPoint location;
    private String name;
    private Long points;
    private String username;

    public Cache() { }

    public Cache(Timestamp additionDate, String description, GeoPoint location, String name, Long points, String username) {
        this.additionDate = additionDate;
        this.description = description;
        this.location = location;
        this.name = name;
        this.points = points;
        this.username = username;
    }

    // Gettery
    public Timestamp getAdditionDate() {
        return additionDate;
    }

    public String getDescription() {
        return description;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public Long getPoints() {
        return points;
    }

    public String getUsername() {
        return username;
    }

    // Settery
    public void setAdditionDate(Timestamp additionDate) {
        this.additionDate = additionDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}