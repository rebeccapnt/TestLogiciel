package tempguardiantests.integration;

import exceptions.WeatherException;
import managers.AlertManager;
import models.*;
import models.enums.ThresholdEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import repositories.UserRepository;
import services.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AlertManagerTestIT {
    private WeatherAgent weatherAgent;
    private AlertWriter alertWriter;
    private UserRepository userRepository;
    private AlertManager alertManager;
    private IGeoCodingAgent geoCodingAgent;

    private Threshold thresholdTemperatureMaxReached;
    private Threshold thresholdTemperatureMinReached;
    private Threshold thresholdTemperature;
    private Threshold thresholdRain;
    private Threshold thresholdRainMinMaxReached;
    private Threshold thresholdRainOnlyMinDefined;
    private Threshold thresholdRainOnlyMaxDefined;

    private Threshold thresholdWind;
    private Threshold thresholdWindMinMaxReached;
    private final double VALUE_MIN = 0.0;
    private final double VALUE_MAX = 20.0;
    private final double VALUE_1 = 10;
    private final double VALUE_2 = 25;
    private final String firstAddress = "15 Rue de la Paix, Paris";
    private final Location firstLocation = new Location(2.3312846, 48.8695088);
    private final String username = "Tintin";
    private final String username2 = "Milou";
    private final int INDEX_CSV_THRESHOLD_REACHED = 5;
    private final int INDEX_CSV_THRESHOLD_NAME = 1;
    private final int INDEX_CSV_DATE = 2;
    private final int INDEX_CSV_HOUR = 3;

    String ALERT_PATH = "data/alert.csv";

    private final ArrayList<ThresholdEnum> API_REQUEST_THRESHOLD_ENUM = new ArrayList<>(List.of(ThresholdEnum.RAIN, ThresholdEnum.WIND, ThresholdEnum.TEMPERATURE));

    private String[] readLineCSV(int lineNumber) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(ALERT_PATH))) {
            String line;
            for (int currentLine = 0; (line = bufferedReader.readLine()) != null && currentLine <= lineNumber; ++currentLine) {
                if (currentLine == lineNumber) {
                    return line.split(",");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String[0];
    }

    private void removeCSVIfExist(){
        Path filePath = Paths.get(ALERT_PATH);
        if (Files.exists(filePath)) {
            try {
                Files.delete(filePath);
            } catch (IOException e) {
                System.err.println("Error while deleting the CSV file: " + e.getMessage());
            }
        } else {
            System.out.println("The CSV file does not exist.");
        }
    }

    @BeforeEach
    void setUp() throws InterruptedException {
        weatherAgent = new WeatherAgent();
        alertWriter = new AlertWriter();
        geoCodingAgent = new GeoCodingAgent();
        userRepository = new UserRepository();
        alertManager = new AlertManager(userRepository, weatherAgent, alertWriter);
        //Threshold
        thresholdTemperatureMaxReached = new Threshold(ThresholdEnum.TEMPERATURE, Double.MIN_VALUE, Double.MIN_VALUE);
        thresholdTemperatureMinReached = new Threshold(ThresholdEnum.TEMPERATURE, Double.MAX_VALUE, Double.MAX_VALUE);
        thresholdTemperature = new Threshold(ThresholdEnum.TEMPERATURE, VALUE_MIN, VALUE_MAX);

        thresholdRain = new Threshold(ThresholdEnum.RAIN, VALUE_MIN, VALUE_MAX);
        thresholdRainOnlyMaxDefined = new Threshold(ThresholdEnum.RAIN, Double.NaN, VALUE_MAX);
        thresholdRainOnlyMinDefined = new Threshold(ThresholdEnum.RAIN, VALUE_MIN, Double.NaN);
        thresholdRainMinMaxReached = new Threshold(ThresholdEnum.RAIN, Double.MAX_VALUE, Double.MIN_VALUE);

        thresholdWind = new Threshold(ThresholdEnum.WIND, VALUE_MIN, VALUE_MAX);
        thresholdWindMinMaxReached = new Threshold(ThresholdEnum.WIND, Double.MAX_VALUE, Double.MIN_VALUE);
        removeCSVIfExist();
    }

    @DisplayName("Alert sent when test alert enabled for the user and addresses with a max threshold, check if writeAlert indicates it's a max threshold")
    @Test
    void should_alert_sent_and_writing_max_threshold_reached_when_test_alert_enabled_for_user_addresses_enabled_with_threshold_only_max() throws WeatherException, InterruptedException {
        //Arrange
        ArrayList<Threshold>  thresholdArrayList = new ArrayList<>(List.of(thresholdTemperatureMaxReached));
        Address address = new Address(firstAddress, false, thresholdArrayList, geoCodingAgent);
        User user = new User(username);
        user.addAddress(address);
        user.setDisableAllAlerts(false);
        userRepository.put(firstLocation, user);
        //Act
        alertManager.checkAlert();
        //Assert
        String value = readLineCSV(0)[INDEX_CSV_THRESHOLD_REACHED];
        Double maxTemperatureReached = Double.parseDouble(value.substring(1, value.length()-1));
        assertEquals(thresholdTemperatureMaxReached.getMaxThreshold(), maxTemperatureReached);
    }



    @DisplayName("Alert sent when test alert enabled for the user and addresses with a min threshold, check if writeAlert indicates it's a min threshold")
    @Test
    void should_alert_sent_and_writing_min_threshold_reached_when_test_alert_enabled_for_user_addresses_enabled_with_threshold_only_min() throws WeatherException, InterruptedException {
        //Arrange
        ArrayList<Threshold>  thresholdArrayList = new ArrayList<>(List.of(thresholdTemperatureMinReached));
        Address address = new Address(firstAddress, false, thresholdArrayList, geoCodingAgent);
        User user = new User(username);
        user.addAddress(address);
        user.setDisableAllAlerts(false);
        userRepository.put(firstLocation, user);
        //Act
        alertManager.checkAlert();
        //Assert
        String value = readLineCSV(0)[INDEX_CSV_THRESHOLD_REACHED];
        Double minTemperatureReached = Double.parseDouble(value.substring(1, value.length()-1));
        assertEquals(thresholdTemperatureMinReached.getMinThreshold(), minTemperatureReached);
    }

    @DisplayName("Alert sent when test alert enabled for the user and addresses with a min threshold of temperature, check if it is correctly a temperature sent to the AlertWriter")
    @Test
    void should_alert_sent_and_writing_temperature_threshold_reached_when_test_alert_enabled_for_user_addresses_enabled_with_threshold_only_min() throws WeatherException, InterruptedException {
        //Arrange
        ArrayList<Threshold>  thresholdArrayList = new ArrayList<>(List.of(thresholdTemperatureMinReached));
        Address address = new Address(firstAddress, false, thresholdArrayList, geoCodingAgent);
        User user = new User(username);
        user.addAddress(address);
        System.out.println(user.getAddresses().get(firstLocation));
        user.setDisableAllAlerts(false);
        userRepository.put(firstLocation, user);
        //Act
        alertManager.checkAlert();
        //Assert
        String value = readLineCSV(0)[INDEX_CSV_THRESHOLD_NAME];
        String thresholdName = value.substring(1, value.length()-1);
        assertEquals(thresholdTemperatureMinReached.getName().toString(), thresholdName);
    }

    @DisplayName("Alert sent when test alert enabled for the user and addresses with a max threshold of temperature, check if it is correctly a temperature sent to the AlertWriter")
    @Test
    void should_alert_sent_and_writing_temperature_threshold_reached_when_test_alert_enabled_for_user_addresses_enabled_with_threshold_only_max() throws WeatherException, InterruptedException {
        //Arrange
        ArrayList<Threshold>  thresholdArrayList = new ArrayList<>(List.of(thresholdTemperatureMaxReached));
        Address address = new Address(firstAddress, false, thresholdArrayList, geoCodingAgent);
        User user = new User(username);
        user.addAddress(address);
        System.out.println(user.getAddresses().get(firstLocation));
        user.setDisableAllAlerts(false);
        userRepository.put(firstLocation, user);
        //Act
        alertManager.checkAlert();
        //Assert
        String value = readLineCSV(0)[INDEX_CSV_THRESHOLD_NAME];
        String thresholdName = value.substring(1, value.length()-1);
        assertEquals(thresholdTemperatureMaxReached.getName().toString(), thresholdName);
        value = Arrays.toString(readLineCSV(0));
        assertTrue(value.contains(thresholdTemperatureMaxReached.getName().toString()));
    }

    @DisplayName("Two users have the same date for the same address when writing an alert")
    @Test
    void should_have_the_same_date_for_the_same_address_when_writing_alert() throws WeatherException, InterruptedException {
        // Arrange
        ArrayList<Threshold> thresholdArrayList = new ArrayList<>(List.of(this.thresholdTemperatureMinReached));
        Address address = new Address(firstAddress, false, thresholdArrayList, geoCodingAgent);
        User user = new User(username);
        User user2 = new User(username2);
        user.addAddress(address);
        user2.addAddress(address);
        user.setDisableAllAlerts(false);
        this.userRepository.put(this.firstLocation, user);
        this.userRepository.put(this.firstLocation, user2);
        //Act
        this.alertManager.checkAlert();

        //Assert
        String value = readLineCSV(0)[INDEX_CSV_DATE];
        String value2 = readLineCSV(1)[INDEX_CSV_DATE];
        String date = value.substring(1, value.length()-1);
        String date2 = value.substring(1, value2.length()-1);
        assertEquals(date, date2);
    }

    @DisplayName("Two users have the same hour for the same address when writing an alert")
    @Test
    void should_have_the_same_hour_for_the_same_address_when_writing_alert() throws WeatherException, InterruptedException {
        // Arrange
        ArrayList<Threshold> thresholdArrayList = new ArrayList<>(List.of(this.thresholdTemperatureMinReached));
        Address address = new Address(firstAddress, false, thresholdArrayList, geoCodingAgent);
        User user = new User(username);
        User user2 = new User(username2);
        user.addAddress(address);
        user2.addAddress(address);
        user.setDisableAllAlerts(false);
        this.userRepository.put(this.firstLocation, user);
        this.userRepository.put(this.firstLocation, user2);
        //Act
        this.alertManager.checkAlert();

        //Assert
        String value = readLineCSV(0)[INDEX_CSV_HOUR];
        String value2 = readLineCSV(1)[INDEX_CSV_HOUR];
        String hour = value.substring(1, value.length()-1);
        String hour2 = value.substring(1, value2.length()-1);
        assertEquals(hour, hour2);
    }
}
