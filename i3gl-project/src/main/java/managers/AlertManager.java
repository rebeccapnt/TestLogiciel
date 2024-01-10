package managers;

import com.opencsv.CSVWriter;
import exceptions.AlertException;
import exceptions.WeatherException;
import models.*;
import models.enums.ThresholdEnum;
import repositories.UserRepository;
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
    private static final String ALERT_PATH = "data/alert.csv";

    private final UserRepository userRepository;
    private final IWeatherAgent weatherAgent;

    private final ArrayList<ThresholdEnum> API_REQUEST_THRESHOLD_ENUM = new ArrayList<>(List.of(ThresholdEnum.RAIN, ThresholdEnum.WIND, ThresholdEnum.TEMPERATURE));

    public AlertManager(UserRepository userRepository, WeatherAgent weatherAgent) {
        this.userRepository = userRepository;
        this.weatherAgent = weatherAgent;
    }

    /**
     * Write the content of the alert in a CSV file
     *
     * @param alertData model with the data of the alert such as the user, the threshold...
     */
    public void writeAlert(AlertData alertData) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(ALERT_PATH, true))) {
            String[] data = {
                    alertData.user.getUsername(),
                    alertData.thresholdEnum.toString(),
                    alertData.hour.toString(),
                    String.valueOf(alertData.valueMeasured),
                    String.valueOf(alertData.thresholdReached)
            };
            writer.writeNext(data);
        }
        catch (IOException e){
            throw new AlertException("Error when writing alert", e);
        }
    }

    private void createAndWriteAlert(User user, ThresholdEnum thresholdName, Instant time, double currentValue, double minThreshold) throws IOException {
        AlertData alertData = new AlertData(user, thresholdName, time, currentValue, minThreshold);
        writeAlert(alertData);
    }

    private void checkThreshold(List<Threshold> thresholds, HashMap<ThresholdEnum, Double> currentValues, User user, Instant time) throws IOException {
        for (Threshold threshold : thresholds) {
            double currentValue = currentValues.get(threshold.getName());
            if (!threshold.compareMin(currentValue)) {
                createAndWriteAlert(user, threshold.getName(), time, currentValue, threshold.getMinThreshold());
            }
            if (!threshold.compareMax(currentValue)) {
                createAndWriteAlert(user, threshold.getName(), Instant.now(), currentValue, threshold.getMinThreshold());
            }
        }
    }

    public void checkAlert() throws WeatherException, IOException {
        Map<Location, ArrayList<User>> entries = userRepository.getAllLocationsWithUsers();
        for (Map.Entry<Location, ArrayList<User>> entry : entries.entrySet()) {
            Location location = entry.getKey();
            HashMap<ThresholdEnum, Double> currentValues = weatherAgent.getValuesFromData(location, API_REQUEST_THRESHOLD_ENUM);
            Instant time = Instant.now();
            ArrayList<User> userList = entry.getValue();
            for (User user : userList) {
                Address address = user.getAdresses().get(location);
                if (!address.isDisableAlerts() && !user.isDisableAllAlerts()) {
                    List<Threshold> thresholds = address.getThresholds();
                    checkThreshold(thresholds, currentValues, user, time);
                }
            }
        }
    }
}
