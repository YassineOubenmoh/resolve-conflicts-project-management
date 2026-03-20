package ma.inwi.msproject.exceptions;

public class UserNotBelongingToDepartmentException extends RuntimeException {
    public UserNotBelongingToDepartmentException(String message) {
        super(message);
    }
}
