package ru.stormcraft.tgconsole;

import java.util.List;

public class group {
	public String name;
	public List<String> commands;
	public List<String> forbidden;
	public List<Long> ids;
	
	public boolean havePermission(String cmd, Long id){
		boolean have = false;
		if(this.ids.contains(id)){
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

	public group(String name, List<String> commands,List<String> forbidden, List<Long> ids) {
		super();
		this.name = name;
		this.commands = commands;
		this.forbidden = forbidden;
		this.ids = ids;
	}
	
}
