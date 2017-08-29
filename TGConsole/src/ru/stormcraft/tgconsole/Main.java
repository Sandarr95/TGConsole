package ru.stormcraft.tgconsole;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class Main extends JavaPlugin implements Listener {
	static List<String> admins;
	static List<Long> ids;
	static boolean sendids = true;
	static boolean debug;
	static File tfolder;
	TGself bot;
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
					bot = new TGself();
					botsApi.registerBot(bot);
					
					if(getConfig().getBoolean("notify.enabled")){
						Bukkit.getPluginManager().registerEvents(this, this);
					}
					
					Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "|----------------------------------------|");
					Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "|  Successfully started the remote bot!  |");
					Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "|----------------------------------------|");
					if(getConfig().getBoolean("notify.sendServerStart")&&getConfig().getBoolean("notify.enabled")){
						sendAll(bot,locale.get("notifyStart"),this);
					}
					
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
	@SuppressWarnings("deprecation")
	@Override
	public void onDisable() {
		if(getConfig().getBoolean("notify.sendServerShutdown")){
			//sendAll(bot,locale.get("notifyShutdown"),this);
			for(long user_id:ids){
				SendMessage message = new SendMessage().setChatId(user_id).setText(locale.get("notifyShutdown")).enableMarkdown(true);
				try {
					bot.sendMessage(message);
					Thread.sleep(500);
				} catch (TelegramApiException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
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
			if(debug){
				e.printStackTrace();
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED +"Shortly: "+e.getMessage());
			}
		}
	}
  
	public void loadConfigs() {
		getConfig().addDefault("debug", false);
		getConfig().addDefault("enabled", false);
		getConfig().addDefault("sendids", true);
		getConfig().addDefault("token", "YOUR_BOT_TOKEN");
		getConfig().addDefault("botName", "YOUR_BOT_NAME");
		getConfig().addDefault("delay", 10000);
		ArrayList<String> adminss = new ArrayList<String>();
		adminss.add("@SPC_Azim");
		getConfig().addDefault("admins", adminss);
		
		getConfig().addDefault("locale.Admin", "Admin:");
		getConfig().addDefault("locale.Action", "Action:");
		getConfig().addDefault("locale.commandOutput", "Command Output:");
		getConfig().addDefault("locale.unknownCommand", "Unknown command!");
		getConfig().addDefault("locale.getid", "Your personal id is: USER_ID NEW_LINE (Click the number to copy)");
		getConfig().addDefault("locale.nothingHappened", "Nothing happened.");
		getConfig().addDefault("locale.serverStart", "Server is now working!");
		getConfig().addDefault("locale.serverShutdown", "Server is shutting down=(");
		
		getConfig().addDefault("locale.onJoin", "JOINED_MSG");
		getConfig().addDefault("locale.onLeave", "LEAVE_MSG");
		getConfig().addDefault("locale.onDeath", "DEATH_MSG");
		getConfig().addDefault("locale.onChat", "PLAYER : MESSAGE");
		getConfig().addDefault("locale.onCommand", "PLAYER : COMMAND");
		
		getConfig().addDefault("notify.enabled", false);
		ArrayList<Long> idss = new ArrayList<Long>();
		idss.add((long) 0);
		getConfig().addDefault("notify.ids", idss);
		getConfig().addDefault("notify.sendOnJoinLeave", false);
		getConfig().addDefault("notify.sendOnDeath", false);
		getConfig().addDefault("notify.sendChat", false);
		getConfig().addDefault("notify.sendServerStart", false);
		getConfig().addDefault("notify.sendServerShutdown", false);
		getConfig().addDefault("notify.sendCommands", false);
		
		
		
		
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		botName = getConfig().getString("botName");
		botToken = getConfig().getString("token");
		admins = getConfig().getStringList("admins");
		ids = getConfig().getLongList("notify.ids");
		
		sendids = getConfig().getBoolean("sendids");
		debug = getConfig().getBoolean("debug");
		delay = getConfig().getLong("delay");
		
		locale = new HashMap<String, String>();
		locale.put("Admin", getConfig().getString("locale.Admin"));
		locale.put("Action", getConfig().getString("locale.Action"));
		locale.put("commandOutput", getConfig().getString("locale.commandOutput"));
		locale.put("unknownCommand", getConfig().getString("locale.unknownCommand"));
		locale.put("getid", getConfig().getString("locale.getid"));
		locale.put("nothingHappened", getConfig().getString("locale.nothingHappened"));
		locale.put("notifyShutdown", getConfig().getString("locale.serverShutdown"));
		locale.put("notifyStart", getConfig().getString("locale.serverStart"));
		locale.put("notifyJoin", getConfig().getString("locale.onJoin"));
		locale.put("notifyLeave", getConfig().getString("locale.onLeave"));
		locale.put("notifyDeath", getConfig().getString("locale.onDeath"));
		locale.put("notifyChat", getConfig().getString("locale.onChat"));
		locale.put("notifyCMD", getConfig().getString("locale.onCommand"));
	}
  
  	public static void debug(String msng){
  		if (debug) {
  			Bukkit.getLogger().info(msng);
  		}
  	}
  	@SuppressWarnings("deprecation")
	static void sendAll(TGself bot, String notify, JavaPlugin plugin){
  		Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable(){
			@Override
			public void run() {
				for(long user_id:ids){
					SendMessage message = new SendMessage().setChatId(user_id).setText(notify.replaceAll("§.", "")).enableMarkdown(true);
					try {
						bot.sendMessage(message);
						Thread.sleep(500);
					} catch (TelegramApiException | InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
  		});
  		
  	}
  	
  	@EventHandler(priority = EventPriority.LOW)
	public void onJoin(PlayerJoinEvent e){
  		if(getConfig().getBoolean("notify.sendOnJoinLeave") && getConfig().getBoolean("notify.enabled")){
  			String msg = locale.get("notifyJoin").replace("JOINED_MSG", e.getJoinMessage()).replace("PLAYER", e.getPlayer().getName()).replace("`", "");
  			sendAll(bot,"`"+msg+"`",this);
  		}
  	}
  	@EventHandler(priority = EventPriority.LOW)
  	public void onLeave(PlayerQuitEvent e){
  		if(getConfig().getBoolean("notify.sendOnJoinLeave") && getConfig().getBoolean("notify.enabled")){
  			String msg = locale.get("notifyLeave").replace("LEAVE_MSG", e.getQuitMessage()).replace("PLAYER", e.getPlayer().getName()).replace("`", "");
  			sendAll(bot,"`"+msg+"`",this);
  		}
  	}
  	@EventHandler(priority = EventPriority.LOW)
  	public void onDeath(PlayerDeathEvent e){
  		if(getConfig().getBoolean("notify.sendOnDeath") && getConfig().getBoolean("notify.enabled")){
  			String msg = locale.get("notifyDeath").replace("DEATH_MSG", e.getDeathMessage()).replace("PLAYER", e.getEntity().getName()).replace("`", "");
  			sendAll(bot,"`"+msg+"`",this);
  		}
  	}
  	@EventHandler(priority = EventPriority.LOW)
  	public void onChat(AsyncPlayerChatEvent e){
  		if(getConfig().getBoolean("notify.sendChat") && getConfig().getBoolean("notify.enabled")){
  			String msg = locale.get("notifyChat").replace("MESSAGE", e.getMessage()).replace("PLAYER", e.getPlayer().getName()).replace("`", "");
  			sendAll(bot,"`"+msg+"`",this);
  		}
  	}
  	@EventHandler(priority = EventPriority.LOW)
  	public void onCmd(PlayerCommandPreprocessEvent e){
  		if(getConfig().getBoolean("notify.sendCommands") && getConfig().getBoolean("notify.enabled")){
  			String msg = locale.get("notifyCMD").replace("COMMAND", e.getMessage()).replace("PLAYER", e.getPlayer().getName()).replace("`", "");
  			sendAll(bot,"`"+msg+"`", this);
  		}
  	}
}
