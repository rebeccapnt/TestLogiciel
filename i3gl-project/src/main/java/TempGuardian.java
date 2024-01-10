import exceptions.WeatherDataException;
import exceptions.WeatherException;
import managers.AlertManager;
import managers.UserManager;
import repositories.UserRepository;
import services.AlertWriter;
import services.WeatherAgent;

import java.io.IOException;
import java.util.Scanner;

public class TempGuardian {
    public static void main(String[] args) throws WeatherDataException, IOException, WeatherException, InterruptedException {

        UserRepository userRepository = new UserRepository();
        UserManager userManager = new UserManager(userRepository);
        WeatherAgent weatherAgent = new WeatherAgent();
        AlertWriter alertWriter = new AlertWriter();

        userManager.loadDataFromCSV("data/weather_data_input.csv");
        AlertManager alertManager = new AlertManager(userRepository, weatherAgent, alertWriter);

        System.out.println("Appuyez sur K pour stopper la vérification des alertes");
        Scanner sc = new Scanner(System.in);
        String name = sc.nextLine();

        do {
            alertManager.checkAlert();
        }while(!name.equalsIgnoreCase("k"));
    }
}
