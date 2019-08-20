package ru.stormcraft.tgconsole;

import java.util.ArrayList;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import ru.stormcraft.tgconsole.util.ConsoleUtils;
import ru.stormcraft.tgconsole.util.Permissions;

public class TGself extends TelegramLongPollingBot {
	private final String botUsername;
	private final String botToken;
	TGself(String botUsername,String botToken){
		this.botUsername = botUsername;
		this.botToken = botToken;
	}
	
	private ArrayList<SendMessage> queue;
	
	public void addToQueue(SendMessage msg){
		this.queue.add(msg);
	}
	
	public SendMessage getFromQueue(){
		if(this.queue.isEmpty()){
			return null;
		}else{
			return this.queue.remove(0);
		}
	}
	
	@Override
	public String getBotUsername(){
		return this.botUsername;
	}
	@Override
	public String getBotToken(){
		return this.botToken;
	}
  
	@Override
	public void onUpdateReceived(Update update){
		Main.debug("got update;");
		if (update.hasMessage() && update.getMessage().hasText() && !(update.getMessage().getChat().isChannelChat())){
			Main.debug("it have text;");
			if (update.getMessage().getText().toLowerCase().startsWith("/start")) {
				return;
			}
			if (update.getMessage().getText().toLowerCase().startsWith("/getid")) {
				try{
					execute(getid(update));
				} catch (TelegramApiException e){
					e.printStackTrace();
				}
				return;
			}
			if ((update.getMessage().getText().toLowerCase().startsWith("/showmenu")
					||update.getMessage().getText().toLowerCase().startsWith("/show"))
					&& Main.menu
					&& Permissions.isAdmin(update)){
				ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
				keyboardMarkup.setKeyboard(Main.keyboard);
				SendMessage message = new SendMessage().setChatId(update.getMessage().getChatId()).setText(Main.locale.get("showMenu")).setReplyMarkup(keyboardMarkup);
				try {
					execute(message);
					Thread.sleep(500);
				} catch (TelegramApiException | InterruptedException e) {
					e.printStackTrace();
				}
				return;
			}
			if ((update.getMessage().getText().toLowerCase().startsWith("/hidemenu")
					||update.getMessage().getText().toLowerCase().startsWith("/hide"))
					&& Main.menu
					&& Permissions.isAdmin(update)){
				SendMessage message = new SendMessage().setChatId(update.getMessage().getChatId()).setText(Main.locale.get("hideMenu")).setReplyMarkup(new ReplyKeyboardRemove());
				try {
					execute(message);
					Thread.sleep(500);
				} catch (TelegramApiException | InterruptedException e) {
					e.printStackTrace();
				}
				return;
			}
			if((update.getMessage().getText().toLowerCase().startsWith("/multiple"))&&Permissions.isAdmin(update)){
				Bukkit.getLogger().info(Main.locale.get("User") + " " + update.getMessage().getFrom().getFirstName() + " " + update.getMessage().getFrom().getLastName() + " @" + update.getMessage().getFrom().getUserName());
				Bukkit.getLogger().info(Main.locale.get("Action") + " " + update.getMessage().getText());
				ConsoleCommandSender sender = Bukkit.getConsoleSender();
				Scanner ms = new Scanner(update.getMessage().getText());
				ArrayList<String> commands = new ArrayList<String>();
				while(ms.hasNextLine()){
					commands.add(ms.nextLine());
				}
				ms.close();
				commands.remove(0);
				if(commands.isEmpty()){
					SendMessage message = new SendMessage()
					.setChatId(update.getMessage().getChatId())
					.setText(Main.locale.get("nothingHappened")).enableMarkdown(true);
					try{
						execute(message);
						Thread.sleep(500);
					}catch(InterruptedException|TelegramApiException e){
						e.printStackTrace();
					}
					return;
				}
				for(String cmd:commands){
					long del;
					try{
						del = Long.valueOf(cmd.substring(0,cmd.indexOf(":")));
					}catch(IllegalArgumentException e){
						del = 4;
					}
					if(del>30||del<4){
						del = 4;
					}
					del = del*1000;
					String toexe = cmd.substring(cmd.indexOf(":")+1);
					ConsoleUtils.executeCommand(toexe,del,sender,update.getMessage().getChatId(),this);
					
				}
				
				return;
			
			}
			
			if (Permissions.havePerms(update)){
				Bukkit.getLogger().info(Main.locale.get("User") + " " + update.getMessage().getFrom().getFirstName() + " " + update.getMessage().getFrom().getLastName() + " @" + update.getMessage().getFrom().getUserName());
				Bukkit.getLogger().info(Main.locale.get("Action") + " " + update.getMessage().getText());
				ConsoleCommandSender sender = Bukkit.getConsoleSender();
				
				ConsoleUtils.executeCommand(update.getMessage().getText(), Main.delay, sender, update.getMessage().getFrom().getId(), this);
				return;
			}else{
				Main.debug("not an admin;");
				return;
			}
			
		}
	}
	
	public SendMessage getid(Update update){
		SendMessage message;
		if ((update.getMessage().hasText()) && (update.getMessage().getText().toLowerCase().startsWith("/getid")) && (Main.sendids)){
			String replace = "`" + update.getMessage().getChatId() + "`";
			message = new SendMessage().setChatId(update.getMessage().getChatId()).setText((Main.locale.get("getid")).replace("USER_ID", replace).replace("NEW_LINE", System.lineSeparator())).enableMarkdown(true);
		}else{
			message = new SendMessage().setChatId(update.getMessage().getChatId()).setText(Main.locale.get("unknownCommand"));
		}
		return message;
	}
}
