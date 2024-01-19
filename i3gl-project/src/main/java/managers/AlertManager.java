package managers;

import com.opencsv.CSVWriter;
import exceptions.AlertException;
import exceptions.WeatherException;
import models.*;
import models.enums.ThresholdEnum;
import repositories.UserRepository;
import services.AlertWriter;
import services.IAlertWriter;
import services.IWeatherAgent;
import services.WeatherAgent;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlertManager {
    private final UserRepository userRepository;
    private final IWeatherAgent weatherAgent;
    private final IAlertWriter alertWriter;

    private final ArrayList<ThresholdEnum> API_REQUEST_THRESHOLD_ENUM = new ArrayList<>(List.of(ThresholdEnum.RAIN, ThresholdEnum.WIND, ThresholdEnum.TEMPERATURE));

    public AlertManager(UserRepository userRepository, IWeatherAgent weatherAgent, IAlertWriter alertWriter) {
        this.userRepository = userRepository;
        this.weatherAgent = weatherAgent;
        this.alertWriter = alertWriter;
    }

    /**
     * Call the writer service for writing the alert
     *
     * @param user           : user associated with the alert
     * @param thresholdName  : name of the threshold : temperature, wind, rainfall
     * @param time           : time associated with the alert
     * @param currentValue   : value to be compared with the threshold
     * @param thresholdValue : threshold value to be compared against 'currentValue'.
     */
    private void createAndWriteAlert(User user, ThresholdEnum thresholdName, Instant time, double currentValue, double thresholdValue) {
        AlertData alertData = new AlertData(user, thresholdName, time, currentValue, thresholdValue);
        alertWriter.writeAlert(alertData);
    }

    /**
     * Performs a comparison between sets of thresholds and corresponding current values.
     *
     * @param thresholds     : Array or collection of threshold values for comparison
     * @param currentValues  : Array or collection of current values to be compared against thresholds
     * @param user           : User associated with the thresholds to check
     * @param time           : Time associated with the API request to get current values
     */
    private void checkThreshold(List<Threshold> thresholds, HashMap<ThresholdEnum, Double> currentValues, User user, Instant time) {
        for (Threshold threshold : thresholds) {
            double currentValue = currentValues.get(threshold.getName());
            if (!Double.isNaN(threshold.getMinThreshold()) && !threshold.compareMin(currentValue)) {
                createAndWriteAlert(user, threshold.getName(), time, currentValue, threshold.getMinThreshold());
            }
            if (!Double.isNaN(threshold.getMaxThreshold()) && !threshold.compareMax(currentValue)) {
                createAndWriteAlert(user, threshold.getName(), time, currentValue, threshold.getMaxThreshold());
            }
        }
    }

    /**
     * Check if there is an alert for the addresses enabled
     *
     * @throws WeatherException     : Signals an exception related to weather conditions
     * @throws InterruptedException : Signals that a thread has been interrupted during execution.
     */
    public void checkAlert() throws WeatherException, InterruptedException {
        Map<Location, ArrayList<User>> entries = userRepository.getAllLocationsWithUsers();;
        for (Map.Entry<Location, ArrayList<User>> entry : entries.entrySet()) {
            Location location = entry.getKey();
            try {
                HashMap<ThresholdEnum, Double> currentValues = weatherAgent.getValuesFromData(location, API_REQUEST_THRESHOLD_ENUM);
                Instant time = Instant.now();
                ArrayList<User> userList = entry.getValue();
                for (User user : userList) {
                    Address address = user.getAddresses().get(location);
                    if (!address.isDisableAlerts() && !user.isDisableAllAlerts()) {
                        List<Threshold> thresholds = address.getThresholds();
                        checkThreshold(thresholds, currentValues, user, time);
                    }
                }
            }catch (WeatherException e){
                throw new WeatherException("Can't get the value from Weather API", e);
            }
        }
    }
}
