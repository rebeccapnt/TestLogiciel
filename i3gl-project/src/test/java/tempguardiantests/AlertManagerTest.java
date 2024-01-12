package tempguardiantests;

import managers.AlertManager;
import models.Address;
import models.AlertData;
import models.Threshold;
import models.User;
import models.enums.ThresholdEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import repositories.UserRepository;
import services.AlertWriter;
import services.IAlertWriter;
import services.IWeatherAgent;
import services.WeatherAgent;

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
    private final Double VALUE_MIN = 0.0;
    private final Double VALUE_MAX = 20.0;
    private final String firstAddress = "2 Rue du Pré Nian, 44000 Nantes";
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
    void should_not_alert_sent_when_test_alert_disabled_for_user_adresses_enabled() {
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


}
