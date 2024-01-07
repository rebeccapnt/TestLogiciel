package exceptions;

public class GeoCodingException extends RuntimeException{
    public GeoCodingException(String message) {
        super(message);
    }
    public GeoCodingException(String message, Exception cause) {
        super(message, cause);
    }

}
