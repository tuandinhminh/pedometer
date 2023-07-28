package pedometer.momo.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "step_count")
@Data
public class StepCount {

    @Id
    private String id;
    @Indexed
    private String email;
    private Integer step;
    @Indexed
    LocalDate date = LocalDate.now();
}
