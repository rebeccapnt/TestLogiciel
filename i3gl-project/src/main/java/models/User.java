package models;

import java.util.ArrayList;
import java.util.List;

public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private List<Address> addresses;
    private boolean allAlertEnable;

    public User(Long id, String firstName, String lastName, boolean allAlertEnable) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.addresses = new ArrayList<>();
        this.allAlertEnable = allAlertEnable;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Address> getAdresses() {
        return addresses;
    }

    public void setAdresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public boolean isAllAlertEnable() {
        return allAlertEnable;
    }

    public void setAllAlertEnable(boolean allAlertEnable) {
        this.allAlertEnable = allAlertEnable;
    }
}
