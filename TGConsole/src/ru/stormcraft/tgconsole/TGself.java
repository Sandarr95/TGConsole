package ru.stormcraft.tgconsole;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
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

public class TGself extends TelegramLongPollingBot {
	@Override
	public String getBotUsername(){
		return Configos.botName;
	}
	@Override
	public String getBotToken(){
		return Configos.botToken;
	}
  
	@SuppressWarnings({ "deprecation" })
	@Override
	public void onUpdateReceived(Update update){
		Main.debug("got update;");
		if ((update.hasMessage()) && (update.getMessage().hasText())){
			Main.debug("it have text;");
			if (update.getMessage().getText().startsWith("/start")) {
				return;
			}
			if (update.getMessage().getText().startsWith("/getid")) {
				try{
					sendMessage(getid(update));
				} catch (TelegramApiException e){
					e.printStackTrace();
				}
				return;
			}
			if ((update.getMessage().getText().startsWith("/showmenu")||update.getMessage().getText().startsWith("/show")) && Main.menu && isAdmin(update)){
				ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
				keyboardMarkup.setKeyboard(Main.keyboard);
				SendMessage message = new SendMessage().setChatId(update.getMessage().getChatId()).setText(Main.locale.get("showMenu")).setReplyMarkup(keyboardMarkup);
				try {
					sendMessage(message);
					Thread.sleep(500);
				} catch (TelegramApiException | InterruptedException e) {
					e.printStackTrace();
				}
				return;
			}
			if ((update.getMessage().getText().startsWith("/hidemenu")||update.getMessage().getText().startsWith("/hide")) && Main.menu && isAdmin(update)){
				SendMessage message = new SendMessage().setChatId(update.getMessage().getChatId()).setText(Main.locale.get("hideMenu")).setReplyMarkup(new ReplyKeyboardRemove());
				try {
					sendMessage(message);
					Thread.sleep(500);
				} catch (TelegramApiException | InterruptedException e) {
					e.printStackTrace();
				}
				return;
			}
			if((update.getMessage().getText().startsWith("/multiple"))&&isAdmin(update)){
				Bukkit.getLogger().info(Main.locale.get("User") + " " + update.getMessage().getChat().getFirstName() + " " + update.getMessage().getChat().getLastName() + " @" + update.getMessage().getChat().getUserName());
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
						sendMessage(message);
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
					execmd(toexe,del,sender,update.getMessage().getChatId());
					
				}
				
				return;
			
			}
			
			
			if (havePerms(update)){
				Bukkit.getLogger().info(Main.locale.get("User") + " " + update.getMessage().getFrom().getFirstName() + " " + update.getMessage().getFrom().getLastName() + " @" + update.getMessage().getFrom().getUserName());
				Bukkit.getLogger().info(Main.locale.get("Action") + " " + update.getMessage().getText());
				ConsoleCommandSender sender = Bukkit.getConsoleSender();
				
				
				execmd(update.getMessage().getText(), Main.delay, sender, update.getMessage().getFrom().getId());
				return;
			}else{
				Main.debug("not an admin;");
				
			}
			
			try{
				sendMessage(getid(update));
			} catch (TelegramApiException e){
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void execmd(String cmd, long delay, ConsoleCommandSender sender, long chat_id){
		
		
		
		
			File templog = new File("templog.txt");
			TiedOutputStream tos = null;
			try {
				tos = new TiedOutputStream(templog);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				return;
			}
			PrintStream def = System.out;

			System.setOut(tos);
		try{
			Bukkit.getServer().dispatchCommand(sender, cmd);
			Thread.sleep(delay);
			System.setOut(def);
			
			String result = " " + Main.locale.get("commandOutput") + " `";
	          
			Scanner sc = new Scanner(templog);
			while (sc.hasNextLine()){
				result = result + System.lineSeparator();
				result = result + sc.nextLine();
			}
			tos.close();
			sc.close();
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
				.setChatId(chat_id)
				.setText(part).enableMarkdown(true);
				sendMessage(message);
				Thread.sleep(500);
			}
			
			Main.clearLog();
		
		}catch(InterruptedException|SecurityException|IOException | TelegramApiException e){
			e.printStackTrace();
			try{
				tos.close();
			}catch(Exception ignored){ }
		}
	}
	public SendMessage getid(Update update){
		SendMessage message;
		if ((update.getMessage().hasText()) && (update.getMessage().getText().startsWith("/getid")) && (Main.sendids)){
			String replace = "`" + update.getMessage().getChatId() + "`";
			message = new SendMessage().setChatId(update.getMessage().getChatId()).setText((Main.locale.get("getid")).replace("USER_ID", replace).replace("NEW_LINE", System.lineSeparator())).enableMarkdown(true);
		}else{
			message = new SendMessage().setChatId(update.getMessage().getChatId()).setText(Main.locale.get("unknownCommand"));
		}
		return message;
	}
	public boolean isAdmin(Update update){
		return (Main.admins.contains( update.getMessage().getFrom().getId().toString())||Main.admins.contains(update.getMessage().getFrom().getUserName())||Main.admins.contains("@"+update.getMessage().getFrom().getUserName()));
	}
	public boolean havePerms(Update update){
		if(isAdmin(update)){
			Main.debug("is admin");
			return true;
		}
		for(group gr:Main.groups){
			if(gr.havePermission(update.getMessage().getText(), update.getMessage().getFrom())){
				Main.debug("is"+ gr.name);
				return true;
			}
		}
		return false;
	}
	
}
