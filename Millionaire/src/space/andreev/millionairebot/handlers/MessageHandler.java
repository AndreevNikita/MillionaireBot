package space.andreev.millionairebot.handlers;

import space.andreev.millionairebot.CommonMessage.ResponseMessage;
import space.andreev.millionairebot.CommonMessage.UserMessage;

public interface MessageHandler {
	
	public ResponseMessage handle(UserMessage message);
	
}
