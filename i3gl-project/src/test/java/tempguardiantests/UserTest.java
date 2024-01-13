package tempguardiantests;

import exceptions.GeoCodingException;
import exceptions.UserException;
import models.Address;
import models.Location;
import models.Threshold;
import models.User;
import models.enums.ThresholdEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import services.GeoCodingAgent;
import services.IGeoCodingAgent;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserTest {
	private User user;
	private Address address;
	private Location location;
	private IGeoCodingAgent geoCodingAgentMock;

	@BeforeEach
	void setUp() throws InterruptedException {
		user = new User("sophieP");
		this.geoCodingAgentMock = mock(GeoCodingAgent.class);
		String addressName = "108 rue Rivay, Levallois-Perret 92300";
		location = new Location(2.2857371, 48.8991715);
		when(geoCodingAgentMock.convertAddressToLocation(addressName))
				.thenReturn(location);
		address = new Address(addressName, false, null, geoCodingAgentMock);
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
	void should_not_add_duplicate_address_for_user() throws InterruptedException {
		// Arrange
		String addressName1And2 = "2 passage Marie Rogissart, Paris 75012";
		Location location1And2 = new Location(2.3855299, 48.8473525);
		when(geoCodingAgentMock.convertAddressToLocation(addressName1And2))
				.thenReturn(location1And2);
		Address address1 = new Address(addressName1And2, false, null, new GeoCodingAgent());
		Address address2 = new Address(addressName1And2, false, null, new GeoCodingAgent());


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
	void should_add_address_with_valid_thresholds() throws InterruptedException {
		// Arrange
		String addressValid = "2 passage Marie Rogissart, Paris 75012";
		Location locationValid = new Location(2.3855299, 48.8473525);
		Threshold temperature = new Threshold(ThresholdEnum.TEMPERATURE, 0.2, 24.0);
		Threshold rain = new Threshold(ThresholdEnum.RAIN, 0.2, 1.8);
		when(geoCodingAgentMock.convertAddressToLocation(addressValid))
				.thenReturn(locationValid);
		Address addressValidThreshold = new Address(addressValid, false, List.of(temperature, rain), new GeoCodingAgent());

		// Act
		user.addAddress(addressValidThreshold);

		// Assert
		assertFalse(addressValidThreshold.isDisableAlerts());
		assertEquals(1, user.getAddresses().size());
	}

}
