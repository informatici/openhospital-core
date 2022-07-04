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
package org.isf.medicalstock.manager;

import java.time.LocalDateTime;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.medicalstock.model.Movement;
import org.isf.medicalstock.service.MedicalStockIoOperations;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.isf.ward.model.Ward;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MovBrowserManager {

	@Autowired
	private MedicalStockIoOperations ioOperations;

	/**
	 * Retrieves all the {@link Movement}s.
	 *
	 * @return the retrieved movements.
	 * @throws OHServiceException
	 */
	public List<Movement> getMovements() throws OHServiceException {
		return ioOperations.getMovements();
	}

	/**
	 * Retrieves all the movement associated to the specified {@link Ward}.
	 *
	 * @param wardId the ward id.
	 * @param dateTo
	 * @param dateFrom
	 * @return the retrieved movements.
	 * @throws OHServiceException
	 */
	public List<Movement> getMovements(String wardId, LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		return ioOperations.getMovements(wardId, dateFrom, dateTo);
	}

	/**
	 * Retrieves all the movement associated to the specified reference number.
	 *
	 * @param refNo the reference number.
	 * @return the retrieved movements.
	 * @throws OHServiceException
	 */
	public List<Movement> getMovementsByReference(String refNo) throws OHServiceException {
		return ioOperations.getMovementsByReference(refNo);
	}

	/**
	 * Retrieves all the {@link Movement}s with the specified criteria.
	 *
	 * @param medicalCode the medical code.
	 * @param medicalType the medical type.
	 * @param wardId the ward type.
	 * @param movType the movement type.
	 * @param movFrom the lower bound for the movement date range.
	 * @param movTo the upper bound for the movement date range.
	 * @param lotPrepFrom the lower bound for the lot preparation date range.
	 * @param lotPrepTo the upper bound for the lot preparation date range.
	 * @param lotDueFrom the lower bound for the lot due date range.
	 * @param lotDueTo the lower bound for the lot due date range.
	 * @return the retrieved movements.
	 * @throws OHServiceException
	 */
	public List<Movement> getMovements(Integer medicalCode, String medicalType,
			String wardId, String movType, LocalDateTime movFrom, LocalDateTime movTo,
			LocalDateTime lotPrepFrom, LocalDateTime lotPrepTo,
			LocalDateTime lotDueFrom, LocalDateTime lotDueTo) throws OHServiceException {

		if (medicalCode == null &&
				medicalType == null &&
				movType == null &&
				movFrom == null &&
				movTo == null &&
				lotPrepFrom == null &&
				lotPrepTo == null &&
				lotDueFrom == null &&
				lotDueTo == null) {
			return getMovements();
		}

		check(movFrom, movTo, "angal.medicalstock.chooseavalidmovementdate.msg");
		check(lotPrepFrom, lotPrepTo, "angal.medicalstock.chooseavalidmovementdate.msg");
		check(lotDueFrom, lotDueTo, "angal.medicalstock.chooseavalidduedate.msg");

		return ioOperations.getMovements(medicalCode, medicalType, wardId, movType, movFrom, movTo, lotPrepFrom, lotPrepTo, lotDueFrom, lotDueTo);
	}

	private void check(LocalDateTime from, LocalDateTime to, String errMsgKey) throws OHDataValidationException {
		if (from == null || to == null) {
			if (!(from == null && to == null)) {
				throw new OHDataValidationException(
						new OHExceptionMessage(
								MessageBundle.getMessage("angal.common.error.title"),
								MessageBundle.getMessage(errMsgKey),
								OHSeverityLevel.ERROR
						)
				);
			}
		}
	}
}
