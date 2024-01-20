package tempguardiantests.unit;

import exceptions.WeatherException;
import models.AlertData;
import models.Location;
import models.User;
import models.enums.ThresholdEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.ZoneId;

public class AlertDataTest {

    @DisplayName("Check if get hour has the correct format")
    @Test
    void should_get_hour_correct_format() throws WeatherException {
        // Arrange
        User user = new User("Sophie");
        int year = 2024;
        int month = 1;
        int day = 19;
        int hour = 12;
        int minute = 30;
        int second = 0;
        LocalDateTime localDateTime = LocalDateTime.of(year, month, day, hour, minute, second);
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        AlertData alertData = new AlertData(user, ThresholdEnum.RAIN, instant, 10, 20);
        // Act
        String hourResult = alertData.getHour();
        // Assert
        assertEquals("12:30:00", hourResult);
    }
}
