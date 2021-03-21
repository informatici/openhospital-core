package org.isf.utils.validator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

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

	// With the current regex in the validator these email address are considered valid
	// Using the proposed alternative regex in the validator thiese email address are not valid
	@Test
	public void testQuestionablePatterns() throws Exception {
		// just numbers (like an IP address)
		assertThat(EmailValidator.isValid("1.2@3.4")).isTrue();
		// no domain (.com, .org, .net, etc.)
		assertThat(EmailValidator.isValid("1point@domain")).isTrue();
	}
}
