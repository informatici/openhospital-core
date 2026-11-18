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

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.menu.manager.Context;
import org.isf.sessionaudit.model.UserSession;
import org.isf.sms.TestSms;
import org.isf.sms.model.Sms;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

class SmsOperationsTest extends OHCoreTestCase {

	private static TestSms testSms;

	@Autowired
	SmsOperations smsIoOperation;
	@Autowired
	SmsIoOperationRepository smsIoOperationRepository;

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
	void saveOrUpdate_shouldSaveSms() throws Exception {
		Sms sms = testSms.setup(true);
		Sms newSaveUpdateSms = smsIoOperation.saveOrUpdate(sms);
		checkSmsIntoDb(newSaveUpdateSms.getSmsId());
	}

	@Test
	void getByID_shouldReturnSms_whenIdExists() throws Exception {
		int code = setupTestSms(false);
		Sms foundSms = smsIoOperation.getByID(code);
		assertThat(foundSms).isNotNull();
		checkSmsIntoDb(foundSms.getSmsId());
	}

	@Test
	void getAll_shouldReturnSmsScheduledBetweenDates() throws Exception {
		LocalDateTime smsDateStart = LocalDateTime.of(2011, 9, 6, 0, 0, 0);
		LocalDateTime smsDateEnd = LocalDateTime.of(2011, 9, 9, 0, 0, 0);
		int code = setupTestSms(false);
		Sms foundSms = smsIoOperation.getByID(code);
		assertThat(foundSms).isNotNull();
		List<Sms> smsList = smsIoOperation.getAll(smsDateStart, smsDateEnd);
		assertThat(smsList.get(0).getSmsText()).isEqualTo(foundSms.getSmsText());
	}

	@Test
	void getList_shouldReturnNotSentSms_whenScheduledBetweenDates() throws Exception {
		LocalDateTime smsDateStart = LocalDateTime.of(2011, 9, 6, 0, 0, 0);
		LocalDateTime smsDateEnd = LocalDateTime.of(2011, 9, 9, 0, 0, 0);
		int code = setupTestSms(false);
		Sms foundSms = smsIoOperation.getByID(code);
		assertThat(foundSms).isNotNull();
		List<Sms> smsList = smsIoOperation.getList(smsDateStart, smsDateEnd);
		assertThat(smsList).hasSize(1);
	}

	@Test
	void getList_shouldReturnNotSentSms() throws Exception {
		int code = setupTestSms(false);
		Sms foundSms = smsIoOperation.getByID(code);
		assertThat(foundSms).isNotNull();
		List<Sms> sms = smsIoOperation.getList();
		assertThat(sms.get(0).getSmsText()).isEqualTo(foundSms.getSmsText());
	}

	@Test
	void delete_shouldRemoveSms() throws Exception {
		int code = setupTestSms(false);
		Sms foundSms = smsIoOperation.getByID(code);
		assertThat(foundSms).isNotNull();
		smsIoOperation.delete(foundSms);
		boolean result = smsIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	@Test
	void deleteByModuleModuleID_shouldRemoveNotSentSms() throws Exception {
		int code = setupTestSms(false);
		Sms foundSms = smsIoOperation.getByID(code);
		assertThat(foundSms).isNotNull();
		smsIoOperation.deleteByModuleModuleID(foundSms.getModule(), foundSms.getModuleID());

		boolean result = smsIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
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
