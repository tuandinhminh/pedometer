package pedometer.momo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pedometer.momo.entity.StepCount;

public interface StepCountRepository extends MongoRepository<StepCount, String> {
}
