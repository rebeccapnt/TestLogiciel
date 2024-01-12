package tempguardiantests;

import exceptions.GeoCodingException;
import models.Address;
import models.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class AddressTest {

    @DisplayName("Add address with an invalid value")
    @Test
    void should_throw_exception_for_invalid_address() {
        // Act & Assert
        assertThrows(GeoCodingException.class, () -> new Address("false address", true, null));
    }

}
