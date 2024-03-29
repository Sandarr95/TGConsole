package ru.stormcraft.tgconsole.util;

import java.util.List;

import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

public class TGroup {
	
	public String name;
	
	public List<String> commands;
	
	public List<String> forbidden;
	
	public List<String> users;
	
	public List<KeyboardRow> keyboard;
	
	
	public boolean havePermission(String cmd, User user){
		boolean have = false;
		if(this.users.contains(user.getId().toString())||this.users.contains(user.getUserName())||this.users.contains("@"+user.getUserName())){
			for(String com:this.commands){
				if(cmd.startsWith(com)){
					have = true;
				}
			}
			for(String com:this.forbidden){
				if(cmd.startsWith(com)){
					have = false;
				}
			}
		}
		return have;
	}

	public TGroup(String name, List<String> commands,List<String> forbidden, List<String> users, List<KeyboardRow> keyboard) {
		this.name = name;
		this.commands = commands;
		this.forbidden = forbidden;
		this.users = users;
	}
	
}
