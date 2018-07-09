package ru.stormcraft.tgconsole.util;

import org.telegram.telegrambots.api.objects.Update;

import ru.stormcraft.tgconsole.Main;

public class Permissions {
	public static boolean isAdmin(Update update){
		return (Main.admins.contains( update.getMessage().getFrom().getId().toString()) || Main.admins.contains(update.getMessage().getFrom().getUserName()) || Main.admins.contains("@"+update.getMessage().getFrom().getUserName()));
	}
	
	public static boolean havePerms(Update update){
		if(Permissions.isAdmin(update)){
			Main.debug("is admin");
			return true;
		}
		for(TGroup gr:Main.groups){
			if(gr.havePermission(update.getMessage().getText(), update.getMessage().getFrom())){
				Main.debug("is"+ gr.name);
				return true;
			}
		}
		return false;
	}
}
