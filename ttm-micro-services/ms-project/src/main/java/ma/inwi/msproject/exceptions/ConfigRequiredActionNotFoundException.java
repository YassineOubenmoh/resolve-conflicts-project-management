package ma.inwi.msproject.exceptions;

public class ConfigRequiredActionNotFoundException extends RuntimeException {
    public ConfigRequiredActionNotFoundException(String message) {
        super(message);
    }
}
