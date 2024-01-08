package models;

import models.enums.ThresholdEnum;

public class Threshold {
    private ThresholdEnum name;
    private double minThreshold;
    private double maxThreshold;

    public Threshold(ThresholdEnum value) {
        this.name = value;
    }

    public Threshold(ThresholdEnum value, double minThreshold, double maxThreshold) {
        this.name = value;
        this.minThreshold = minThreshold;
        this.maxThreshold = maxThreshold;
    }

    public double getMinThreshold() {
        return minThreshold;
    }

    public void setMinThreshold(double minThreshold) {
        this.minThreshold = minThreshold;
    }

    public ThresholdEnum getName() {
        return name;
    }

    public void setName(ThresholdEnum name) {
        this.name = name;
    }

    public double getMaxThreshold() {
        return maxThreshold;
    }

    public void setMaxThreshold(double maxThreshold) {
        this.maxThreshold = maxThreshold;
    }

    public boolean compareMin(Double min){
        return minThreshold<min;
    }

    public boolean compareMax(Double max){
        return maxThreshold>max;
    }
}
