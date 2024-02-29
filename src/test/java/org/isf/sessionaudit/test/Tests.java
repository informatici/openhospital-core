package org.isf.sessionaudit.test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.isf.sessionaudit.model.SessionAudit;
import org.isf.sessionaudit.service.SessionAuditIoOperation;
import org.isf.sessionaudit.service.SessionAuditIoOperationRepository;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.isf.OHCoreTestCase;

import java.util.Optional;

public class Tests extends OHCoreTestCase {
	@Mock
	private SessionAuditIoOperationRepository repository;

	@InjectMocks
	private SessionAuditIoOperation service;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void getSessionAuditById_whenFound() {
		int sessionAuditId = 1;
		SessionAudit expected = new SessionAudit();
		expected.setCode(sessionAuditId);

		when(repository.findById(sessionAuditId)).thenReturn(Optional.of(expected));

		Optional<SessionAudit> result = service.getSessionAuditById(sessionAuditId);

		assertTrue(result.isPresent());
		assertEquals(expected, result.get());
	}

}
