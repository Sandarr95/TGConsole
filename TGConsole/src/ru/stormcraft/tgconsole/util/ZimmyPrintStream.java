package ru.stormcraft.tgconsole.util;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class ZimmyPrintStream extends PrintStream{
	private final PrintStream sout;
	private final PrintStream serr;
	private ArrayList<String> data;
	
	public ZimmyPrintStream(OutputStream stream) {
		super(stream);
    	this.sout = System.out;
    	this.serr = System.err;
    	data = new ArrayList<String>();
	}
	
	public ArrayList<String> getData(){
		return data;
	}
	
	public PrintStream getSerr() {
		return serr;
	}
	@Override
	public void print(Object obj) {
		data.set(data.size()-1, data.get(data.size()-1)+obj);
		this.sout.print(obj);
	}
	@Override
	public void println(String obj) {
		data.add(obj);
		this.sout.println(obj);
	}
	@Override
	public PrintStream printf(String format, Object... args) {
		data.set(data.size()-1, data.get(data.size()-1)+String.format(format, args));
		this.sout.printf(format, args);
		return this;
	}
	@Override
	public void println(Object args) {
		data.add(args.toString());
		this.sout.println(args);
	}
	@Override
	public PrintStream append(char c) {
		data.set(data.size()-1, data.get(data.size()-1)+c);
		this.sout.append(c);
		return this;
	}
	@Override
	public PrintStream append(CharSequence c) {
		data.set(data.size()-1, data.get(data.size()-1)+c);
		this.sout.append(c);
		return this;
	}
	@Override
	public PrintStream append(CharSequence c, int start, int end) {
		if(c != null) {
			data.set(data.size()-1, data.get(data.size()-1)+c.subSequence(start, end));
			this.sout.append(c.subSequence(start, end));
		}
		return this;
	}
	
}
