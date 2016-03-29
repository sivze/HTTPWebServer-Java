package DataBase;

import java.util.HashMap;
import java.util.Map;

import Models.Message;

/**
 * 
 * @author Siva
 * Mock data to test GET and DELETE methods for UriHandler functionality.
 * In real-time this would be a data pulled from databases or any other storage.
 */
public class DataBaseClass {
	private static Map<Long, Message> messages = new HashMap<Long, Message>();
	
	public static Map<Long, Message> getMessages() {
		messages.put(1l,new Message(1, "Thank you for", "Siva"));
		messages.put(2l,new Message(2, "reviewing my project.", "Siva"));
		messages.put(3l,new Message(3, "I look forward", "Siva"));
		messages.put(4l,new Message(4, "to meet you", "Siva"));
		messages.put(5l,new Message(5, "guys.", "Siva"));
		return messages;
	}
}
