package ru.stormcraft.tgconsole.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

public class Config {
	public boolean enabled;
	public boolean debug;
	public boolean sendIds;
	public String botToken;
	public String botUsername;
	public Long delay;
	public boolean shareStats;
	public Locale localization;
	
	public Config(FileConfiguration cfg) {
		enabled = cfg.getBoolean("enabled");
		debug = cfg.getBoolean("debug");
		sendIds = cfg.getBoolean("sendids");
		botToken = cfg.getString("botToken");
		botUsername = cfg.getString("botUsername");
		delay = cfg.getLong("delay");
		shareStats = cfg.getBoolean("share_stats");
		
		

		
	}
	

	public FileConfiguration generateDefaultConfiguration(FileConfiguration cfg) {
		return null;
	}
	
	
	
}
