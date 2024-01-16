package tempguardiantests;

import exceptions.WeatherDataException;
import managers.UserManager;
import models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import repositories.UserRepository;
import services.GeoCodingAgent;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
    public void should_load_data_from_CSV_with_valid_data() throws Exception {
        // Arrange
        File tempFile = File.createTempFile("test", ".csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("username,address,minRain,maxRain,minWind,maxWind,minTemp,maxTemp\n");
            writer.write("john_doe,15 Rue de la Paix Paris,5.7,15.2,10,20,12,28\n");
        }

        // Act
        userManager.loadDataFromCSV(tempFile.getAbsolutePath());

        // Assert
        verify(userRepository, times(1)).put(any(), any(User.class));

        tempFile.delete();
    }

    @Test
    public void should_load_data_from_CSV_with_invalid_data() throws IOException {
        // Arrange
        File tempFile = File.createTempFile("test", ".csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("username,address,minRain,maxRain,minWind,maxWind,minTemp,maxTemp\n");
            writer.write("john_doe,2 passage marie Rogissart Paris,invalidValue,15.2,10,20,12,28\n");
        }

        // Act & Assert
        assertThrows(WeatherDataException.class, () -> userManager.loadDataFromCSV(tempFile.getAbsolutePath()));

        tempFile.delete();
    }

    @Test
    public void should_throw_exception_when_reading_CSV_with_invalid_lines() throws IOException {
        // Arrange
        File tempFile = File.createTempFile("test", ".csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("username,address,minRain,maxRain,minWind,maxWind,minTemp,maxTemp\n");
            writer.write("john_doe,2 passage marie Rogissart Paris,15.2,10,20,12,28\n");
        }

        // Act & Assert
        assertThrows(WeatherDataException.class, () -> userManager.loadDataFromCSV(tempFile.getAbsolutePath()));

        tempFile.delete();
    }
}
