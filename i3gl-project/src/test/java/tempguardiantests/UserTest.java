package tempguardiantests;

import models.Address;
import models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
	private User user;
	@BeforeEach
	void setUp() {
		user = new User("sophieP");
	}

	@DisplayName("Add an address for user")
	@Test
	void shouldAddAddressForUser() {
		// Arrange
		Address address = new Address("108 rue Rivay, Levallois-Perret 92300", false, null);

		// Act
		user.addAddress(address);

		// Assert
		assertTrue(user.getAddresses().containsValue(address));
	}
	@DisplayName("Disable all alerts for user")
	@Test
	void shouldDisableAllAlertsForUser() {
		// Arrange
		assertFalse(user.isDisableAllAlerts());

		// Act
		user.setDisableAllAlerts(true);

		// Assert
		assertTrue(user.isDisableAllAlerts());
	}

	@DisplayName("Add duplicate address for user")
	@Test
	void shouldNotAddDuplicateAddressForUser() {
		// Arrange
		Address address1 = new Address("2 passage Marie Rogissart, Paris 75012", false, null);
		Address address2 = new Address("2 passage Marie Rogissart, Paris 75012", false, null);

		// Act
		user.addAddress(address1);
		user.addAddress(address2);

		// Assert
		assertEquals(1, user.getAddresses().size());
		// TODO : Assert Throw ?
	}

	@DisplayName("Disable alerts for one address")
	@Test
	void shouldDisableAlertsForAddress() {
		// Arrange
		Address address = new Address("206 rue de l'Abbé Bonpain, Lille 59000", true, null);
		user.addAddress(address);

		// Act
		user.disableAlert(address.getLocation());

		// Assert
		assertTrue(address.isDisableAlerts());
	}
}
