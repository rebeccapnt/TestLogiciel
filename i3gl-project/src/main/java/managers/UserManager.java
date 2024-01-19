package managers;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import exceptions.WeatherDataException;
import models.Address;
import models.Threshold;
import models.User;
import models.enums.ThresholdEnum;
import repositories.UserRepository;
import services.GeoCodingAgent;
import services.IGeoCodingAgent;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for managing user data loaded from a CSV file.
 */
public class UserManager {

    private final UserRepository userRepository;
    private final IGeoCodingAgent geoCodingAgent;
    public UserManager(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.geoCodingAgent = new GeoCodingAgent();
    }

    /**
     * Load user data from a CSV file and add it to the UserRepository.
     *
     * @param fileName                : path to the CSV file containing user data.
     * @throws WeatherDataException   : issue with the weather data in the CSV file.
     */
    public void loadDataFromCSV(String fileName) throws WeatherDataException {
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            List<String[]> lines = reader.readAll();

            for (int i = 1; i < lines.size(); i++) {
                String[] parts = lines.get(i);
                if (parts.length == 8) {
                    String username = parts[0];
                    String addressValue = parts[1];

                    Double minRain = parseDoubleWithEmptyCheck(parts[2]);
                    Double maxRain = parseDoubleWithEmptyCheck(parts[3]);
                    Double minWind = parseDoubleWithEmptyCheck(parts[4]);
                    Double maxWind = parseDoubleWithEmptyCheck(parts[5]);
                    Double minTemp = parseDoubleWithEmptyCheck(parts[6]);
                    Double maxTemp = parseDoubleWithEmptyCheck(parts[7]);

                    User user = new User(username);
                    List<Threshold> thresholds = new ArrayList<>();
                    thresholds.add(new Threshold(ThresholdEnum.RAIN, minRain, maxRain));
                    thresholds.add(new Threshold(ThresholdEnum.WIND, minWind, maxWind));
                    thresholds.add(new Threshold(ThresholdEnum.TEMPERATURE, minTemp, maxTemp));
                    Address address = new Address(addressValue,false, thresholds, geoCodingAgent);
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

    /**
     * Parse a string to a double, if empty value, return a Double.NaN.
     *
     * @param value    : string value to parse.
     * @return         : parsed value.
     */
    private double parseDoubleWithEmptyCheck(String value) {
        if (value.isEmpty()) {
            return Double.NaN;
        } else {
            return Double.parseDouble(value);
        }
    }
}
