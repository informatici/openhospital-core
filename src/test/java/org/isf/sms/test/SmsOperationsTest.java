package org.isf.sms.test;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.isf.sms.model.Sms;
import org.isf.sms.service.SmsOperations;
import org.isf.sms.service.SmsIoOperationRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SmsOperationsTest {

	@Mock
	private SmsIoOperationRepository repository;

	@InjectMocks
	private SmsOperations smsOperations;
	@Test
	public void testSaveOrUpdate() throws OHServiceException {
		Sms sms = mock(Sms.class);
		when(repository.save(sms)).thenReturn(sms);
		boolean result = smsOperations.saveOrUpdate(sms);
		assertTrue(result);
		verify(repository, times(1)).save(sms);
	}

	@Test
	public void testGetList() throws OHServiceException {
		// Arrange
		LocalDateTime dateFrom = LocalDateTime.now();
		LocalDateTime dateTo = dateFrom.plusDays(1);
		Sms sms1 = mock(Sms.class);
		Sms sms2 = mock(Sms.class);
		List<Sms> smsList = Arrays.asList(sms1, sms2);
		when(repository.findBySmsDateSchedBetweenAndSmsDateSentIsNullOrderBySmsDateSchedAsc(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(smsList);
		// Act
		List<Sms> result = smsOperations.getList(dateFrom, dateTo);
		// Assert
		assertEquals(smsList, result);
		verify(repository, times(1)).findBySmsDateSchedBetweenAndSmsDateSentIsNullOrderBySmsDateSchedAsc(any(LocalDateTime.class), any(LocalDateTime.class));
	}
}
