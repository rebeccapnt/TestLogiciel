package exceptions;

public class UserException extends RuntimeException {
        public UserException(String message) {
            super(message);
        }
        public UserException(String message, Exception cause) {
            super(message, cause);
        }
}
