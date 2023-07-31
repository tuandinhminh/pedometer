package pedometer.momo.websocket_client;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.Scanner;

public class StompClient {

    private static String URL = "ws://localhost:3002/chart";

    public static void main(String[] args) {
        WebSocketClient client = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(client);

        stompClient.setMessageConverter(new StringMessageConverter());

        StompSessionHandler sessionHandler = new MyStompSessionHandler();
        stompClient.connect(URL, sessionHandler);

        new Scanner(System.in).nextLine(); // Don't close immediately.
    }
}