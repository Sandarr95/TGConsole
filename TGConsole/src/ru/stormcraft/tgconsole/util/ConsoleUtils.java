package ru.stormcraft.tgconsole.util;

import java.io.PrintStream;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import ru.stormcraft.tgconsole.Main;

public class ConsoleUtils {
	public static void executeCommand(String cmd, long delay, ConsoleCommandSender sender, long chat_id, TelegramLongPollingBot bot) {
		ZimmyOutputStream zos = new ZimmyOutputStream();
		ZimmyPrintStream zps = new ZimmyPrintStream(zos);
		PrintStream def = System.out;
		System.setOut(zps);
		try{
			Bukkit.getServer().dispatchCommand(sender, cmd);
			Thread.sleep(delay);
			System.setOut(def);
			String result = " " + Main.locale.get("commandOutput") + " `";
			ArrayList<String> res = zps.getData();
			for(String line:res) {
				result = result+System.lineSeparator();
				result = result+line;
			}
			zps.close();
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
				bot.execute(message);
				Thread.sleep(500);
			}
			
			Main.clearLog();
		
		}catch(InterruptedException|SecurityException|TelegramApiException e){
			e.printStackTrace();
			try{
				zps.close();
			}catch(Exception ignored){ }
		}
	}
}
