package ru.stormcraft.tgconsole;

import java.util.List;

import org.telegram.telegrambots.api.objects.User;

public class group {
	public String name;
	public List<String> commands;
	public List<String> forbidden;
	public List<String> users;
	
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

	public group(String name, List<String> commands,List<String> forbidden, List<String> users) {
		super();
		this.name = name;
		this.commands = commands;
		this.forbidden = forbidden;
		this.users = users;
	}
	
}
