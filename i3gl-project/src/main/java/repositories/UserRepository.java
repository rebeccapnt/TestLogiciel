package repositories;

import models.Location;
import models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepository {
    private HashMap<Location, ArrayList<User>> addresses;

    public UserRepository() {
        this.addresses = new HashMap<>();
    }

    public Map<Location, ArrayList<User>> getAllLocationsWithUsers() {
        Map<Location, ArrayList<User>> result = new HashMap<>();

        for (Map.Entry<Location, ArrayList<User>> entry : addresses.entrySet()) {
            Location location = entry.getKey();
            ArrayList<User> users = entry.getValue();

            result.put(location, new ArrayList<>(users));
        }

        return result;
    }

    public void put(Location location, User user) {
        if (addresses.containsKey(location)) {
            addresses.get(location).add(user);
        } else {
            addresses.put(location, new ArrayList<>(List.of(user)));
        }
    }
}
