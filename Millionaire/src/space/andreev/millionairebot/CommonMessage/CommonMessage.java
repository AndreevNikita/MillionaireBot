package space.andreev.millionairebot.CommonMessage;

public class CommonMessage {

	public final String messageText;
	public final long chatId;
	
	public CommonMessage(String text, long chatId) {
		this.messageText = text;
		this.chatId = chatId;
	}
}
