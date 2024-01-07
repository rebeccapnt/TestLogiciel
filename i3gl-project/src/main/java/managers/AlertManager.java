package managers;

import com.opencsv.CSVWriter;
import models.AlertData;

import java.io.FileWriter;
import java.io.IOException;

public class AlertManager {
    private static final String ALERT_PATH = "alert.csv";

    /**
     * Write the content of the alert in a CSV file
     * @param alertData model with the data of the alert such as the user, the threshold...
     * @throws IOException exception threw if there is an error during the writing
     */
    public void writeAlert(AlertData alertData) throws IOException{
        try (CSVWriter writer = new CSVWriter(new FileWriter(ALERT_PATH, true))) {
            String[] data = {
                    alertData.user.toString(),
                    alertData.thresholdEnum.toString(),
                    alertData.hour.toString(),
                    String.valueOf(alertData.valueMeasured),
                    String.valueOf(alertData.thresholdReached)
            };
            writer.writeNext(data);
        }
    }
}
