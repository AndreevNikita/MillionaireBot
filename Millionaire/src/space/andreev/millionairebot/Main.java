/*
	Nikita Andreev (c) 2019
	https://nickandreev.space
*/

package space.andreev.millionairebot;

import java.util.ArrayList;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;

import space.andreev.millionairebot.CommonMessage.ResponseMessage;
import space.andreev.millionairebot.CommonMessage.UserMessage;
import space.andreev.millionairebot.handlers.MessageHandler;
import space.andreev.millionairebot.handlers.MillionaireGameHandler;
import space.andreev.millionairebot.handlers.SimpleMenuHandler;


public class Main {

	public static final String botUsername = "<Имя бота, как пользователя>";
	public static final String accessToken = "<Telegram Access Token>";
	public static final String siteDomain = "176.123.160.11";
	public static final String domainPath = "C:/OSPanel/domains/millionairegamebot.ru";
	
	public static ArrayList<MessageHandler> messageHandlers = new ArrayList<MessageHandler>();
	public static MillionaireBotTelegram millionaireBotTelegram;
	
	
	public static void main(String[] args) {
		messageHandlers.add(new MillionaireGameHandler());
		messageHandlers.add(new SimpleMenuHandler());
		
		System.out.println("Подключение к базе данных");
		System.out.println(DB.connect() ? "Соединение с бд установлено" : "Соединение с бд не установлено");
		ApiContextInitializer.init();
		TelegramBotsApi botsApi = new TelegramBotsApi();
		try {
			System.out.println("Подключение бота");
			botsApi.registerBot(millionaireBotTelegram = new MillionaireBotTelegram());
			System.out.println("Бот подключен");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static ResponseMessage handle(UserMessage message) {
		for(MessageHandler messageHandler : messageHandlers) {
			ResponseMessage result = messageHandler.handle(message);
			if(result != null)
				return result;
		}
		return null;
	}
	
}
