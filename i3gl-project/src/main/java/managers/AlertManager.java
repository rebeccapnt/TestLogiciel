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

    private void createAndWriteAlert(User user, ThresholdEnum thresholdName, Instant time, double currentValue, double minThreshold) {
        AlertData alertData = new AlertData(user, thresholdName, time, currentValue, minThreshold);
        alertWriter.writeAlert(alertData);
    }

    private void checkThreshold(List<Threshold> thresholds, HashMap<ThresholdEnum, Double> currentValues, User user, Instant time) {
        for (Threshold threshold : thresholds) {
            double currentValue = currentValues.get(threshold.getName());
            if (!Double.isNaN(threshold.getMinThreshold()) && !threshold.compareMin(currentValue)) {
                createAndWriteAlert(user, threshold.getName(), time, currentValue, threshold.getMinThreshold());
            }
            if (!Double.isNaN(threshold.getMaxThreshold()) && !threshold.compareMax(currentValue)) {
                createAndWriteAlert(user, threshold.getName(), Instant.now(), currentValue, threshold.getMinThreshold());
            }
        }
    }

    public void checkAlert() throws WeatherException, InterruptedException {
        Map<Location, ArrayList<User>> entries = userRepository.getAllLocationsWithUsers();
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
