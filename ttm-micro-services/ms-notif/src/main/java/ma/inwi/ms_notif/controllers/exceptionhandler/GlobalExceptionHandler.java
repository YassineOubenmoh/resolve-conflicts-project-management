package ma.inwi.ms_notif.controllers.exceptionhandler;

import ma.inwi.ms_notif.exceptions.NotificationNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

//@ControllerAdvice
public class GlobalExceptionHandler {

    /*
    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleNotificationNotFoundException(NotificationNotFoundException ex) {
        ErrorMessage errorResponse = new ErrorMessage(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

     */


}
