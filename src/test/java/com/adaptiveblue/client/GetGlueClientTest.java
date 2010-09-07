package com.adaptiveblue.client;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

public class GetGlueClientTest {
	private GetGlueClient getTestClient() throws Exception {
		GetGlueClient client = new GetGlueClient();
		client.login("racobac", "provethis");
		return client;
	}
	
	@Test
	public void loginTest() throws Exception {
		GetGlueClient client = new GetGlueClient();
		client.login("racobac", "provethis");
	}
	
	@Test
	public void categoryTest() throws Exception {
		GetGlueClient client = getTestClient();
		List<Category> categories = client.getCategories();
	}
	
	@Test
	public void friendsTest() throws Exception {
		GetGlueClient client = getTestClient();
		List<String> friends = client.getFriends();
	}
	
	@Test
	public void followersTest() throws Exception {
		GetGlueClient client = getTestClient();
		List<String> friends = client.getFollowers();
	}
	
//	@Test
//	public void addAndRemoveCheckInTest() throws Exception {
//		final String objectId = "http://en.wikipedia.org/wiki/Cheese";
//		GetGlueClient client = getTestClient();
//		Interaction interaction = client.addCheckIn(objectId, "http://github.com/mattinsler/getglue-client", "GetGlue Java Client");
//		Assert.assertEquals("Checkin", interaction.action);
//		Assert.assertEquals("topics/p/cheese", interaction.objectKey);
//		System.out.println("REMOVE CHECKIN");
//		Assert.assertTrue(client.removeCheckIn(objectId, interaction.timestamp));
//	}
	
	@Test
	public void removeMissingCheckInTest() throws Exception {
		GetGlueClient client = getTestClient();
		Assert.assertTrue(client.removeCheckIn("topics/p/cheese", new Date()));
	}
	
//	@Test
//	public void multipleQueryTest() throws Exception {
//		GetGlueClient client = getTestClient();
//		client.getCategories();
//		client.getFollowers();
//		client.getFriends();
//		client.getUserProfile();
//		
//		client.getUserProfile("lilmagic51");
//	}
}
