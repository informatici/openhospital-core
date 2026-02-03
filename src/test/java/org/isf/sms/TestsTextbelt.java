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

import org.isf.OHCoreTestCase;
import org.isf.sms.providers.textbelt.model.TextbeltSmsRequest;
import org.isf.sms.providers.textbelt.model.TextbeltSmsResponse;
import org.junit.jupiter.api.Test;

class TestsTextbelt extends OHCoreTestCase {

	@Test
	void testTextbeltSmsRequestGetSets() throws Exception {
		TextbeltSmsRequest textbeltSmsRequest = new TextbeltSmsRequest();

		textbeltSmsRequest.setMessage("message");
		assertThat(textbeltSmsRequest.getMessage()).isEqualTo("message");

		textbeltSmsRequest.setKey("key");
		assertThat(textbeltSmsRequest.getKey()).isEqualTo("key");

		textbeltSmsRequest.setPhone("phone");
		assertThat(textbeltSmsRequest.getPhone()).isEqualTo("phone");

		assertThat(textbeltSmsRequest).hasToString("TextbeltSmsRequest [phone=***, message=***, key=***]");
	}

	@Test
	void testTextbeltSmsResponseGetSets() throws Exception {
		TextbeltSmsResponse textbeltSmsResponse = new TextbeltSmsResponse();

		textbeltSmsResponse.setSuccess(true);
		assertThat(textbeltSmsResponse.getSuccess()).isTrue();

		textbeltSmsResponse.setQuotaRemaining(999);
		assertThat(textbeltSmsResponse.getQuotaRemaining()).isEqualTo(999);

		textbeltSmsResponse.setTextId(12345l);
		assertThat(textbeltSmsResponse.getTextId()).isEqualTo(12345l);

		textbeltSmsResponse.setError("error");
		assertThat(textbeltSmsResponse.getError()).isEqualTo("error");

		assertThat(textbeltSmsResponse).hasToString("TextbeltSmsResponse [success=true, quotaRemaining=999, textId=12345, error=error]");
	}
}
