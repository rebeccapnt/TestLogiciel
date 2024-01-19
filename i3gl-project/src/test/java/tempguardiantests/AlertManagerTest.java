package tempguardiantests;

import exceptions.GeoCodingException;
import exceptions.WeatherException;
import managers.AlertManager;
import models.*;
import models.enums.ThresholdEnum;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import repositories.UserRepository;
import services.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AlertManagerTest {
    private WeatherAgent weatherAgent;
    private IWeatherAgent weatherAgentMock;
    private AlertWriter alertWriter;
    private IAlertWriter alertWriterMock;
    private UserRepository userRepository;
    private AlertManager alertManager;
    private IGeoCodingAgent geoCodingAgentMock;

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
    private final String secondAddress = "29 Rue Saint-Antoine, Paris";
    private final Location secondLocation = new Location(2.3652377, 48.8536675);
    private final String thirdAddress = "Place Bellecour, 69002 Lyon, France";
    private final String username = "Tintin";
    private final String username2 = "Milou";

    private final ArrayList<ThresholdEnum> API_REQUEST_THRESHOLD_ENUM = new ArrayList<>(List.of(ThresholdEnum.RAIN, ThresholdEnum.WIND, ThresholdEnum.TEMPERATURE));

    @BeforeEach
    void setUp() throws InterruptedException {
        weatherAgent = new WeatherAgent();
        weatherAgentMock = mock(IWeatherAgent.class);
        alertWriter = new AlertWriter();
        alertWriterMock = mock(IAlertWriter.class);
        geoCodingAgentMock = mock(IGeoCodingAgent.class);
        userRepository = new UserRepository();
        alertManager = new AlertManager(userRepository, weatherAgentMock, alertWriterMock);
        //Threshold
        thresholdTemperatureMaxReached = new Threshold(ThresholdEnum.TEMPERATURE, VALUE_MIN, Double.MIN_VALUE);
        thresholdTemperatureMinReached = new Threshold(ThresholdEnum.TEMPERATURE, Double.MAX_VALUE, VALUE_MAX);
        thresholdTemperature = new Threshold(ThresholdEnum.TEMPERATURE, VALUE_MIN, VALUE_MAX);

        thresholdRain = new Threshold(ThresholdEnum.RAIN, VALUE_MIN, VALUE_MAX);
        thresholdRainOnlyMaxDefined = new Threshold(ThresholdEnum.RAIN, Double.NaN, VALUE_MAX);
        thresholdRainOnlyMinDefined = new Threshold(ThresholdEnum.RAIN, VALUE_MIN, Double.NaN);
        thresholdRainMinMaxReached = new Threshold(ThresholdEnum.RAIN, Double.MAX_VALUE, Double.MIN_VALUE);

        thresholdWind = new Threshold(ThresholdEnum.WIND, VALUE_MIN, VALUE_MAX);
        thresholdWindMinMaxReached = new Threshold(ThresholdEnum.WIND, Double.MAX_VALUE, Double.MIN_VALUE);

        when(geoCodingAgentMock.convertAddressToLocation(firstAddress)).thenReturn(firstLocation);
        when(geoCodingAgentMock.convertAddressToLocation(secondAddress)).thenReturn(secondLocation);
    }


    @DisplayName("Not alert sent when test alert disabled for the user and addresses enabled")
    @Test
    void should_not_alert_sent_when_test_alert_disabled_for_user_addresses_enabled() throws WeatherException, InterruptedException {
        //Arrange
        ArrayList<Threshold>  thresholdArrayList = new ArrayList<>(List.of(thresholdWindMinMaxReached));
        HashMap<ThresholdEnum, Double> resultWeatherAPI = new HashMap<>();
        resultWeatherAPI.put(thresholdWindMinMaxReached.getName(), VALUE_1);
        Address address = new Address(firstAddress, false, thresholdArrayList, geoCodingAgentMock);
        User user = new User(username);
        user.addAddress(address);
        user.setDisableAllAlerts(true);
        userRepository.put(firstLocation, user);
        when(weatherAgentMock.getValuesFromData(firstLocation, API_REQUEST_THRESHOLD_ENUM)).thenReturn(resultWeatherAPI);
        //Act
        alertManager.checkAlert();
        //Assert
        verify(alertWriterMock, never()).writeAlert(any(AlertData.class));
    }

    @DisplayName("Not alert sent when test alert disabled for the user and addresses disabled")
    @Test
    void should_not_alert_sent_when_test_alert_disabled_for_user_addresses_disabled() throws WeatherException, InterruptedException {
        //Arrange
        ArrayList<Threshold>  thresholdArrayList = new ArrayList<>(List.of(thresholdWindMinMaxReached));
        HashMap<ThresholdEnum, Double> resultWeatherAPI = new HashMap<>();
        resultWeatherAPI.put(thresholdWindMinMaxReached.getName(), VALUE_1);
        Address address = new Address(firstAddress, true, thresholdArrayList, geoCodingAgentMock);
        User user = new User(username);
        user.addAddress(address);
        user.setDisableAllAlerts(true);
        userRepository.put(firstLocation, user);
        when(weatherAgentMock.getValuesFromData(firstLocation, API_REQUEST_THRESHOLD_ENUM)).thenReturn(resultWeatherAPI);
        //Act
        alertManager.checkAlert();
        //Assert
        verify(alertWriterMock, never()).writeAlert(any(AlertData.class));
    }

    @DisplayName("Not alert sent when test alert enabled for the user and addresses disabled")
    @Test
    void should_alert_sent_when_test_alert_enabled_for_user_adresses_disabled() throws WeatherException, InterruptedException {
        //Arrange
        ArrayList<Threshold>  thresholdArrayList = new ArrayList<>(List.of(thresholdWindMinMaxReached));
        HashMap<ThresholdEnum, Double> resultWeatherAPI = new HashMap<>();
        resultWeatherAPI.put(thresholdWindMinMaxReached.getName(), VALUE_1);
        Address address = new Address(firstAddress, true, thresholdArrayList, geoCodingAgentMock);
        User user = new User(username);
        user.addAddress(address);
        user.setDisableAllAlerts(false);
        userRepository.put(firstLocation, user);
        when(weatherAgentMock.getValuesFromData(firstLocation, API_REQUEST_THRESHOLD_ENUM)).thenReturn(resultWeatherAPI);
        user.setDisableAllAlerts(false);
        //Act
        alertManager.checkAlert();
        //Assert
        verify(alertWriterMock, never()).writeAlert(any(AlertData.class));
    }

    @DisplayName("Alert sent when test alert enabled for the user and addresses, there are two thresholds : min and max for wind")
    @Test
    void should_alert_sent_when_test_alert_enabled_for_user_addresses_enabled_with_threshold_max_and_min() throws WeatherException, InterruptedException {
        //Arrange
        ArrayList<Threshold>  thresholdArrayList = new ArrayList<>(List.of(thresholdWindMinMaxReached));
        HashMap<ThresholdEnum, Double> resultWeatherAPI = new HashMap<>();
        resultWeatherAPI.put(thresholdWindMinMaxReached.getName(), VALUE_1);
        Address address = new Address(firstAddress, false, thresholdArrayList, geoCodingAgentMock);
        User user = new User(username);
        user.addAddress(address);
        user.setDisableAllAlerts(false);
        userRepository.put(firstLocation, user);
        when(weatherAgentMock.getValuesFromData(firstLocation, API_REQUEST_THRESHOLD_ENUM)).thenReturn(resultWeatherAPI);
        //Act
        alertManager.checkAlert();
        //Assert
        verify(alertWriterMock, times(2)).writeAlert(any(AlertData.class));
    }

    @DisplayName("Alert sent when test alert enabled for the user and addresses, there is only one threshold : min temperature")
    @Test
    void should_alert_sent_when_test_alert_enabled_for_user_addresses_enabled_with_threshold_only_min() throws WeatherException, InterruptedException {
        //Arrange
        ArrayList<Threshold>  thresholdArrayList = new ArrayList<>(List.of(thresholdTemperatureMinReached));
        HashMap<ThresholdEnum, Double> resultWeatherAPI = new HashMap<>();
        resultWeatherAPI.put(thresholdTemperatureMinReached.getName(), VALUE_1);
        Address address = new Address(firstAddress, false, thresholdArrayList, geoCodingAgentMock);
        User user = new User(username);
        user.addAddress(address);
        user.setDisableAllAlerts(false);
        userRepository.put(firstLocation, user);
        when(weatherAgentMock.getValuesFromData(firstLocation, API_REQUEST_THRESHOLD_ENUM)).thenReturn(resultWeatherAPI);
        //Act
        alertManager.checkAlert();
        //Assert
        verify(alertWriterMock, times(1)).writeAlert(any(AlertData.class));
    }

    @DisplayName("Alert sent when test alert enabled for the user and addresses, there is only one threshold : max temperature")
    @Test
    void should_alert_sent_when_test_alert_enabled_for_user_addresses_enabled_with_threshold_only_max() throws WeatherException, InterruptedException {
        //Arrange
        ArrayList<Threshold>  thresholdArrayList = new ArrayList<>(List.of(thresholdTemperatureMaxReached));
        HashMap<ThresholdEnum, Double> resultWeatherAPI = new HashMap<>();
        resultWeatherAPI.put(thresholdTemperatureMaxReached.getName(), VALUE_1);
        Address address = new Address(firstAddress, false, thresholdArrayList, geoCodingAgentMock);
        User user = new User(username);
        user.addAddress(address);
        user.setDisableAllAlerts(false);
        userRepository.put(firstLocation, user);
        when(weatherAgentMock.getValuesFromData(firstLocation, API_REQUEST_THRESHOLD_ENUM)).thenReturn(resultWeatherAPI);
        //Act
        alertManager.checkAlert();
        //Assert
        verify(alertWriterMock, times(1)).writeAlert(any(AlertData.class));
    }

    @DisplayName("Alert not sent when test alert enabled for the user and addresses, no threshold must be reached")
    @Test
    void should_alert_not_sent_when_test_alert_enabled_for_user_addresses_enabled_without_threshold_reached() throws WeatherException, InterruptedException {
        //Arrange
        ArrayList<Threshold>  thresholdArrayList = new ArrayList<>(List.of(thresholdTemperature));
        HashMap<ThresholdEnum, Double> resultWeatherAPI = new HashMap<>();
        resultWeatherAPI.put(thresholdTemperature.getName(), VALUE_1);
        Address address = new Address(firstAddress, false, thresholdArrayList, geoCodingAgentMock);
        User user = new User(username);
        user.addAddress(address);
        user.setDisableAllAlerts(false);
        userRepository.put(firstLocation, user);
        when(weatherAgentMock.getValuesFromData(firstLocation, API_REQUEST_THRESHOLD_ENUM)).thenReturn(resultWeatherAPI);
        //Act
        alertManager.checkAlert();
        //Assert
        verify(alertWriterMock, never()).writeAlert(any(AlertData.class));
    }


    @DisplayName("Alert not sent when test alert enabled for the user and addresses, min and max defined but only max reached")
    @Test
    void should_alert_not_sent_when_test_alert_enabled_for_user_addresses_enabled_with_threshold_min_and_max_but_only_max_reached() throws WeatherException, InterruptedException {
        //Arrange
        ArrayList<Threshold>  thresholdArrayList = new ArrayList<>(List.of(thresholdTemperature));
        HashMap<ThresholdEnum, Double> resultWeatherAPI = new HashMap<>();
        resultWeatherAPI.put(thresholdTemperature.getName(), VALUE_2);
        Address address = new Address(firstAddress, false, thresholdArrayList, geoCodingAgentMock);
        User user = new User(username);
        user.addAddress(address);
        user.setDisableAllAlerts(false);
        userRepository.put(firstLocation, user);
        when(weatherAgentMock.getValuesFromData(firstLocation, API_REQUEST_THRESHOLD_ENUM)).thenReturn(resultWeatherAPI);
        //Act
        alertManager.checkAlert();
        //Assert
        verify(alertWriterMock, times(1)).writeAlert(any(AlertData.class));
    }

    @DisplayName("Alert not sent when test alert enabled for the user and addresses, min and max defined but only min reached")
    @Test
    void should_alert_not_sent_when_test_alert_enabled_for_user_addresses_enabled_with_threshold_min_and_max_but_only_min_reached() throws WeatherException, InterruptedException {
        //Arrange
        ArrayList<Threshold>  thresholdArrayList = new ArrayList<>(List.of(thresholdTemperature));
        HashMap<ThresholdEnum, Double> resultWeatherAPI = new HashMap<>();
        resultWeatherAPI.put(thresholdTemperature.getName(), -VALUE_2);
        Address address = new Address(firstAddress, false, thresholdArrayList, geoCodingAgentMock);
        User user = new User(username);
        user.addAddress(address);
        user.setDisableAllAlerts(false);
        userRepository.put(firstLocation, user);
        when(weatherAgentMock.getValuesFromData(firstLocation, API_REQUEST_THRESHOLD_ENUM)).thenReturn(resultWeatherAPI);
        //Act
        alertManager.checkAlert();
        //Assert
        verify(alertWriterMock, times(1)).writeAlert(any(AlertData.class));
    }

    @DisplayName("WeatherAgent called good times with one address enabled with three threshold and alert user enabled")
    @Test
    void should_WeatherAgent_called_good_times_with_one_address_three_thresholds_and_alert_user_enabled() throws WeatherException, InterruptedException {
        //Arrange
        ArrayList<Threshold>  thresholdArrayList = new ArrayList<>(List.of(thresholdTemperature, thresholdWindMinMaxReached, thresholdRainOnlyMaxDefined));
        HashMap<ThresholdEnum, Double> resultWeatherAPI = new HashMap<>();
        resultWeatherAPI.put(thresholdTemperature.getName(), VALUE_1);
        resultWeatherAPI.put(thresholdWindMinMaxReached.getName(), VALUE_1);
        resultWeatherAPI.put(thresholdRainOnlyMaxDefined.getName(), VALUE_1);
        Address address = new Address(firstAddress, false, thresholdArrayList, geoCodingAgentMock);
        User user = new User(username);
        user.addAddress(address);
        user.setDisableAllAlerts(false);
        userRepository.put(firstLocation, user);
        when(weatherAgentMock.getValuesFromData(firstLocation, API_REQUEST_THRESHOLD_ENUM)).thenReturn(resultWeatherAPI);
        //Act
        alertManager.checkAlert();
        //Assert
        verify(weatherAgentMock, times(1)).getValuesFromData(firstLocation, API_REQUEST_THRESHOLD_ENUM);
    }

    @DisplayName("WeatherAgent called good times with one address enabled with one threshold and alert user enabled")
    @Test
    void should_WeatherAgent_called_good_times_with_one_address_one_threshold_and_alert_user_enabled() throws WeatherException, InterruptedException {
        //Arrange
        ArrayList<Threshold>  thresholdArrayList = new ArrayList<>(List.of(thresholdTemperature));
        HashMap<ThresholdEnum, Double> resultWeatherAPI = new HashMap<>();
        resultWeatherAPI.put(thresholdTemperature.getName(), VALUE_1);
        Address address = new Address(firstAddress, false, thresholdArrayList, geoCodingAgentMock);
        User user = new User(username);
        user.addAddress(address);
        user.setDisableAllAlerts(false);
        userRepository.put(firstLocation, user);
        when(weatherAgentMock.getValuesFromData(firstLocation, API_REQUEST_THRESHOLD_ENUM)).thenReturn(resultWeatherAPI);
        //Act
        alertManager.checkAlert();
        //Assert
        verify(weatherAgentMock, times(1)).getValuesFromData(firstLocation, API_REQUEST_THRESHOLD_ENUM);
    }

    @DisplayName("WeatherAgent called good times with one address enabled with one threshold and alert user disabled")
    @Test
    void should_WeatherAgent_called_good_times_with_one_address_one_threshold_and_alert_user_disabled() throws WeatherException, InterruptedException {
        //Arrange
        ArrayList<Threshold>  thresholdArrayList = new ArrayList<>(List.of(thresholdTemperature));
        HashMap<ThresholdEnum, Double> resultWeatherAPI = new HashMap<>();
        resultWeatherAPI.put(thresholdTemperature.getName(), VALUE_1);
        Address address = new Address(firstAddress, false, thresholdArrayList, geoCodingAgentMock);
        User user = new User(username);
        user.addAddress(address);
        user.setDisableAllAlerts(true);
        userRepository.put(firstLocation, user);
        when(weatherAgentMock.getValuesFromData(firstLocation, API_REQUEST_THRESHOLD_ENUM)).thenReturn(resultWeatherAPI);
        //Act
        alertManager.checkAlert();
        //Assert
        verify(weatherAgentMock, times(1)).getValuesFromData(firstLocation, API_REQUEST_THRESHOLD_ENUM);
    }

    @DisplayName("WeatherAgent called good times with one address disables with one threshold and alert user disabled")
    @Test
    void should_WeatherAgent_called_good_times_with_one_address_disabled_one_threshold_and_alert_user_disabled() throws WeatherException, InterruptedException {
        //Arrange
        ArrayList<Threshold>  thresholdArrayList = new ArrayList<>(List.of(thresholdTemperature));
        HashMap<ThresholdEnum, Double> resultWeatherAPI = new HashMap<>();
        resultWeatherAPI.put(thresholdTemperature.getName(), VALUE_1);
        Address address = new Address(firstAddress, true, thresholdArrayList, geoCodingAgentMock);
        User user = new User(username);
        user.addAddress(address);
        user.setDisableAllAlerts(false);
        userRepository.put(firstLocation, user);
        when(weatherAgentMock.getValuesFromData(firstLocation, API_REQUEST_THRESHOLD_ENUM)).thenReturn(resultWeatherAPI);
        //Act
        alertManager.checkAlert();
        //Assert
        verify(weatherAgentMock, times(1)).getValuesFromData(firstLocation, API_REQUEST_THRESHOLD_ENUM);
    }

    @DisplayName("WeatherAgent called good times with two addresses enabled with three thresholds each and alert user enabled")
    @Test
    void should_WeatherAgent_called_good_times_with_two_addresses_three_thresholds_and_alert_user_enabled() throws WeatherException, InterruptedException {
        //Arrange
        ArrayList<Threshold>  thresholdArrayList = new ArrayList<>(List.of(thresholdTemperature, thresholdWindMinMaxReached, thresholdRainOnlyMaxDefined));
        HashMap<ThresholdEnum, Double> resultWeatherAPI = new HashMap<>();
        resultWeatherAPI.put(thresholdTemperature.getName(), VALUE_1);
        resultWeatherAPI.put(thresholdWindMinMaxReached.getName(), VALUE_1);
        resultWeatherAPI.put(thresholdRainOnlyMaxDefined.getName(), VALUE_1);
        ArrayList<Threshold> thresholdArrayList2 = new ArrayList<>(List.of(thresholdTemperatureMinReached, thresholdRainOnlyMinDefined));
        HashMap<ThresholdEnum, Double> resultWeatherAPI2 = new HashMap<>();
        resultWeatherAPI2.put(thresholdTemperatureMinReached.getName(), VALUE_1);
        resultWeatherAPI2.put(thresholdRainOnlyMinDefined.getName(), VALUE_1);
        Address address = new Address(firstAddress, false, thresholdArrayList, geoCodingAgentMock);
        Address address2 = new Address(secondAddress, false, thresholdArrayList2, geoCodingAgentMock);
        User user = new User(username);
        user.addAddress(address);
        user.addAddress(address2);
        user.setDisableAllAlerts(false);
        userRepository.put(firstLocation, user);
        userRepository.put(secondLocation, user);
        when(weatherAgentMock.getValuesFromData(firstLocation, API_REQUEST_THRESHOLD_ENUM)).thenReturn(resultWeatherAPI);
        when(weatherAgentMock.getValuesFromData(secondLocation, API_REQUEST_THRESHOLD_ENUM)).thenReturn(resultWeatherAPI2);
        //Act
        alertManager.checkAlert();
        //Assert
        verify(weatherAgentMock, times(1)).getValuesFromData(firstLocation, API_REQUEST_THRESHOLD_ENUM);
        verify(weatherAgentMock, times(1)).getValuesFromData(secondLocation, API_REQUEST_THRESHOLD_ENUM);
    }

    @DisplayName("WeatherAgent called good times, there are two users with one address each one enabled with three thresholds each and alert user enabled")
    @Test
    void should_WeatherAgent_called_good_times_two_users_with_one_address_each_one_three_thresholds_and_alert_user_enabled() throws WeatherException, InterruptedException {
        //Arrange
        ArrayList<Threshold>  thresholdArrayList = new ArrayList<>(List.of(thresholdTemperature, thresholdWindMinMaxReached, thresholdRainOnlyMaxDefined));
        HashMap<ThresholdEnum, Double> resultWeatherAPI = new HashMap<>();
        resultWeatherAPI.put(thresholdTemperature.getName(), VALUE_1);
        resultWeatherAPI.put(thresholdWindMinMaxReached.getName(), VALUE_1);
        resultWeatherAPI.put(thresholdRainOnlyMaxDefined.getName(), VALUE_1);
        ArrayList<Threshold> thresholdArrayList2 = new ArrayList<>(List.of(thresholdTemperatureMinReached, thresholdRainOnlyMinDefined));
        HashMap<ThresholdEnum, Double> resultWeatherAPI2 = new HashMap<>();
        resultWeatherAPI2.put(thresholdTemperatureMinReached.getName(), VALUE_1);
        resultWeatherAPI2.put(thresholdRainOnlyMinDefined.getName(), VALUE_1);
        Address address = new Address(firstAddress, false, thresholdArrayList, geoCodingAgentMock);
        Address address2 = new Address(secondAddress, false, thresholdArrayList2, geoCodingAgentMock);
        User user = new User(username);
        User user2 = new User(username2);
        user.addAddress(address);
        user2.addAddress(address2);
        user.setDisableAllAlerts(false);
        userRepository.put(firstLocation, user);
        userRepository.put(secondLocation, user2);
        when(weatherAgentMock.getValuesFromData(firstLocation, API_REQUEST_THRESHOLD_ENUM)).thenReturn(resultWeatherAPI);
        when(weatherAgentMock.getValuesFromData(secondLocation, API_REQUEST_THRESHOLD_ENUM)).thenReturn(resultWeatherAPI2);
        //Act
        alertManager.checkAlert();
        //Assert
        verify(weatherAgentMock, times(1)).getValuesFromData(firstLocation, API_REQUEST_THRESHOLD_ENUM);
        verify(weatherAgentMock, times(1)).getValuesFromData(secondLocation, API_REQUEST_THRESHOLD_ENUM);
    }

    @DisplayName("WeatherAgent called good times, there are two users with the same address each one enabled with three thresholds each and alert user enabled")
    @Test
    void should_WeatherAgent_called_good_times_two_users_with_the_same_address_with_three_thresholds_and_alert_user_enabled() throws WeatherException, InterruptedException {
        //Arrange
        ArrayList<Threshold>  thresholdArrayList = new ArrayList<>(List.of(thresholdTemperature, thresholdWindMinMaxReached, thresholdRainOnlyMaxDefined));
        HashMap<ThresholdEnum, Double> resultWeatherAPI = new HashMap<>();
        resultWeatherAPI.put(thresholdTemperature.getName(), VALUE_1);
        resultWeatherAPI.put(thresholdWindMinMaxReached.getName(), VALUE_1);
        resultWeatherAPI.put(thresholdRainOnlyMaxDefined.getName(), VALUE_1);
        Address address = new Address(firstAddress, false, thresholdArrayList, geoCodingAgentMock);
        User user = new User(username);
        User user2 = new User(username2);
        user.addAddress(address);
        user2.addAddress(address);
        user.setDisableAllAlerts(false);
        userRepository.put(firstLocation, user);
        userRepository.put(firstLocation, user2);
        when(weatherAgentMock.getValuesFromData(firstLocation, API_REQUEST_THRESHOLD_ENUM)).thenReturn(resultWeatherAPI);
        //Act
        alertManager.checkAlert();
        //Assert
        verify(weatherAgentMock, times(1)).getValuesFromData(firstLocation, API_REQUEST_THRESHOLD_ENUM);
    }


    @DisplayName("Not alert sent when test alert disabled for the user and addresses disabled")
    @Test
    void should_not_error_throw_by_weather_agent() throws WeatherException, InterruptedException {
        //Arrange
        ArrayList<Threshold>  thresholdArrayList = new ArrayList<>(List.of(thresholdWindMinMaxReached));
        HashMap<ThresholdEnum, Double> resultWeatherAPI = new HashMap<>();
        resultWeatherAPI.put(thresholdWindMinMaxReached.getName(), VALUE_1);
        Address address = new Address(firstAddress, true, thresholdArrayList, geoCodingAgentMock);
        User user = new User(username);
        user.addAddress(address);
        user.setDisableAllAlerts(true);
        userRepository.put(firstLocation, user);
        when(weatherAgentMock.getValuesFromData(firstLocation, API_REQUEST_THRESHOLD_ENUM)).thenThrow(WeatherException.class);
        //Act and Assert
        assertThrows(WeatherException.class, () -> alertManager.checkAlert());
    }

    @DisplayName("Not alert sent when test alert enabled for the user and addresses enabled but none of threshold is enabled")
    @Test
    void should_not_alert_sent_when_test_alert_enabled_for_user_addresses_enabled_none_of_threshold_is_enabled() throws WeatherException, InterruptedException {
        //Arrange
        Threshold thresholdWindWithoutMinMax = new Threshold(ThresholdEnum.WIND, Double.NaN, Double.NaN);
        ArrayList<Threshold>  thresholdArrayList = new ArrayList<>(List.of(thresholdWindWithoutMinMax));
        HashMap<ThresholdEnum, Double> resultWeatherAPI = new HashMap<>();
        resultWeatherAPI.put(thresholdWindWithoutMinMax.getName(), VALUE_1);
        Address address = new Address(firstAddress, false, thresholdArrayList, geoCodingAgentMock);
        User user = new User(username);
        user.addAddress(address);
        user.setDisableAllAlerts(false);
        userRepository.put(firstLocation, user);
        when(weatherAgentMock.getValuesFromData(firstLocation, API_REQUEST_THRESHOLD_ENUM)).thenReturn(resultWeatherAPI);
        //Act
        alertManager.checkAlert();
        //Assert
        verify(alertWriterMock, never()).writeAlert(any(AlertData.class));
    }

    @DisplayName("Alert sent when test alert enabled for the user and addresses with a max threshold, check if writeAlert indicates it's a max threshold")
    @Test
    void should_alert_sent_and_writing_max_threshold_reached_when_test_alert_enabled_for_user_addresses_enabled_with_threshold_only_max() throws WeatherException, InterruptedException {
        //Arrange
        ArrayList<Threshold>  thresholdArrayList = new ArrayList<>(List.of(thresholdTemperatureMaxReached));
        HashMap<ThresholdEnum, Double> resultWeatherAPI = new HashMap<>();
        resultWeatherAPI.put(thresholdTemperatureMaxReached.getName(), VALUE_1);
        Address address = new Address(firstAddress, false, thresholdArrayList, geoCodingAgentMock);
        User user = new User(username);
        user.addAddress(address);
        user.setDisableAllAlerts(false);
        userRepository.put(firstLocation, user);
        when(weatherAgentMock.getValuesFromData(firstLocation, API_REQUEST_THRESHOLD_ENUM)).thenReturn(resultWeatherAPI);
        ArgumentCaptor<AlertData> alertDataCaptor = ArgumentCaptor.forClass(AlertData.class);
        //Act
        alertManager.checkAlert();
        //Assert
        verify(alertWriterMock, times(1)).writeAlert(alertDataCaptor.capture());
        AlertData capturedAlertData = alertDataCaptor.getValue();
        assertEquals(thresholdTemperatureMaxReached.getMaxThreshold(), capturedAlertData.getThresholdReached());
    }

    @DisplayName("Alert sent when test alert enabled for the user and addresses with a min threshold, check if writeAlert indicates it's a min threshold")
    @Test
    void should_alert_sent_and_writing_min_threshold_reached_when_test_alert_enabled_for_user_addresses_enabled_with_threshold_only_min() throws WeatherException, InterruptedException {
        //Arrange
        ArrayList<Threshold>  thresholdArrayList = new ArrayList<>(List.of(thresholdTemperatureMinReached));
        HashMap<ThresholdEnum, Double> resultWeatherAPI = new HashMap<>();
        resultWeatherAPI.put(thresholdTemperatureMinReached.getName(), VALUE_1);
        Address address = new Address(firstAddress, false, thresholdArrayList, geoCodingAgentMock);
        User user = new User(username);
        user.addAddress(address);
        user.setDisableAllAlerts(false);
        userRepository.put(firstLocation, user);
        when(weatherAgentMock.getValuesFromData(firstLocation, API_REQUEST_THRESHOLD_ENUM)).thenReturn(resultWeatherAPI);
        ArgumentCaptor<AlertData> alertDataCaptor = ArgumentCaptor.forClass(AlertData.class);
        //Act
        alertManager.checkAlert();
        //Assert
        verify(alertWriterMock, times(1)).writeAlert(alertDataCaptor.capture());
        AlertData capturedAlertData = alertDataCaptor.getValue();
        assertEquals(thresholdTemperatureMinReached.getMinThreshold(), capturedAlertData.getThresholdReached());
    }

    @DisplayName("Alert sent when test alert enabled for the user and addresses with a min threshold of temperature, check if it is correctly a temperature sent to the AlertWriter")
    @Test
    void should_alert_sent_and_writing_temperature_threshold_reached_when_test_alert_enabled_for_user_addresses_enabled_with_threshold_only_min() throws WeatherException, InterruptedException {
        //Arrange
        ArrayList<Threshold>  thresholdArrayList = new ArrayList<>(List.of(thresholdTemperatureMinReached));
        HashMap<ThresholdEnum, Double> resultWeatherAPI = new HashMap<>();
        resultWeatherAPI.put(thresholdTemperatureMinReached.getName(), VALUE_1);
        Address address = new Address(firstAddress, false, thresholdArrayList, geoCodingAgentMock);
        User user = new User(username);
        user.addAddress(address);
        user.setDisableAllAlerts(false);
        userRepository.put(firstLocation, user);
        when(weatherAgentMock.getValuesFromData(firstLocation, API_REQUEST_THRESHOLD_ENUM)).thenReturn(resultWeatherAPI);
        ArgumentCaptor<AlertData> alertDataCaptor = ArgumentCaptor.forClass(AlertData.class);
        //Act
        alertManager.checkAlert();
        //Assert
        verify(alertWriterMock, times(1)).writeAlert(alertDataCaptor.capture());
        AlertData capturedAlertData = alertDataCaptor.getValue();
        assertEquals(thresholdTemperatureMinReached.getName(), capturedAlertData.getThresholdEnum());
    }

    @DisplayName("Alert sent when test alert enabled for the user and addresses with a max threshold of temperature, check if it is correctly a temperature sent to the AlertWriter")
    @Test
    void should_alert_sent_and_writing_temperature_threshold_reached_when_test_alert_enabled_for_user_addresses_enabled_with_threshold_only_max() throws WeatherException, InterruptedException {
        //Arrange
        ArrayList<Threshold>  thresholdArrayList = new ArrayList<>(List.of(thresholdTemperatureMaxReached));
        HashMap<ThresholdEnum, Double> resultWeatherAPI = new HashMap<>();
        resultWeatherAPI.put(thresholdTemperatureMaxReached.getName(), VALUE_1);
        Address address = new Address(firstAddress, false, thresholdArrayList, geoCodingAgentMock);
        User user = new User(username);
        user.addAddress(address);
        user.setDisableAllAlerts(false);
        userRepository.put(firstLocation, user);
        when(weatherAgentMock.getValuesFromData(firstLocation, API_REQUEST_THRESHOLD_ENUM)).thenReturn(resultWeatherAPI);
        ArgumentCaptor<AlertData> alertDataCaptor = ArgumentCaptor.forClass(AlertData.class);
        //Act
        alertManager.checkAlert();
        //Assert
        verify(alertWriterMock, times(1)).writeAlert(alertDataCaptor.capture());
        AlertData capturedAlertData = alertDataCaptor.getValue();
        assertEquals(thresholdTemperatureMaxReached.getName(), capturedAlertData.getThresholdEnum());
    }


    @DisplayName("Two users have the same time for the same address when writing an alert")
    @Test
    void should_have_the_same_time_for_the_same_address_when_writing_alert() throws WeatherException, InterruptedException {
        // Arrange
        ArrayList<Threshold> thresholdArrayList = new ArrayList<>(List.of(this.thresholdTemperatureMinReached));
        HashMap<ThresholdEnum, Double> resultWeatherAPI = new HashMap<>();
        resultWeatherAPI.put(this.thresholdTemperatureMinReached.getName(), VALUE_1);
        Address address = new Address(firstAddress, false, thresholdArrayList, this.geoCodingAgentMock);
        User user = new User(username);
        User user2 = new User(username2);
        user.addAddress(address);
        user2.addAddress(address);
        user.setDisableAllAlerts(false);
        this.userRepository.put(this.firstLocation, user);
        this.userRepository.put(this.firstLocation, user2);
        Mockito.when(this.weatherAgentMock.getValuesFromData(this.firstLocation, this.API_REQUEST_THRESHOLD_ENUM)).thenReturn(resultWeatherAPI);
        ArgumentCaptor<AlertData> alertDataCaptor = ArgumentCaptor.forClass(AlertData.class);
        //Act
        this.alertManager.checkAlert();

        //Assert
        Mockito.verify(alertWriterMock, times(2)).writeAlert(alertDataCaptor.capture());
        List<AlertData> alertDataValues = alertDataCaptor.getAllValues();
        assertEquals(alertDataValues.get(0).getDate(), alertDataValues.get(1).getDate());
    }
}
