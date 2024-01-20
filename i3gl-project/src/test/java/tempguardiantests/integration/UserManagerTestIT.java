package tempguardiantests.integration;

import managers.UserManager;
import models.Location;
import models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repositories.UserRepository;
import exceptions.WeatherDataException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class UserManagerTestIT {

    private UserManager userManager;
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        // Arrange
        userRepository = new UserRepository();
        userManager = new UserManager(userRepository);
    }

    @Test
    public void should_test_load_data_from_CSV() throws WeatherDataException {
        // Arrange
        String testFile = "data/testfile.csv";

        // Act
        userManager.loadDataFromCSV(testFile);

        // Assert
        Map<Location, ArrayList<User>> usersWithLocation = userRepository.getAllLocationsWithUsers();
        assertNotNull(usersWithLocation);
        assertFalse(usersWithLocation.isEmpty());
    }
}
