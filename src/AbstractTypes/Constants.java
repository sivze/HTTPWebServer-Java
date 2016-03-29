package AbstractTypes;
/**
 * 
 * @author Siva
 * Some helpful constants.
 */
public abstract class Constants {
	
	//Methods
	public static final String GET = "GET";
	public static final String HEAD = "HEAD";
	public static final String POST = "POST";
	public static final String PUT = "PUT";
	public static final String DELETE = "DELETE";
	
	//Status Codes
	public static final String HTTP_OK = "200 OK";
	public static final String HTTP_BAD_REQUEST = "400 Bad Request";
	public static final String HTTP_NOT_FOUND = "404 Not Found";
	public static final String HTTP_NOT_ACCEPTABLE = "406 Not Acceptable";
	public static final String HTTP_REQUEST_TIMEOUT = "408 Request Timeout";
	
	//Content Type
	public static final String CONTENT_TEXT = "text/plain";
	public static final String CONTENT_HTML = "text/html";
	public static final String CONTENT_JSON = "application/json";
	
}
