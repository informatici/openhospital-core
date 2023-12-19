package org.isf.utils.exception;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OHExceptionTest {

	@Test
	void testOHExceptionConstructorWithMessageAndCause() {
		// Arrange
		String message = "Test Exception Message";
		Throwable cause = new RuntimeException("Test Cause");

		// Act
		OHException exceptionWithCause = new OHException(message, cause);
		OHException exceptionWithoutCause = new OHException(message);

		// Assert
		assertNotNull(exceptionWithCause);
		assertEquals(message, exceptionWithCause.getMessage());

		assertNotNull(exceptionWithoutCause);
		assertEquals(message, exceptionWithoutCause.getMessage());
	}
}



