package pedometer.momo.common.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ApiException {
    @JsonFormat(pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;

    private HttpStatus status;
    private String code;
    private List<ValidationError> validationErrors = new ArrayList<>();

    private ApiException() {
        timestamp = LocalDateTime.now();
    }

    public ApiException(HttpStatus status, String code, Throwable ex) {
        this();
        this.status = status;
        this.code = code;
        this.validationErrors.add(new ValidationError(ex.getMessage()));
    }
}
