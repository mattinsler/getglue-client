package com.adaptiveblue.client;

public class Category {
	public String name;
	public String modelName;
	
	@Override
	public String toString() {
		return String.format("[%s] %s", modelName, name);
	}
}
