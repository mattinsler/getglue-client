package com.adaptiveblue.client;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import com.adaptiveblue.util.digester.Status;

public class GetGlueClientTest {
	private static GetGlueClient client = new GetGlueClient();
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		client.login("racobac", "provethis");
	}

	@Test
	public void loginTest() throws Exception {
		GetGlueClient client = new GetGlueClient();
		client.login("racobac", "provethis");
	}
	
	@Test
	public void categoryTest() throws Exception {
		List<Category> categories = client.getCategories();
	}
	
	@Test
	public void friendsTest() throws Exception {
		List<String> friends = client.getFriends();
	}
	
	@Test
	public void followersTest() throws Exception {
		List<String> friends = client.getFollowers();
	}
	
//	@Test
//	public void addAndRemoveCheckInTest() throws Exception {
//		final String objectId = "http://en.wikipedia.org/wiki/Cheese";
//		GetGlueClient client = client;
//		Interaction interaction = client.addCheckIn(objectId, "http://github.com/mattinsler/getglue-client", "GetGlue Java Client");
//		Assert.assertEquals("Checkin", interaction.action);
//		Assert.assertEquals("topics/p/cheese", interaction.objectKey);
//		System.out.println("REMOVE CHECKIN");
//		Assert.assertTrue(client.removeCheckIn(objectId, interaction.timestamp));
//	}
	
	@Test
	public void removeMissingCheckInTest() throws Exception {
		Assert.assertTrue(client.removeCheckIn("topics/p/cheese", new Date()));
	}
	
//	@Test
//	public void multipleQueryTest() throws Exception {
//		GetGlueClient client = client;
//		client.getCategories();
//		client.getFollowers();
//		client.getFriends();
//		client.getUserProfile();
//		
//		client.getUserProfile("lilmagic51");
//	}
	
	@Test
	public void followUserTest() throws Exception {
		Assert.assertEquals(Status.Success, client.followUser("mtab"));
	}
	
	@Test
	public void unFollowUserTest() throws Exception {
		Assert.assertTrue(client.unFollowUser("mtab"));
	}
	
	@Test
	public void isUserFriendTest() throws Exception {
		Assert.assertEquals(Status.Success, client.isUserFriend("lilmagic51"));
		Assert.assertEquals(Status.Failure, client.isUserFriend("mtab"));
	}
	
	@Test
	public void findUsersTest() throws Exception {
		List<String> users = client.findUsers("racobac");
		Assert.assertEquals(1, users.size());
	}
}
