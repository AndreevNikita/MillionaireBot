package space.andreev.millionairebot.handlers;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.inlinequery.result.InlineQueryResultContact;

import space.andreev.millionairebot.DB;
import space.andreev.millionairebot.Main;
import space.andreev.millionairebot.CommonMessage.ResponseMessage;
import space.andreev.millionairebot.CommonMessage.ResponseMessage.ResponseVariant;
import space.andreev.millionairebot.CommonMessage.UserMessage;

public class SimpleMenuHandler implements MessageHandler {

	public static String FRIEND_INVITE_PREFIX = "friendinvite";
	
	@Override
	public ResponseMessage handle(UserMessage message) {
		if(!message.userInfo.hasHistory()) {
			if(message.messageText == null || message.messageText.isEmpty())
				return null;
			String lowerCaseText = message.messageText.toLowerCase();
			if(lowerCaseText.equals("пригласить друга") || lowerCaseText.equals("friend invite")) {
				DB.setHistory(message.userInfo, FRIEND_INVITE_PREFIX);
				return new ResponseMessage("Отправьте его контакт", message.chatId, new ResponseVariant("отмена", "cancel"));
			}
		} else {
			if(message.userInfo.history.equals(FRIEND_INVITE_PREFIX)) {
				String[] splittedText = message.messageText.split(":");
				if(splittedText[0].equals("contact")) {
					try {
						DB.setHistory(message.userInfo, "");
						//Main.millionaireBotTelegram.send(new SendMessage("Пригласить друга").setChatId(message.chatId).setReplyMarkup(new InlineQueryResultContact()))
						Main.millionaireBotTelegram.send(new ResponseMessage("Добрый день, " + splittedText[2] + "!\nТолько что вас пригласил поиграть " + splittedText[4] + " (" + splittedText[3] + ")", Long.parseLong(splittedText[1]), new ResponseVariant("Открыть меню", "open menu")));
						return new ResponseMessage("Приглашение успешно доставлено :)", message.chatId, new ResponseVariant("Открыть меню", "open menu"));
					} catch(Exception e) {
						e.printStackTrace();
						return null;
					}
				} else
					return null;
			}
		}
		return null;
	}

}
