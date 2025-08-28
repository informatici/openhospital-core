/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.conditioning.service;

import org.isf.admission.model.Admission;
import org.isf.conditioning.model.Conditioning;
import org.isf.generaldata.MessageBundle;
import org.isf.menu.model.User;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class ConditioningOperations {
	private final ConditioningOperationRepository operationRepository;

	public ConditioningOperations(ConditioningOperationRepository operationRepository) {
		this.operationRepository = operationRepository;
	}

	/**
	 * Retrieve an existing {@link Conditioning} by its ID.
	 *
	 * @param id Conditioning ID
	 * @return found {@link Conditioning} if present, or {@code null} if not found
	 * @throws OHServiceException When the retrieval operation fails
	 */
	public Conditioning getConditioning(int id) throws OHServiceException {
		return operationRepository.findById(id).orElse(null);
	}

	/**
	 * Update an existing {@link Conditioning}.
	 *
	 * @param conditioning Conditioning entity to update
	 * @return updated {@link Conditioning} if successful
	 * @throws OHServiceException When the update operation fails
	 */
	public Conditioning updateConditioning(Conditioning conditioning) throws OHServiceException {
		return operationRepository.save(conditioning);
	}


}
