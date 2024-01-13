package tempguardiantests;

import exceptions.WeatherException;
import managers.AlertManager;
import models.*;
import models.enums.ThresholdEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import repositories.UserRepository;
import services.AlertWriter;
import services.IAlertWriter;
import services.IWeatherAgent;
import services.WeatherAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AlertManagerTest {
    private WeatherAgent weatherAgent;
    private IWeatherAgent weatherAgentMock;
    private AlertWriter alertWriter;
    private IAlertWriter alertWriterMock;
    private UserRepository userRepository;
    private AlertManager alertManager;

    private Threshold thresholdTemperatureMaxReached;
    private Threshold thresholdTemperatureMinReached;
    private Threshold thresholdTemperature;
    private Threshold thresholdRain;
    private Threshold thresholdRainOnlyMinDefined;
    private Threshold thresholdRainOnlyMaxDefined;

    private Threshold thresholdWind;
    private Threshold thresholdWindMinMaxReached;
    private final double VALUE_MIN = 0.0;
    private final double VALUE_MAX = 20.0;
    private final double VALUE_1 = 10;
    private final double VALUE_2 = 15;
    private final String firstAddress = "15 Rue de la Paix, Paris";
    private final Location firstLocation = new Location(2.3312846, 48.8695088);
    private final String secondAddress = "29 Rue Saint-Antoine, Paris";
    private final String thirdAddress = "Place Bellecour, 69002 Lyon, France";
    private final String username = "Tintin";
    private Address addressOneThresholdMax;
    public Address addressOneThresholdMin;
    public Address addressThresholdMaxMin;

    @BeforeEach
    void setUp() {
        weatherAgent = new WeatherAgent();
        weatherAgentMock = mock(IWeatherAgent.class);
        alertWriter = new AlertWriter();
        alertWriterMock = mock(IAlertWriter.class);
        userRepository = new UserRepository();
        alertManager = new AlertManager(userRepository, weatherAgentMock, alertWriterMock);
        //Threshold
        thresholdTemperatureMaxReached = new Threshold(ThresholdEnum.TEMPERATURE, VALUE_MIN, Double.MIN_VALUE);
        thresholdTemperatureMinReached = new Threshold(ThresholdEnum.TEMPERATURE, Double.MAX_VALUE, VALUE_MAX);
        thresholdTemperature = new Threshold(ThresholdEnum.TEMPERATURE, VALUE_MIN, VALUE_MAX);

        thresholdRain = new Threshold(ThresholdEnum.RAIN, VALUE_MIN, VALUE_MAX);
        thresholdRainOnlyMaxDefined = new Threshold(ThresholdEnum.RAIN, Double.NaN, VALUE_MAX);
        thresholdRainOnlyMinDefined = new Threshold(ThresholdEnum.RAIN, VALUE_MIN, Double.NaN);

        thresholdWind = new Threshold(ThresholdEnum.WIND, VALUE_MIN, VALUE_MAX);
        thresholdWindMinMaxReached = new Threshold(ThresholdEnum.WIND, Double.MAX_VALUE, Double.MIN_VALUE);
    }

    @DisplayName("Not alert sent when test alert disabled for the user and addresses enabled")
    @Test
    void should_not_alert_sent_when_test_alert_disabled_for_user_adresses_enabled() throws WeatherException, InterruptedException {
        //Arrange
        Address address = new Address(firstAddress, true, List.of(thresholdWindMinMaxReached));
        User user = new User(username);
        user.addAddress(address);
        user.setDisableAllAlerts(true);
        //Act
        alertManager.checkAlert();
        //Assert
        verify(alertWriterMock, never()).writeAlert(any(AlertData.class));
    }

    @DisplayName("Not alert sent when test alert disabled for the user and addresses disabled")
    @Test
    void should_not_alert_sent_when_test_alert_disabled_for_user_adresses_disabled() throws WeatherException, InterruptedException {
        //Arrange
        Address address = new Address(firstAddress, false, List.of(thresholdWindMinMaxReached));
        User user = new User(username);
        user.addAddress(address);
        user.setDisableAllAlerts(true);
        //Act
        alertManager.checkAlert();
        //Assert
        verify(alertWriterMock, never()).writeAlert(any(AlertData.class));
    }

    @DisplayName("Not alert sent when test alert enabled for the user and addresses disabled")
    @Test
    void should_alert_sent_when_test_alert_enabled_for_user_adresses_disabled() throws WeatherException, InterruptedException {
        //Arrange
        Address address = new Address(firstAddress, false, List.of(thresholdWindMinMaxReached));
        User user = new User(username);
        user.addAddress(address);
        user.setDisableAllAlerts(false);
        //Act
        alertManager.checkAlert();
        //Assert
        verify(alertWriterMock, never()).writeAlert(any(AlertData.class));
    }

    @DisplayName("Not alert sent when test alert enabled for the user and addresses disabled")
    @Test
    void should_alert_sent_when_test_alert_enabled_for_user_adresses_enabled() throws WeatherException, InterruptedException {
        //Arrange
        ArrayList<Threshold>  thresholdArrayList = new ArrayList<>(List.of(thresholdWindMinMaxReached));
        ArrayList<ThresholdEnum> thresholdsEnumList =  new ArrayList<>(List.of(ThresholdEnum.WIND));
        HashMap<ThresholdEnum, Double> resultWeatherAPI = new HashMap<>();
        resultWeatherAPI.put(ThresholdEnum.WIND, VALUE_1);
        Address address = new Address(firstAddress, false, thresholdArrayList);
        User user = new User(username);
        user.addAddress(address);
        user.setDisableAllAlerts(false);
        when(.getValuesFromData(firstLocation, thresholdsEnumList)).thenReturn(resultWeatherAPI);
        when(weatherAgentMock.getValuesFromData(firstLocation, thresholdsEnumList)).thenReturn(resultWeatherAPI);
        //Act
        alertManager.checkAlert();
        //Assert
        verify(alertWriterMock, times(1)).writeAlert(any(AlertData.class));
    }


}
