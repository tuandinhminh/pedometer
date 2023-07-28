package pedometer.momo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import pedometer.momo.common.constant.PedometerConstant;
import pedometer.momo.common.util.CommonUtils;
import pedometer.momo.dto.ChartDTO;
import pedometer.momo.dto.StepCountDTO;
import pedometer.momo.entity.StepCount;
import pedometer.momo.mapper.StepCountMapper;
import pedometer.momo.service.CacheService;
import pedometer.momo.service.StepCountService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
@Slf4j
public class StepCountServiceImpl implements StepCountService {
    private final MongoTemplate mongoTemplate;
    private final StepCountMapper stepCountMapper;
    private final CacheService cacheService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ExecutorService executorService;

    @Override
    public List<StepCountDTO> getChartByDate(ChartDTO dto) {
        LocalDate date = LocalDate.of(dto.getYear(), dto.getMonth(), dto.getDay());
        if (cacheService.getCache(date.toString()) != null)
            return (List<StepCountDTO>) cacheService.getCache(date.toString());

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where(PedometerConstant.DATE).is(date)),
                Aggregation.group(PedometerConstant.EMAIL).sum(PedometerConstant.STEP).as(PedometerConstant.TOTAL_VALUE),
                Aggregation.sort(Sort.Direction.DESC, PedometerConstant.TOTAL_VALUE),
                Aggregation.limit(10),
                Aggregation.project().andExpression(PedometerConstant.TOTAL_VALUE).as(PedometerConstant.STEP).andExpression(PedometerConstant.ID).as(PedometerConstant.EMAIL)
        );

        AggregationResults<StepCount> result = mongoTemplate
                .aggregate(aggregation, PedometerConstant.COLLECTION_NAME, StepCount.class);
        List<StepCountDTO> response = result.getMappedResults().stream().map(stepCountMapper::toDTO).toList();
        if (!response.isEmpty()) cacheService.addCache(date.toString(), response);
        return response;
    }

    @Override
    public StepCountDTO saveSteps(StepCountDTO dto) {
        StepCount stepCount = stepCountMapper.toEntity(dto);
        stepCount = mongoTemplate.save(stepCount);
        executorService.execute(() -> {
            log.info("START NEW TASK");
            LocalDate localDate = LocalDate.now();
            //clear cache
            cacheService.deleteCache(List.of(dto.getEmail() + PedometerConstant.WEEK_SUFFIX, dto.getEmail() + PedometerConstant.MONTH_SUFFIX, localDate.toString()));
            //send socket message
            List<StepCountDTO> stepCountDTOS = getChartByDate(new ChartDTO(localDate.getDayOfMonth(), localDate.getMonth().getValue(), localDate.getYear()));
            Optional.ofNullable(CommonUtils.stringify(stepCountDTOS)).ifPresent(message -> messagingTemplate.convertAndSend("/topic/chart", message));
            log.info("COMPLETE NEW TASK");
        });
        return stepCountMapper.toDTO(stepCount);
    }

    @Override
    public Integer getTotalWeekStep(String email) {
        if (cacheService.getCache(email + PedometerConstant.WEEK_SUFFIX) != null)
            return (Integer) cacheService.getCache(email + PedometerConstant.WEEK_SUFFIX);
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfWeek = now.with(DayOfWeek.MONDAY);
        LocalDate lastDayOfWeek = now.with(DayOfWeek.SUNDAY);
        Integer response = getTotalStepBetweenDate(firstDayOfWeek, lastDayOfWeek, email);
        cacheService.addCache(email + PedometerConstant.WEEK_SUFFIX, response);
        return response;
    }

    @Override
    public Integer getTotalMonthStep(String email) {
        if (cacheService.getCache(email + PedometerConstant.MONTH_SUFFIX) != null)
            return (Integer) cacheService.getCache(email + PedometerConstant.MONTH_SUFFIX);
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfMonth = now.withDayOfMonth(1);
        LocalDate lastDayOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        Integer response = getTotalStepBetweenDate(firstDayOfMonth, lastDayOfMonth, email);
        cacheService.addCache(email + PedometerConstant.MONTH_SUFFIX, response);
        return response;
    }

    private Integer getTotalStepBetweenDate(LocalDate start, LocalDate end, String email) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where(PedometerConstant.DATE).gte(start).lte(end).and(PedometerConstant.EMAIL).is(email)),
                Aggregation.group().sum(PedometerConstant.STEP).as(PedometerConstant.TOTAL_VALUE)
        );
        AggregationResults<Document> result = mongoTemplate
                .aggregate(aggregation, PedometerConstant.COLLECTION_NAME, Document.class);
        Document document = result.getUniqueMappedResult();
        if (document != null && document.containsKey(PedometerConstant.TOTAL_VALUE))
            return (Integer) document.get(PedometerConstant.TOTAL_VALUE);
        return 0;
    }
}
