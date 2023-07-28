package pedometer.momo.service;

import pedometer.momo.dto.ChartDTO;
import pedometer.momo.dto.StepCountDTO;

import java.util.List;

public interface StepCountService {
    List<StepCountDTO> getChartByDate(ChartDTO dto);

    StepCountDTO saveSteps(StepCountDTO dto);

    Integer getTotalWeekStep(String email);

    Integer getTotalMonthStep(String email);
}
