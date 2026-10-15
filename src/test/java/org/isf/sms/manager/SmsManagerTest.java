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
package org.isf.sms.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.menu.manager.Context;
import org.isf.menu.model.User;
import org.isf.menu.model.UserGroup;
import org.isf.sessionaudit.model.UserSession;
import org.isf.sms.TestSms;
import org.isf.sms.model.Sms;
import org.isf.sms.service.SmsIoOperationRepository;
import org.isf.sms.service.SmsOperations;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.time.TimeTools;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

class SmsManagerTest extends OHCoreTestCase {

	private static TestSms testSms;

	@Autowired
	SmsOperations smsIoOperation;
	@Autowired
	SmsIoOperationRepository smsIoOperationRepository;
	@Autowired
	SmsManager smsManager;

	@Mock
	ApplicationContext applicationContextMock;

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
	void getAll_shouldReturnSmsScheduledBetweenDates() throws Exception {
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
	void delete_shouldRemoveSms() throws Exception {
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
	void saveOrUpdate_shouldSaveOneSms_whenTextIsShortAndSplitIsFalse() throws Exception {
		Sms sms = testSms.setup(true);
		sms.setSmsNumber("+1320241494");
		smsManager.saveOrUpdate(sms, false);
		List<Sms> allSms = smsManager.getAll(LocalDateTime.of(2011, 8, 8, 0, 0, 0),
			LocalDateTime.of(2011, 9, 15, 0, 0, 0));
		assertThat(allSms).hasSize(1);
		assertThat(smsIoOperation.getByID(1)).isNotNull();
	}

	@Test
	void saveOrUpdate_shouldSaveOneSms_whenTextIsShortAndSplitIsTrue() throws Exception {
		Sms sms = testSms.setup(true);
		sms.setSmsNumber("+1320241494");
		smsManager.saveOrUpdate(sms, true);
		List<Sms> allSms = smsManager.getAll(LocalDateTime.of(2011, 8, 8, 0, 0, 0),
			LocalDateTime.of(2011, 9, 15, 0, 0, 0));
		assertThat(allSms).hasSize(1);
		assertThat(smsIoOperation.getByID(1)).isNotNull();
	}

	@Test
	void saveOrUpdate_shouldSplitSms_whenTextIsTooLongAndSplitIsTrue() throws Exception {
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
	void saveOrUpdate_shouldThrow_whenTextIsTooLongAndSplitIsFalse() throws Exception {
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
	void saveOrUpdate_shouldThrowOneError_whenNumberIsInvalid() throws Exception {
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
	void saveOrUpdate_shouldThrowOneError_whenTextIsEmpty() throws Exception {
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

	private int setupTestSms(boolean usingSet) throws OHException {
		Sms sms = testSms.setup(usingSet);
		smsIoOperationRepository.saveAndFlush(sms);
		return sms.getSmsId();
	}

}
