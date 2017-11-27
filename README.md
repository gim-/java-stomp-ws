# STOMP over WebSocket implementation in Java

This is a [STOMP](http://eu.mivrenik.stomp.github.io/) protocol implementation.

## Example backend using Spring Boot

The following example is taken from Spring's ["Using WebSocket to build an interactive web application"](https://spring.io/guides/gs/messaging-stomp-websocket/) guide but with SockJS disabled.

### Configuration

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/gs-guide-websocket");
    }
}
```

### Controller

```java
@Controller
public class GreetingController {

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) throws Exception {
        Thread.sleep(1000); // simulated delay
        return new Greeting("Hello, " + message.getName() + "!");
    }
}
```

## Usage

See the included client [example module](example).

```java
final StompClient stompSocket = new StompClient(URI.create("ws://localhost:8080/gs-guide-websocket"));

// Wait for a connection to establish
boolean connected;
try {
    connected = stompSocket.connectBlocking();
} catch (InterruptedException e) {
    e.printStackTrace();
    return;
}

if (!connected) {
    System.out.println("Failed to connect to the socket");
    return;
}

// Subscribing to a topic
stompSocket.subscribe("/topic/greetings", new StompMessageListener() {

    @Override
    public void onMessage(String message) {
        System.out.println("Server message: " + message);

        // Disconnect
        stompSocket.close();
    }
});

// Sending JSON message to a server
String message = "{\"name\": \"Jack\"}";
stompSocket.send("/app/hello", message);
```
