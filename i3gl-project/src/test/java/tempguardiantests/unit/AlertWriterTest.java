package tempguardiantests.unit;

import exceptions.WeatherException;
import managers.AlertManager;
import models.Address;
import models.AlertData;
import models.Threshold;
import models.User;
import models.enums.ThresholdEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import repositories.UserRepository;
import services.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AlertWriterTest {

    String ALERT_PATH = "data/alert.csv";
    private final int INDEX_CSV_THRESHOLD_REACHED = 5;
    private final int INDEX_CSV_THRESHOLD_MEASURED = 4;
    private final int INDEX_CSV_THRESHOLD_NAME = 1;
    private final int INDEX_CSV_USERNAME = 0;
    private final int INDEX_CSV_DATE = 3;
    private final int INDEX_CSV_HOUR = 2;
    private AlertWriter alertWriter;
    private AlertData alertData;
    private String username;
    private ThresholdEnum thresholdEnum;
    private Instant instant;
    private double valueMeasured;
    private double thresholdReached;
    private User user;

    @BeforeEach
    void setUp() {
        alertWriter = new AlertWriter();
        LocalDate localDate = LocalDate.of(2001, 2, 18);
        instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        instant = instant.plusSeconds(10);
        username = "Tintin";
        thresholdEnum = ThresholdEnum.RAIN;
        valueMeasured = 10;
        thresholdReached = 20;
        user = new User(username);
        alertData = new AlertData(user, thresholdEnum, instant, valueMeasured, thresholdReached);
        removeCSVIfExist();
    }

    private String[] readLineCSV(int lineNumber) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(ALERT_PATH))) {
            String line;
            for (int currentLine = 0; (line = bufferedReader.readLine()) != null && currentLine <= lineNumber; ++currentLine) {
                if (currentLine == lineNumber) {
                    return line.split(",");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String[0];
    }

    private void removeCSVIfExist(){
        Path filePath = Paths.get(ALERT_PATH);
        if (Files.exists(filePath)) {
            try {
                Files.delete(filePath);
            } catch (IOException e) {
                System.err.println("Error while deleting the CSV file: " + e.getMessage());
            }
        } else {
            System.out.println("The CSV file does not exist.");
        }
    }

    @DisplayName("Check correct username written in the csv ")
    @Test
    void should_correct_username_written_in_csv() throws WeatherException, InterruptedException {
        //Act
        alertWriter.writeAlert(alertData);
        //Assert
        String value = readLineCSV(0)[INDEX_CSV_USERNAME];
        String usernameWritten = value.substring(1, value.length()-1);
        assertEquals(username, usernameWritten);
    }

    @DisplayName("Check correct thresholdEnum written in the csv ")
    @Test
    void should_correct_thresholdEnum_written_in_csv() {
        //Act
        alertWriter.writeAlert(alertData);
        //Assert
        String value = readLineCSV(0)[INDEX_CSV_THRESHOLD_NAME];
        String thresholdEnumWritten = value.substring(1, value.length()-1);
        assertEquals(thresholdEnum.toString(), thresholdEnumWritten);
    }

    @DisplayName("Check correct hour written in the csv ")
    @Test
    void should_correct_hour_written_in_csv() {
        //Act
        alertWriter.writeAlert(alertData);
        //Assert
        String value = readLineCSV(0)[INDEX_CSV_HOUR];
        String hourWritten = value.substring(1, value.length()-1);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String hour = localDateTime.format(formatter);

        assertEquals(hour, hourWritten);
    }

    @DisplayName("Check correct date written in the csv")
    @Test
    void should_correct_date_written_in_csv() {
        // Act
        alertWriter.writeAlert(alertData);

        // Assert
        String value = readLineCSV(0)[INDEX_CSV_DATE];
        String dateWritten = value.substring(1, value.length() - 1);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String date = localDateTime.format(formatter);

        assertEquals(date, dateWritten);
    }

    @DisplayName("Check correct threshold measured written in the csv")
    @Test
    void should_correct_threshold_measured_written_in_csv() {
        // Act
        alertWriter.writeAlert(alertData);

        // Assert
        String value = readLineCSV(0)[INDEX_CSV_THRESHOLD_MEASURED];
        double thresholdMeasuredWritten = Double.parseDouble(value.substring(1, value.length()-1));
        assertEquals(valueMeasured, thresholdMeasuredWritten);
    }

    @DisplayName("Check correct threshold reached written in the csv")
    @Test
    void should_correct_threshold_reached_written_in_csv() {
        // Act
        alertWriter.writeAlert(alertData);

        // Assert
        String value = readLineCSV(0)[INDEX_CSV_THRESHOLD_REACHED];
        double thresholdMeasuredWritten = Double.parseDouble(value.substring(1, value.length()-1));
        assertEquals(thresholdReached, thresholdMeasuredWritten);
    }

}
