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
package org.isf.sms.providers.skebby;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.isf.OHCoreTestCase;
import org.isf.sms.EnvironmentStub;
import org.isf.sms.TestSms;
import org.isf.sms.model.Sms;
import org.isf.sms.providers.skebby.model.MessageType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;

class SkebbyGatewayServiceTest extends OHCoreTestCase {

	static TestSms testSms;

	@BeforeAll
	static void setUpClass() {
		testSms = new TestSms();
	}

	@Test
	void sendSMS_shouldThrowNullPointerException_whenBaseUrlIsMissing() throws Exception {
		Sms sms = testSms.setup(true);
		Environment newSmsProperties = new MyEnv2();
		SkebbyGatewayService skebbyGatewayService = new SkebbyGatewayService(newSmsProperties, new SkebbyGatewayConverter());
		assertThat(skebbyGatewayService.initialize()).isTrue();
		assertThat(skebbyGatewayService.terminate()).isTrue();
		assertThatThrownBy(() -> skebbyGatewayService.sendSMS(sms))
						.isInstanceOf(NullPointerException.class);
	}

	class MyEnv2 extends EnvironmentStub {
		@Override
		public String getProperty(String key) {
			return switch (key) {
				case SkebbyGatewayService.KEY_USER_KEY -> "key_user_key";
				case SkebbyGatewayService.KEY_ACCESS_TOKEN -> "key_access_token";
				case SkebbyGatewayService.KEY_USERNAME -> "key_username";
				case SkebbyGatewayService.KEY_PASSWORD -> "key_password";
				case SkebbyGatewayService.KEY_MESSAGE_TYPE -> MessageType.SI.name();
				default -> "";
			};
		}
	}

}
