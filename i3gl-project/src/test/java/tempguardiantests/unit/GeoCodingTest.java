package tempguardiantests.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.GeoCodingAgent;


public class GeoCodingTest {
    private GeoCodingAgent geoCodingAgent;
    private String address = "15 Rue de la Paix Paris";

    /*
    @BeforeEach
    void setUp() {
        geoCodingAgent = new GeoCodingAgent();
    }

    @Test
    public void should_wait_two_second_for_converting_address_to_location() throws InterruptedException {
        // Act
        long beginTime = System.currentTimeMillis();
        geoCodingAgent.convertAddressToLocation(address);
        long endTime = System.currentTimeMillis();
        long timeExecution = endTime - beginTime;
        long time = 2000;

        // Assert
        assert( timeExecution >= time);

    }
*/
}
