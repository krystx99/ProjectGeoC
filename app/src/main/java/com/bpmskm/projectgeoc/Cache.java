package com.bpmskm.projectgeoc;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

public class Cache {
    private String id;
    private Timestamp additionDate;
    private String description;
    private GeoPoint location;
    private String name;
    private Long points;
    private String addedBy;

    public Cache() { }

    public Cache(String id, Timestamp additionDate, String description, GeoPoint location, String name, Long points, String username) {
        this.id = id;
        this.additionDate = additionDate;
        this.description = description;
        this.location = location;
        this.name = name;
        this.points = points;
        this.addedBy = username;
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
        return addedBy;
    }

    public String getId() {
        return id;
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
        this.addedBy = username;
    }

    public void setId(String id) {
        this.id = id;
    }
}