package com.n0tice.api.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import com.n0tice.api.client.exceptions.AuthorisationException;
import com.n0tice.api.client.exceptions.BadRequestException;
import com.n0tice.api.client.exceptions.MissingCredentialsExeception;
import com.n0tice.api.client.exceptions.N0ticeException;
import com.n0tice.api.client.exceptions.NotAllowedException;
import com.n0tice.api.client.exceptions.NotFoundException;
import com.n0tice.api.client.exceptions.ParsingException;
import com.n0tice.api.client.model.AccessToken;
import com.n0tice.api.client.model.Content;
import com.n0tice.api.client.model.Group;
import com.n0tice.api.client.model.HistoryItem;
import com.n0tice.api.client.model.MediaFile;
import com.n0tice.api.client.model.MediaType;
import com.n0tice.api.client.model.ModerationComplaint;
import com.n0tice.api.client.model.ModerationComplaintType;
import com.n0tice.api.client.model.NewUserResponse;
import com.n0tice.api.client.model.Noticeboard;
import com.n0tice.api.client.model.NoticeboardResultSet;
import com.n0tice.api.client.model.NoticeboardSearchQuery;
import com.n0tice.api.client.model.Reoccurence;
import com.n0tice.api.client.model.ResultSet;
import com.n0tice.api.client.model.SearchQuery;
import com.n0tice.api.client.model.Update;
import com.n0tice.api.client.model.User;
import com.n0tice.api.client.model.VideoAttachment;
import com.n0tice.api.client.oauth.N0ticeOauthApi;
import com.n0tice.api.client.parsers.ExifParser;
import com.n0tice.api.client.parsers.HistoryParser;
import com.n0tice.api.client.parsers.ModerationComplaintParser;
import com.n0tice.api.client.parsers.NoticeboardParser;
import com.n0tice.api.client.parsers.SearchParser;
import com.n0tice.api.client.parsers.UserParser;
import com.n0tice.api.client.urls.SearchUrlBuilder;
import com.n0tice.api.client.urls.UrlBuilder;
import com.n0tice.api.client.util.HttpFetcher;

public class N0ticeApi {
	
	private static Logger log = Logger.getLogger(N0ticeApi.class);
	
	private static final String UTF_8 = "UTF-8";
	private static final String COMMA = ",";	
	private static DateTimeFormatter LOCAL_DATE_TIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
	private static DateTimeFormatter ZULE_TIME_FORMAT = ISODateTimeFormat.dateTimeNoMillis();
	
	private final String apiUrl;	
	private final UrlBuilder urlBuilder;
	private final SearchUrlBuilder searchUrlBuilder;
	private final HttpFetcher httpFetcher;
	private final SearchParser searchParser;
	private final UserParser userParser;
	private final NoticeboardParser noticeboardParser;
	private final ModerationComplaintParser moderationComplaintParser;
	private final HistoryParser historyParser;
	private final ExifParser exifParser;

	private OAuthService service;
	private Token scribeAccessToken;
	
	public N0ticeApi(String apiUrl) {
		this.apiUrl = apiUrl;
		this.urlBuilder = new UrlBuilder(apiUrl);
		this.searchUrlBuilder = new SearchUrlBuilder(apiUrl);
		this.httpFetcher = new HttpFetcher();
		this.searchParser = new SearchParser();
		this.userParser = new UserParser();
		this.noticeboardParser = new NoticeboardParser();
		this.historyParser = new HistoryParser();
		this.moderationComplaintParser = new ModerationComplaintParser();
		this.exifParser = new ExifParser();
	}
	
	public N0ticeApi(String apiUrl, String consumerKey, String consumerSecret, AccessToken accessToken) {
		this.apiUrl = apiUrl;
		this.urlBuilder = new UrlBuilder(apiUrl);
		this.searchUrlBuilder = new SearchUrlBuilder(apiUrl);
		this.httpFetcher = new HttpFetcher();
		this.searchParser = new SearchParser();
		this.userParser = new UserParser();
		this.noticeboardParser = new NoticeboardParser();
		this.historyParser = new HistoryParser();
		this.moderationComplaintParser = new ModerationComplaintParser();
		this.exifParser = new ExifParser();
		
		service = new ServiceBuilder().provider(new N0ticeOauthApi(apiUrl))
			.apiKey(consumerKey)
			.apiSecret(consumerSecret)
			.build();
		scribeAccessToken = new Token(accessToken.getToken(), accessToken.getSecret());
	}
	
	public Content get(String id) throws N0ticeException {
		return searchParser.parseReport(httpFetcher.fetchContent(urlBuilder.get(id), UTF_8));
	}
	
	public List<HistoryItem> getHistory(String id) throws N0ticeException {
		return historyParser.parse(httpFetcher.fetchContent(urlBuilder.getHistory(id), UTF_8));
	}
	
	public List<ModerationComplaint> getModerationComplaints(String id) throws N0ticeException {
		final OAuthRequest request = createOauthRequest(Verb.GET, urlBuilder.get(id) + "/flags");
		oauthSignRequest(request);
		
		final Response response = request.send();
		if (response.getCode() == 200) {
			return moderationComplaintParser.parse(response.getBody());
		}
		
		handleExceptions(response);
		throw new N0ticeException(response.getBody());		
	}
	
	public void closeModerationComplaint(String contentId, int flagId) throws N0ticeException {
		final OAuthRequest request = createOauthRequest(Verb.POST, urlBuilder.closeModerationComplaint(contentId, flagId));
		oauthSignRequest(request);
		
		final Response response = request.send();
		if (response.getCode() == 200) {
			return;
		}
		
		handleExceptions(response);
		throw new N0ticeException(response.getBody());		
	}
	
	public List<String> getReposts(String id) throws N0ticeException {
		final OAuthRequest request = createOauthRequest(Verb.GET, urlBuilder.get(id) + "/reposts");
		oauthSignRequest(request);
		
		final Response response = request.send();
		if (response.getCode() == 200) {
			return searchParser.parseReposts(response.getBody());
		}
		
		handleExceptions(response);
		throw new N0ticeException(response.getBody());		
	}
	
	public Update getUpdate(String id) throws N0ticeException {
		return searchParser.parseUpdate(httpFetcher.fetchContent(urlBuilder.get(id), UTF_8));
	}
	
	public Content authedGet(String id) throws N0ticeException {		
		final OAuthRequest request = createOauthRequest(Verb.GET, urlBuilder.get(id));
		oauthSignRequest(request);
		
		final Response response = request.send();
		if (response.getCode() == 200) {
			return searchParser.parseReport(response.getBody());
		}
		
		handleExceptions(response);
		throw new N0ticeException(response.getBody());
	}
	
	public Map<String, Map<String, String>> imageExif(String id) throws N0ticeException {
		final OAuthRequest request = createOauthRequest(Verb.GET, urlBuilder.get("image/" + id + "/exif"));
		oauthSignRequest(request);

		final Response response = request.send();
		if (response.getCode() == 200) {
			return exifParser.parse(response.getBody());
		}
		
		handleExceptions(response);
		throw new N0ticeException(response.getBody());
	}
	
	public ResultSet search(SearchQuery searchQuery) throws N0ticeException {
		return searchParser.parseSearchResults(httpFetcher.fetchContent(searchUrlBuilder.toUrl(searchQuery), UTF_8));
	}
	
	public NoticeboardResultSet searchNoticeboards(NoticeboardSearchQuery noticeboardSearchQuery) throws N0ticeException {
		return noticeboardParser.parseNoticeboardSearchResults(httpFetcher.fetchContent(searchUrlBuilder.toUrl(noticeboardSearchQuery), UTF_8));
	}
	
	public User user(String username) throws N0ticeException {
		return userParser.parseUserProfile(httpFetcher.fetchContent(urlBuilder.userProfile(username), UTF_8));
	}
	
	public List<User> followedUsers(String username) throws N0ticeException {
		return userParser.parseUserProfiles(httpFetcher.fetchContent(urlBuilder.userFollowedUsers(username), UTF_8));
	}
	
	public List<Noticeboard> followedNoticeboards(String username) throws N0ticeException {
		return noticeboardParser.parseNoticeboards(httpFetcher.fetchContent(urlBuilder.userFollowedNoticeboards(username), UTF_8));
	}
	
	public List<Noticeboard> noticeboards(String username) throws N0ticeException {
		return noticeboardParser.parseNoticeboards(httpFetcher.fetchContent(urlBuilder.userNoticeboards(username), UTF_8));
	}
	
	public Noticeboard noticeBoard(String noticeboard) throws N0ticeException {
		return noticeboardParser.parseNoticeboardResult(httpFetcher.fetchContent(urlBuilder.noticeBoard(noticeboard), UTF_8));
	}
	
	public User verify() throws N0ticeException {
		OAuthRequest request = createOauthRequest(Verb.GET, apiUrl + "/verify");	// TODO most be a POST
		oauthSignRequest(request);
		
		Response response = request.send();
		
		final String responseBody = response.getBody();
		if (response.getCode() == 200) {
	    	return userParser.parseUserProfile(responseBody);
		}
		
		handleExceptions(response);
		throw new N0ticeException(response.getBody());
	}
	
	public Content postReport(String headline, Double latitude, Double longitude, String body, String link, MediaFile image, VideoAttachment video, String noticeboard) throws N0ticeException {
		return postReport(headline, latitude, longitude, body, link, image, video, noticeboard, null);		
	}
	
	public Noticeboard createNoticeboard(String name, String description, boolean moderated, Date endDate, Set<MediaType> supportedMediaTypes, String group, 
			MediaFile cover, boolean featured) throws N0ticeException {
		return postNewNoticeboard(null, name, description, moderated, endDate, supportedMediaTypes, group, cover, featured);
	}
	
	@Deprecated // omit domain argument - the api will generate this for you, based on the name
	public Noticeboard createNoticeboard(String domain, String name, String description, boolean moderated, Date endDate, Set<MediaType> supportedMediaTypes, String group, MediaFile cover, boolean featured) throws N0ticeException {
		return postNewNoticeboard(domain, name, description, moderated, endDate, supportedMediaTypes, group, cover, featured);
	}
	
	public Noticeboard editNoticeboard(String domain, String name, String description, Boolean moderated, Boolean featured, MediaFile cover) throws N0ticeException {		
		OAuthRequest request = createOauthRequest(Verb.POST, urlBuilder.noticeBoard(domain));
		MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		
		addEntityPartParameter(entity, "name", name);
		addEntityPartParameter(entity, "description", description);
		addEntityPartParameter(entity, "moderated", moderated != null ? Boolean.toString(moderated) : null);
		addEntityPartParameter(entity, "featured", featured != null ? Boolean.toString(featured) : null);
		if (cover != null) {
			entity.addPart("cover", new ByteArrayBody(cover.getData(), cover.getFilename()));
		}
		
		// TODO implement
		/*
		if (endDate != null) {
			addEntityPartParameter(entity, "endDate", ISODateTimeFormat.dateTimeNoMillis().print(new DateTime(endDate)));
		}
		if (cover != null) {
			entity.addPart("cover", new ByteArrayBody(cover.getData(), cover.getFilename()));
		}
		
		StringBuilder supportedMediaTypesValue = new StringBuilder();
		Iterator<MediaType> supportedMediaTypesIterator = supportedMediaTypes.iterator();
		while(supportedMediaTypesIterator.hasNext()) {
			supportedMediaTypesValue.append(supportedMediaTypesIterator.next());
			if (supportedMediaTypesIterator.hasNext()) {
				supportedMediaTypesValue.append(COMMA);
			}
		}
		addEntityPartParameter(entity, "supportedMediaTypes", supportedMediaTypesValue.toString());
		
		if (group != null) {
			addEntityPartParameter(entity, "group", group);
		}
		*/
		
		request.addHeader("Content-Type", entity.getContentType().getValue());
		addMultipartEntity(request, entity);
		oauthSignRequest(request);
		
		Response response = request.send();
		
		final String responseBody = response.getBody();
		if (response.getCode() == 200) {
	    	return noticeboardParser.parseNoticeboardResult(responseBody);
		}
		
		handleExceptions(response);
		throw new N0ticeException(response.getBody());
	}
 	
	public void closeNoticeboard(String domain) throws N0ticeException {
		OAuthRequest request = createOauthRequest(Verb.POST, urlBuilder.closeNoticeboard(domain));
		oauthSignRequest(request);
		
		final Response response = request.send();		
		if (response.getCode() == 200) {
	    	return;
		}
		
		handleExceptions(response);
		throw new N0ticeException(response.getBody());
	}
	
	public Group createGroup(String name) throws N0ticeException {
		OAuthRequest request = createOauthRequest(Verb.POST, apiUrl + "/groups/new");
		MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		addEntityPartParameter(entity, "name", name);
		
		request.addHeader("Content-Type", entity.getContentType().getValue());
		addMultipartEntity(request, entity);
		oauthSignRequest(request);
		
		Response response = request.send();
		
		final String responseBody = response.getBody();
		if (response.getCode() == 200) {
	    	return searchParser.parseGroupResult(responseBody);
		}
		
		handleExceptions(response);
		throw new N0ticeException(response.getBody());
	}
	
	public Content postReport(String headline, Double latitude, Double longitude, String body, String link, MediaFile image, VideoAttachment video, String noticeboard, DateTime date) throws N0ticeException {
		OAuthRequest request = createOauthRequest(Verb.POST, apiUrl + "/report/new");
		MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		if (headline != null) {
			addStringPart(entity, "headline", headline);
		}
		if (noticeboard != null) {
			addStringPart(entity, "noticeboard", noticeboard);
		}

		if (latitude != null && longitude != null) {			
			addStringPart(entity, "latitude", Double.toString(latitude));
			addStringPart(entity, "longitude", Double.toString(longitude));						
		}
		
		populateUpdateFields(body, link, image, video, entity);
		
		if (date != null) {
			addStringPart(entity, "date", date.toString(ZULE_TIME_FORMAT));
		}
		
		request.addHeader("Content-Type", entity.getContentType().getValue());
		addMultipartEntity(request, entity);
		oauthSignRequest(request);
		
		Response response = request.send();
		
		final String responseBody = response.getBody();
		if (response.getCode() == 200) {
	    	return searchParser.parseReport(responseBody);
		}
		
		handleExceptions(response);
		throw new N0ticeException(response.getBody());
	}
	
	public ResultSet authedSearch(SearchQuery searchQuery) throws N0ticeException {				
		OAuthRequest request = createOauthRequest(Verb.GET, searchUrlBuilder.toUrl(searchQuery));
		oauthSignRequest(request);
		
		Response response = request.send();
		
		final String responseBody = response.getBody();
		if (response.getCode() == 200) {
	    	return searchParser.parseSearchResults(responseBody);
		}
		
		handleExceptions(response);
		throw new N0ticeException(response.getBody());
	}
	
	public Content postEvent(String headline, double latitude,
			double longitude, String body, String link, MediaFile image, VideoAttachment video,
			String noticeboard, LocalDateTime startDate, LocalDateTime endDate, Reoccurence reoccurence, LocalDateTime reoccursTo) throws N0ticeException {		
		final OAuthRequest request = createOauthRequest(Verb.POST, apiUrl + "/event/new");

		final MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		addEntityPartParameter(entity, "headline", headline);
		addEntityPartParameter(entity, "noticeboard", noticeboard);
		addEntityPartParameter(entity, "latitude", Double.toString(latitude));
		addEntityPartParameter(entity, "longitude", Double.toString(longitude));
		populateUpdateFields(body, link, image, video, entity);
		
		addEntityPartParameter(entity, "startDate", startDate.toString(LOCAL_DATE_TIME_FORMAT));
		addEntityPartParameter(entity, "endDate", endDate.toString(LOCAL_DATE_TIME_FORMAT));
		if (reoccurence != null && reoccursTo != null) {
			addEntityPartParameter(entity, "reoccurs", reoccurence.toString());
			addEntityPartParameter(entity, "reoccursTo", reoccursTo.toString(LOCAL_DATE_TIME_FORMAT));
		}
		
		request.addHeader("Content-Type", entity.getContentType().getValue());
		addMultipartEntity(request, entity);
		oauthSignRequest(request);
		
		Response response = request.send();
		
		final String responseBody = response.getBody();
		if (response.getCode() == 200) {
	    	return searchParser.parseReport(responseBody);
		}
		
		handleExceptions(response);
		throw new N0ticeException(response.getBody());
	}
	
	public Update postReportUpdate(String reportId, String body, String link, MediaFile image, VideoAttachment video) throws N0ticeException {
		OAuthRequest request = createOauthRequest(Verb.POST, apiUrl + "/" + reportId  + "/update/new");
		MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		populateUpdateFields(body, link, image, video, entity);

		request.addHeader("Content-Type", entity.getContentType().getValue());
		addMultipartEntity(request, entity);
		oauthSignRequest(request);
		
		final Response response = request.send();
		
		if (response.getCode() == 200) {
	    	return searchParser.parseUpdate(response.getBody());
		}
		
		handleExceptions(response);
		throw new N0ticeException(response.getBody());
	}
	
	public boolean voteInteresting(String id) throws N0ticeException {
		OAuthRequest request = createOauthRequest(Verb.POST, apiUrl + "/" + id + "/vote/interesting");	
		oauthSignRequest(request);
		
		final Response response = request.send();		
		if (response.getCode() == 200) {
	    	return true;
		}

		handleExceptions(response);
		throw new N0ticeException(response.getBody());
	}
	
	public boolean flagAsInappropriate(String id, ModerationComplaintType type, String notes, String email) throws N0ticeException {
		final OAuthRequest request = createOauthRequest(Verb.POST, apiUrl + "/" + id + "/flag");
		if (type != null) {
			addBodyParameter(request, "type", type.toString());
		}
		if (notes != null) {
			addBodyParameter(request, "notes", notes);
		}
		if (email != null) {
			addBodyParameter(request, "email", email);
		}
		oauthSignRequest(request);
		
		final Response response = request.send();		
		if (response.getCode() == 200) {
	    	return true;
		}
		
		handleExceptions(response);
		throw new N0ticeException(response.getBody());
	}
	
	public boolean repost(String id, String noticeboard) throws N0ticeException {
		OAuthRequest request = createOauthRequest(Verb.POST, apiUrl + "/" + id + "/repost");
		addBodyParameter(request, "noticeboard", noticeboard);
		oauthSignRequest(request);

		final Response response = request.send();		
		if (response.getCode() == 200) {
	    	return true;
		}

		handleExceptions(response);
		throw new N0ticeException(response.getBody());
	}
	
	public List<String> moderationActions() throws N0ticeException {
		return moderationComplaintParser.parseModerationActions(httpFetcher.fetchContent(urlBuilder.moderationActions(), UTF_8));
	}
	
	public List<String> moderationStates() throws N0ticeException {
		return moderationComplaintParser.parseModerationActions(httpFetcher.fetchContent(urlBuilder.moderationStates(), UTF_8));
	}
	
	public boolean moderate(String id, String notes, String action) throws N0ticeException {
		OAuthRequest request = createOauthRequest(Verb.POST, apiUrl + "/" + id + "/moderate/" + action);
		addBodyParameter(request, "notes", notes);
		oauthSignRequest(request);
		
		final Response response = request.send();		
		if (response.getCode() == 200) {
	    	return true;
		}

		handleExceptions(response);
		throw new N0ticeException(response.getBody());
	}
	
	public int interestingVotes(String id) throws N0ticeException {
		OAuthRequest request = createOauthRequest(Verb.GET, apiUrl + "/" + id + "/votes/interesting");	
			
		final Response response = request.send();
		
		if (response.getCode() == 200) {
			return searchParser.parseVotes(response.getBody());
		}
		
		handleExceptions(response);
		throw new N0ticeException(response.getBody());
	}
	
	public List<String> notifications(String username) throws N0ticeException {		
		OAuthRequest request = createOauthRequest(Verb.GET, urlBuilder.userNotifications(username));
		oauthSignRequest(request);
		
		final Response response = request.send();
		
		if (response.getCode() == 200) {
			return searchParser.parseNotifications(response.getBody());
		}
		
		handleExceptions(response);
		throw new N0ticeException(response.getBody());
	}
	
	public Content updateReport(String id, String headline, String body) throws N0ticeException {	
		OAuthRequest request = createOauthRequest(Verb.POST, apiUrl + "/" + id);	
		addBodyParameter(request, "headline", headline);
		oauthSignRequest(request);
		
		Response response = request.send();
		
		final String responseBody = response.getBody();
		if (response.getCode() == 200) {
	    	return searchParser.parseReport(responseBody);
		}
	
		handleExceptions(response);
		throw new N0ticeException(response.getBody());
	}
	
	public boolean followUser(String username) throws N0ticeException {
		OAuthRequest request = createOauthRequest(Verb.POST, apiUrl + "/user/" + username + "/follow");	
		oauthSignRequest(request);
		
		final Response response = request.send();
		
		if (response.getCode() == 200) {
	    	return true;
		}
		
		handleExceptions(response);
		throw new N0ticeException(response.getBody());
	}
	
	public boolean unfollowUser(String username) throws N0ticeException {
		OAuthRequest request = createOauthRequest(Verb.POST, apiUrl + "/user/" + username + "/unfollow");	
		oauthSignRequest(request);
		
		final Response response = request.send();
		
		if (response.getCode() == 200) {
	    	return true;
		}
		
		handleExceptions(response);
		throw new N0ticeException(response.getBody());
	}
	
	public boolean followNoticeboard(String noticeboard) throws N0ticeException {
		OAuthRequest request = createOauthRequest(Verb.POST, apiUrl + "/noticeboard/" + noticeboard + "/follow");	
		oauthSignRequest(request);
		
		final Response response = request.send();
		
		if (response.getCode() == 200) {
	    	return true;
		}
		
		handleExceptions(response);
		throw new N0ticeException(response.getBody());
	}
	
	public boolean unfollowNoticeboard(String noticeboard) throws N0ticeException {
		OAuthRequest request = createOauthRequest(Verb.POST, apiUrl + "/noticeboard/" + noticeboard + "/unfollow");	
		oauthSignRequest(request);
		
		final Response response = request.send();
		
		if (response.getCode() == 200) {
	    	return true;
		}
		
		handleExceptions(response);
		throw new N0ticeException(response.getBody());
	}
	
	public NewUserResponse createUser(String consumerKey, String username, String password, String email) throws N0ticeException {
		OAuthRequest request = createOauthRequest(Verb.POST, apiUrl + "/user/new");		
		addBodyParameter(request, "consumerkey", consumerKey);
		addBodyParameter(request, "username", username);
		addBodyParameter(request, "password", password);
		addBodyParameter(request, "email", email);
		
		final Response response = request.send();

		final String repsonseBody = response.getBody();
		if (response.getCode() == 200) {		
			return new UserParser().parseNewUserResponse(repsonseBody);
		}
		
		handleExceptions(response);
		throw new N0ticeException(response.getBody());
	}
	
	public AccessToken authUser(String consumerKey, String username, String password, String consumerSecret) throws N0ticeException {
		log.info("Attempting to auth user: " + consumerKey + ", " + username + ", " + password + ", " + consumerSecret);
		OAuthRequest request = createOauthRequest(Verb.POST, apiUrl + "/user/auth");
		addBodyParameter(request, "consumerkey", consumerKey);
		addBodyParameter(request, "username", username);
		addBodyParameter(request, "password", password);

		// Manually sign this request using the consumer secret rather than the access key/access secret.
		addBodyParameter(request, "oauth_signature_method", "HMAC-SHA1");
		addBodyParameter(request, "oauth_version", "1.0");
		addBodyParameter(request, "oauth_timestamp", Long.toString(DateTimeUtils.currentTimeMillis()));
		final String effectiveUrl = request.getCompleteUrl() + "?" + request.getBodyContents();
		addBodyParameter(request, "oauth_signature", sign(effectiveUrl, consumerSecret));
		
		final Response response = request.send();
		final String responseBody = response.getBody();
		if (response.getCode() == 200) {		
			return new UserParser().parseAuthUserResponse(responseBody);
		}
		
		handleExceptions(response);
		throw new N0ticeException(response.getBody());
	}
	
	public AccessToken authGuardianUser(String consumerKey, String token, String consumerSecret) throws N0ticeException {
		log.info("Attempting to auth guardian user: " + consumerKey + ", " + token);
		final OAuthRequest request = createOauthRequest(Verb.POST, apiUrl + "/user/auth");
		addBodyParameter(request, "consumerkey", consumerKey);
		addBodyParameter(request, "guardianToken", token);

		// Manually sign this request using the consumer secret rather than the access key/access secret.
		addBodyParameter(request, "oauth_signature_method", "HMAC-SHA1");
		addBodyParameter(request, "oauth_version", "1.0");
		addBodyParameter(request, "oauth_timestamp", Long.toString(DateTimeUtils.currentTimeMillis()));
		final String effectiveUrl = request.getCompleteUrl() + "?" + request.getBodyContents();
		addBodyParameter(request, "oauth_signature", sign(effectiveUrl, consumerSecret));
		
		final Response response = request.send();
		final String responseBody = response.getBody();
		if (response.getCode() == 200) {		
			return new UserParser().parseAuthUserResponse(responseBody);
		}
		
		handleExceptions(response);
		throw new N0ticeException(response.getBody());
	}
	
	public AccessToken authGuardianCookie(String consumerKey, String cookie, String consumerSecret) throws N0ticeException {
		final OAuthRequest request = createOauthRequest(Verb.POST, apiUrl + "/user/auth");
		addBodyParameter(request, "consumerkey", consumerKey);
		addBodyParameter(request, "guardianCookie", cookie);

		// Manually sign this request using the consumer secret rather than the access key/access secret.
		addBodyParameter(request, "oauth_signature_method", "HMAC-SHA1");
		addBodyParameter(request, "oauth_version", "1.0");
		addBodyParameter(request, "oauth_timestamp", Long.toString(DateTimeUtils.currentTimeMillis()));
		final String effectiveUrl = request.getCompleteUrl() + "?" + request.getBodyContents();
		addBodyParameter(request, "oauth_signature", sign(effectiveUrl, consumerSecret));
		
		final Response response = request.send();
		final String responseBody = response.getBody();
		if (response.getCode() == 200) {		
			return new UserParser().parseAuthUserResponse(responseBody);
		}
		
		handleExceptions(response);
		throw new N0ticeException(response.getBody());
	}
	
	public User updateUserDetails(String username, String displayName, String bio, MediaFile image) throws N0ticeException {
		OAuthRequest request = createOauthRequest(Verb.POST, apiUrl + "/user/" + username);		
		MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		if (displayName != null) {
			addStringPart(entity, "displayName", displayName);
		}
		if (bio != null) {
			addStringPart(entity, "bio", bio);
		}
		if (image != null) {
			entity.addPart("image", new ByteArrayBody(image.getData(), image.getFilename()));
		}
		
		request.addHeader("Content-Type", entity.getContentType().getValue());
		addMultipartEntity(request, entity);
		oauthSignRequest(request);
		
		Response response = request.send();

		final String repsonseBody = response.getBody();
		if (response.getCode() == 200) {
			return new UserParser().parseUserProfile(repsonseBody);
		}
		
		handleExceptions(response);
		throw new N0ticeException(response.getBody());
	}
	
	public boolean deleteReport(String id) throws N0ticeException {
		OAuthRequest request = createOauthRequest(Verb.DELETE, apiUrl + "/" + id);	
		oauthSignRequest(request);
		
		final Response response = request.send();
		
		if (response.getCode() == 200) {
			return true;
		}
		
		handleExceptions(response);
		throw new N0ticeException(response.getBody());
	}
	
	private void populateUpdateFields(String body, String link, MediaFile image, VideoAttachment video, MultipartEntity entity) throws N0ticeException {
		if (body != null) {
			try {
				entity.addPart("body", new StringBody(body, Charset.forName(UTF_8)));
			} catch (UnsupportedEncodingException e) {
				throw new N0ticeException();
			}
		}
		if (link != null) {
			try {
				entity.addPart("link", new StringBody(link, Charset.forName(UTF_8)));
			} catch (UnsupportedEncodingException e) {
				throw new N0ticeException();
			}
		}
		if (image != null) {
			entity.addPart("image", new ByteArrayBody(image.getData(), image.getFilename()));
		}
		if (video != null) {
			entity.addPart("video", new InputStreamBody(video.getData(), video.getFilename()));			
		}
	}
	
	private Noticeboard postNewNoticeboard(String domain, String name,
			String description, boolean moderated, Date endDate,
			Set<MediaType> supportedMediaTypes, String group, MediaFile cover, boolean featured)
			throws N0ticeException, MissingCredentialsExeception,
			ParsingException {
		OAuthRequest request = createOauthRequest(Verb.POST, apiUrl + "/noticeboards/new");
		MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		if (domain != null) {
			addEntityPartParameter(entity, "domain", domain);
		}
		addEntityPartParameter(entity, "name", name);
		addEntityPartParameter(entity, "description", description);
		addEntityPartParameter(entity, "moderated", Boolean.toString(moderated));
		addEntityPartParameter(entity, "featured", Boolean.toString(featured));
		
		if (endDate != null) {
			addEntityPartParameter(entity, "endDate", ISODateTimeFormat.dateTimeNoMillis().print(new DateTime(endDate)));
		}
		if (cover != null) {
			entity.addPart("cover", new ByteArrayBody(cover.getData(), cover.getFilename()));
		}
		
		StringBuilder supportedMediaTypesValue = new StringBuilder();
		Iterator<MediaType> supportedMediaTypesIterator = supportedMediaTypes.iterator();
		while(supportedMediaTypesIterator.hasNext()) {
			supportedMediaTypesValue.append(supportedMediaTypesIterator.next());
			if (supportedMediaTypesIterator.hasNext()) {
				supportedMediaTypesValue.append(COMMA);
			}
		}
		addEntityPartParameter(entity, "supportedMediaTypes", supportedMediaTypesValue.toString());
		
		if (group != null) {
			addEntityPartParameter(entity, "group", group);
		}
		
		request.addHeader("Content-Type", entity.getContentType().getValue());
		addMultipartEntity(request, entity);
		oauthSignRequest(request);
		
		Response response = request.send();
		
		final String responseBody = response.getBody();
		if (response.getCode() == 200) {
	    	return noticeboardParser.parseNoticeboardResult(responseBody);
		}
		
		handleExceptions(response);
		throw new N0ticeException(response.getBody());
	}
		
	private byte[] extractMultpartBytes(MultipartEntity entity) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		entity.writeTo(byteArrayOutputStream);			
		byte[] byteArray = byteArrayOutputStream.toByteArray();
		return byteArray;
	}
	
	private void handleExceptions(Response response) throws N0ticeException {
		log.error("Exception during n0tice api call: " + response.getCode() + ", " + response.getBody());
		if (response.getCode() == 404) {
			throw new NotFoundException("Not found");
		}
		if (response.getCode() == 403) {
			throw new NotAllowedException();
		}		
		if (response.getCode() == 401) {
			throw new AuthorisationException(response.getBody());
		}
		if (response.getCode() == 400) {
			throw new BadRequestException(response.getBody());
		}		
	}
	
	private void addBodyParameter(OAuthRequest request, String parameter, String value) {
		if (value != null) {
			request.addBodyParameter(parameter, value);
		}
	}
	
	private void addEntityPartParameter(MultipartEntity entity, String parameter, String value) {
		if (value != null) {
			try {
				entity.addPart(parameter, new StringBody(value, Charset.forName(UTF_8)));
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private void addMultipartEntity(OAuthRequest request, MultipartEntity entity) throws N0ticeException {
		try {
			request.addPayload(extractMultpartBytes(entity));
		} catch (IOException e) {			
			e.printStackTrace();
			throw new N0ticeException();
		}
	}
	
	private void oauthSignRequest(OAuthRequest request) throws MissingCredentialsExeception {
		if (scribeAccessToken != null) {
			service.signRequest(scribeAccessToken, request);
			return;
		}
		throw new MissingCredentialsExeception();
	}

	private String sign(String effectiveUrl, String secret) throws N0ticeException {
	    SecretKeySpec key;
		try {
			key = new SecretKeySpec(secret.getBytes(UTF_8), "HmacSHA1");
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(key);
			byte[] bytes = mac.doFinal(effectiveUrl.getBytes(UTF_8));
			return new String(Base64.encodeBase64(bytes)).replace("\r\n", "");
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new N0ticeException();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new N0ticeException();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			throw new N0ticeException();
		}
	}
	
	private void addStringPart(MultipartEntity entity, String parameter, String value) throws N0ticeException {
		try {
			entity.addPart(parameter, new StringBody(value, Charset.forName(UTF_8)));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new N0ticeException();
		}
	}
    
    private OAuthRequest createOauthRequest(Verb verb, String url) {
        OAuthRequest request = new OAuthRequest(verb, url);
        request.setConnectTimeout(HttpFetcher.HTTP_TIMEOUT, TimeUnit.MILLISECONDS);
        request.setReadTimeout(HttpFetcher.HTTP_TIMEOUT, TimeUnit.MILLISECONDS);
        return request;
    }
	
}
