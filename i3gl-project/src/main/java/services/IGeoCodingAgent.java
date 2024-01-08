package services;

import exceptions.GeoCodingException;
import models.Location;

public interface IGeoCodingAgent {
    Location convertAddressToLocation(String address) throws GeoCodingException, InterruptedException;
}
