package pedometer.momo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pedometer.momo.controller.PedometerController;
import pedometer.momo.controller.WebSocketController;

@SpringBootTest
class MomoApplicationTests {
	@Autowired
	PedometerController pedometerController;
	@Autowired
	WebSocketController webSocketController;

	@Test
	void contextLoads() {
		Assertions.assertNotNull(pedometerController);
		Assertions.assertNotNull(webSocketController);
	}

}
