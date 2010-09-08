package com.adaptiveblue.client;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;

import com.adaptiveblue.client.exception.GetGlueMethodException;
import com.adaptiveblue.util.CreateMap;
import com.adaptiveblue.util.digester.DigesterExistsField;
import com.adaptiveblue.util.digester.Status;
import com.adaptiveblue.util.digester.TypedDigester;
import com.adaptiveblue.util.digester.TypedDigesters;
import com.adaptiveblue.util.digester.User;

public class GetGlueClient {
	public static final class API {
		public static final String Version = "4.5";
		
		public static final String Base = "https://api.getglue.com/v2";
		
		public static final class Authentication {
			public static final String Login = "/user/login";
		}
		public static final class User {
			public static final String Profile = "/user/profile";
			public static final String Friends = "/user/friends";
			public static final String Followers = "/user/followers";
			public static final String AddCheckIn = "/user/addCheckin";
			public static final String RemoveCheckIn = "/user/removeCheckin";
			public static final String Follow = "/user/follow";
			public static final String UnFollow = "/user/unfollow";
			public static final String IsFriend = "/user/isFriend";
		}
		public static final class Glue {
			public static final String Categories = "/glue/categories";
			public static final String FindUsers = "/glue/findUsers";
		}
		public static final class Object {
			public static final String Get = "/object/get";
		}
	}

	private final HttpClient _client = new HttpClient();

	private <T> T request(String method, TypedDigester<T> digester) throws Exception {
		return request(method, null, digester);
	}
	
	private final TypedDigester<Error> _errorDigester = TypedDigesters.create(Error.class, "adaptiveblue/error");
	
	private <T> T request(String method, Map<String, String> parameters, TypedDigester<T> digester) throws Exception {
		GetMethod getMethod = new GetMethod(API.Base + method);
		
		if (_loginPing != null) {
			if (parameters == null)
				parameters = CreateMap.of();
			parameters.put("token", _loginPing.token);
		}
		
		if (parameters != null && parameters.size() > 0) {
			NameValuePair[] params = new NameValuePair[parameters.size()];
			int x = 0;
			for (Map.Entry<String, String> p : parameters.entrySet())
				params[x++] = new NameValuePair(p.getKey(), p.getValue());
			getMethod.setQueryString(params);
		}
		
		try {
			int statusCode = _client.executeMethod(getMethod);
			System.out.println(getMethod.getResponseBodyAsString());
			if (statusCode != HttpStatus.SC_OK) {
				throw new GetGlueMethodException(_errorDigester.digest(getMethod.getResponseBodyAsStream()));
			} else {
				return digester.digest(getMethod.getResponseBodyAsStream());
			}
		} finally {
			getMethod.releaseConnection();
		}
	}
	
	private Ping _loginPing;
	
	private final TypedDigester<Ping> _pingDigester = TypedDigesters.create(Ping.class, "adaptiveblue/response/ping");
	public void login(String username, String password) throws Exception {
		_loginPing = request(API.Authentication.Login, CreateMap.of("userId", username, "password", password, "version", API.Version), _pingDigester);
	}
	
	private final TypedDigester<Profile> _profileDigester = TypedDigesters.create(Profile.class, "adaptiveblue/response/profile");
	public Profile getUserProfile() throws Exception {
		if (_loginPing != null) {
			return getUserProfile(_loginPing.userId);
		}
		throw new RuntimeException();
	}
	
	public Profile getUserProfile(String userId) throws Exception {
		return request(API.User.Profile, CreateMap.of("userId", userId), _profileDigester);
	}
	
	private final TypedDigester<List<Category>> _categoryDigester = TypedDigesters.createList(Category.class, "adaptiveblue/response/categories", "category");
	public List<Category> getCategories() throws Exception {
		return request(API.Glue.Categories, _categoryDigester);
	}

	private final TypedDigester<List<String>> _friendsDigester = TypedDigesters.createList(String.class, "adaptiveblue/response/friends", "userId");
	public List<String> getFriends() throws Exception {
		if (_loginPing != null) {
			return getFriends(_loginPing.userId);
		}
		throw new RuntimeException();
	}
	
	public List<String> getFriends(String userId) throws Exception {
		return request(API.User.Friends, CreateMap.of("userId", userId), _friendsDigester);
	}
	
	private final TypedDigester<List<String>> _followersDigester = TypedDigesters.createList(String.class, "adaptiveblue/response/followers", "userId");
	public List<String> getFollowers() throws Exception {
		if (_loginPing != null) {
			return getFollowers(_loginPing.userId);
		}
		throw new RuntimeException();
	}
	
	public List<String> getFollowers(String userId) throws Exception {
		return request(API.User.Followers, CreateMap.of("userId", userId), _followersDigester);
	}
	
	private final TypedDigester<Interaction> _interactionDigester = TypedDigesters.create(Interaction.class, "adaptiveblue/response/interactions/interaction");
	public Interaction addCheckIn(String objectId, String source, String application) throws Exception {
		return addCheckIn(objectId, source, application, null);
	}
	
	public Interaction addCheckIn(String objectId, String source, String application, String comment) throws Exception {
		Map<String, String> args = CreateMap.of("objectId", objectId, "source", source, "app", application);
		if (comment != null)
			args.put("comment", comment.length() <= 140 ? comment : comment.substring(0, 139));
		return request(API.User.AddCheckIn, args, _interactionDigester);
	}
	
	private static final DateFormat _dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	public boolean removeCheckIn(String objectId, Date timestamp) throws Exception {
		return request(API.User.RemoveCheckIn, CreateMap.of("objectId", objectId, "timestamp", _dateFormat.format(timestamp)), _successOrPendingDigester).success;
	}
	
	public static class SuccessOrPending {
		@DigesterExistsField
		public boolean success;
		@DigesterExistsField
		public boolean pending;
	}
	
	private final TypedDigester<SuccessOrPending> _successOrPendingDigester = TypedDigesters.create(SuccessOrPending.class, "adaptiveblue/response");
	public Status followUser(String userId) throws Exception {
		SuccessOrPending result = request(API.User.Follow, CreateMap.of("followUserId", userId), _successOrPendingDigester);
		if (result.pending)
			return Status.Pending;
		if (result.success)
			return Status.Success;
		throw new RuntimeException();
	}
	
	public boolean unFollowUser(String userId) throws Exception {
		return request(API.User.UnFollow, CreateMap.of("unfollowUserId", userId), _successOrPendingDigester).success;
	}
	
	public Status isUserFriend(String friendUserId) throws Exception {
		if (_loginPing != null) {
			return isUserFriend(_loginPing.userId, friendUserId);
		}
		throw new RuntimeException();
	}
	
	public static class StringResponse {
		public String response;
	}
	
	TypedDigester<StringResponse> _responseStringDigester = TypedDigesters.create(StringResponse.class, "adaptiveblue");
	public Status isUserFriend(String userId, String friendUserId) throws Exception {
		String result = request(API.User.IsFriend, CreateMap.of("userId", userId, "friendUserId", friendUserId), _responseStringDigester).response;
		if ("true".equals(result))
			return Status.Success;
		else if ("false".equals(result))
			return Status.Failure;
		else if ("pending".equals(result))
			return Status.Pending;
		throw new RuntimeException();
	}

	TypedDigester<List<String>> _userListDigester = TypedDigesters.createList(String.class, "adaptiveblue/response/users", "user/username");
	public List<String> findUsers(String userId) throws Exception {
		return request(API.Glue.FindUsers, CreateMap.of("userId", userId), _userListDigester);
	}
}
