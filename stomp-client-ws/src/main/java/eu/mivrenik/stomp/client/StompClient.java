/*
 * Copyright (c) 2016 Andrejs Mivreņiks
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package eu.mivrenik.stomp.client;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketListener;
import com.neovisionaries.ws.client.WebSocketState;
import eu.mivrenik.stomp.StompCommand;
import eu.mivrenik.stomp.StompFrame;
import eu.mivrenik.stomp.StompHeader;
import eu.mivrenik.stomp.StompMessageListener;
import eu.mivrenik.stomp.StompSubscription;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * STOMP v1.2 client using `nv-websocket-client` WebSocket client
 * implementation.
 *
 * @see https://eu.mivrenik.stomp.github.io/eu.mivrenik.stomp-specification-1.2.html
 * @see https://github.com/TakahikoKawasaki/nv-websocket-client
 */
public class StompClient {
    private static final String STOMP_VERSION = "1.2";
    private final Map<Integer, StompSubscription> subscriptions = new HashMap<>();
    private WebSocket webSocket;
    private boolean stompConnected;

    public StompClient(String url) throws IOException {
        this.webSocket = new WebSocketFactory().createSocket(url);
        this.webSocket.addListener(webSocketListener);
    }

    public void connect() throws WebSocketException, IOException {
        if (webSocket.getState() != WebSocketState.CREATED) {
            webSocket = webSocket.recreate();
        }
        webSocket.connect();
        connectStomp();
    }

    protected void connectStomp() {
        if (stompConnected) {
            return;
        }
        Map<String, String> headers = new HashMap<>();
        headers.put(StompHeader.ACCEPT_VERSION.toString(), STOMP_VERSION);
        headers.put(StompHeader.HOST.toString(), webSocket.getURI().getHost());
        webSocket.sendText(new StompFrame(StompCommand.CONNECT, headers).toString());
    }

    public void disconnect() {
        disconnectStomp();
        webSocket.disconnect();
    }

    protected void disconnectStomp() {
        if (!stompConnected) {
            return;
        }
        webSocket.sendText(new StompFrame(StompCommand.DISCONNECT).toString());
    }

    public boolean isConnected() {
        return stompConnected;
    }

    public void send(String destination, String message) {
        send(destination, message, null);
    }

    public void send(String destination, String message, Map<String, String> headers) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put(StompHeader.DESTINATION.toString(), destination);
        webSocket.sendText(new StompFrame(StompCommand.SEND, headers, message).toString());
    }

    public StompSubscription subscribe(String destination, StompMessageListener listener) {
        StompSubscription subscription = new StompSubscription(UUID.randomUUID().hashCode(), destination, listener);
        Map<String, String> headers = new HashMap<>();
        headers.put(StompHeader.ID.toString(), String.valueOf(subscription.getId()));
        headers.put(StompHeader.DESTINATION.toString(), subscription.getDestination());

        webSocket.sendText(new StompFrame(StompCommand.SUBSCRIBE, headers).toString());
        subscriptions.put(subscription.getId(), subscription);
        return subscription;
    }

    public void unsubscribe(StompSubscription subscription) {
        Map<String, String> headers = new HashMap<>();
        headers.put(StompHeader.ID.toString(), String.valueOf(subscription.getId()));
        webSocket.sendText(new StompFrame(StompCommand.UNSUBSCRIBE, headers).toString());
        subscriptions.remove(subscription.getId());
    }

    public void unsubscribeAll(String destination) {
        for (StompSubscription s : subscriptions.values()) {
            if (s.getDestination().equals(destination)) {
                unsubscribe(s);
            }
        }
    }

    private final WebSocketListener webSocketListener = new WebSocketListener() {

        @Override
        public void handleCallbackError(WebSocket websocket, Throwable cause) throws Exception {
        }

        @Override
        public void onBinaryFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
        }

        @Override
        public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {
        }

        @Override
        public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
        }

        @Override
        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
        }

        @Override
        public void onConnectError(WebSocket websocket, WebSocketException cause) throws Exception {
        }

        @Override
        public void onContinuationFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
        }

        @Override
        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame,
                                   WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
            stompConnected = false;
        }

        @Override
        public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
        }

        @Override
        public void onFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
        }

        @Override
        public void onFrameError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame)
                throws Exception {
        }

        @Override
        public void onFrameSent(WebSocket websocket, WebSocketFrame frame) throws Exception {
        }

        @Override
        public void onFrameUnsent(WebSocket websocket, WebSocketFrame frame) throws Exception {
        }

        @Override
        public void onMessageDecompressionError(WebSocket websocket, WebSocketException cause, byte[] compressed)
                throws Exception {
        }

        @Override
        public void onMessageError(WebSocket websocket, WebSocketException cause, List<WebSocketFrame> frames)
                throws Exception {
        }

        @Override
        public void onPingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
        }

        @Override
        public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
        }

        @Override
        public void onSendError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame)
                throws Exception {
        }

        @Override
        public void onSendingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
        }

        @Override
        public void onSendingHandshake(WebSocket websocket, String requestLine, List<String[]> headers)
                throws Exception {
        }

        @Override
        public void onStateChanged(WebSocket websocket, WebSocketState newState) throws Exception {
        }

        @Override
        public void onTextFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
            StompFrame stompFrame = StompFrame.fromString(frame.getPayloadText());

            switch (stompFrame.getCommand()) {
                case CONNECTED:
                    stompConnected = true;
                    break;
                case MESSAGE:
                    Integer subscriptionId = Integer
                            .valueOf(stompFrame.getHeaders().get(StompHeader.SUBSCRIPTION.toString()));

                    subscriptions.get(subscriptionId).getListener().onMessage(stompFrame.getBody());
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onTextMessage(WebSocket websocket, String text) throws Exception {
        }

        @Override
        public void onTextMessageError(WebSocket websocket, WebSocketException cause, byte[] data)
                throws Exception {
        }

        @Override
        public void onUnexpectedError(WebSocket websocket, WebSocketException cause) throws Exception {
        }
    };
}
