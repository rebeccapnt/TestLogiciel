package services;

import com.opencsv.CSVWriter;
import exceptions.AlertException;
import models.AlertData;

import java.io.FileWriter;
import java.io.IOException;

public class AlertWriter implements IAlertWriter{
    private static final String ALERT_PATH = "data/alert.csv";

    /**
     * Write the content of the alert in a CSV file
     *
     * @param alertData model with the data of the alert such as the user, the threshold...
     */
    public void writeAlert(AlertData alertData) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(ALERT_PATH, true))) {
            String[] data = {
                    alertData.getUser().getUsername(),
                    alertData.getThresholdEnum().toString(),
                    alertData.getHour(),
                    alertData.getDate(),
                    String.valueOf(alertData.getValueMeasured()),
                    String.valueOf(alertData.getThresholdReached())
            };
            writer.writeNext(data);
        }
        catch (IOException e){
            throw new AlertException("Error when writing alert", e);
        }
    }
}
