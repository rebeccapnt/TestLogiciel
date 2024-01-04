package models;

import services.GeoCodingAgent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Address {
    private final String value;
    private boolean alertEnable;
    private Double[] location;
    private List<User> users;
    private List<Threshold> thresholds;

    public Address(String value, boolean alertEnable, List<Threshold> thresholds) {
        this.value = value;
        this.alertEnable = alertEnable;
        this.thresholds = thresholds;
        this.users = new ArrayList<>();
    }

    public String getValue() {
        return value;
    }

    public boolean isAlertEnable() {
        return alertEnable;
    }

    public void setAlertEnable(boolean alertEnable) {
        this.alertEnable = alertEnable;
    }

    public Double[] getLocation() {
        return location;
    }

    public void setLocation(Double[] location) {
        this.location = location;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Threshold> getThresholds() {
        return thresholds;
    }

    public void setThresholds(List<Threshold> thresholds) {
        this.thresholds = thresholds;
    }

    private void updateLocation(GeoCodingAgent geoCodingAgent) {
        try {
            this.location = geoCodingAgent.convertAddressToLocation(value);
        } catch (IOException e) {
            System.out.println("Error converting address to location: " + e.getMessage());
        }
    }
}
