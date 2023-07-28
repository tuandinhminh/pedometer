package pedometer.momo.service.impl;

import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import pedometer.momo.dto.ChartDTO;
import pedometer.momo.dto.StepCountDTO;
import pedometer.momo.entity.StepCount;
import pedometer.momo.mapper.StepCountMapper;
import pedometer.momo.service.CacheService;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StepCountServiceImplTest {

    @Mock
    private MongoTemplate mockMongoTemplate;
    @Mock
    private StepCountMapper mockStepCountMapper;
    @Mock
    private CacheService mockCacheService;
    @Mock
    private SimpMessagingTemplate mockMessagingTemplate;
    @Mock
    private ExecutorService mockExecutorService;

    private StepCountServiceImpl stepCountServiceImplUnderTest;

    @BeforeEach
    void setUp() {
        stepCountServiceImplUnderTest = new StepCountServiceImpl(mockMongoTemplate, mockStepCountMapper,
                mockCacheService, mockMessagingTemplate, mockExecutorService);
    }

    @Test
    void testGetChartByDate() {
        // Setup
        final ChartDTO dto = new ChartDTO(1, 1, 2020);
        final StepCountDTO stepCountDTO = new StepCountDTO();
        stepCountDTO.setEmail("email");
        stepCountDTO.setStep(0);
        final List<StepCountDTO> expectedResult = List.of(stepCountDTO);
        when(mockCacheService.getCache(Mockito.any())).thenReturn(null);

        // Configure MongoTemplate.aggregate(...).
        final StepCount stepCount = new StepCount();
        stepCount.setId("id");
        stepCount.setEmail("email");
        stepCount.setStep(0);
        stepCount.setDate(LocalDate.of(2020, 1, 1));
        final AggregationResults<StepCount> stepCounts = new AggregationResults<>(List.of(stepCount),
                new Document("key", "value"));
        when(mockMongoTemplate.aggregate(any(Aggregation.class), eq("step_count"), eq(StepCount.class)))
                .thenReturn(stepCounts);

        // Configure StepCountMapper.toDTO(...).
        final StepCountDTO stepCountDTO1 = new StepCountDTO();
        stepCountDTO1.setEmail("email");
        stepCountDTO1.setStep(0);
        when(mockStepCountMapper.toDTO(Mockito.any())).thenReturn(stepCountDTO1);

        // Run the test
        final List<StepCountDTO> result = stepCountServiceImplUnderTest.getChartByDate(dto);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
        verify(mockCacheService).addCache(Mockito.any(), Mockito.any());
    }

    @Test
    void testSaveSteps() {
        // Setup
        final StepCountDTO dto = new StepCountDTO();
        dto.setEmail("email");
        dto.setStep(0);

        final StepCountDTO expectedResult = new StepCountDTO();
        expectedResult.setEmail("email");
        expectedResult.setStep(0);

        // Configure StepCountMapper.toEntity(...).
        final StepCount stepCount = new StepCount();
        stepCount.setId("id");
        stepCount.setEmail("email");
        stepCount.setStep(0);
        stepCount.setDate(LocalDate.of(2020, 1, 1));
        when(mockStepCountMapper.toEntity(Mockito.any())).thenReturn(stepCount);

        // Configure MongoTemplate.save(...).
        final StepCount stepCount1 = new StepCount();
        stepCount1.setId("id");
        stepCount1.setEmail("email");
        stepCount1.setStep(0);
        stepCount1.setDate(LocalDate.of(2020, 1, 1));
        when(mockMongoTemplate.save(Mockito.any())).thenReturn(stepCount1);

        doAnswer(invocation -> {
            ((Runnable) invocation.getArguments()[0]).run();
            return null;
        }).when(mockExecutorService).execute(any(Runnable.class));
        when(mockCacheService.getCache(Mockito.any())).thenReturn(null);

        // Configure MongoTemplate.aggregate(...).
        final StepCount stepCount2 = new StepCount();
        stepCount2.setId("id");
        stepCount2.setEmail("email");
        stepCount2.setStep(0);
        stepCount2.setDate(LocalDate.of(2020, 1, 1));
        final AggregationResults<StepCount> stepCounts = new AggregationResults<>(List.of(stepCount2),
                new Document("key", "value"));
        when(mockMongoTemplate.aggregate(any(Aggregation.class), eq("step_count"), eq(StepCount.class)))
                .thenReturn(stepCounts);

        // Configure StepCountMapper.toDTO(...).
        final StepCountDTO stepCountDTO = new StepCountDTO();
        stepCountDTO.setEmail("email");
        stepCountDTO.setStep(0);
        when(mockStepCountMapper.toDTO(Mockito.any())).thenReturn(stepCountDTO);

        // Run the test
        final StepCountDTO result = stepCountServiceImplUnderTest.saveSteps(dto);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
        verify(mockExecutorService).execute(any(Runnable.class));
        verify(mockCacheService).deleteCache(Mockito.any());
        verify(mockCacheService).addCache(Mockito.any(), Mockito.any());
        verify(mockMessagingTemplate).convertAndSend("/topic/chart", List.of(new StepCountDTO("email", 0)));
    }

    @Test
    void testGetTotalWeekStep() {
        // Setup
        when(mockCacheService.getCache(Mockito.any())).thenReturn(null);

        // Configure MongoTemplate.aggregate(...).
        final AggregationResults<Document> documents = new AggregationResults<>(List.of(new Document("key", "value")),
                new Document("key", "value"));
        when(mockMongoTemplate.aggregate(any(Aggregation.class), eq("step_count"), eq(Document.class)))
                .thenReturn(documents);

        // Run the test
        final Integer result = stepCountServiceImplUnderTest.getTotalWeekStep("email");

        // Verify the results
        assertThat(result).isEqualTo(0);
        verify(mockCacheService).addCache(Mockito.any(), Mockito.any());
    }

    @Test
    void testGetTotalMonthStep() {
        // Setup
        when(mockCacheService.getCache(Mockito.any())).thenReturn(null);

        // Configure MongoTemplate.aggregate(...).
        final AggregationResults<Document> documents = new AggregationResults<>(List.of(new Document("key", "value")),
                new Document("key", "value"));
        when(mockMongoTemplate.aggregate(any(Aggregation.class), eq("step_count"), eq(Document.class)))
                .thenReturn(documents);

        // Run the test
        final Integer result = stepCountServiceImplUnderTest.getTotalMonthStep("email");

        // Verify the results
        assertThat(result).isEqualTo(0);
        verify(mockCacheService).addCache(Mockito.any(), Mockito.any());
    }
}
