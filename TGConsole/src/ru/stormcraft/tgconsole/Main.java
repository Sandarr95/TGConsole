package ru.stormcraft.tgconsole;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class Main extends JavaPlugin {
	static List<Long> ids = null;
	static boolean sendids = true;
	static boolean debug;
	static File tfolder;
	public static String botName = "test";
	public static String botToken = "resd";
	public static long delay;
	public static HashMap<String, String> locale;
	@Override
	public void onEnable() {
		tfolder = this.getDataFolder();
		clearLog();
		loadConfigs();
    
		Configos cfg = new Configos();
		if (getConfig().getBoolean("enabled")){
			if ((!botName.equals("YOUR_BOT_NAME")) && (!botToken.equals("YOUR_TOKEN_NAME"))) {
				try{
					ApiContextInitializer.init();
					TelegramBotsApi botsApi = new TelegramBotsApi();
					botsApi.registerBot(new TGself());
					Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "|----------------------------------------|");
					Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "|  Successfully started the remote bot!  |");
					Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "|----------------------------------------|");
				}catch (TelegramApiException e){
					Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "|----------------------------------------------|");
					Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "| Fatal Error Occured while enabling TGConsole,|");
					Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "|            see a stacktrace below            |");
					Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "|----------------------------------------------|");
					e.printStackTrace();
					getLogger().info("--");
					getLogger().info("Error message: " + e.getMessage());
				}
			} else {
				getLogger().info("TG console: wrong name or token!");
			}
		} else {
			getLogger().info("TG Console: disabled from configs!");
		}
	}
	@Override
	public void onDisable() {
		getLogger().info("Disabled TGConsole!");
	}
  
	static void clearLog() {
		try{
		String pat = tfolder.getAbsolutePath().replace("\\" + tfolder.getPath(), "");
		File folder = new File(pat);
		File[] files = folder.listFiles();
		debug("Files in the main folder: " + files.length);
		File[] arrayOfFile1;
		int j = (arrayOfFile1 = files).length;
		for (int i = 0; i < j; i++){
			File file = arrayOfFile1[i];
			if ((file.getName().contains("templog")) && (!file.delete())) {
				debug("Can't remove " + file.getAbsolutePath());
			}
    	}
		}catch(NullPointerException e){
			debug("Unknown error, wow.");
		}
	}
  
	public void loadConfigs() {
		getConfig().addDefault("debug", false);
		getConfig().addDefault("enabled", false);
		getConfig().addDefault("sendids", true);
		getConfig().addDefault("token", "YOUR_BOT_TOKEN");
		getConfig().addDefault("botName", "YOUR_BOT_NAME");
		getConfig().addDefault("delay", 10000);
		getConfig().addDefault("locale.Admin", "Admin:");
		getConfig().addDefault("locale.Action", "Action:");
		getConfig().addDefault("locale.commandOutput", "Command Output:");
		getConfig().addDefault("locale.unknownCommand", "Unknown command!");
		getConfig().addDefault("locale.getid", "Your personal id is: USER_ID NEW_LINE (Click the number to copy)");
    
		ArrayList<Long> admins = new ArrayList<Long>();
		admins.add((long) 0);
		admins.add((long) 1);
		getConfig().addDefault("adminids", admins);
		getConfig().options().copyDefaults(true);
		saveConfig();
    
		botName = getConfig().getString("botName");
		botToken = getConfig().getString("token");
		ids = getConfig().getLongList("adminids");
		sendids = getConfig().getBoolean("sendids");
		debug = getConfig().getBoolean("debug");
		delay = getConfig().getLong("delay");
		locale = new HashMap<String, String>();
		locale.put("Admin", getConfig().getString("locale.Admin"));
		locale.put("Action", getConfig().getString("locale.Action"));
		locale.put("commandOutput", getConfig().getString("locale.commandOutput"));
		locale.put("unknownCommand", getConfig().getString("locale.unknownCommand"));
		locale.put("getid", getConfig().getString("locale.getid"));
	}
  
  	public static void debug(String msng){
  		if (debug) {
  			Bukkit.getLogger().info(msng);
  		}
  	}
}
