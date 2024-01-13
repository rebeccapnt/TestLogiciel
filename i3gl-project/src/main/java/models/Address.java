package models;

import exceptions.GeoCodingException;
import services.GeoCodingAgent;
import services.IGeoCodingAgent;

import java.util.List;
import java.util.Objects;

public class Address {
    private final String value;
    private boolean disableAlerts;
    private Location location;
    private List<Threshold> thresholds;
    private final IGeoCodingAgent geoCodingAgent;

    public Address(String value, boolean disableAlerts, List<Threshold> thresholds) {
        this.value = value;
        this.disableAlerts = disableAlerts;
        this.thresholds = thresholds;
        this.geoCodingAgent = new GeoCodingAgent();
        updateLocation();
    }

    public String getValue() {
        return value;
    }

    public boolean isDisableAlerts() {
        return disableAlerts;
    }

    public void setDisableAlerts(boolean disableAlerts) {
        this.disableAlerts = disableAlerts;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<Threshold> getThresholds() {
        return thresholds;
    }

    public void setThresholds(List<Threshold> thresholds) {
        this.thresholds = thresholds;
    }

    public void updateLocation() throws GeoCodingException {
        try {
            this.location = geoCodingAgent.convertAddressToLocation(value);
        } catch (GeoCodingException | InterruptedException e) {
            throw new GeoCodingException("Error converting address to location: ", e);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Address address = (Address) obj;
        return value.equals(address.value) && location.equals(address.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, location);
    }

}
