package tempguardiantests;

import exceptions.WeatherDataException;
import managers.UserManager;
import models.Address;
import models.Location;
import models.Threshold;
import models.User;
import models.enums.ThresholdEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import repositories.UserRepository;
import services.GeoCodingAgent;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserManagerTest {
    private UserManager userManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GeoCodingAgent geoCodingAgent;

    @BeforeEach
    public void setUp() {
        userRepository = mock(UserRepository.class);
        userManager = new UserManager(userRepository);
    }

    @Test
    public void should_throw_exception_for_invalid_number_format() throws IOException {
        // Arrange
        File tempFile = File.createTempFile("test", ".csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("username,address,minRain,maxRain,minWind,maxWind,minTemp,maxTemp\n");
            writer.write("john_doe,15 Rue de la Paix Paris,not_a_number,15.2,10,20,12,28\n");
        }

        // Act & Assert
        assertThrows(WeatherDataException.class, () -> userManager.loadDataFromCSV(tempFile.getAbsolutePath()));

        // Cleanup
        tempFile.delete();
    }

    @Test
    public void should_correctly_load_CSV_data() throws IOException, WeatherDataException {
        // Arrange
        File tempFile = File.createTempFile("test", ".csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("username,address,minRain,maxRain,minWind,maxWind,minTemp,maxTemp\n");
            writer.write("john_doe,15 Rue de la Paix Paris,5.7,15.2,10,20,12,28\n");        }

        // Act
        userManager.loadDataFromCSV(tempFile.getAbsolutePath());

        // Assert
        ArgumentCaptor<User> user = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).put(any(), user.capture());
        User capturedUser = user.getValue();
        Address capturedAddress = capturedUser.getAddresses().values().iterator().next();
        assertEquals("15 Rue de la Paix Paris", capturedAddress.getValue());

        Threshold rainThreshold = capturedAddress.getThresholds().stream()
                .filter(t -> t.getName() == ThresholdEnum.RAIN)
                .findFirst().orElseThrow();
        Threshold windThreshold = capturedAddress.getThresholds().stream()
                .filter(t -> t.getName() == ThresholdEnum.WIND)
                .findFirst().orElseThrow();
        Threshold tempThreshold = capturedAddress.getThresholds().stream()
                .filter(t -> t.getName() == ThresholdEnum.TEMPERATURE)
                .findFirst().orElseThrow();
        assertEquals("john_doe", capturedUser.getUsername());
        assertEquals("15 Rue de la Paix Paris", capturedAddress.getValue());
        assertEquals(capturedAddress.getLocation(), new Location(2.3312846, 48.8695088));
        assertEquals(5.7, rainThreshold.getMinThreshold());
        assertEquals(15.2, rainThreshold.getMaxThreshold());
        assertEquals(10, windThreshold.getMinThreshold());
        assertEquals(20, windThreshold.getMaxThreshold());
        assertEquals(12, tempThreshold.getMinThreshold());
        assertEquals(28, tempThreshold.getMaxThreshold());

        // Cleanup
        tempFile.delete();
    }

    @Test
    public void should_handle_NaN_correctly_in_parseDoubleWithEmptyCheck() throws IOException, WeatherDataException {
        // Arrange
        File tempFile = File.createTempFile("test", ".csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("username,address,minRain,maxRain,minWind,maxWind,minTemp,maxTemp\n");
            writer.write("john_doe,15 Rue de la Paix Paris,,,10,20,12,28\n");
        }

        // Act
        userManager.loadDataFromCSV(tempFile.getAbsolutePath());

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).put(any(), userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        Address capturedAddress = capturedUser.getAddresses().values().iterator().next();
        Threshold rainThreshold = capturedAddress.getThresholds().stream()
                .filter(t -> t.getName() == ThresholdEnum.RAIN)
                .findFirst().orElseThrow();
        assertEquals(Double.NaN, rainThreshold.getMinThreshold());
        assertEquals(Double.NaN, rainThreshold.getMaxThreshold());

        // Cleanup
        tempFile.delete();
    }

    @Test
    public void should_correctly_use_location_in_userRepository_put() throws IOException, WeatherDataException {
        // Arrange
        File tempFile = File.createTempFile("test", ".csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("username,address,minRain,maxRain,minWind,maxWind,minTemp,maxTemp\n");
            writer.write("john_doe,15 Rue de la Paix Paris,,,10,20,12,28\n");
        }

        // Act
        userManager.loadDataFromCSV(tempFile.getAbsolutePath());

        // Assert
        ArgumentCaptor<Location> locationCaptor = ArgumentCaptor.forClass(Location.class);
        verify(userRepository, times(1)).put(locationCaptor.capture(), any(User.class));
        assertEquals(locationCaptor.getValue(), new Location(2.3312846, 48.8695088));

        // Cleanup
        tempFile.delete();
    }

    @Test
    public void should_throw_exception_with_detailed_message_for_invalid_lines() throws IOException, WeatherDataException {
        // Arrange
        File tempFile = File.createTempFile("test", ".csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("username,address,minRain,maxRain,minWind,maxWind,minTemp,maxTemp\n");
            writer.write("john_doe,15 Rue de la Paix Paris10,20,12,28\n");
        }

        // Act & Assert
        WeatherDataException thrownException = assertThrows(WeatherDataException.class, () -> userManager.loadDataFromCSV(tempFile.getAbsolutePath()));
        assertTrue(thrownException.getMessage().contains("Invalid line in CSV: john_doe,15 Rue de la Paix Paris10,20,12,28"));

        // Cleanup
        tempFile.delete();
    }

}
