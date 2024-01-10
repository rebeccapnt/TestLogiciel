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

    public void addAddress(Address address) {
        if(!addresses.containsValue(address)){
            this.addresses.put(address.getLocation(), address);
        }
    }

    public boolean isDisableAllAlerts() {
        return disableAllAlerts;
    }

    public void setDisableAllAlerts(boolean disableAllAlerts) {
        this.disableAllAlerts = disableAllAlerts;
    }
}
