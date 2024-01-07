package exceptions;

public class WeatherDataException extends Exception {
    public WeatherDataException(String message) {
        super(message);
    }

    public WeatherDataException(String message, Exception cause) {
        super(message, cause);
    }
}
