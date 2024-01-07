package models;

import models.enums.ThresholdEnum;

public class Threshold {
    private String name;
    private double minThreshold;
    private double maxThreshold;

    public Threshold(ThresholdEnum name) {
        this.name = name.getName();
    }

    public Threshold(ThresholdEnum name, double minThreshold, double maxThreshold) {
        this.name = name.getName();
        this.minThreshold = minThreshold;
        this.maxThreshold = maxThreshold;
    }

    public double getMinThreshold() {
        return minThreshold;
    }

    public void setMinThreshold(double minThreshold) {
        this.minThreshold = minThreshold;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMaxThreshold() {
        return maxThreshold;
    }

    public void setMaxThreshold(double maxThreshold) {
        this.maxThreshold = maxThreshold;
    }
}
