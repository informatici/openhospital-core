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

import java.time.LocalDateTime;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.sms.model.Sms;
import org.isf.sms.service.SmsIoOperationRepository;
import org.isf.sms.service.SmsOperations;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class Tests extends OHCoreTestCase {

	private static TestSms testSms;

	@Autowired
	SmsOperations smsIoOperation;
	@Autowired
	SmsIoOperationRepository smsIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testSms = new TestSms();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void testSmsGets() throws Exception {
		int code = setupTestSms(false);
		checksmsIntoDb(code);
	}

	@Test
	void testSmsSets() throws Exception {
		int code = setupTestSms(true);
		checksmsIntoDb(code);
	}

	@Test
	void testSmsSaveOrUpdate() throws Exception {
		Sms sms = testSms.setup(true);
		Sms newSaveUpdateSms = smsIoOperation.saveOrUpdate(sms);
		checksmsIntoDb(newSaveUpdateSms.getSmsId());
	}

	@Test
	void testSmsGetByID() throws Exception {
		int code = setupTestSms(false);
		Sms foundSms = smsIoOperation.getByID(code);
		assertThat(foundSms).isNotNull();
		checksmsIntoDb(foundSms.getSmsId());
	}

	@Test
	void testSmsGetAll() throws Exception {
		LocalDateTime smsDateStart = LocalDateTime.of(2011, 9, 6, 0, 0, 0);
		LocalDateTime smsDateEnd = LocalDateTime.of(2011, 9, 9, 0, 0, 0);
		int code = setupTestSms(false);
		Sms foundSms = smsIoOperation.getByID(code);
		assertThat(foundSms).isNotNull();
		List<Sms> sms = smsIoOperation.getAll(smsDateStart, smsDateEnd);
		assertThat(sms.get(0).getSmsText()).isEqualTo(foundSms.getSmsText());
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

	private int setupTestSms(boolean usingSet) throws OHException {
		Sms sms = testSms.setup(usingSet);
		smsIoOperationRepository.saveAndFlush(sms);
		return sms.getSmsId();
	}

	private void checksmsIntoDb(int code) throws OHServiceException {
		Sms foundSms = smsIoOperation.getByID(code);
		assertThat(foundSms).isNotNull();
		testSms.check(foundSms);
	}
}