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
package eu.mivrenik.stomp;

import java.util.HashMap;
import java.util.Map;

/**
 * STOMP Frame.
 *
 * @link https://eu.mivrenik.stomp.github.io/eu.mivrenik.stomp-specification-1.2.html#STOMP_Frames
 */
public class StompFrame {
    private StompCommand command;
    private Map<String, String> headers = new HashMap<>();
    private String body;

    public StompFrame(StompCommand command) {
        this(command, null, null);
    }

    public StompFrame(StompCommand command, Map<String, String> headers) {
        this(command, headers, null);
    }

    public StompFrame(StompCommand command, Map<String, String> headers, String body) {
        this.command = command;
        this.body = body;

        if (headers != null) {
            for (String k : headers.keySet()) {
                this.headers.put(k, headers.get(k));
            }
        }
    }

    public StompCommand getCommand() {
        return command;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public static StompFrame fromString(String data) {
        String[] lines = data.split("\n");
        int pos = 0;

        // Ignore empty lines
        while (pos < lines.length && lines[pos].isEmpty()) {
            pos++;
        }

        // Command
        StompCommand command = StompCommand.fromValue(lines[pos++]);

        // Headers
        Map<String, String> headers = new HashMap<>();
        while (!lines[pos].isEmpty()) {
            String[] keyValue = lines[pos].split(":");
            headers.put(keyValue[0], keyValue[1]);
            pos++;
        }
        pos++;

        // Body
        StringBuilder body = new StringBuilder();
        for (; pos < lines.length; pos++) {
            body.append(lines[pos]);
            if (body.charAt(body.length() - 1) == '\0') {
                body.deleteCharAt(body.length() - 1);
                break;
            }
        }

        return new StompFrame(command, headers, body.toString());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // Command
        sb.append(command);
        sb.append('\n');
        // Headers
        for (String k : headers.keySet()) {
            sb.append(k);
            sb.append(':');
            sb.append(headers.get(k));
            sb.append('\n');
        }
        sb.append('\n');
        // Body
        if (body != null) {
            sb.append(body);
        }
        sb.append('\0');

        return sb.toString();
    }
}
