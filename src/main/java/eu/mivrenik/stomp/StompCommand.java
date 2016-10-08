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

/**
 * STOMP commands.
 *
 */
enum StompCommand {
	CONNECT("CONNECT"),
	CONNECTED("CONNECTED"),
	DISCONNECT("DISCONNECT"),
	ERROR("ERROR"),
	MESSAGE("MESSAGE"),
	RECEIPT("RECEIPT"),
	SEND("SEND"),
	SUBSCRIBE("SUBSCRIBE"),
	UNSUBSCRIBE("UNSUBSCRIBE");

	private final String value;

	public static StompCommand fromValue(String value) {
		for (StompCommand c : StompCommand.values())
			if (c.value.equals(value))
				return c;

		throw new IllegalArgumentException("Unknown STOMP command:" + value);
	}

	private StompCommand(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return this.value;
	}
}
