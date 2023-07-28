package pedometer.momo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StepCountDTO {
    @NotBlank
    @Email
    private String email;
    @NotNull
    @Min(1)
    private Integer step;
}
