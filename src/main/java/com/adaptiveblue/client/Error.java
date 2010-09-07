package com.adaptiveblue.client;

public class Error {
	public int code;
	public String name;
	public String message;
	
	@Override
	public String toString() {
		return String.format("[%s] %s:\n%s", code, name, message);
	}
}
