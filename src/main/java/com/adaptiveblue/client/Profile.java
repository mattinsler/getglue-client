package com.adaptiveblue.client;

import java.util.List;

import com.adaptiveblue.util.digester.DigesterField;
import com.adaptiveblue.util.digester.DigesterList;

public class Profile {
	@DigesterField("private")
	public boolean isPrivate;
	public boolean hideVisits;
	public String displayName;
	public String description;
	public int friends;
	public int followers;
	public Stats stats;
	public Favorites favorites;
	public boolean postToTwitter;
	public boolean postToFacebook;
	public String username;
	@DigesterList(itemName = "sticker", type = Sticker.class)
	public List<Sticker> stickers;
	public boolean unlockedStream;
}
