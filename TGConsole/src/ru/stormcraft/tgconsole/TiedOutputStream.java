package ru.stormcraft.tgconsole;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class TiedOutputStream extends PrintStream {
	private final PrintStream sout;
	private final PrintStream serr;
	
	public TiedOutputStream(File logFile) throws FileNotFoundException {
		super(logFile);
    	this.sout = System.out;
    	this.serr = System.err;
	}
	@Override
	public void print(Object obj) {
		super.print(obj);
		this.sout.print(obj);
	}
	@Override
	public void println(String obj) {
		super.println(obj);
		this.sout.println(obj);
	}
	@Override
	public PrintStream printf(String format, Object... args) {
		super.printf(format, args);
		this.sout.printf(format, args);
		return this;
	}
	@Override
	public void println(Object args) {
		super.println(args);
		this.sout.println(args);
	}
	
	public PrintStream getSerr() {
		return serr;
	}
}
