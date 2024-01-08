import exceptions.WeatherDataException;
import managers.UserManager;

public class TempGuardian {
    public static void main(String[] args) throws WeatherDataException {
        UserManager userManager = new UserManager();
        userManager.loadDataFromCSV("data/weather_data_input.csv");
    }
}
