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
package org.isf.sms;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.menu.manager.Context;
import org.isf.menu.model.User;
import org.isf.menu.model.UserGroup;
import org.isf.sessionaudit.model.UserSession;
import org.isf.sms.manager.SmsManager;
import org.isf.sms.model.Sms;
import org.isf.sms.service.SmsIoOperationRepository;
import org.isf.sms.service.SmsOperations;
import org.isf.sms.service.SmsSender;
import org.isf.sms.service.SmsSenderOperations;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.time.TimeTools;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

class Tests extends OHCoreTestCase {

	private static TestSms testSms;

	@Autowired
	SmsOperations smsIoOperation;
	@Autowired
	SmsIoOperationRepository smsIoOperationRepository;
	@Autowired
	SmsManager smsManager;
	@Autowired
	SmsSenderOperations smsSenderOperations;
	@Autowired
	private ApplicationContext applicationContext;

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
	void testSmsGets() throws Exception {
		int code = setupTestSms(false);
		checkSmsIntoDb(code);
	}

	@Test
	void testSmsSets() throws Exception {
		int code = setupTestSms(true);
		checkSmsIntoDb(code);
	}

	@Test
	void testSmsSaveOrUpdate() throws Exception {
		Sms sms = testSms.setup(true);
		Sms newSaveUpdateSms = smsIoOperation.saveOrUpdate(sms);
		checkSmsIntoDb(newSaveUpdateSms.getSmsId());
	}

	@Test
	void testSmsGetByID() throws Exception {
		int code = setupTestSms(false);
		Sms foundSms = smsIoOperation.getByID(code);
		assertThat(foundSms).isNotNull();
		checkSmsIntoDb(foundSms.getSmsId());
	}

	@Test
	void testSmsGetAll() throws Exception {
		LocalDateTime smsDateStart = LocalDateTime.of(2011, 9, 6, 0, 0, 0);
		LocalDateTime smsDateEnd = LocalDateTime.of(2011, 9, 9, 0, 0, 0);
		int code = setupTestSms(false);
		Sms foundSms = smsIoOperation.getByID(code);
		assertThat(foundSms).isNotNull();
		List<Sms> smsList = smsIoOperation.getAll(smsDateStart, smsDateEnd);
		assertThat(smsList.get(0).getSmsText()).isEqualTo(foundSms.getSmsText());
	}

	@Test
	void testSmsGetListNotSent() throws Exception {
		LocalDateTime smsDateStart = LocalDateTime.of(2011, 9, 6, 0, 0, 0);
		LocalDateTime smsDateEnd = LocalDateTime.of(2011, 9, 9, 0, 0, 0);
		int code = setupTestSms(false);
		Sms foundSms = smsIoOperation.getByID(code);
		assertThat(foundSms).isNotNull();
		List<Sms> smsList = smsIoOperation.getList(smsDateStart, smsDateEnd);
		assertThat(smsList).hasSize(1);
	}

	@Test
	void testSmsGetList() throws Exception {
		int code = setupTestSms(false);
		Sms foundSms = smsIoOperation.getByID(code);
		assertThat(foundSms).isNotNull();
		List<Sms> sms = smsIoOperation.getList();
		assertThat(sms.get(0).getSmsText()).isEqualTo(foundSms.getSmsText());
	}

	@Test
	void testIoDeleteSms() throws Exception {
		int code = setupTestSms(false);
		Sms foundSms = smsIoOperation.getByID(code);
		assertThat(foundSms).isNotNull();
		smsIoOperation.delete(foundSms);
		boolean result = smsIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	@Test
	void testIoDeleteByModuleModuleID() throws Exception {
		int code = setupTestSms(false);
		Sms foundSms = smsIoOperation.getByID(code);
		assertThat(foundSms).isNotNull();
		smsIoOperation.deleteByModuleModuleID(foundSms.getModule(), foundSms.getModuleID());

		boolean result = smsIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	@Test
	void testEquals() throws Exception {
		int code = setupTestSms(true);
		Sms foundSms = smsIoOperation.getByID(code);
		assertThat(foundSms).isNotNull();

		assertThat(foundSms.equals(foundSms)).isTrue();

		assertThat(foundSms.equals(Integer.valueOf(1))).isFalse();

		Sms secondSms = new Sms(TimeTools.getNow(), "smsNumber", "smsText", "smsUser");
		assertThat(foundSms.equals(secondSms)).isFalse();
	}

	@Test
	void testHash() throws Exception {
		int code = setupTestSms(true);
		Sms foundSms = smsIoOperation.getByID(code);
		assertThat(foundSms).isNotNull();
		assertThat(foundSms.hashCode()).isEqualTo(3060);

		// this uses the value stored in the object
		assertThat(foundSms.hashCode()).isEqualTo(3060);
	}

	@Test
	void testToString() throws Exception {
		int code = setupTestSms(false);
		Sms foundSms = smsIoOperation.getByID(code);
		assertThat(foundSms).isNotNull();
		assertThat(foundSms.toString())
			.isEqualTo(
				"Sms [smsId=1, smsDate=2010-09-08T00:00, smsDateSched=2011-09-08T00:00, smsNumber=***, smsText=***, smsDateSent=null, smsUser=TestUser, module=TestModule, moduleID=TestModId, hashCode=0]");
	}

	@Test
	void testGetSetSmsId() throws Exception {
		int code = setupTestSms(false);
		Sms foundSms = smsIoOperation.getByID(code);
		assertThat(foundSms).isNotNull();

		assertThat(foundSms.getSmsId()).isEqualTo(code);
		foundSms.setSmsId(-999);
		assertThat(foundSms.getSmsId()).isNotEqualTo(code);
	}

	@Test
	void testGetAll() throws Exception {
		int code = setupTestSms(false);
		Sms foundSms = smsIoOperation.getByID(code);
		assertThat(foundSms).isNotNull();

		List<Sms> allSms = smsManager.getAll(TimeTools.getDateToday0(), TimeTools.getDateToday24());
		assertThat(allSms).isEmpty();

		allSms = smsManager.getAll(LocalDateTime.of(2011, 8, 8, 0, 0, 0),
			LocalDateTime.of(2011, 9, 15, 0, 0, 0));
		assertThat(allSms).hasSize(1);
	}

	@Test
	void testDelete() throws Exception {
		int code = setupTestSms(false);
		Sms foundSms = smsIoOperation.getByID(code);
		assertThat(foundSms).isNotNull();

		List<Sms> smsList = new ArrayList<>();
		smsList.add(foundSms);
		smsManager.delete(smsList);

		Sms notFoundSms = smsIoOperation.getByID(code);
		assertThat(notFoundSms).isNull();
	}

	@Test
	void testSaveSplitFalse() throws Exception {
		Sms sms = testSms.setup(true);
		sms.setSmsNumber("+1320241494");
		smsManager.saveOrUpdate(sms, false);
		List<Sms> allSms = smsManager.getAll(LocalDateTime.of(2011, 8, 8, 0, 0, 0),
			LocalDateTime.of(2011, 9, 15, 0, 0, 0));
		assertThat(allSms).hasSize(1);
		assertThat(smsIoOperation.getByID(1)).isNotNull();
	}

	@Test
	void testSaveSplitTrue() throws Exception {
		Sms sms = testSms.setup(true);
		sms.setSmsNumber("+1320241494");
		smsManager.saveOrUpdate(sms, true);
		List<Sms> allSms = smsManager.getAll(LocalDateTime.of(2011, 8, 8, 0, 0, 0),
			LocalDateTime.of(2011, 9, 15, 0, 0, 0));
		assertThat(allSms).hasSize(1);
		assertThat(smsIoOperation.getByID(1)).isNotNull();
	}

	@Test
	void testSaveSplitTrueTextGreaterThanMax() throws Exception {
		Sms sms = testSms.setup(true);
		sms.setSmsNumber("+1320241494");
		sms.setSmsText(sms.toString() + " xxxx " + sms.toString());
		UserGroup userGroup = new UserGroup("Z", "description");
		User user = new User("admin", userGroup, "password", "description");
		UserSession.setUser(user);
		smsManager.saveOrUpdate(sms, true);
		List<Sms> allSms = smsManager.getAll(LocalDateTime.of(2011, 8, 8, 0, 0, 0),
			LocalDateTime.of(2011, 9, 15, 0, 0, 0));
		assertThat(allSms).hasSize(3);
	}

	@Test
	void testSaveSplitFalseTextGreaterThanMax() throws Exception {
		Sms sms = testSms.setup(true);
		sms.setSmsNumber("+1320241494");
		sms.setSmsText(sms.toString() + " xxxx " + sms.toString());
		UserGroup userGroup = new UserGroup("Z", "description");
		User user = new User("admin", userGroup, "password", "description");
		UserSession.setUser(user);
		assertThatThrownBy(() -> smsManager.saveOrUpdate(sms, false))
			.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void testSaveValidationSmsNumberBad() throws Exception {
		Sms sms = testSms.setup(true);
		sms.setSmsText("someText");
		UserGroup userGroup = new UserGroup("Z", "description");
		User user = new User("admin", userGroup, "password", "description");
		UserSession.setUser(user);
		assertThatThrownBy(() -> smsManager.saveOrUpdate(sms, false))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error"));
	}

	@Test
	void testSaveValidationSmsTextEmpty() throws Exception {
		Sms sms = testSms.setup(true);
		sms.setSmsNumber("+1320241494");
		sms.setSmsText("");
		UserGroup userGroup = new UserGroup("Z", "description");
		User user = new User("admin", userGroup, "password", "description");
		UserSession.setUser(user);
		assertThatThrownBy(() -> smsManager.saveOrUpdate(sms, false))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error"));
	}

	@Test
	void testSmsSenderOperationsInitialize() throws Exception {
		assertThat(smsSenderOperations.initialize()).isTrue();
	}

	@Test
	void testSmsSenderOperationsInitializeFails() throws Exception {
		Environment newSmsProperties = new EnvironmentStub();
		SmsSenderOperations dummySmsSenderOperatinos = new SmsSenderOperations(newSmsProperties, new ArrayList<>());
		assertThat(dummySmsSenderOperatinos.initialize()).isFalse();
	}

	@Test
	void testSmsSenderOperationsPreSMSSending() throws Exception {
		Sms sms = testSms.setup(true);
		sms.setSmsNumber("+39320241494");
		sms.setSmsText("SomeText");
		assertThat(smsSenderOperations.initialize()).isTrue();
		smsSenderOperations.preSMSSending(sms);
		assertThat(sms.getSmsNumber()).isEqualTo("+39320241494");
	}

	@Test
	void testSmsSenderOperationsPreSMSSendingAddPrefix() throws Exception {
		Sms sms = testSms.setup(true);
		sms.setSmsNumber("1320241494");
		sms.setSmsText("SomeText");
		assertThat(smsSenderOperations.initialize()).isTrue();
		smsSenderOperations.preSMSSending(sms);
		assertThat(sms.getSmsNumber()).isEqualTo("+391320241494");
	}

	@Test
	void testSmsSenderOperationsSendSMSNoGateway() throws Exception {
		Sms sms = testSms.setup(true);
		sms.setSmsNumber("+1320241494");
		sms.setSmsText("SomeText");
		Environment newSmsProperties = new EnvironmentStub();
		SmsSenderOperations dummySmsSenderOperatinos = new SmsSenderOperations(newSmsProperties, new ArrayList<>());
		assertThat(dummySmsSenderOperatinos.sendSMS(sms)).isFalse();
	}

//	Waiting for OP-1354 fix
//	@Test
//	void testSmsSenderOperationsSendSMSFailure() throws Exception {
//		Sms sms = testSms.setup(true);
//		sms.setSmsNumber("+1320241494");
//		sms.setSmsText("SomeText");
//		assertThat(smsSenderOperations.initialize()).isTrue();
//		smsSenderOperations.preSMSSending(sms);
//		assertThat(smsSenderOperations.sendSMS(sms)).isFalse();
//	}

	@Test
	void testSmsSenderOperationsTerminate() throws Exception {
		assertThat(smsSenderOperations.initialize()).isTrue();
		assertThat(smsSenderOperations.terminate()).isTrue();
	}

	@Test
	void testSmsSenderOperationsTerminateNoGateways() throws Exception {
		Environment newSmsProperties = new EnvironmentStub();
		SmsSenderOperations dummySmsSenderOperatinos = new SmsSenderOperations(newSmsProperties, new ArrayList<>());
		assertThat(dummySmsSenderOperatinos.terminate()).isFalse();
	}

	@Test
	void testSmsSenderNeverStarts() throws Exception {
		assertDoesNotThrow(() -> {
			SmsSender smsSender = new SmsSender();
			smsSender.setRunning(false);
			smsSender.run();
		});
	}

	@Test
	void testSmsSenderNothingToSend() throws Exception {
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
	void testSmsSenderStartAndStopSmsNotSent() throws Exception {
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
	void testSmsSenderException() throws Exception {
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
	void testSmsSenderStartAndStopSmsSent() throws Exception {
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
	void testSmsSenderStartAndStopSmsSentException() throws Exception {
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

	private int setupTestSms(boolean usingSet) throws OHException {
		Sms sms = testSms.setup(usingSet);
		smsIoOperationRepository.saveAndFlush(sms);
		return sms.getSmsId();
	}

	private void checkSmsIntoDb(int code) throws OHServiceException {
		Sms foundSms = smsIoOperation.getByID(code);
		assertThat(foundSms).isNotNull();
		testSms.check(foundSms);
	}

}
