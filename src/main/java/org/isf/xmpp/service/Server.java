/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.xmpp.service;

import org.isf.generaldata.XmppData;
import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {
	private static Server server;
	private XMPPConnection connection;
	private String user;

	private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

	private Server() {
	}

	public void login(String userName, String password) throws XMPPException {
		XmppData.initialize();
		String domain = XmppData.domain;
		int port = XmppData.port;
		user = userName;
		ConnectionConfiguration config = new ConnectionConfiguration(domain, port);
		connection = new XMPPConnection(config);
		connection.connect();

		try {
			AccountManager user = new AccountManager(connection);
			user.createAccount(userName, password);
			LOGGER.debug("XMPP user created");
			connection.login(userName, password);
		} catch (XMPPException e) {
			LOGGER.debug("XMPP user existing");
			connection.login(userName, password);
		}
	}

	public Roster getRoster() {
		return connection.getRoster();
	}

	public Chat getChat(String to, String id, MessageListener listener) {
		Chat chat;
		id = id + '@' + user;
		if (connection.getChatManager().getThreadChat(id) == null) {
			LOGGER.debug("Creation chat: {}, id = {}", to, id);
			chat = connection.getChatManager().createChat(to, id, listener);
		} else {
			LOGGER.debug("Existing chat: {}, id = {}", to, id);
			chat = connection.getChatManager().getThreadChat(id);
		}
		return chat;
	}

	public ChatManager getChatManager() {
		return connection.getChatManager();
	}

	public String getUserAddress() {
		return '@' + connection.getHost();
	}

	public FileTransferManager getTransferManager() {
		return new FileTransferManager(connection);
	}

	public Connection getConnection() {
		return connection;
	}

	public static Server getInstance() {
		if (server == null) {
			server = new Server();
		}
		return server;
	}

}
