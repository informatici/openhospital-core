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
package org.isf.sms.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.menu.manager.Context;
import org.isf.sessionaudit.model.UserSession;
import org.isf.sms.TestSms;
import org.isf.sms.service.SmsIoOperationRepository;
import org.isf.sms.service.SmsOperations;
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

class SmsTest extends OHCoreTestCase {

	private static TestSms testSms;

	@Autowired
	SmsOperations smsIoOperation;
	@Autowired
	SmsIoOperationRepository smsIoOperationRepository;
	@Autowired
	private ApplicationContext applicationContext;

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
	void constructor_shouldSetFields() throws Exception {
		int code = setupTestSms(false);
		checkSmsIntoDb(code);
	}

	@Test
	void setters_shouldSetFields() throws Exception {
		int code = setupTestSms(true);
		checkSmsIntoDb(code);
	}

	@Test
	void equals_shouldUseSmsId() throws Exception {
		int code = setupTestSms(true);
		Sms foundSms = smsIoOperation.getByID(code);
		assertThat(foundSms).isNotNull();

		assertThat(foundSms.equals(foundSms)).isTrue();

		assertThat(foundSms.equals(Integer.valueOf(1))).isFalse();

		Sms secondSms = new Sms(TimeTools.getNow(), "smsNumber", "smsText", "smsUser");
		assertThat(foundSms.equals(secondSms)).isFalse();
	}

	@Test
	void hashCode_shouldUseSmsId() throws Exception {
		int code = setupTestSms(true);
		Sms foundSms = smsIoOperation.getByID(code);
		assertThat(foundSms).isNotNull();
		assertThat(foundSms.hashCode()).isEqualTo(3060);

		// this uses the value stored in the object
		assertThat(foundSms.hashCode()).isEqualTo(3060);
	}

	@Test
	void toString_shouldMaskNumberAndText() throws Exception {
		int code = setupTestSms(false);
		Sms foundSms = smsIoOperation.getByID(code);
		assertThat(foundSms).isNotNull();
		assertThat(foundSms.toString())
			.isEqualTo(
				"Sms [smsId=1, smsDate=2010-09-08T00:00, smsDateSched=2011-09-08T00:00, smsNumber=***, smsText=***, smsDateSent=null, smsUser=TestUser, module=TestModule, moduleID=TestModId, hashCode=0]");
	}

	@Test
	void setSmsId_shouldUpdateSmsId() throws Exception {
		int code = setupTestSms(false);
		Sms foundSms = smsIoOperation.getByID(code);
		assertThat(foundSms).isNotNull();

		assertThat(foundSms.getSmsId()).isEqualTo(code);
		foundSms.setSmsId(-999);
		assertThat(foundSms.getSmsId()).isNotEqualTo(code);
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
