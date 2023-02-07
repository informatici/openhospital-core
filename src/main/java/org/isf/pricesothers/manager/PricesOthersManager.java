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
package org.isf.pricesothers.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.isf.generaldata.MessageBundle;
import org.isf.pricesothers.model.PricesOthers;
import org.isf.pricesothers.service.PriceOthersIoOperations;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PricesOthersManager {

	@Autowired
	private PriceOthersIoOperations ioOperations;

	/**
	 * Return the list of {@link PricesOthers}s in the DB
	 *
	 * @return the list of {@link PricesOthers}s
	 * @throws OHServiceException
	 */
	public List<PricesOthers> getOthers() throws OHServiceException {
		return ioOperations.getOthers();
	}

	/**
	 * Insert a new {@link PricesOthers} in the DB
	 *
	 * @param other - the {@link PricesOthers} to insert
	 * @return <code>true</code> if the list has been inserted, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	public PricesOthers newOther(PricesOthers other) throws OHServiceException {
		validatePricesOthers(other);
		return ioOperations.newOthers(other);
	}

	/**
	 * Delete a {@link PricesOthers} in the DB
	 *
	 * @param other - the {@link PricesOthers} to delete
	 * @return <code>true</code> if the list has been deleted, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	public boolean deleteOther(PricesOthers other) throws OHServiceException {
		return ioOperations.deleteOthers(other);
	}

	/**
	 * Update a {@link PricesOthers} in the DB
	 *
	 * @param other - the {@link PricesOthers} to update
	 * @return <code>true</code> if the list has been updated, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	public PricesOthers updateOther(PricesOthers other) throws OHServiceException {
		validatePricesOthers(other);
		return ioOperations.updateOther(other);
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 *
	 * @param pricesOthers
	 * @throws OHDataValidationException
	 */
	protected void validatePricesOthers(PricesOthers pricesOthers) throws OHDataValidationException {
		List<OHExceptionMessage> errors = new ArrayList<>();

		if (StringUtils.isEmpty(pricesOthers.getCode())) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.common.pleaseinsertacode.msg"),
					OHSeverityLevel.ERROR));
		}
		if (StringUtils.isEmpty(pricesOthers.getDescription())) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.common.pleaseinsertavaliddescription.msg"),
					OHSeverityLevel.ERROR));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}
}
