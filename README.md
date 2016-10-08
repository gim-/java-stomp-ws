# STOMP over WebSocket implementation in Java

This is a [STOMP](http://stomp.github.io/) protocol implementation.

## Example backend using Spring Boot

### Configuration

```java
@Configuration
public class WebSocketMessageBrokerConfigurer extends AbstractWebSocketMessageBrokerConfigurer {

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/topic");
		config.setApplicationDestinationPrefixes("/app");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/hello");
	}
}
```

### Controller

```java
@Controller
public class GreetingController {

	@MessageMapping("/hello")
	@SendTo("/topic/greetings")
	public String greeting(String message) throws Exception {
		return "Hello, " + message + "!";
	}
}
```

## Usage

```java
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

// ...
// Disconnect at some point
// stompClient.disconnect();
```
