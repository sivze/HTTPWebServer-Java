package AbstractTypes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Utilities.Logger;

/**
 * Reference - https://github.com/jrudolph/Pooling-web-server 
 * An abstract BaseHandler class which does basic parsing for a HTTP request and defers the
 * actual response generation to the abstract method serve.
 * 
 * Refactored and optimized from original file to improve performance and readability.
 * Takes care of some basic http server fuctions like reading
 * request type, headers, preparing response and writing it to client.
 */
public abstract class BaseHandler {
	// states used for getRequestType() behaviour
	protected final static Pattern requestTypePattern = Pattern.compile("(GET|HEAD|DELETE) ([^ ]+) HTTP/(\\d\\.\\d)");
	protected static String method;
	protected static String uri;
	protected static String version;
	protected static boolean isHeaderOnly;

	// states used for getHeaders() behaviour
	protected final static Pattern headerPattern = Pattern.compile("([^:]*):\\s*(.*)");
	protected final static Map<String, String> headers = new LinkedHashMap<String, String>();

	// states used for Response() behaviour
	protected abstract Response serve(String uri); // implemented by specific
													// type handler

	/**
	 * Class Response represents the response which has to be sent back to the
	 * client. Implementors have to implement addHeaders and addBody. This helps
	 * different types of implementor handlers to write their headers and body
	 * to client.
	 */
	public static abstract class Response {
		private final String resultCode;
		private final StringBuffer headerBuffer = new StringBuffer();

		public Response(String resultCode) {
			this.resultCode = resultCode;
		}

		public String getResultCode() {
			return resultCode;
		}

		public String getHeaders() {
			return headerBuffer.toString();
		}

		// accessed from implementors to write their headers
		public void addResponseHeader(String header, String value) {
			headerBuffer.append(header).append(": ").append(value).append("\r\n");
		}

		// lets BaseHandler to instruct implementors to add headers
		public abstract void addHeaders();

		public abstract String addBody(OutputStream out) throws IOException;
	}

	public boolean handleConnection(Socket client) throws IOException {
		// entry point for a request
		client.setSoTimeout(ServerSettings.firstReadTimeout);
		return response(client);
	}

	/**
	 * read the request type form the client stores method, uri, version and
	 * isHeaderOnly which is also accessed by implementors
	 */
	protected boolean getRequestType(String requestType) throws IOException {
		

		Matcher lineMatcher = requestTypePattern.matcher(requestType);

		if (lineMatcher.matches()) {
			method = lineMatcher.group(1);
			uri = lineMatcher.group(2);
			version = lineMatcher.group(3);
			isHeaderOnly = Constants.HEAD.equals(method);
			return true;
		}
		return false;
	}

	/**
	 * Reads all header content and stores it in headers map.
	 * 
	 * @param reader
	 *            - Stream from client Content read from 'reader' after this
	 *            method execution will be mostly inputs for POST or PUT request
	 */
	protected void getHeaders(BufferedReader reader) {
		try {
			String line = reader.readLine();
			while (line != null && !line.isEmpty() && line.length() > 0) {

				Matcher matcher = headerPattern.matcher(line);
				if (matcher.matches())
					headers.put(matcher.group(1), matcher.group(2).toLowerCase());
				else
					Logger.log("Skipping invalid header: '%s'", line);
				line = reader.readLine();
			}

		} catch (Exception e) {
			System.err.println("Error while reading headers: " + e.getMessage());
		}
	}

	/**
	 * Decides whether a connection should kept alive based on the "Connection"
	 * parameter in the Headers. returns whether connection is alive
	 * 
	 * @param headers
	 */
	protected boolean shouldKeepAlive(Map<String, String> headers) {

		// In HTTP/1.1 (RFC 2616): A connection is considered persistent
		// by default. The sender can flag a connection close with the request
		// header
		// "Connection: close"
		// it returns false if connection is close
		return headers.containsKey("Connection") && headers.get("Connection").contains("keep-alive");
	}

	private void fail(Writer writer, String code) throws IOException {
		writer.append("HTTP/1.1 ").append(code).append("\r\n");
	}

	private boolean response(Socket client) throws IOException {
		final OutputStream os = client.getOutputStream();
		final Writer writer = new OutputStreamWriter(os);
		
		try {
			final InputStream is = client.getInputStream();
			final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			
			String requestTypeLine = reader.readLine();
			Logger.log("Got request '%s'", requestTypeLine);
			
			// check if valid request and assign it to method, uri and version
			if (getRequestType(requestTypeLine)) {
				
				// get headers
				client.setSoTimeout(ServerSettings.headerTimeout);
				getHeaders(reader);

				// get result using result class and implementors
				Response res = serve(uri);
				res.addHeaders();

				// prepare response
				boolean keepAlive = shouldKeepAlive(headers);

				// write headers
				writer.append("HTTP/").append(version).append(' ').append(res.getResultCode()).append("\r\n")
						.append(res.getHeaders());

				// Close if client requests Connection: close
				if (!keepAlive)
					writer.append("Connection: close\r\n");

				writer.append("\r\n");

				// write body by calling addBody() method in Response class
				if (!isHeaderOnly) {
					String bodyContent = res.addBody(os);
					if (!bodyContent.isEmpty())
						writer.append(bodyContent);
				}

				writer.flush();
				writer.close();

				return keepAlive;

			} else {
				System.err.printf("Bad Request: '%s'\n", requestTypeLine);
				fail(writer, Constants.HTTP_BAD_REQUEST);
			}
		} catch (SocketTimeoutException e) {
			fail(writer, Constants.HTTP_REQUEST_TIMEOUT);
		}

		writer.flush();
		writer.close();

		return false;
	}
}
