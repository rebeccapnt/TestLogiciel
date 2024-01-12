package tempguardiantests;

import exceptions.GeoCodingException;
import exceptions.UserException;
import models.Address;
import models.Threshold;
import models.User;
import models.enums.ThresholdEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
	private User user;
	private Address address;
	@BeforeEach
	void setUp() {
		user = new User("sophieP");
		address = new Address("108 rue Rivay, Levallois-Perret 92300", false, null);
	}

	@DisplayName("Add an address for user")
	@Test
	void should_add_address_for_user() {
		// Act
		user.addAddress(address);

		// Assert
		assertTrue(user.getAddresses().containsValue(address));
	}
	@DisplayName("Disable all alerts for user")
	@Test
	void should_disable_all_alerts_for_user() {
		// Arrange
		assertFalse(user.isDisableAllAlerts());

		// Act
		user.setDisableAllAlerts(true);

		// Assert
		assertTrue(user.isDisableAllAlerts());
	}

	@DisplayName("Add duplicate address for user")
	@Test
	void should_not_add_duplicate_address_for_user() {
		// Arrange
		Address address1 = new Address("2 passage Marie Rogissart, Paris 75012", false, null);
		Address address2 = new Address("2 passage Marie Rogissart, Paris 75012", false, null);

		// Act
		user.addAddress(address1);

		// Assert
		assertEquals(1, user.getAddresses().size());
		assertThrows(UserException.class, () -> user.addAddress(address2));
	}

	@DisplayName("Disable alerts for one address")
	@Test
	void should_disable_alerts_for_address() {
		// Arrange
		user.addAddress(address);

		// Act
		user.disableAlert(address.getLocation());

		// Assert
		assertTrue(address.isDisableAlerts());
	}

	@DisplayName("Add address with valid thresholds")
	@Test
	void should_add_address_with_valid_thresholds() {
		// Arrange
		Threshold temperature = new Threshold(ThresholdEnum.TEMPERATURE, 0.2, 24.0);
		Threshold rain = new Threshold(ThresholdEnum.RAIN, 0.2, 1.8);
		Address addressValidThreshold = new Address("2 passage Marie Rogissart, Paris 75012", false, List.of(temperature, rain));

		// Act
		user.addAddress(addressValidThreshold);

		// Assert
		assertFalse(addressValidThreshold.isDisableAlerts());
		assertEquals(1, user.getAddresses().size());
	}

}
