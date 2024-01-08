package managers;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import exceptions.WeatherDataException;
import models.Address;
import models.Threshold;
import models.User;
import models.enums.ThresholdEnum;
import repositories.UserRepository;
import services.IGeoCodingAgent;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private final UserRepository userRepository;
    public UserManager() {
        this.userRepository = new UserRepository();
    }

    public void loadDataFromCSV() throws WeatherDataException {
        System.out.println("Current working directory: " + System.getProperty("user.dir"));
        try (CSVReader reader = new CSVReader(new FileReader(System.getProperty("user.dir") + "/src/main/java/managers/weather_data_input.csv"))) {
            List<String[]> lines = reader.readAll();

            for (int i = 1; i < lines.size(); i++) {
                String[] parts = lines.get(i);
                if (parts.length == 8) {
                    String username = parts[0].trim();
                    String addressValue = parts[1].trim();
                    double minRain = Double.parseDouble(parts[2].trim());
                    double maxRain = Double.parseDouble(parts[3].trim());
                    double minWind = Double.parseDouble(parts[4].trim());
                    double maxWind = Double.parseDouble(parts[5].trim());
                    double minTemp = Double.parseDouble(parts[6].trim());
                    double maxTemp = Double.parseDouble(parts[7].trim());

                    User user = new User(username);
                    List<Threshold> thresholds = new ArrayList<>();
                    thresholds.add(new Threshold(ThresholdEnum.RAIN, minRain, maxRain));
                    thresholds.add(new Threshold(ThresholdEnum.WIND, minWind, maxWind));
                    thresholds.add(new Threshold(ThresholdEnum.TEMPERATURE, minTemp, maxTemp));
                    Address address = new Address(addressValue,false, thresholds);

                    userRepository.put(address.getLocation(),user);

                } else {
                    throw new WeatherDataException("Invalid line in CSV: " + String.join(",", parts));
                }
            }
        } catch (IOException | CsvException | NumberFormatException e) {
           throw new WeatherDataException("Error" + e);
        }
    }
}
