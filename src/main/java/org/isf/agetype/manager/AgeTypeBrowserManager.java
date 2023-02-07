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
package org.isf.agetype.manager;

import java.util.ArrayList;
import java.util.List;

import org.isf.agetype.model.AgeType;
import org.isf.agetype.service.AgeTypeIoOperations;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AgeTypeBrowserManager {

	@Autowired
	private AgeTypeIoOperations ioOperations;

	/**
	 * Returns all available age types.
	 *
	 * @return a list of {@link AgeType} or <code>null</code> if the operation fails.
	 * @throws OHServiceException
	 */
	public List<AgeType> getAgeType() throws OHServiceException {
		return ioOperations.getAgeType();
	}

	/**
	 * Updates the list of {@link AgeType}s.
	 *
	 * @param ageTypes the {@link AgeType}s to update.
	 * @return <code>true</code> if the list has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public boolean updateAgeType(List<AgeType> ageTypes) throws OHServiceException {
		validateAgeTypes(ageTypes);
		return ioOperations.updateAgeType(ageTypes);
	}

	/**
	 * Retrieves the {@link AgeType} code using the age value.
	 *
	 * @param age the age value.
	 * @return the retrieved code, <code>null</code> if age value is out of any range.
	 * @throws OHServiceException
	 */
	public String getTypeByAge(int age) throws OHServiceException {
		List<AgeType> ageTable = ioOperations.getAgeType();

		for (AgeType ageType : ageTable) {

			if (age >= ageType.getFrom() && age <= ageType.getTo()) {
				return ageType.getCode();
			}
		}
		return null;
	}

	/**
	 * Gets the {@link AgeType} from the code index.
	 *
	 * @param index the code index.
	 * @return the retrieved element, <code>null</code> otherwise.
	 * @throws OHServiceException
	 */
	public AgeType getTypeByCode(int index) throws OHServiceException {
		return ioOperations.getAgeTypeByCode(index);
	}
	
	/**
	 * Gets the {@link AgeType} from the code.
	 *
	 * @param code of agetype.
	 * @return the retrieved element, <code>null</code> otherwise.
	 * @throws OHServiceException
	 */
	public AgeType getTypeByCode(String code) throws OHServiceException {
		return ioOperations.getAgeTypeByCode(code);
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 *
	 * @param ageTypes
	 * @throws OHDataValidationException
	 */
	protected void validateAgeTypes(List<AgeType> ageTypes) throws OHDataValidationException {
		List<OHExceptionMessage> errors = new ArrayList<>();
		for (int i = 1; i < ageTypes.size(); i++) {
			if (ageTypes.get(i).getFrom() <= ageTypes.get(i - 1).getTo()) {
				errors.add(
						new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
								MessageBundle.getMessage("angal.agetype.overlappedrangespleasecheckthevalues.msg"),
								OHSeverityLevel.ERROR));
			}
			if (ageTypes.get(i).getFrom() - ageTypes.get(i - 1).getTo() > 1) {
				errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
						MessageBundle.getMessage("angal.agetype.somerangesarenotdefinedpleasecheckthevalues.msg"),
						OHSeverityLevel.ERROR));
			}
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}
}
