package Testing;

import static org.junit.Assert.assertTrue;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.junit.Test;
import AbstractTypes.BaseHandler;
/**
 * @author Siva
 */
public class BaseHandlerTest extends BaseHandler{
	
	@Test
	public void valid_Request_types() throws IOException {
		assertTrue(getRequestType("HEAD /mytwitter.html HTTP/1.1"));
		assertTrue(method.equals("HEAD"));
		assertTrue(uri.equals("/mytwitter.html"));
		assertTrue(version.equals("1.1"));

		assertTrue(getRequestType("GET /messages HTTP/1.1"));
		assertTrue(method.equals("GET"));
		assertTrue(uri.equals("/messages"));
		assertTrue(version.equals("1.1"));

		assertTrue(getRequestType("DELETE /messages/1 HTTP/1.1"));
		assertTrue(method.equals("DELETE"));
		assertTrue(uri.equals("/messages/1"));
		assertTrue(version.equals("1.1"));
	}

	@Test
	public void invalid_Request_types() throws IOException {
		//expected false so inversing it
		assertTrue(!getRequestType("POST /messages/1 HTTP/1.1"));
		//expected false so inversing it
		assertTrue(!getRequestType("PUT /messages/1 HTTP/1.1"));
	}

	@Test
	public void parsing_Headers_forValid_Request_types() {

		String headersInput = "Host:localhost:9090\n"
				+ "Connection:keep-alive\n"
				+ "Accept:text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\n"
				+ "Upgrade-Insecure-Requests:1\n"
				+ "User-Agent:mozilla/5.0 (windows nt 10.0; wow64) applewebkit/537.36 (khtml, like gecko) chrome/49.0.2623.75 safari/537.36\n"
				+ "Accept-Encoding:gzip, deflate, sdch\n" + "Accept-Language:en-us,en;q=0.8\n"
				+ "Referer:http://localhost:9090/\n" 
				+ "";
		InputStream is = new ByteArrayInputStream(headersInput.getBytes());
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		getHeaders(br);
		System.out.println(headers);
		assertTrue(headers.toString().equals(
				"{Host=localhost:9090, Connection=keep-alive, Accept=text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8, Upgrade-Insecure-Requests=1, User-Agent=mozilla/5.0 (windows nt 10.0; wow64) applewebkit/537.36 (khtml, like gecko) chrome/49.0.2623.75 safari/537.36, Accept-Encoding=gzip, deflate, sdch, Accept-Language=en-us,en;q=0.8, Referer=http://localhost:9090/}"));
		
		//shouldKeepAlive() should return true as connection is 'Keep-Alive'
		assertTrue(shouldKeepAlive(headers));
	}

	@Override
	protected Response serve(String uri) {
		// not implemented
		return null;
	}
}


