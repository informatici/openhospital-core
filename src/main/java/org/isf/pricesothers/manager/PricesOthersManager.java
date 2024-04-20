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
import org.springframework.stereotype.Component;

@Component
public class PricesOthersManager {

	private PriceOthersIoOperations ioOperations;

	public PricesOthersManager(PriceOthersIoOperations priceOthersIoOperations) {
		this.ioOperations = priceOthersIoOperations;
	}

	/**
	 * Return a list of {@link PricesOthers}s.
	 *
	 * @return the list of {@link PricesOthers}s
	 * @throws OHServiceException
	 */
	public List<PricesOthers> getOthers() throws OHServiceException {
		return ioOperations.getOthers();
	}

	/**
	 * Insert a new {@link PricesOthers} object.
	 *
	 * @param other - the {@link PricesOthers} to insert
	 * @return the newly inserted {@link PricesOthers} object.
	 * @throws OHServiceException
	 */
	public PricesOthers newOther(PricesOthers other) throws OHServiceException {
		validatePricesOthers(other);
		return ioOperations.newOthers(other);
	}

	/**
	 * Delete a {@link PricesOthers} object.
	 *
	 * @param other - the {@link PricesOthers} to delete
	 * @throws OHServiceException
	 */
	public void deleteOther(PricesOthers other) throws OHServiceException {
		ioOperations.deleteOthers(other);
	}

	/**
	 * Update a {@link PricesOthers} object.
	 *
	 * @param other - the {@link PricesOthers} to update
	 * @return the newly updated {@link PricesOthers} object.
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
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertacode.msg")));
		}
		if (StringUtils.isEmpty(pricesOthers.getDescription())) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertavaliddescription.msg")));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}
}
