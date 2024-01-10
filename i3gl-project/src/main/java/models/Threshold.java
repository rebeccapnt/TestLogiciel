package models;

import models.enums.ThresholdEnum;

public class Threshold {
    private ThresholdEnum name;
    private Double minThreshold;
    private Double maxThreshold;

    public Threshold(ThresholdEnum value) {
        this.name = value;
    }

    public Threshold(ThresholdEnum value, Double minThreshold, Double maxThreshold) {
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

    public Double getMaxThreshold() {
        return maxThreshold;
    }

    public void setMaxThreshold(Double maxThreshold) {
        this.maxThreshold = maxThreshold;
    }

    public boolean compareMin(Double min){
        return minThreshold<min;
    }

    public boolean compareMax(Double max){
        return maxThreshold>max;
    }
}
