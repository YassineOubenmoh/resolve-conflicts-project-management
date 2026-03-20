package ma.inwi.msproject.exceptions;

public class ProjectAlreadyExistingException extends RuntimeException {
    public ProjectAlreadyExistingException(String message) {
        super(message);
    }
}
