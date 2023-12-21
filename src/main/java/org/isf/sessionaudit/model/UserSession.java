package org.isf.sessionaudit.model;

import java.util.HashMap;
import java.util.Map;

import org.isf.menu.model.User;
import org.isf.utils.time.DelayTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserSession {

	protected static final int LOGIN_FAILED = 2;
	private static final String LOGOUT_TIMER = "logoutTimer";
	private static final String USER = "user";
	private static final String SESSION_ID = "sessionAuditId";

	private static Map<String, Object> map = new HashMap<>();

	protected static final Logger LOGGER = LoggerFactory.getLogger(UserSession.class);

	public static DelayTimer getTimer() {
		return (DelayTimer) map.get(LOGOUT_TIMER);
	}

	public static void setTimer(DelayTimer logoutTimer) {
		map.put(LOGOUT_TIMER, logoutTimer);
	}

	public static void setUser(User myUser) {
		map.put(USER, myUser);
	}

	public static User getUser() {
		return (User) map.get(USER);
	}

	public static boolean isLoggedIn() {
		return map.get(USER) != null;
	}

	public static void removeUser() {
		map.remove(USER);
	}
	
	public static void setSessionAuditId(int sessionId) {
		map.put(SESSION_ID, sessionId);
	}
	
	public static Integer getSessionAuditId() {
		return (Integer) map.get(SESSION_ID);
	}
}
