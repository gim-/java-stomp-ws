package eu.mivrenik.stomp.example;

import com.neovisionaries.ws.client.WebSocketException;
import eu.mivrenik.stomp.StompMessageListener;
import eu.mivrenik.stomp.client.StompClient;

import java.io.IOException;

public class StompClientExample {

    public static void main(String[] args) {
        StompClient stompClient;
        try {
            stompClient = new StompClient("ws://localhost:7777/hello");
            stompClient.connect();
        } catch (WebSocketException | IOException e) {
            e.printStackTrace();
            return;
        }

        // Subscribing to a topic
        stompClient.subscribe("/topic/greetings", new StompMessageListener() {

            @Override
            public void onMessage(String message) {
                System.out.println("Server message: " + message);
            }
        });

        // Sending JSON message to a server
        String message = "Jack";
        stompClient.send("/app/hello", message);
    }
}
