/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.xmpp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.isf.OHCoreTestCase;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.junit.jupiter.api.Test;

class TestServer extends OHCoreTestCase {

	Server server;

	@Test
	void testNewServer() {
		server = Server.getInstance();
		assertThat(server).isNotNull();
		Server newServer = Server.getInstance();
		assertThat(server).isSameAs(newServer);
	}

	@Test
	void testLogin() {
		server = Server.getInstance();
		assertThat(server).isNotNull();
		assertThatThrownBy(() -> server.login("isf", "isf123"));
	}

	@Test
	void testGets() {
		server = Server.getInstance();
		assertThat(server).isNotNull();
		assertThat(server.getRoster()).isInstanceOf(Roster.class);
		assertThat(server.getChatManager()).isInstanceOf(ChatManager.class);
		assertThat(server.getUserAddress()).isEqualTo("@null");
		assertThat(server.getTransferManager()).isInstanceOf(FileTransferManager.class);
		assertThat(server.getConnection()).isInstanceOf(XMPPConnection.class);
		assertThat(server.getChat("to", "id", new MessageListener() {

			@Override
			public void processMessage(Chat chat, Message message) {

			}
		})).isInstanceOf(Chat.class);
	}

}
