package pedometer.momo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import pedometer.momo.dto.StepCountDTO;
import pedometer.momo.entity.StepCount;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface StepCountMapper {
    StepCount toEntity(StepCountDTO dto);
    StepCountDTO toDTO(StepCount entity);
}
