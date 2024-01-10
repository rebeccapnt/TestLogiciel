package models;

import models.enums.ThresholdEnum;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class AlertData {
    private final User user;
    private final ThresholdEnum thresholdEnum;
    private final Instant date;
    private final double valueMeasured;
    private final double thresholdReached;

    public AlertData(User user, ThresholdEnum thresholdEnum, Instant date, double valueMeasured, double thresholdReached) {
        this.user = user;
        this.thresholdEnum = thresholdEnum;
        this.date = date;
        this.valueMeasured = valueMeasured;
        this.thresholdReached = thresholdReached;
    }

    public User getUser() {
        return user;
    }

    public double getThresholdReached() {
        return thresholdReached;
    }

    public double getValueMeasured() {
        return valueMeasured;
    }

    public ThresholdEnum getThresholdEnum() {
        return thresholdEnum;
    }

    public String getHour() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return localDateTime.format(formatter);
    }

    public String getDate() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return localDateTime.format(formatter);
    }
}
