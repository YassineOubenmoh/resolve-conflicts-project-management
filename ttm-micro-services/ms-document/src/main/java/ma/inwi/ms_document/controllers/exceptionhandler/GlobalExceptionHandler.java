package ma.inwi.ms_document.controllers.exceptionhandler;

import ma.inwi.ms_document.exceptions.DocumentNotFoundException;
import ma.inwi.ms_document.exceptions.UnauthorizedExtensionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedExtensionException.class)
    public ResponseEntity<ErrorMessage> handleUnauthorizedExtensionException(UnauthorizedExtensionException ex) {
        ErrorMessage errorResponse = new ErrorMessage(ex.getMessage(), 400);
        return ResponseEntity.ok(errorResponse);
    }

    @ExceptionHandler(DocumentNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleDocumentNotFoundException(DocumentNotFoundException ex) {
        ErrorMessage errorResponse = new ErrorMessage(ex.getMessage(), 400);
        return ResponseEntity.ok(errorResponse);
    }

}
