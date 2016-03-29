package Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import DataBase.DataBaseClass;
import Models.Message;
/**
 * @author Siva
 * Service class for messages.
 * Contains methods to get list of all messages, get a single message with message id
 * and delete a message with message id based on GET and DELETE methods
 */

public class MessageService {
private static Map<Long, Message> messages = DataBaseClass.getMessages(); 
	
	public static List<Message> getAllMessages(){
		return new ArrayList<Message>(messages.values());
	}
	
	public static Message getMessage(long id){
		Message message = messages.get(id);
		return message;
	}
	
	public static Message deleteMessage(long id){
		return messages.remove(id);
	}
}
