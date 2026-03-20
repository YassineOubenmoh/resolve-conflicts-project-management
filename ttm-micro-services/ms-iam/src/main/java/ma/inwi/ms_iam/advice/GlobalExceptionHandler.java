package ma.inwi.ms_iam.advice;

import ma.inwi.ms_iam.exception.ProjectAlreadyAffectedException;
import ma.inwi.ms_iam.exception.SignUpBadRequestException;
import ma.inwi.ms_iam.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {

        // Return exception message with HTTP 404 status code
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SignUpBadRequestException.class)
    public ResponseEntity<String> handleSignUpException(SignUpBadRequestException ex) {

        // Return exception message with HTTP 400 status code
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(ProjectAlreadyAffectedException.class)
    public ResponseEntity<String> handleProjectAlreadyAffectedException(ProjectAlreadyAffectedException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }


}
