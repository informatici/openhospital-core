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
package org.isf.xmpp.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.isf.OHCoreTestCase;
import org.isf.xmpp.service.Server;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

class TestInteraction extends OHCoreTestCase {

	Interaction interaction;
	@TempDir
	File tempDir;

	@Mock
	Server serverMock;
	@Mock
	Roster rosterMock;
	@Mock
	RosterEntry rosterEntryMock;
	@Mock
	Presence presenceMock;
	@Mock
	Connection connectionMock;

	private AutoCloseable closeable;

	@BeforeEach
	void setUp() {
		closeable = MockitoAnnotations.openMocks(this);
		try (MockedStatic<Server> mockedInteraction = mockStatic(Server.class)) {
			when(Server.getInstance()).thenReturn(serverMock);
			when(serverMock.getRoster()).thenReturn(rosterMock);
			interaction = new Interaction();
		}
	}

	@AfterEach
	void closeService() throws Exception {
		closeable.close();
	}

	@Test
	void testNewInteraction() {
		assertThat(interaction).isNotNull();
	}

	@Test
	void testGetContactOnline() {
		Collection<RosterEntry> rosterEntryCollection = new ArrayList<>();
		rosterEntryCollection.add(rosterEntryMock);
		when(rosterMock.getEntries()).thenReturn(rosterEntryCollection);
		when(rosterEntryMock.getUser()).thenReturn("isf");
		when(rosterMock.getPresence(any(String.class))).thenReturn(presenceMock);
		when(presenceMock.isAvailable()).thenReturn(true);
		when(rosterEntryMock.getName()).thenReturn("admin");
		Collection<String> contacts = interaction.getContactOnline();
		assertThat(contacts).isNotEmpty();
		String firstElement = contacts.iterator().next();
		assertThat(firstElement).isEqualTo("admin");
	}

	@Test
	void testUserFromAddress() {
		assertThat(interaction.userFromAddress("first@last")).isEqualTo("first");
	}

	@Test
	void testSendMessage() {
		MessageListener listener = new MessageListener() {

			@Override
			public void processMessage(Chat chat, Message message) {

			}
		};
		interaction.sendMessage(listener, "Text", "TO", false);
	}

	@Test
	void testSendFile() {
		File file = new File(tempDir, "someFileName");
		when(serverMock.getConnection()).thenReturn(connectionMock);
		assertThatThrownBy(() -> interaction.sendFile("admin", file, "someDescription"))
						.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void testGets() {
		assertThat(interaction.getRoster()).isInstanceOf(Roster.class);
		assertThat(interaction.getChatManager()).isNull();
		assertThat(interaction.getConnection()).isNull();
		assertThat(interaction.getServer()).isSameAs(serverMock);
	}
}
