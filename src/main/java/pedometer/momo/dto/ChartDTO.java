package pedometer.momo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class ChartDTO {
    @NotNull
    private Integer day;
    @NotNull
    private Integer month;
    @NotNull
    private Integer year;
}
