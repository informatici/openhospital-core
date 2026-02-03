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
import static org.mockito.Mockito.mock;

import org.isf.OHCoreTestCase;
import org.isf.sms.model.Sms;
import org.isf.sms.providers.gsm.GSMGatewayService;
import org.isf.sms.service.SmsIoOperationRepository;
import org.isf.sms.service.SmsOperations;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;

class TestsGSM extends OHCoreTestCase {

	private static TestSms testSms;

	@Autowired
	SmsOperations smsIoOperation;
	@Autowired
	SmsIoOperationRepository smsIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testSms = new TestSms();
	}

	@Test
	void testInitialize() throws Exception {
		GSMGatewayService gsmGatewayService = new GSMGatewayService();
		// does not get very far into the method
		assertThat(gsmGatewayService.initialize()).isFalse();
	}

	@Test
	void testSendSMS() throws Exception {
		Sms sms = setupTestSms(false);
		GSMGatewayService gsmGatewayService = new GSMGatewayService();
		// does not get very far into the method
		assertThat(gsmGatewayService.sendSMS(sms)).isFalse();
	}

	@Test
	void testSendSMSDebug() throws Exception {
		Sms sms = setupTestSms(false);
		GSMGatewayService gsmGatewayService = new GSMGatewayService();
		// does not get very far into the method
		assertThat(gsmGatewayService.sendSMS(sms, true)).isFalse();
	}

	@Test
	void testGets() throws Exception {
		GSMGatewayService gsmGatewayService = new GSMGatewayService();
		assertThat(gsmGatewayService.getName()).isEqualTo("gsm-gateway-service");
		assertThat(gsmGatewayService.getRootKey()).isEqualTo("gsm-gateway-service");
		assertThat(gsmGatewayService.getListeningEvents()).isEqualTo(1);
	}

	@Test
	void testTerminate() throws Exception {
		GSMGatewayService gsmGatewayService = new GSMGatewayService();
		assertThatThrownBy(gsmGatewayService::terminate)
						.isInstanceOf(NullPointerException.class);
	}

	@Test
	void testSerialEvent() throws Exception {
		GSMGatewayService gsmGatewayService = new GSMGatewayService();
		SerialPort serialPort = mock(SerialPort.class);
		SerialPortEvent serialPortEvent = new SerialPortEvent(serialPort, 1);
		assertThatThrownBy(() -> gsmGatewayService.serialEvent(serialPortEvent))
						.isInstanceOf(NullPointerException.class);
	}

	private Sms setupTestSms(boolean usingSet) throws Exception {
		Sms sms = testSms.setup(usingSet);
		smsIoOperationRepository.saveAndFlush(sms);
		Sms foundSms = smsIoOperation.getByID(sms.getSmsId());
		assertThat(foundSms).isNotNull();
		return foundSms;
	}
}
