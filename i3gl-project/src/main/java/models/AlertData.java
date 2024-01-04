package models;

import models.enums.ThresholdEnum;

import java.time.Instant;

public class AlertData {
    public User user;
    public ThresholdEnum thresholdEnum;
    public Instant hour;
    public double valueMeasured;
    public double thresholdReached;
}
