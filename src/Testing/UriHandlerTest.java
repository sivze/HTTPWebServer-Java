package Testing;

import static org.junit.Assert.assertTrue;
import java.io.IOException;
import org.junit.Test;
import AbstractTypes.Constants;
import Handlers.UriHandler;
/**
 * @author Siva
 */
public class UriHandlerTest extends UriHandler{
	@Test
	public void valid_api_request_for_messages() throws IOException{
		method = Constants.GET; 
		headers.put("Accept", Constants.CONTENT_JSON);
		Response res = serve("/webapi/messages");
		assertTrue(res.getResultCode().equals(Constants.HTTP_OK));
	}
	
	@Test
	public void valid_api_request_for_message() throws IOException{
		method = Constants.GET; 
		headers.put("Accept", Constants.CONTENT_JSON);
		Response res = serve("/webapi/messages/1");
		assertTrue(res.getResultCode().equals(Constants.HTTP_OK));
	}
	
	@Test
	public void valid_api_request_for_message_DELETE() throws IOException{
		method = Constants.DELETE; 
		Response res = serve("/webapi/messages/1");
		assertTrue(res.getResultCode().equals(Constants.HTTP_OK));
	}
	
	@Test
	public void invalid_api_requests() throws IOException{
		Response res;
		res = serve("webapi/messages/100");
		assertTrue(res.getResultCode().equals(Constants.HTTP_BAD_REQUEST));
		
		res = serve("webapi/messages/100");
		assertTrue(res.getResultCode().equals(Constants.HTTP_BAD_REQUEST));
		
		res = serve("webapi/messages/");
		assertTrue(res.getResultCode().equals(Constants.HTTP_BAD_REQUEST));
		
		res = serve("webapi/invalid");
		assertTrue(res.getResultCode().equals(Constants.HTTP_BAD_REQUEST));
		
		method = Constants.POST;
		res = serve("webapi/messages/1");
		assertTrue(res.getResultCode().equals(Constants.HTTP_BAD_REQUEST));
		
		method = Constants.PUT;
		res = serve("webapi/messages/1");
		assertTrue(res.getResultCode().equals(Constants.HTTP_BAD_REQUEST));
	}
}
