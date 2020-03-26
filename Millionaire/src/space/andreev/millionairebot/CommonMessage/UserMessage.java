package space.andreev.millionairebot.CommonMessage;

import org.telegram.telegrambots.api.objects.Contact;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.User;

import space.andreev.millionairebot.DB;
import space.andreev.millionairebot.DB.UserInfo;

public class UserMessage extends CommonMessage {
	
	public final UserInfo userInfo;
	
	public UserMessage(String text, UserInfo userInfo, long chatId) {
		super(text, chatId);
		this.userInfo = userInfo;
	}
	
	public static UserMessage fromTGMessage(Message message) {
		User from = message.getFrom();
		UserInfo userInfo = DB.getUserInfo(from.getId(), 1);
		return new UserMessage(message.getText(), userInfo, message.getChatId());
	}
	
}
