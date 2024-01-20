package tempguardiantests.unit;


import exceptions.WeatherException;
import models.Location;
import models.enums.ThresholdEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import services.WeatherAgent;

import java.util.ArrayList;
import java.util.List;

public class WeatherAgentTest {
    private WeatherAgent weatherAgent;

    /*
    @BeforeEach
    void setUp() {
        weatherAgent = new WeatherAgent();
    }
    @DisplayName("Check time to make request to API inferior 1 second")
    @Test
    void should_wait_two_second_for_weather_agent() throws WeatherException {
        // Arrange
        Location location = new Location(2, 48);
        ThresholdEnum thresholdEnum= ThresholdEnum.TEMPERATURE;
        ArrayList<ThresholdEnum> thresholdEnums= new ArrayList<>(List.of(thresholdEnum));
        // Act
        long beginTime = System.currentTimeMillis();
        weatherAgent.getValuesFromData(location, thresholdEnums);
        long endTime = System.currentTimeMillis();
        long timeExecution = endTime - beginTime;
        long time = 2000;
        // Assert
        assert(timeExecution >= time);
    }*/
}
