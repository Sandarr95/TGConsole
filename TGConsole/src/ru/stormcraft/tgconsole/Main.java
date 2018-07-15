package ru.stormcraft.tgconsole;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.generics.BotSession;

import ru.stormcraft.tgconsole.util.TGroup;

public class Main extends JavaPlugin implements Listener {
	public static List<String> admins;
	static List<Long> ids;
	static boolean sendids = true;
	static boolean debug;
	static boolean menu;
	static File tfolder;
	TGself bot;
	TelegramBotsApi botsApi;
	BotSession session;
	static List<KeyboardRow> keyboard;
	public static String botName = "test";
	public static String botToken = "resd";
	public static long delay;
	public static HashMap<String, String> locale;
	public static ArrayList<TGroup> groups;
	@Override
	public void onEnable() {
		tfolder = this.getDataFolder();
		clearLog();
		loadConfigs();
    
		if (getConfig().getBoolean("enabled")){
			if ((!botName.equals("YOUR_BOT_NAME")) && (!botToken.equals("YOUR_BOT_TOKEN"))) {
				try{
					ApiContextInitializer.init();
					botsApi = new TelegramBotsApi();
					bot = new TGself(botName,botToken);
					session = botsApi.registerBot(bot);
					
					if(getConfig().getBoolean("notify.enabled")){
						Bukkit.getPluginManager().registerEvents(this, this);
					}
					Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Starting remote control bot - "+ChatColor.YELLOW+botName);
					Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "|----------------------------------------|");
					Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "|  Successfully started the remote bot!  |");
					Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "|----------------------------------------|");
					if(getConfig().getBoolean("notify.sendServerStart")&&getConfig().getBoolean("notify.enabled")){
						sendAll(bot,locale.get("notifyStart"),this);
					}
					if(getConfig().getBoolean("share_stats")){
						Metrics metrics = new Metrics(this);
						metrics.addCustomChart(new Metrics.SimplePie("notify_usage", new Callable<String>() {
			            	@Override
			            	public String call() throws Exception {
			            		if(getConfig().getBoolean("notify.enabled")){
			            			return "enabled";
			            		}else{
			            			return "disabled";
			            		}
			            	}
			        	}));
						metrics.addCustomChart(new Metrics.SimplePie("menu_usage", new Callable<String>() {
			            	@Override
			            	public String call() throws Exception {
			            		if(getConfig().getBoolean("menu.enabled")){
			            			return "enabled";
			            		}else{
			            			return "disabled";
			            		}
			            	}
			        	}));
					}else{
						Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "#  WARNING:  #");
						Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "You are not sharing stats with developer!");
						Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "That makes him sad :.( ");
					}
					Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW+"TGConsole is up and running,"+ChatColor.GREEN+" all is OK! :)");
					
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
			getLogger().info("Sending shutdown messages...");
			for(long user_id:ids){
				SendMessage message = new SendMessage().setChatId(user_id).setText(locale.get("notifyShutdown")).enableMarkdown(true);
				try {
					bot.execute(message);
					Thread.sleep(500);
				} catch (TelegramApiException | InterruptedException e) {
					e.printStackTrace();
				}
			}
			getLogger().info("Done!");
		}
		session.close();
		getLogger().info("Disabled TGConsole!");
	}
  
	public static void clearLog() {
		try{
			String pat = tfolder.getAbsolutePath().replace("\\" + tfolder.getPath(), "");
			File folder = new File(pat);
			File[] files = folder.listFiles();
			
			for (File file : files){
				if (file.getName().contains("templog")){
					if(!file.delete()) {
						debug("Can't remove " + file.getAbsolutePath());
					}
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
		getConfig().addDefault("share_stats", true);
		getConfig().addDefault("groups.speaker.users", Arrays.asList("@username","username","0"));
		getConfig().addDefault("groups.speaker.commands", Arrays.asList("say","broadcast","kick"));
		getConfig().addDefault("groups.speaker.blocked", Arrays.asList("kickall"));
		
		getConfig().addDefault("locale.User", "User:");
		getConfig().addDefault("locale.Action", "Action:");
		getConfig().addDefault("locale.commandOutput", "Command Output:");
		getConfig().addDefault("locale.unknownCommand", "Unknown command!");
		getConfig().addDefault("locale.getid", "Your personal id is: USER_ID NEW_LINE (Click the number to copy)");
		getConfig().addDefault("locale.nothingHappened", "Nothing happened.");
		getConfig().addDefault("locale.serverStart", "Server is now working!");
		getConfig().addDefault("locale.serverShutdown", "Server is shutting down=(");
		getConfig().addDefault("locale.showMenu", "Here is your menu!");
		getConfig().addDefault("locale.hideMenu", "Menu is now hidden!");
				
		getConfig().addDefault("locale.onJoin", "JOINED_MSG");
		getConfig().addDefault("locale.onLeave", "LEAVE_MSG");
		getConfig().addDefault("locale.onDeath", "DEATH_MSG");
		getConfig().addDefault("locale.onChat", "PLAYER : MESSAGE");
		getConfig().addDefault("locale.onCommand", "PLAYER : COMMAND");
		
		getConfig().addDefault("menu.enabled", false);

		ArrayList<String> defaultMenuCommands1 = new ArrayList<String>();
		defaultMenuCommands1.add("command 1");
		getConfig().addDefault("menu.row1", defaultMenuCommands1);
		ArrayList<String> defaultMenuCommands2 = new ArrayList<String>();
		defaultMenuCommands2.add("command 2");
		defaultMenuCommands2.add("command 3");
		getConfig().addDefault("menu.row2", defaultMenuCommands2);
		ArrayList<String> defaultMenuCommands3 = new ArrayList<String>();
		defaultMenuCommands3.add("command 4");
		defaultMenuCommands3.add("///command 5");
		defaultMenuCommands3.add("command 6");
		getConfig().addDefault("menu.row3", defaultMenuCommands3);
		
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
		
		getConfig().options().header(
				"# # # # # # # # # # # # # # # # # # # # # # # # # # # # #\n"+
				"                    TGConsole v1.20                     #\n"+
				"# # # # # # # # # # # # # # # # # # # # # # # # # # # # #\n"+
				"TGConsole: control your server remotely using Telegram! #\n"+
				"                      Autor: Azim                       #\n"+
				"contact me if you are having troubles with this plugin, #\n"+
				"                  i will do my best to help you =)      #\n"+
				"                                                        #\n"+
				" Telegram: t.me/SPC_Azim            Discord: Azim#6620  #\n"+
				"# # # # # # # # # # # # # # # # # # # # # # # # # # # # #"
				).
		copyDefaults(true);
		saveConfig();
		
		botName = getConfig().getString("botName");
		botToken = getConfig().getString("token");
		admins = getConfig().getStringList("admins");
		ids = getConfig().getLongList("notify.ids");
		groups = getGroups("groups");
		for(TGroup gr:groups){
			debug(" "+gr.name); //replace with debugs and add functional
			for(String id:gr.users){ 
				debug("user : "+id);
			}
			for(String ccc:gr.commands){
				debug("command : "+ccc);
			}
			for(String ccc:gr.forbidden){
				debug("blocked command : "+ccc);
			}
		}
		sendids = getConfig().getBoolean("sendids");
		debug = getConfig().getBoolean("debug");
		delay = getConfig().getLong("delay");
		
		locale = new HashMap<String, String>();
		locale.put("User", getConfig().getString("locale.User"));
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
		locale.put("showMenu", getConfig().getString("locale.showMenu"));
		locale.put("hideMenu", getConfig().getString("locale.hideMenu"));
		
		menu = getConfig().getBoolean("menu.enabled");
		keyboard = new ArrayList<>();
		for(int i = 1;i<=3;i++){
			KeyboardRow row = new KeyboardRow();
			for(String menucmd:getConfig().getStringList("menu.row"+i)){
				if(!menucmd.startsWith("///")){
					row.add(menucmd);
				}
			}
			keyboard.add(row);
		}
	}
	
	public void reloadCfg(){
		locale.clear();
		keyboard.clear();
		admins.clear();
		ids.clear();
		session.stop();
		reloadConfig();
		loadConfigs();
		bot = new TGself(botName,botToken);
		try {
			session = botsApi.registerBot(bot);
			getLogger().info("ALL IS OK! ignore error code above, it's just bot disliking it's shutdown...");
			getLogger().info("TGConsole  reloaded!");
		} catch (TelegramApiRequestException e) {
			e.printStackTrace();
			getLogger().info("Something went wrong =(");
		}
		
	}
	
	
	public ArrayList<TGroup> getGroups(String section){
		ArrayList<TGroup> result = new ArrayList<TGroup>();
		for (String key : getConfig().getConfigurationSection(section).getKeys(false)){
			TGroup gr = new TGroup(
					key,  //group name
					getConfig().getStringList(section+"."+key+".commands"),  //allowed commands
					getConfig().getStringList(section+"."+key+".blocked"),   //exclusions from allowed commands
					getConfig().getStringList(section+"."+key+".users"), keyboard);    //users of the group
			result.add(gr);
		}
		return result;
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length<1){
			if(sender instanceof Player){
				if(((Player)sender).hasPermission("tgconsole.info")){
					sender.sendMessage("TGConsole - control your server remotely using telegram! Author: Azim");
					return true;
				}else{
					return false;
				}
			}else{
				getLogger().info("TGConsole - control your server remotely using telegram! Author: Azim");
				return true;
			}
		}
		if(args[0].equalsIgnoreCase("reload")){
			if(sender instanceof Player){
				if(((Player)sender).hasPermission("tgconsole.reload")){
					reloadCfg();
					sender.sendMessage("TGConsole reloaded!");
					return true;
				}else{
					return false;
				}
			}else{
				reloadCfg();
				return true;
			}
		}
		
		return false;
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
						bot.execute(message);
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
  		if(!e.isCancelled()){
  			if(getConfig().getBoolean("notify.sendChat") && getConfig().getBoolean("notify.enabled")){
  				String msg = locale.get("notifyChat").replace("MESSAGE", e.getMessage()).replace("PLAYER", e.getPlayer().getName()).replace("`", "");
  				sendAll(bot,"`"+msg+"`",this);
  			}
  		}
  	}
  	@EventHandler(priority = EventPriority.LOW)
  	public void onCmd(PlayerCommandPreprocessEvent e){
  		if(!e.isCancelled()){
  			if(getConfig().getBoolean("notify.sendCommands") && getConfig().getBoolean("notify.enabled")){
  				String msg = locale.get("notifyCMD").replace("COMMAND", e.getMessage()).replace("PLAYER", e.getPlayer().getName()).replace("`", "");
  				sendAll(bot,"`"+msg+"`", this);
  			}
  		}
  	}
}
