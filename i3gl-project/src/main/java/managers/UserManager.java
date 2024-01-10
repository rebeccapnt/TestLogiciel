package managers;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import exceptions.WeatherDataException;
import models.Address;
import models.Threshold;
import models.User;
import models.enums.ThresholdEnum;
import repositories.UserRepository;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserManager {

    private final UserRepository userRepository;
    public UserManager(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void loadDataFromCSV(String fileName) throws WeatherDataException {
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            List<String[]> lines = reader.readAll();

            for (int i = 1; i < lines.size(); i++) {
                String[] parts = lines.get(i);
                if (parts.length == 8) {
                    String username = parts[0].trim();
                    String addressValue = parts[1].trim();

                    Double minRain = parseDoubleWithEmptyCheck(parts[2].trim());
                    Double maxRain = parseDoubleWithEmptyCheck(parts[3].trim());
                    Double minWind = parseDoubleWithEmptyCheck(parts[4].trim());
                    Double maxWind = parseDoubleWithEmptyCheck(parts[5].trim());
                    Double minTemp = parseDoubleWithEmptyCheck(parts[6].trim());
                    Double maxTemp = parseDoubleWithEmptyCheck(parts[7].trim());

                    User user = new User(username);
                    List<Threshold> thresholds = new ArrayList<>();
                    thresholds.add(new Threshold(ThresholdEnum.RAIN, minRain, maxRain));
                    thresholds.add(new Threshold(ThresholdEnum.WIND, minWind, maxWind));
                    thresholds.add(new Threshold(ThresholdEnum.TEMPERATURE, minTemp, maxTemp));
                    Address address = new Address(addressValue,false, thresholds);
                    user.addAddress(address);

                    userRepository.put(address.getLocation(),user);

                } else {
                    throw new WeatherDataException("Invalid line in CSV: " + String.join(",", parts));
                }
            }
        } catch (IOException | CsvException | NumberFormatException e) {
           throw new WeatherDataException("Error when reading file", e);
        }
    }

    private double parseDoubleWithEmptyCheck(String value) {
        if (value.isEmpty()) {
            return Double.NaN;
        } else {
            return Double.parseDouble(value);
        }
    }
}
