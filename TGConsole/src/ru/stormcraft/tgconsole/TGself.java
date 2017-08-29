package ru.stormcraft.tgconsole;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class TGself extends TelegramLongPollingBot {
	@Override
	public String getBotUsername(){
		return Configos.botName;
	}
	@Override
	public String getBotToken(){
		return Configos.botToken;
	}
  
	@SuppressWarnings({ "deprecation", "resource" })
	@Override
	public void onUpdateReceived(Update update){
		Main.debug("got update;");
		if ((update.hasMessage()) && (update.getMessage().hasText())){
			Main.debug("it have text;");
			if (update.getMessage().getText().startsWith("/start")) {
				return;
			}
			if (Main.admins.contains( update.getMessage().getChatId().toString())||Main.admins.contains(update.getMessage().getChat().getUserName())||Main.admins.contains("@"+update.getMessage().getChat().getUserName())){
				Main.debug("one of the admins - " + update.getMessage().getChatId() + " ;");
				Bukkit.getLogger().info(Main.locale.get("Admin") + " " + update.getMessage().getChat().getFirstName() + " " + update.getMessage().getChat().getLastName() + " @" + update.getMessage().getChat().getUserName());
				Bukkit.getLogger().info(Main.locale.get("Action") + " " + update.getMessage().getText());
				ConsoleCommandSender sender = Bukkit.getConsoleSender();
        
				long chat_id = update.getMessage().getChatId().longValue();
				try{
					File templog = new File("templog.txt");
					TiedOutputStream tos = new TiedOutputStream(templog);
					PrintStream def = System.out;
          
					System.setOut(tos);
          
					Bukkit.getServer().dispatchCommand(sender, update.getMessage().getText());
					Thread.sleep(Main.delay);
          
					System.setOut(def);
					String result = " " + Main.locale.get("commandOutput") + " `";
          
					Scanner sc = new Scanner(templog);
					while (sc.hasNextLine()){
						result = result + System.lineSeparator();
						result = result + sc.nextLine();
					}
					result = result.replaceAll("[\\[;0-9]+m", " ");
					ArrayList<String> newStrings = new ArrayList<String>();
					while (result.length() > 4001){
						newStrings.add(result.substring(0, 4000));
						result = "`" + result.substring(4000);
					}
					if (result.length() < 4002) {
						newStrings.add(result);
					}
					for (String part : newStrings){
						part = part + "`";
            
						SendMessage message = new SendMessage()
						.setChatId(Long.valueOf(chat_id))
						.setText(part).enableMarkdown(true);
						sendMessage(message);
						Thread.sleep(500L);
					}
					tos.close();
          
					Main.clearLog();
				}
				catch (TelegramApiException|InterruptedException|SecurityException|IOException e){
					e.printStackTrace();
				}
			}else{
				Main.debug("not an admin;");
				try{
					sendMessage(getid(update));
				} catch (TelegramApiException e){
					e.printStackTrace();
				}
			}
		}
	}
  
	public SendMessage getid(Update update){
		SendMessage message;
		if ((update.getMessage().hasText()) && (update.getMessage().getText().startsWith("getid")) && (Main.sendids)){
			String replace = "`" + update.getMessage().getChatId() + "`";
			message = new SendMessage().setChatId(update.getMessage().getChatId()).setText((Main.locale.get("getid")).replace("USER_ID", replace).replace("NEW_LINE", System.lineSeparator())).enableMarkdown(true);
		}else{
			message = new SendMessage().setChatId(update.getMessage().getChatId()).setText((String)Main.locale.get("unknownCommand"));
		}
		return message;
	}
	
}
