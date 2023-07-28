package pedometer.momo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class Status {
    private Integer status;
    private String message = "";
    public static Status ok() {
        return new Status(HttpStatus.OK.value(), "Success");
    }
}
