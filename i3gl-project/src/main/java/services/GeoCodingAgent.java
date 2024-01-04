package services;

import java.io.IOException;

public interface GeoCodingAgent {
    Double[] convertAddressToLocation(String address) throws IOException;
}
