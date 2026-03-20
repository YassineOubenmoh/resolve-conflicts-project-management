package ma.inwi.msproject.controller.exceptionhandler;

import ma.inwi.msproject.exceptions.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GateNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleGateNotFoundException(GateNotFoundException ex) {
        return ResponseEntity.ok(new ErrorMessage(ex.getMessage(), 400));
    }

    @ExceptionHandler(GateProjectNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleGateProjectNotFoundException(GateProjectNotFoundException ex) {
        return ResponseEntity.ok(new ErrorMessage(ex.getMessage(), 400));
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleProjectNotFoundException(ProjectNotFoundException ex) {
        return ResponseEntity.ok(new ErrorMessage(ex.getMessage(), 400));
    }

    @ExceptionHandler(ActionNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleActionNotFoundException(ActionNotFoundException ex) {
        return ResponseEntity.ok(new ErrorMessage(ex.getMessage(), 400));
    }

    @ExceptionHandler(DepartementGateProjectNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleDepartementGateProjectNotFoundException(DepartementGateProjectNotFoundException ex) {
        return ResponseEntity.ok(new ErrorMessage(ex.getMessage(), 400));
    }

    @ExceptionHandler(DepartementNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleDepartementNotFoundException(DepartementNotFoundException ex) {
        return ResponseEntity.ok(new ErrorMessage(ex.getMessage(), 400));
    }

    @ExceptionHandler(RequiredActionNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleRequiredActionNotFoundException(RequiredActionNotFoundException ex) {
        return ResponseEntity.ok(new ErrorMessage(ex.getMessage(), 400));
    }

    @ExceptionHandler(TrackingNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleTrackingNotFoundException(TrackingNotFoundException ex) {
        return ResponseEntity.ok(new ErrorMessage(ex.getMessage(), 400));
    }


    @ExceptionHandler(UnauthorizedPassageToNextGateException.class)
    public ResponseEntity<ErrorMessage> handleUnauthorizedPassageToNextGateException(UnauthorizedPassageToNextGateException ex) {
        return ResponseEntity.ok(new ErrorMessage(ex.getMessage(), 400));
    }

    @ExceptionHandler(ReachedFinalGateException.class)
    public ResponseEntity<ErrorMessage> handleFinalGate(ReachedFinalGateException ex) {
        return ResponseEntity.ok(new ErrorMessage(ex.getMessage(), 200));
    }

    @ExceptionHandler(ProjectAlreadyExistingException.class)
    public ResponseEntity<ErrorMessage> handleProjectAlreadyExistingException(ProjectAlreadyExistingException ex) {
        return ResponseEntity.ok(new ErrorMessage(ex.getMessage(), 400));
    }

    @ExceptionHandler(ConfigRequiredActionAlreadyExistingException.class)
    public ResponseEntity<ErrorMessage> handleConfigRequiredActionAlreadyExistingException(ConfigRequiredActionAlreadyExistingException ex) {
        return ResponseEntity.ok(new ErrorMessage(ex.getMessage(), 400));
    }

    @ExceptionHandler(UserNotBelongingToDepartmentException.class)
    public ResponseEntity<ErrorMessage> handleUserNotBelongingToDepartmentException(UserNotBelongingToDepartmentException ex) {
        return ResponseEntity.ok(new ErrorMessage(ex.getMessage(), 400));
    }

    @ExceptionHandler(UnauthorizedUpdateFinalGateException.class)
    public ResponseEntity<ErrorMessage> handleUnauthorizedUpdateFinalGateException(UnauthorizedUpdateFinalGateException ex) {
        return ResponseEntity.ok(new ErrorMessage(ex.getMessage(), 400));
    }


    @ExceptionHandler(CaseOneNextGateNotExistantException.class)
    public ResponseEntity<ErrorMessage> handleCaseOneNextGateNotExistantException(CaseOneNextGateNotExistantException ex) {
        return ResponseEntity.ok(new ErrorMessage(ex.getMessage(), 400));
    }

    @ExceptionHandler(SecondCaseNextGateNotExistantException.class)
    public ResponseEntity<ErrorMessage> handleSecondCaseNextGateNotExistantException(SecondCaseNextGateNotExistantException ex) {
        return ResponseEntity.ok(new ErrorMessage(ex.getMessage(), 200));
    }


    /*
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleGenericException(Exception ex) {
        return ResponseEntity.ok(new ErrorMessage("An unexpected error occurred.", 500));
    }

     */




}