/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.sms.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.menu.manager.Context;
import org.isf.sessionaudit.model.UserSession;
import org.isf.sms.TestSms;
import org.isf.sms.model.Sms;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

class SmsSenderTest extends OHCoreTestCase {

	private static TestSms testSms;

	@Mock
	ApplicationContext applicationContextMock;
	@Mock
	SmsOperations smsOperationsMock;
	@Mock
	SmsSenderOperations smsSenderOperationsMock;

	@BeforeAll
	static void setUpClass() {
		testSms = new TestSms();
	}

	private AutoCloseable closeable;

	@BeforeEach
	void setUp() {
		closeable = MockitoAnnotations.openMocks(this);
		cleanH2InMemoryDb();
		UserSession.removeUser();
		Context.setApplicationContext(applicationContextMock);
	}

	@AfterEach
	void closeService() throws Exception {
		closeable.close();
	}

	@Test
	void run_shouldNotThrow_whenRunningIsFalse() throws Exception {
		assertDoesNotThrow(() -> {
			SmsSender smsSender = new SmsSender();
			smsSender.setRunning(false);
			smsSender.run();
		});
	}

	@Test
	void run_shouldNotThrow_whenNoSmsToSend() throws Exception {
		assertDoesNotThrow(() -> {
			when(applicationContextMock.getBean(SmsOperations.class)).thenReturn(smsOperationsMock);
			when(applicationContextMock.getBean(SmsSenderOperations.class)).thenReturn(smsSenderOperationsMock);
			SmsSender smsSender = new SmsSender();
			when(smsOperationsMock.getList()).thenReturn(null);
			new Thread(smsSender::run).start();
			Thread.sleep(2000);
			smsSender.setRunning(false);
		});
	}

	@Test
	void run_shouldNotThrow_whenSmsIsNotSent() throws Exception {
		assertDoesNotThrow(() -> {
			when(applicationContextMock.getBean(SmsOperations.class)).thenReturn(smsOperationsMock);
			when(applicationContextMock.getBean(SmsSenderOperations.class)).thenReturn(smsSenderOperationsMock);
			List<Sms> smsList = new ArrayList<>();
			Sms sms = testSms.setup(true);
			smsList.add(sms);
			when(smsOperationsMock.getList()).thenReturn(smsList);
			when(smsSenderOperationsMock.initialize()).thenReturn(true);

			SmsSender smsSender = new SmsSender();
			new Thread(smsSender::run).start();
			Thread.sleep(2000);
			smsSender.setRunning(false);
		});
	}

	@Test
	void run_shouldNotThrow_whenSmsListLoadingFails() throws Exception {
		assertDoesNotThrow(() -> {
			when(applicationContextMock.getBean(SmsOperations.class)).thenReturn(smsOperationsMock);
			when(applicationContextMock.getBean(SmsSenderOperations.class)).thenReturn(smsSenderOperationsMock);
			when(smsOperationsMock.getList()).thenThrow(new OHServiceException(new OHExceptionMessage("some message")));
			when(smsSenderOperationsMock.initialize()).thenReturn(false);

			SmsSender smsSender = new SmsSender();
			new Thread(smsSender::run).start();
			Thread.sleep(2000);
			smsSender.setRunning(false);
		});
	}

	@Test
	void run_shouldNotThrow_whenSmsIsSent() throws Exception {
		assertDoesNotThrow(() -> {
			when(applicationContextMock.getBean(SmsOperations.class)).thenReturn(smsOperationsMock);
			when(applicationContextMock.getBean(SmsSenderOperations.class)).thenReturn(smsSenderOperationsMock);
			List<Sms> smsList = new ArrayList<>();
			Sms sms = testSms.setup(true);
			smsList.add(sms);
			when(smsOperationsMock.getList()).thenReturn(smsList);
			when(smsSenderOperationsMock.initialize()).thenReturn(true);
			when(smsSenderOperationsMock.sendSMS(sms)).thenReturn(true);

			SmsSender smsSender = new SmsSender();
			new Thread(smsSender::run).start();
			Thread.sleep(2000);
			smsSender.setRunning(false);
		});
	}

	@Test
	void run_shouldNotThrow_whenSavingSentSmsFails() throws Exception {
		assertDoesNotThrow(() -> {
			when(applicationContextMock.getBean(SmsOperations.class)).thenReturn(smsOperationsMock);
			when(applicationContextMock.getBean(SmsSenderOperations.class)).thenReturn(smsSenderOperationsMock);
			List<Sms> smsList = new ArrayList<>();
			Sms sms = testSms.setup(true);
			smsList.add(sms);
			when(smsOperationsMock.getList()).thenReturn(smsList);
			when(smsSenderOperationsMock.initialize()).thenReturn(true);
			when(smsSenderOperationsMock.sendSMS(sms)).thenReturn(true);
			when(smsOperationsMock.saveOrUpdate(sms)).thenThrow(new OHServiceException(new OHExceptionMessage("some message")));

			SmsSender smsSender = new SmsSender();
			new Thread(smsSender::run).start();
			Thread.sleep(2000);
			smsSender.setRunning(false);
		});
	}

}
