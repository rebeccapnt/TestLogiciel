package services;

import exceptions.WeatherException;
import models.Location;
import models.enums.ThresholdEnum;

import java.util.ArrayList;
import java.util.HashMap;

public interface IWeatherAgent {
    public HashMap<ThresholdEnum, Double> getValuesFromData(Location location, ArrayList<ThresholdEnum> thresholdEnums) throws WeatherException;
}
