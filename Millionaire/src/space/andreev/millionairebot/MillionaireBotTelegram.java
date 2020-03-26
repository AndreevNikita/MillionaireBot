package space.andreev.millionairebot;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Contact;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.PhotoSize;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import space.andreev.millionairebot.DB.UserInfo;
import space.andreev.millionairebot.CommonMessage.ResponseMessage;
import space.andreev.millionairebot.CommonMessage.ResponseMessage.ResponseVariant;
import space.andreev.millionairebot.CommonMessage.UserMessage;

public class MillionaireBotTelegram extends TelegramLongPollingBot {

	@Override
	public void onUpdateReceived(Update update) {
		Message message = update.getMessage();
		CallbackQuery callbackQuery = update.getCallbackQuery();
		Contact contact = message != null ? message.getContact() : null;
		User from = message != null ? message.getFrom() : callbackQuery.getFrom();
		if((message != null && (message.hasText() || contact != null)) || callbackQuery != null) {
			String text = message != null ? (message.hasText() ? message.getText() : "contact:" + contact.getUserID() + ":" + contact.getFirstName() + ":" + from.getUserName() + ":" + from.getFirstName()) : callbackQuery.getData();
			System.out.println(text);
			long chatId = message != null ? message.getChatId() : callbackQuery.getMessage().getChatId();
			User user = message != null ? message.getFrom() : callbackQuery.getFrom();
			UserInfo userInfo = DB.getUserInfo(user.getId(), 1);
			if(text.toLowerCase().equals("/start")) {
				DB.setHistory(userInfo, "");
				send(formMenu(chatId));
			} else if(!userInfo.hasHistory() && (text.toLowerCase().equals("/start") || text.toLowerCase().equals("/help") || text.toLowerCase().equals("open menu") || text.toLowerCase().equals("открыть меню"))) {
				
				send(formMenu(chatId));
				return;
			} else {
				ResponseMessage response = Main.handle(new UserMessage(text, userInfo, chatId));
				if(response != null)
					send(response);
				else if(text.toLowerCase().equals("отмена") || text.toLowerCase().equals("cancel")){
					DB.setHistory(userInfo, "");
					send(formMenu(chatId));
				} else
				 send(new ResponseMessage("Бот вас не понял", chatId));
			}
		}
	}
	
	public boolean send(ResponseMessage message) {
		if(message.isPhotoMessage()) {
			SendPhoto sendPhoto = message.toTGPhoto();
			try {
				Message sendPhotoResponse = sendPhoto(sendPhoto);
				if(sendPhoto.isNewPhoto()) {
					List<PhotoSize> photoList = sendPhotoResponse.getPhoto();
					DB.updateUploadedId(message.photo, 1, photoList.get(photoList.size() - 1).getFileId());
				}
				return true;
			} catch(TelegramApiException e) {
				try {
					sendPhoto.setNewPhoto(new File(Main.domainPath + "/" + message.photo));
					Message sendPhotoResponse = sendPhoto(sendPhoto);
					List<PhotoSize> photoList = sendPhotoResponse.getPhoto();
					DB.updateUploadedId(message.photo, 1, photoList.get(photoList.size() - 1).getFileId());
					return true;
				} catch(TelegramApiException e2) {
					e2.printStackTrace();
					return false;
				}
			}
		} else if(message.isMessage()) {
			try {
				sendMessage(message.toTGMessage());
				return true;
			} catch(TelegramApiException e) {
				e.printStackTrace();
				return false;
			}
		} else
			return false;
	}
	
	public ResponseMessage formMenu(long chatId) {
		ResponseMessage response = new ResponseMessage("Меню", chatId);
		response.photo = "images/logo.jpg";
		response.responseVariants.add(new ResponseVariant("Играть", "start game"));
		response.responseVariants.add(new ResponseVariant("Пригласить друга", "friend invite"));
		return response;
	}
	
	public static SendPhoto getPhotoBase(String path) {
		String photoId = DB.getUploadedId(path, 1);
		if(photoId == null)
			return new SendPhoto().setNewPhoto(new File(Main.domainPath + "/" + path));
		else
			return new SendPhoto().setPhoto(photoId);
	}
	
	@Override
	public String getBotUsername() {
		return Main.botUsername;
	}

	@Override
	public String getBotToken() {
		return Main.accessToken;
	}

}
