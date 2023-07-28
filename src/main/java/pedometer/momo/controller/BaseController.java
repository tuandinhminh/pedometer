package pedometer.momo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pedometer.momo.common.constant.HttpErrorStatusCode;
import pedometer.momo.common.exception.ApiException;
import pedometer.momo.common.exception.ValidationError;

@RestController
public class BaseController {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        ApiException apiException = new ApiException(HttpStatus.BAD_REQUEST,
                HttpErrorStatusCode.PARAMS_VALIDATION_ERROR, ex);
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String errorMessage = error.getDefaultMessage();
            apiException.getValidationErrors().add(new ValidationError(errorMessage));
        });
        return buildResponseEntity(apiException);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleExceptions(Exception ex) {
        return buildResponseEntity(new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, HttpErrorStatusCode.INTERNAL_SERVER_ERROR, ex));
    }

    private ResponseEntity<Object> buildResponseEntity(ApiException apiException) {
        return new ResponseEntity<>(apiException, apiException.getStatus());
    }
}
