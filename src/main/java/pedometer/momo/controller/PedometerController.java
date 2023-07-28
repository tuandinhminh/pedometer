package pedometer.momo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pedometer.momo.common.util.ResponseMapper;
import pedometer.momo.dto.ChartDTO;
import pedometer.momo.dto.ResponseDTO;
import pedometer.momo.dto.StepCountDTO;
import pedometer.momo.service.StepCountService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/pedometer")
@RequiredArgsConstructor
public class PedometerController extends BaseController {
    private final StepCountService stepCountService;


    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_write:data')")
    public ResponseDTO<StepCountDTO> saveSteps(@Valid @RequestBody StepCountDTO dto) {
        return ResponseMapper.toResponseDto(stepCountService.saveSteps(dto));
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('SCOPE_read:data')")
    public ResponseDTO<List<StepCountDTO>> getChartByDate(@Valid ChartDTO dto) {
        return ResponseMapper.toResponseDto(stepCountService.getChartByDate(dto));

    }

    @GetMapping("/week/{email}")
    @PreAuthorize("hasAuthority('SCOPE_read:data')")
    public ResponseDTO<Integer> getTotalWeekStep(@PathVariable @NotNull String email) {
        return ResponseMapper.toResponseDto(stepCountService.getTotalWeekStep(email));
    }

    @GetMapping("/month/{email}")
    @PreAuthorize("hasAuthority('SCOPE_read:data')")
    public ResponseDTO<Integer> getTotalMonthStep(@PathVariable @NotNull String email) {
        return ResponseMapper.toResponseDto(stepCountService.getTotalMonthStep(email));
    }
}
