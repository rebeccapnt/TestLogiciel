package exceptions;

public class AlertException extends RuntimeException {
        public AlertException(String message) {
            super(message);
        }
        public AlertException(String message, Exception cause) {
            super(message, cause);
        }
}
