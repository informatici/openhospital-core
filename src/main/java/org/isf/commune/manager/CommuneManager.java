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
package org.isf.commune.manager;

import org.isf.commune.model.Commune;
import org.isf.commune.service.CommuneIoOperation;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommuneManager {

	private final CommuneIoOperation communeIoOperation;

	public CommuneManager(CommuneIoOperation communeIoOperation) {
		this.communeIoOperation = communeIoOperation;
	}

	/**
	 * Inserts a new {@link Commune} into the DB.
	 *
	 * @param commune the {@link Commune} object to insert
	 * @return the newly inserted {@link Commune} object.
	 */
	public Commune newCommune(Commune commune) throws OHDataValidationException {
		validCommune(commune);
		return communeIoOperation.save(commune);
	}

	/**
	 * Updates the specified {@link Commune} object.
	 *
	 * @param commune the {@link Commune} object to update.
	 * @return the updated {@link Commune} object.
	 */
	public Commune updateCommune(Commune commune) throws OHDataValidationException {
		validCommune(commune);
		return communeIoOperation.update(commune);
	}

	/**
	 * Returns the list of {@link Commune}s in DB.
	 *
	 * @return the list of {@link Commune}s
	 */
	public List<Commune> getAllCommunes() {
		return communeIoOperation.getCommunes();
	}

	/**
	 * Returns a specific {@link Commune} based on commune id.
	 *
	 * @param id the type code.
	 * @return a {@link Commune}
	 */
	public Commune getCommune(Integer id) throws OHServiceException {
		Commune foundCommune = communeIoOperation.getCommuneById(id);
		if (foundCommune == null) {
			throw new OHServiceException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.notfound.msg")));
		}
		return foundCommune;
	}

	/**
	 * Deletes a {@link Commune} in the DB.
	 *
	 * @param commune the item to delete
	 */
	public void deleteCommune(Commune commune) throws OHServiceException {
		communeIoOperation.delete(commune);
	}

	private void validCommune(Commune commune) throws OHDataValidationException {

		if (commune.getName() == null || commune.getName().isBlank() || commune.getName().isEmpty()) {
			throw new OHDataValidationException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertavalidname.msg")));
		}
	}
}
