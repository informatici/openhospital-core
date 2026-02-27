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
package org.isf.town.manager;

import org.isf.generaldata.MessageBundle;
import org.isf.town.model.Town;
import org.isf.town.service.TownIoOperation;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TownManager {

	private final TownIoOperation townIoOperation;

	public TownManager(TownIoOperation townIoOperation) {
		this.townIoOperation = townIoOperation;
	}

	/**
	 * Inserts a new {@link Town} into the DB.
	 *
	 * @param town the {@link Town} object to insert
	 * @return the newly inserted {@link Town} object.
	 */
	public Town newTown(Town town) throws OHDataValidationException {
		return townIoOperation.save(town);
	}

	/**
	 * Updates the specified {@link Town} object.
	 *
	 * @param town the {@link Town} object to update.
	 * @return the updated {@link Town} object.
	 */
	public Town updateTown(Town town) throws OHDataValidationException {
		validTown(town);
		return townIoOperation.update(town);
	}

	/**
	 * Returns the list of {@link Town}s in DB.
	 *
	 * @return the list of {@link Town}s
	 */
	public List<Town> getAllTowns() {
		return townIoOperation.getTowns();
	}

	/**
	 * Returns a specific {@link Town} based on town id.
	 *
	 * @param id the type code.
	 * @return a {@link Town}
	 */
	public Town getTown(Integer id) throws OHServiceException {
		Town foundTown = townIoOperation.getTownById(id);
		if (foundTown == null) {
			throw new OHServiceException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.notfound.msg")));
		}
		return foundTown;
	}

	/**
	 * Deletes a {@link Town} in the DB.
	 *
	 * @param town the item to delete
	 */
	public void deleteTown(Town town) throws OHServiceException {
		townIoOperation.delete(town);
	}

	private void validTown(Town town) throws OHDataValidationException {

		if (town.getName() == null || town.getName().isBlank() || town.getName().isEmpty()) {
			throw new OHDataValidationException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertavalidname.msg")));
		}
	}
}
