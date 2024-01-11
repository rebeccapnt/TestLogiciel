package models;

import java.util.HashMap;

public class User {
    private String username;
    private HashMap<Location, Address> addresses;
    private boolean disableAllAlerts;

    public User(String username) {
        this.username = username;
        this.addresses = new HashMap<>();
        this.disableAllAlerts = false;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public HashMap<Location, Address> getAddresses() {
        return addresses;
    }

    public boolean isDisableAllAlerts() {
        return disableAllAlerts;
    }

    public void setDisableAllAlerts(boolean disableAllAlerts) {
        this.disableAllAlerts = disableAllAlerts;
    }

    public void disableAlert(Location location) {
        if (addresses.containsKey(location)) {
            Address address = addresses.get(location);
            address.setDisableAlerts(true);
        }
        // TODO : Lancer une exception ?
    }

    public void addAddress(Address address) {
        if (!addresses.containsKey(address.getLocation())) {
            this.addresses.put(address.getLocation(), address);
        }
        // TODO : Lancer une exception ?
    }


}
