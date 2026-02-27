/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.ethnic.manager;

import org.isf.ethnic.model.Ethnic;
import org.isf.ethnic.service.EthnicIoOperation;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EthnicManager {

	private final EthnicIoOperation ethnicIoOperation;

	public EthnicManager(EthnicIoOperation ethnicIoOperation) {
		this.ethnicIoOperation = ethnicIoOperation;
	}

	/**
	 * Inserts a new {@link Ethnic} into the DB.
	 *
	 * @param ethnic the {@link Ethnic} object to insert
	 * @return the newly inserted {@link Ethnic} object.
	 */
	public Ethnic newEthnic(Ethnic ethnic) throws OHDataValidationException {
		validEthnic(ethnic);
		return ethnicIoOperation.save(ethnic);
	}

	/**
	 * Updates the specified {@link Ethnic} object.
	 *
	 * @param ethnic the {@link Ethnic} object to update.
	 * @return the updated {@link Ethnic} object.
	 */
	public Ethnic updateEthnic(Ethnic ethnic) throws OHDataValidationException {
		validEthnic(ethnic);
		return ethnicIoOperation.update(ethnic);
	}

	/**
	 * Returns the list of {@link Ethnic}s in DB.
	 *
	 * @return the list of {@link Ethnic}s
	 */
	public List<Ethnic> getAllEthnics() {
		return ethnicIoOperation.getEthnics();
	}

	/**
	 * Returns a specific {@link Ethnic} based on ethnic id.
	 *
	 * @param id the type code.
	 * @return a {@link Ethnic}
	 */
	public Ethnic getEthnic(Integer id) throws OHServiceException {
		Ethnic foundEthnic = ethnicIoOperation.getEthnicById(id);
		if (foundEthnic == null) {
			throw new OHServiceException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.notfound.msg")));
		}
		return foundEthnic;
	}

	/**
	 * Deletes a {@link Ethnic} in the DB.
	 *
	 * @param ethnic the item to delete
	 */
	public void deleteEthnic(Ethnic ethnic) throws OHServiceException {
		ethnicIoOperation.delete(ethnic);
	}

	private void validEthnic(Ethnic ethnic) throws OHDataValidationException {

		if (ethnic.getName() == null || ethnic.getName().isBlank() || ethnic.getName().isEmpty()) {
			throw new OHDataValidationException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertavalidname.msg")));
		}
	}
}
