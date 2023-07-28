package pedometer.momo.common.exception;

import lombok.Data;

@Data
public class ValidationError {
    private String message;

    public ValidationError(String message) {
        this.message = message;
    }
}
