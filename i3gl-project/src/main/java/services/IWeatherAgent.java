package services;

import exceptions.WeatherException;
import models.enums.ThresholdEnum;

import java.util.ArrayList;
import java.util.HashMap;

public interface IWeatherAgent {
    public HashMap<ThresholdEnum, Double> getValuesFromData(double latitude, double longitude, ArrayList<ThresholdEnum> thresholdEnums) throws WeatherException;
}
