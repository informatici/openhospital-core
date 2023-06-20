/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.utils.validator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class TestEmailValidator {

	@Test
	public void testIsEmpty() throws Exception {
		assertThat(EmailValidator.isValid("")).isTrue();
		assertThat(EmailValidator.isValid(null)).isTrue();
	}

	@Test
	public void testDoesNotMatchPattern() throws Exception {
		assertThat(EmailValidator.isValid("abc")).isFalse();
		assertThat(EmailValidator.isValid("\"ThisIsName\"@thisCompany.com")).isFalse();
		assertThat(EmailValidator.isValid("@yahoo.com")).isFalse();
		assertThat(EmailValidator.isValid("point#domain.com")).isFalse();
	}

	@Test
	public void testDoesMatchPattern() throws Exception {
		assertThat(EmailValidator.isValid("ABCD@MYCOMPANY.COM")).isTrue();
		assertThat(EmailValidator.isValid("abcabcd@mycompany.org")).isTrue();
		assertThat(EmailValidator.isValid("someTpoint@domain.co.in")).isTrue();
		assertThat(EmailValidator.isValid("1point@domain.co.in")).isTrue();
	}

	@Test
	public void testQuestionablePatterns() throws Exception {
		// just numbers (like an IP address)
		assertThat(EmailValidator.isValid("1.2@3.4")).isTrue();
		// no domain (.com, .org, .net, etc.)
		assertThat(EmailValidator.isValid("1point@domain")).isFalse();
	}
}
