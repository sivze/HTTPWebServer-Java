package Testing;

import static org.junit.Assert.*;
import java.io.IOException;
import org.junit.Test;
import AbstractTypes.Constants;
import Handlers.FileHandler;
/**
 * @author Siva
 */
public class FileHandlerTest extends FileHandler {

	@Test
	public void valid_FileURL_request() throws IOException{
		Response res = serve("www/google.html");
		assertTrue(res.getResultCode().equals(Constants.HTTP_OK));
	}
	
	@Test
	public void invalid_FileURL_request() throws IOException{
		Response res = serve("www/invalid.html");
		assertTrue(res.getResultCode().equals(Constants.HTTP_NOT_FOUND));
	}
	
}
