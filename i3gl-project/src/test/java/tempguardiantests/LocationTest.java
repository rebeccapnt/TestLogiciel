package tempguardiantests;

import exceptions.UserException;
import models.Address;
import models.Location;
import models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import services.GeoCodingAgent;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LocationTest {
    private Location firstLocation;
    private Location secondLocationSameLongDiffLat;

    @BeforeEach
    void setUp() {
        firstLocation = new Location(10, 11);
        secondLocationSameLongDiffLat = new Location(10, 12);
    }

    @DisplayName("Location with same references are equals")
    @Test
    void should_be_equal_if_locations_have_the_same_reference() throws InterruptedException {
        // Act & Assert
        assertTrue(firstLocation.equals(firstLocation));
    }

    @DisplayName("Use equal with null reference")
    @Test
    void should_test_return_value_false_equal_with_null_reference() throws InterruptedException {
        // Act & Assert
        assertTrue(!firstLocation.equals(null));

    }

    @DisplayName("Use equal with other type of object")
    @Test
    void should_test_return_value_false_equal_with_other_type_of_object() throws InterruptedException {
        // Act & Assert
        assertTrue(!firstLocation.equals(Integer.valueOf("10")));
    }

    @DisplayName("Two location not equal if only latitude is different")
    @Test
    void should_test_two_locations_are_not_equal_with_only_latitude_different() throws InterruptedException {
        // Act & Assert
        assertTrue(!firstLocation.equals(secondLocationSameLongDiffLat));
    }

}
