package pedometer.momo.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/chart")
    @SendTo("/topic/chart")
    public String processMessageFromInsertingNewRecords(String message) {
        return message;
    }
}
