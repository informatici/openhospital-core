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

import java.util.ArrayList;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.menu.manager.Context;
import org.isf.sessionaudit.model.UserSession;
import org.isf.sms.EnvironmentStub;
import org.isf.sms.TestSms;
import org.isf.sms.model.Sms;
import org.isf.sms.providers.SmsSenderInterface;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

class SmsSenderOperationsTest extends OHCoreTestCase {

	private static TestSms testSms;

	@Autowired
	SmsSenderOperations smsSenderOperations;

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
	void initialize_shouldReturnTrue_whenGatewayIsConfigured() throws Exception {
		assertThat(smsSenderOperations.initialize()).isTrue();
	}

	@Test
	void initialize_shouldReturnFalse_whenNoGatewayIsSet() throws Exception {
		Environment newSmsProperties = new EnvironmentStub();
		SmsSenderOperations dummySmsSenderOperatinos = new SmsSenderOperations(newSmsProperties, new ArrayList<>());
		assertThat(dummySmsSenderOperatinos.initialize()).isFalse();
	}

	@Test
	void preSMSSending_shouldKeepNumber_whenItStartsWithCountryCode() throws Exception {
		Sms sms = testSms.setup(true);
		sms.setSmsNumber("+39320241494");
		sms.setSmsText("SomeText");
		assertThat(smsSenderOperations.initialize()).isTrue();
		smsSenderOperations.preSMSSending(sms);
		assertThat(sms.getSmsNumber()).isEqualTo("+39320241494");
	}

	@Test
	void preSMSSending_shouldAddCountryCode_whenNumberLacksIt() throws Exception {
		Sms sms = testSms.setup(true);
		sms.setSmsNumber("1320241494");
		sms.setSmsText("SomeText");
		assertThat(smsSenderOperations.initialize()).isTrue();
		smsSenderOperations.preSMSSending(sms);
		assertThat(sms.getSmsNumber()).isEqualTo("+391320241494");
	}

	@Test
	void sendSMS_shouldReturnFalse_whenNoGatewayIsSet() throws Exception {
		Sms sms = testSms.setup(true);
		sms.setSmsNumber("+1320241494");
		sms.setSmsText("SomeText");
		Environment newSmsProperties = new EnvironmentStub();
		SmsSenderOperations dummySmsSenderOperatinos = new SmsSenderOperations(newSmsProperties, new ArrayList<>());
		assertThat(dummySmsSenderOperatinos.sendSMS(sms)).isFalse();
	}

	@Test
	void sendSMS_shouldReturnFalse_whenGatewayFailsToSend() throws Exception {
		Sms sms = testSms.setup(true);
		sms.setSmsNumber("+1320241494");
		sms.setSmsText("SomeText");
		SmsSenderOperations failingSmsSenderOperations = new SmsSenderOperations(new ConfiguredSmsGatewayEnvironmentStub(), List.of(new FailingSmsGateway()));
		assertThat(failingSmsSenderOperations.initialize()).isTrue();
		failingSmsSenderOperations.preSMSSending(sms);
		assertThat(failingSmsSenderOperations.sendSMS(sms)).isFalse();
	}

	@Test
	void terminate_shouldReturnTrue_whenGatewayIsConfigured() throws Exception {
		assertThat(smsSenderOperations.initialize()).isTrue();
		assertThat(smsSenderOperations.terminate()).isTrue();
	}

	@Test
	void terminate_shouldReturnFalse_whenNoGatewayIsSet() throws Exception {
		Environment newSmsProperties = new EnvironmentStub();
		SmsSenderOperations dummySmsSenderOperatinos = new SmsSenderOperations(newSmsProperties, new ArrayList<>());
		assertThat(dummySmsSenderOperatinos.terminate()).isFalse();
	}

	private static class ConfiguredSmsGatewayEnvironmentStub extends EnvironmentStub {

		@Override
		public String getProperty(String key) {
			return switch (key) {
			case "sms.gateway" -> "failing-test-gateway";
			default -> "";
			};
		}
	}

	private static class FailingSmsGateway implements SmsSenderInterface {

		@Override
		public boolean initialize() {
			return true;
		}

		@Override
		public boolean sendSMS(Sms sms) {
			return false;
		}

		@Override
		public String getName() {
			return "failing-test-gateway";
		}

		@Override
		public String getRootKey() {
			return "failing-test-gateway";
		}

		@Override
		public boolean terminate() {
			return true;
		}
	}

}
