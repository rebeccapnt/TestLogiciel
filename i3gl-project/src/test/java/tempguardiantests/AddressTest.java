package tempguardiantests;

import exceptions.GeoCodingException;
import models.Address;
import models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import services.GeoCodingAgent;
import services.IGeoCodingAgent;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AddressTest {

    private IGeoCodingAgent geoCodingAgentMock;
    private final String FALSE_ADDRESS = "false address";

    @BeforeEach
    void setUp() {
        geoCodingAgentMock = mock(IGeoCodingAgent.class);
    }
    @DisplayName("Add address with an invalid value with GeoCondingAgent mock")
    @Test
    void should_throw_exception_for_invalid_address_with_geoCodingAgent_mock() throws InterruptedException {
        //Arrange
        when(geoCodingAgentMock.convertAddressToLocation(FALSE_ADDRESS))
                .thenThrow(new GeoCodingException("Error converting address to location"));
        // Act & Assert
        assertThrows(GeoCodingException.class, () -> new Address(FALSE_ADDRESS, true, null, new GeoCodingAgent()));
    }

}
