package space.andreev.millionairebot.handlers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.FormatterClosedException;
import java.util.Iterator;
import java.util.Random;

import org.apache.commons.logging.impl.AvalonLogger;

import space.andreev.commonsymbols.CommonSymbols;
import space.andreev.millionairebot.DB;
import space.andreev.millionairebot.DB.UserInfo;
import space.andreev.millionairebot.CommonMessage.ResponseMessage;
import space.andreev.millionairebot.CommonMessage.ResponseMessage.ResponseVariant;
import space.andreev.millionairebot.CommonMessage.UserMessage;

public class MillionaireGameHandler implements MessageHandler {
	
	public static SimpleDateFormat timeDateFormat = new SimpleDateFormat("HH:mm");
	public static final String HINT_PROCESS = "hint";
	public static String[] summs = {
			"100",
			"200",
			"1'000'000"
	};
	
	@Override
	public ResponseMessage handle(UserMessage message) {
		if(!message.userInfo.hasHistory()) {
			String lowerCaseText = message.messageText.toLowerCase();
			if(lowerCaseText.contains("начать игру") || lowerCaseText.contains("start game")) {
				System.out.println(timeDateFormat.format(new Date()).toString() + "| " + message.userInfo.id + " начинает игру");
				
				message.userInfo.history = getHistoryPrefix() + ":" + fullHints + ":";
				return ask(0, message.userInfo, message.chatId, null);
			}
			return null;
		} else if(message.userInfo.history.startsWith(getHistoryPrefix())) {
			String[] historyParams = message.userInfo.history.split(":");
			int hints = Integer.parseInt(historyParams[1]);
			int questionId = Integer.parseInt(historyParams[2]);
			String additionProcess = historyParams.length > 3 ? historyParams[3] : null;
			if(additionProcess == null) {
				if(message.messageText.toLowerCase().equals("использовать подсказку") || message.messageText.toLowerCase().equals("use hint")) {
					if(hints == 0)
						return new ResponseMessage("У вас не осталось подсказок", message.chatId);
					else {
						DB.setHistory(message.userInfo, message.userInfo.history + ":" + HINT_PROCESS);
						return showHints(message.userInfo, message.chatId);
					}
				} else {
					Question question = DB.getQuestionById(questionId);
					char responseChar = Character.toLowerCase(CommonSymbols.getEngWritten(Character.toUpperCase(message.messageText.charAt(0))));
					
					int responseNumber = responseChar - 'a';
					ResponseMessage responseMessage;
					if(question.rightAnswer == responseNumber) {
						if(question.number != summs.length - 1) {
							ResponseMessage questionMessage = ask(question.number + 1, message.userInfo, message.chatId, "Это правильный ответ! Ваш выигрыш " + summs[question.number] + " рублей");
							return questionMessage;
						} else {
							responseMessage = new ResponseMessage("Вы выиграли миллион рублей!\nХотите сыграть ещё?", message.chatId);
							DB.setHistory(message.userInfo, "");
							responseMessage.responseVariants.add(new ResponseVariant("Да", "start game"));
							responseMessage.responseVariants.add(new ResponseVariant("Нет", "/help"));
							return responseMessage;
						}
						
					} else {
						responseMessage = new ResponseMessage("Ответ неправильный :(\n Хотите сыграть ещё?", message.chatId);
						DB.setHistory(message.userInfo, "");
						responseMessage.responseVariants.add(new ResponseVariant("Да", "start game"));
						responseMessage.responseVariants.add(new ResponseVariant("Нет", "/help"));
						return responseMessage;
					}
				}
			} else {
				if(additionProcess.equals(HINT_PROCESS)) {
					if(message.messageText.toLowerCase().equals("отмена") || message.messageText.toLowerCase().equals("cancel")) {
						DB.setHistory(message.userInfo, getHistoryPrefix() + ":" + hints + ":" + questionId);
						return formQuestionMessage(message.userInfo, message.chatId, DB.getQuestionById(questionId), "Хорошо, давайте думать...", true);
					} else {
						int hintNumber = Integer.parseInt(message.messageText.charAt(0) + "") - 1;
						int hintBit = 1 << hintNumber;
						if((hints & hintBit) != 0) {
							Hint hint = MillionaireGameHandler.hints[hintNumber];
							ResponseMessage responseMessage = hint.use(message.userInfo, questionId, message.chatId); 
							DB.setHistory(message.userInfo, getHistoryPrefix() + ":" + (hints ^ hintBit) + ":" + questionId + (hint instanceof ProcessHint ? ":" + ((ProcessHint)hint).getProcess() : ""));
							return responseMessage;
						} else {
							return new ResponseMessage("У вас нет такой подсказки", message.chatId);
						}
					}
					
				} else {
					for(Hint hint : MillionaireGameHandler.hints)
						if(hint instanceof ProcessHint) {
							ProcessHint processHint = (ProcessHint)hint;
							if(processHint.getProcess().equals(additionProcess)) {
								return processHint.process(this, message);
							}
						}
					return null;
				}
			}
		}
		return null;
	}
	
	public static ResponseMessage ask(int questionNumber, UserInfo userInfo, long chatId, String addition) {
		Question question = DB.getRandomQuestion(questionNumber);
		DB.setHistory(userInfo, getHistoryPrefix() + ":" + userInfo.history.split(":")[1] + ":" + question.id);
		return formQuestionMessage(userInfo, chatId, question, addition, true);
	}
	
	public static ResponseMessage formQuestionMessage(UserInfo userInfo, long chatId, Question question, String addition, boolean withHintsOffer) {
		ResponseMessage response = new ResponseMessage(addition != null ? addition + "\n" + question.text : question.text, chatId);
		response.photo = question.photo;
		char variantChar = 'A';
		for(String variant : question.variants) {
			char ch = variantChar++;
			String str = ch + ": " + variant;
			response.responseVariants.add(new ResponseVariant(str, str));
		}
		if(withHintsOffer && Integer.parseInt(userInfo.history.split(":")[1]) != 0) {
			response.responseVariants.add(new ResponseVariant("Использовать подсказку", "Use hint"));
		}
		return response;
	}
	
	public static ResponseMessage showHints(UserInfo userInfo, long chatId) {
		ResponseMessage responseMessage = new ResponseMessage("Какую подсказку вы хотите использовать?", chatId);
		int availableHints = Integer.parseInt(userInfo.history.split(":")[1]);
		for(int index = 0; index < hints.length; index++) {
			if((availableHints & (1 << index)) != 0)
				responseMessage.responseVariants.add(new ResponseVariant((index + 1) + ". " + hints[index].name, "" + (index + 1)));
		}
		responseMessage.responseVariants.add(new ResponseVariant("Отмена", "cancel"));
		return responseMessage;
	}
	
	public static String getHistoryPrefix() {
		return "mnrgame";
	}
	
	public static class Question {
		public int id;
		public String text;
		public ArrayList<String> variants = new ArrayList<String>();
		public int rightAnswer;
		public int number;
		public String photo;
		
		public Question(int id, int number, String text, String photo, boolean ignoreLast, String ... variants) {
			this.id = id;
			this.number = number;
			this.text = text;
			this.photo = photo;
			for(int index = 0; index < (ignoreLast ? variants.length - 1 : variants.length); index++)
				this.variants.add(variants[index]);
			rightAnswer = Integer.parseInt(variants[variants.length - 1]);
		}
	}
	
	public static final Hint[] hints = {
			new Hint("50/50") {
				
				@Override
				public ResponseMessage use(UserInfo userInfo, int questionId, long chatId) {
					Question question = DB.getQuestionById(questionId);
					ResponseMessage response = formQuestionMessage(userInfo, chatId, question, "Хорошо, уберём 2 неверных ответа!", false);
					char rightChar = (char)('a' + question.rightAnswer);
					Random random = new Random();
					for(int counter = 0; counter < 2; counter++) {
						int removeIndex = random.nextInt(response.responseVariants.size() - counter - 1);
						response.responseVariants.remove(Character.toLowerCase(response.responseVariants.get(removeIndex).text.charAt(0)) != rightChar ? removeIndex : removeIndex + 1);
					}
					return response;
				}
			},
			
			new ErrorRightHint()
	};
	
	
	public static class ErrorRightHint extends Hint implements ProcessHint {
		public static final String PROCESS_NAME = "errorright";
		
		public ErrorRightHint() {
			super("Право на ошибку");
		}
		
		@Override
		public ResponseMessage use(UserInfo userInfo, int questionId, long chatId) {
			Question question = DB.getQuestionById(questionId);
			ResponseMessage response = formQuestionMessage(userInfo, chatId, question, "Итак, у вас есть право на ошибку...", false);
			return response;
		}
		
		@Override
		public String getProcess() {
			return PROCESS_NAME;
		}
		
		@Override
		public ResponseMessage process(MillionaireGameHandler instance, UserMessage message) {
			String[] historyParams = message.userInfo.history.split(":");
			int questionId = Integer.parseInt(historyParams[2]);
			Question question = DB.getQuestionById(questionId);
			char responseChar = Character.toLowerCase(CommonSymbols.getEngWritten(Character.toUpperCase(message.messageText.charAt(0))));
			
			int responseNumber = responseChar - 'a';
			DB.setHistory(message.userInfo, message.userInfo.history = historyParams[0] + ":" + historyParams[1] + ":" + historyParams[2]);
			if(question.rightAnswer == responseNumber) { //Если ответ правильный, просто обрабатываем сообщение, как обычно. Нет - говорим, что пользователь ответил неправильно
				return instance.handle(message);
			} else {
				ResponseMessage response = formQuestionMessage(message.userInfo, message.chatId, question, "Этот ответ оказался неверным", false);
				for(Iterator<ResponseVariant> it = response.responseVariants.iterator(); it.hasNext();) {
					ResponseVariant variant = it.next();
					if(Character.toLowerCase(CommonSymbols.getEngWritten(Character.toUpperCase(variant.text.charAt(0)))) == responseChar) {
						it.remove();
						break;
					}
				}
				return response;
			}
		}
	}
	
	public interface ProcessHint {
		
		public String getProcess();
		public ResponseMessage process(MillionaireGameHandler instance, UserMessage message);
	}
	
	public static int fullHints;
	
	static {
		fullHints = 0;
		for(int index = 0; index < hints.length; index++)
			fullHints |= (1 << index);
	}
	
	public static abstract class Hint {
		public String name;
		
		public Hint(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return name;
		}
		
		public abstract ResponseMessage use(UserInfo userInfo, int questionId, long chatId);
	}

}
