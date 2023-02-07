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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.sessionaudit.manager;

import java.util.Optional;

import org.isf.sessionaudit.model.SessionAudit;
import org.isf.sessionaudit.service.SessionAuditIoOperation;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Manager for session audit module.
 */
@Component
public class SessionAuditManager {

	@Autowired
	private SessionAuditIoOperation ioOperation;

	
	/**
	 * Retrieves the {@link SessionAudit} associated to the given user id.
	 *
	 * @param userCode the user id.
	 * @return the users {@link SessionAudit}
	 * @throws OHServiceException
	 */
	public Optional<SessionAudit> getSessionAudit(int sessionAuditId) throws OHServiceException {
		return ioOperation.getSessionAuditById(sessionAuditId);
	}

	/**
	 * Stores the {@link SessionAudit} associated to the given user id in the db.
	 *
	 * @param sessionAudit the {link SessionAudit}.
	 * @return the result of saving operation
	 * @throws OHServiceException
	 */
	public int newSessionAudit(SessionAudit sessionAudit) throws OHServiceException {
		return ioOperation.saveSessionAudit(sessionAudit);
	}

	/**
	 * Updates the specified {@link SessionAudit}.
	 *
	 * @param malnutrition the {@link SessionAudit} to update
	 * @return the updated {@link SessionAudit}
	 * @throws OHServiceException
	 */
	public boolean updateSessionAudit(SessionAudit sessionAudit) throws OHServiceException {
		return ioOperation.updateSessionAudit(sessionAudit);
	}

}
