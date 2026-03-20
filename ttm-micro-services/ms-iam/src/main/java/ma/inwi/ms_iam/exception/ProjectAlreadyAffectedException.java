package ma.inwi.ms_iam.exception;

public class ProjectAlreadyAffectedException extends RuntimeException {
    public ProjectAlreadyAffectedException(String message) {
        super(message);
    }
}
