package ru.stormcraft.tgconsole.util;

import java.util.ArrayList;

public class Notifications {
	private ArrayList<Long> serverStart = new ArrayList<Long>();
	private ArrayList<Long> serverStop = new ArrayList<Long>();
	private ArrayList<Long> joinLeave = new ArrayList<Long>();
	private ArrayList<Long> death = new ArrayList<Long>();
	private ArrayList<Long> chat = new ArrayList<Long>();
	private ArrayList<Long> command = new ArrayList<Long>();
	public Notifications() {
		
	}
	public ArrayList<Long> getServerStart() {
		return serverStart;
	}
	public void setServerStart(ArrayList<Long> serverStart) {
		this.serverStart = serverStart;
	}
	public ArrayList<Long> getServerStop() {
		return serverStop;
	}
	public void setServerStop(ArrayList<Long> serverStop) {
		this.serverStop = serverStop;
	}
	public ArrayList<Long> getJoinLeave() {
		return joinLeave;
	}
	public void setJoinLeave(ArrayList<Long> joinLeave) {
		this.joinLeave = joinLeave;
	}
	public ArrayList<Long> getDeath() {
		return death;
	}
	public void setDeath(ArrayList<Long> death) {
		this.death = death;
	}
	public ArrayList<Long> getChat() {
		return chat;
	}
	public void setChat(ArrayList<Long> chat) {
		this.chat = chat;
	}
	public ArrayList<Long> getCommand() {
		return command;
	}
	public void setCommand(ArrayList<Long> command) {
		this.command = command;
	}
}
