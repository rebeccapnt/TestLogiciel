package tempguardiantests;

import exceptions.GeoCodingException;
import models.Address;
import models.Location;
import models.Threshold;
import models.enums.ThresholdEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import services.GeoCodingAgent;
import services.IGeoCodingAgent;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class AddressTest {

    private IGeoCodingAgent geoCodingAgentMock;
    private final String FALSE_ADDRESS = "false address";
    private final String firstAddress = "15 Rue de la Paix, Paris";
    private final Location firstLocation = new Location(2.3312846, 48.8695088);

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

    @DisplayName("One call of GeoCodingAgent when we instance a new Address")
    @Test
    void should_call_GeoCodingAgent_one_time_when_instantiate_new_address() throws InterruptedException {
        //Arrange
        when(geoCodingAgentMock.convertAddressToLocation(firstAddress))
                .thenReturn(firstLocation);
        Threshold thresholdRainMinMaxReached = new Threshold(ThresholdEnum.RAIN, Double.MAX_VALUE, Double.MIN_VALUE);
        // Act
        Address address = new Address(firstAddress, false, List.of(thresholdRainMinMaxReached), geoCodingAgentMock);
        //Assert
        verify(geoCodingAgentMock, times(1)).convertAddressToLocation(firstAddress);
    }


}
