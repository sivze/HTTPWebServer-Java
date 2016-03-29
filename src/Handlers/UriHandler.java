package Handlers;

import java.io.IOException;
import java.io.OutputStream;
import org.json.JSONException;
import org.json.JSONObject;
import AbstractTypes.BaseHandler;
import AbstractTypes.Constants;
import Models.Message;
import Services.MessageService;
import Utilities.Logger;

/**
 * @author Siva 
 * Uri handler for RESTful services. Created for accessing Messages
 * via this server.
 * 1) contains meaningful Resource Locator Uri Ex: "webapi/messages" or "webapi/messages/{messageId}"
 * 2) Appropriate use of GET for list of messages and single message with message id. Appropriate use
 *    of DELETE method to delete message with message id
 * 3) Return meaningful status after ever operation 
 * 4) HATEOAS - not adapted yet!
 */
public class UriHandler extends BaseHandler {
	private String root;
	public UriHandler(){
		
	}
	public UriHandler(String root) {
		super();
		this.root = root;
	}

	/**
	 * Finds and returns message id for a request with message id. Returns 0 if
	 * messageId is invalid.
	 * 
	 * @param uri
	 */
	private int findMessageId(String uri) {
		if (uri.contains("/messages/")) {
			try {
				int no = Integer.parseInt(uri.substring(("/webapi"+"/messages/").length()));
				return no;
			} catch (Exception e) {
			}
		}
		return 0;
	}

	/**
	 * Implements abstract serve method in the BaseHandler class. 
	 * Scenario 1:
	 * return message list as json (GET) Header Usage: Accept Content Type:
	 * application/json 
	 * Scenario 2: for a valid url with message id 
	 * 	2.1 - return message as json if message id exists (GET) 
	 * 	2.2 - delete message for given message id (DELETE) Scenario 3: returns 400 Bad Request
	 */
	@Override
	protected Response serve(String uri) {
		try {
			int messageId = findMessageId(uri);

			if (uri.equals("/webapi/messages") && method.equals(Constants.GET)) {

				String responseType = BaseHandler.headers.get("Accept");

				if (responseType.equals(Constants.CONTENT_JSON)) {

					final JSONObject msgsObj = new JSONObject();
					try {
						msgsObj.put("messages", MessageService.getAllMessages());
					} catch (JSONException e) {
						e.printStackTrace();
					}

					return new Response(Constants.HTTP_OK) {
						// implements abstract addHeaders() method of Response Class nested inside BaseHandler Class
						@Override
						public void addHeaders() {
							addResponseHeader("Content-Type", Constants.CONTENT_JSON);
							addResponseHeader("Content-Length", Long.toString(msgsObj.toString().length()));
						}

						// implements abstract addBody() method of Response Class nested inside BaseHandler Class
						// returns write content as string to response() method in BaseHandler Class
						@Override
						public String addBody(OutputStream out) throws IOException {
							return msgsObj.toString();
						}
					};
				}
			} else if (messageId > 0) {
				if (BaseHandler.method.equals(Constants.GET)) {
					Message message = MessageService.getMessage(messageId);
					if (message != null) {
						final JSONObject msgObj = new JSONObject();
						try {
							msgObj.put("id", message.getId());
							msgObj.put("message", message.getMessage());
							msgObj.put("author", message.getAuthor());
							msgObj.put("created", message.getCreated());
						} catch (JSONException e) {
							e.printStackTrace();
						}

						return new Response(Constants.HTTP_OK) {
							@Override
							public void addHeaders() {
								addResponseHeader("Content-Type", Constants.CONTENT_JSON);
								addResponseHeader("Content-Length", Long.toString(msgObj.toString().length()));
							}

							@Override
							public String addBody(OutputStream out) throws IOException {
								return msgObj.toString();
							}
						};
					}
				} else if (BaseHandler.method.equals(Constants.DELETE)) {
					final Message deletedMsg = MessageService.deleteMessage(messageId);
					if (deletedMsg != null) {
						return new Response(Constants.HTTP_OK) {
							String mesg = "<html><body>"+Constants.HTTP_OK+"\nMessage: '" + deletedMsg.getMessage()
									+ "' deleted.</body></html>";

							@Override
							public void addHeaders() {
								addResponseHeader("Content-Type", Constants.CONTENT_HTML);
								addResponseHeader("Content-Length", Long.toString(mesg.getBytes().length));
							}

							@Override
							public String addBody(OutputStream out) throws IOException {
								return mesg;
							}
						};
					}

				}
			}
			return new Response(Constants.HTTP_BAD_REQUEST) {
				String mesg = "<html><body>"+Constants.HTTP_BAD_REQUEST+"\nUri not found or Invalid Request!</body></html>";

				@Override
				public void addHeaders() {
					addResponseHeader("Content-Type", Constants.CONTENT_HTML);
					addResponseHeader("Content-Length", Long.toString(mesg.getBytes().length));
				}

				@Override
				public String addBody(OutputStream out) throws IOException {
					return mesg;
				}
			};
		} catch (Exception e) {
			//executor closes connection on returning null i.e due to null exception
			Logger.log("Erorr in processing Uri response: closing client...", e.getMessage());
			return null;
		}
	}

}
