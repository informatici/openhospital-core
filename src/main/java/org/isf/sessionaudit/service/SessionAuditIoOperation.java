/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.sessionaudit.service;

import java.util.Optional;

import org.isf.sessionaudit.model.SessionAudit;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persistence class for the session audit module.
 */
@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class SessionAuditIoOperation {

	private SessionAuditIoOperationRepository repository;

	public SessionAuditIoOperation(SessionAuditIoOperationRepository sessionAuditIoOperationRepository) {
		this.repository = sessionAuditIoOperationRepository;
	}

	/**
	 * Return the {@link SessionAudit} given the session audit id.
	 * 
	 * @param sessionAuditId
	 *            the session audit id
	 * @return the {@link SessionAudit}
	 */
	public Optional<SessionAudit> getSessionAuditById(int sessionAuditId) {
		return repository.findById(sessionAuditId);
	}

	/**
	 * Saves the {@link SessionAudit}.
	 * 
	 * @param sessionAudit
	 *            the new session audit
	 * @return the audit session code
	 */
	public int saveSessionAudit(SessionAudit sessionAudit) {
		return repository.save(sessionAudit).getCode();
	}

	/**
	 * Updates the {@link SessionAudit}.
	 * 
	 * @param sessionAudit
	 *            the updated session audit
	 * @return the updated {@link SessionAudit} object.
	 */
	public SessionAudit updateSessionAudit(SessionAudit sessionAudit) {
		return repository.save(sessionAudit);
	}
}
