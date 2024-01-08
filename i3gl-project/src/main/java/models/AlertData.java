package models;

import models.enums.ThresholdEnum;

import java.time.Instant;

public class AlertData {
    public User user;
    public ThresholdEnum thresholdEnum;
    public Instant hour;
    public double valueMeasured;
    public double thresholdReached;

    public AlertData(User user,ThresholdEnum thresholdEnum, Instant hour, double valueMeasured, double thresholdReached){
        this.user = user;
        this.thresholdEnum = thresholdEnum;
        this.hour = hour;
        this.valueMeasured = valueMeasured;
        this.thresholdReached = thresholdReached;
    }
}
