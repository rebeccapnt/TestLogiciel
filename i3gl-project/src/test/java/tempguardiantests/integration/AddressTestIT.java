package tempguardiantests.integration;

import exceptions.GeoCodingException;
import models.Address;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import services.GeoCodingAgent;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class AddressTestIT {


    @DisplayName("Add address with an invalid value with GeoCoding Agent")
    @Test
    void should_throw_exception_for_invalid_address_with_geocoding_agent() {
        // Act & Assert
        assertThrows(GeoCodingException.class, () -> new Address("false address", true, null, new GeoCodingAgent()));
    }

}
