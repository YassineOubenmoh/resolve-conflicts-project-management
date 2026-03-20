package ma.inwi.msproject.exceptions;

public class RequiredActionNotFoundException extends RuntimeException {
    public RequiredActionNotFoundException(String message) {
        super(message);
    }
}
