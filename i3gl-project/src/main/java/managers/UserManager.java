package managers;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import models.Address;
import models.Location;
import models.User;
import repositories.UserRepository;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class UserManager {
    private UserRepository userRepository;

    public UserManager(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void loadDataFromCSV(String filePath) {
        try (CSVReader reader = new CSVReader(new FileReader("/data/weather_data_input.csv"))) {
            List<String[]> lines = reader.readAll();

            for (int i = 1; i < lines.size(); i++) {
                String[] parts = lines.get(i);
                if (parts.length == 8) {
                    String username = parts[0].trim();
                    String addressString = parts[1].trim();
                    double minRain = Double.parseDouble(parts[2].trim());
                    double maxRain = Double.parseDouble(parts[3].trim());
                    double minWind = Double.parseDouble(parts[4].trim());
                    double maxWind = Double.parseDouble(parts[5].trim());
                    double minTemp = Double.parseDouble(parts[6].trim());
                    double maxTemp = Double.parseDouble(parts[7].trim());
                    // TODO : créer et ajouter au repository.

                } else {
                    System.err.println("Invalid line in CSV: " + String.join(",", parts));
                }
            }
        } catch (IOException | CsvException | NumberFormatException e) {
            e.printStackTrace(); // Handle exceptions appropriately in your application
        }
    }
}
