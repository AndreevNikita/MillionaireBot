package space.andreev.millionairebot.CommonMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import space.andreev.millionairebot.DB;
import space.andreev.millionairebot.Main;
import space.andreev.millionairebot.MillionaireBotTelegram;

public class ResponseMessage extends CommonMessage {

	public ArrayList<ResponseVariant> responseVariants = new ArrayList<ResponseVariant>();
	public String photo;
	
	public ResponseMessage(String text, long chatId, ResponseVariant ... responseVariants) {
		super(text, chatId);
		for(ResponseVariant responseVariant : responseVariants)
			this.responseVariants.add(responseVariant);
	}
	
	public boolean isMessage() {
		return messageText != null && !messageText.isEmpty() && !isPhotoMessage();
	}
	
	public boolean isPhotoMessage() {
		return photo != null && !photo.isEmpty();
	}
	
	public SendMessage toTGMessage() {
		SendMessage result = new SendMessage();
		result.setText(messageText);
		result.setChatId(chatId);
		result.enableMarkdown(false);
		if(!responseVariants.isEmpty()) {
			result.setReplyMarkup(new InlineKeyboardMarkup() {{
				List<List<InlineKeyboardButton>> listsList = new ArrayList<List<InlineKeyboardButton>>();
				
				for(ResponseVariant responseVariant : responseVariants) {
					List<InlineKeyboardButton> list = new ArrayList<InlineKeyboardButton>();
					list.add(new InlineKeyboardButton(responseVariant.text).setCallbackData(responseVariant.callback));
					listsList.add(list);
				}
				setKeyboard(listsList);
			}});
		}
		return result;
	}
	
	public SendPhoto toTGPhoto() {
		SendPhoto result = MillionaireBotTelegram.getPhotoBase(photo);
		result.setCaption(messageText);
		result.setChatId(chatId);
		if(!responseVariants.isEmpty()) {
			result.setReplyMarkup(new InlineKeyboardMarkup() {{
				List<List<InlineKeyboardButton>> listsList = new ArrayList<List<InlineKeyboardButton>>();
				
				for(ResponseVariant responseVariant : responseVariants) {
					List<InlineKeyboardButton> list = new ArrayList<InlineKeyboardButton>();
					list.add(new InlineKeyboardButton(responseVariant.text).setCallbackData(responseVariant.callback));
					listsList.add(list);
				}
				setKeyboard(listsList);
			}});
		}
		return result;
	}
	
	public static class ResponseVariant {
		public String text;
		public String callback;
		
		public ResponseVariant(String text, String callback) {
			this.text = text;
			this.callback = callback;
		}
	}
	
	
	
}
