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
package org.isf.xmpp.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.isf.xmpp.service.Server;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Interaction {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Interaction.class);

	private Server server;
	private Roster roster;
	
	public Interaction() {
		server = Server.getInstance();
		roster = server.getRoster(); 
	}

	public Collection<String> getContactOnline() {

		Presence presence;
		Collection<RosterEntry> entries = roster.getEntries();
		Collection<String> entriesOnline = new ArrayList<>();
		for (RosterEntry rosterEntry : entries) {
			presence = roster.getPresence(rosterEntry.getUser());
			if (presence.isAvailable()) {
				entriesOnline.add(rosterEntry.getName());
			}
		}
		return entriesOnline;
	}

	public void sendMessage(MessageListener listener, String textMessage, String to, final boolean visualize) {
		to = to + server.getUserAddress();
		Message message = new Message(to);
		message.setBody(textMessage);
		message.setThread(to);
		Chat chat = server.getChat(to, message.getThread(), listener);

		try {
			chat.sendMessage(message);
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
		}
	}

	public String userFromAddress(String address) {
		int index;
		index = address.indexOf("@");
		return address.substring(0, index);
	}

	public void sendFile(String user, File file, String description) {
		LOGGER.debug("File transfer requested.");
		new ServiceDiscoveryManager(server.getConnection());
		FileTransferManager manager = new FileTransferManager(server.getConnection());
		FileTransferNegotiator.setServiceEnabled(server.getConnection(), true);
		LOGGER.debug("Manager: {}", manager);
		String userID = user + server.getUserAddress() + "/Smack";
		LOGGER.debug("Recipient: {}", userID);
		OutgoingFileTransfer transfer = manager.createOutgoingFileTransfer(userID);
		try {
			transfer.sendFile(file, "msg");
		} catch (XMPPException xmppException) {
			LOGGER.error(xmppException.getMessage(), xmppException);
		}
		LOGGER.debug("Transfer status: {}, {}", transfer.isDone(), transfer.getStatus());

		if (transfer.isDone()) {
			LOGGER.debug("Transfer successfully completed!");
		}
		if (transfer.getStatus().equals(Status.error)) {
			LOGGER.debug("Error while transferring: {}", transfer.getError());
		}
	}

	public ChatManager getChatManager() {
		return server.getChatManager();
	}

	public Connection getConnection() {
		return server.getConnection();
	}

	public Roster getRoster() {
		return server.getRoster();
	}

	public Server getServer() {
		return server;
	}

}
