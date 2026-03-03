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
package org.isf.base.manager;

import org.isf.base.model.BaseEntity;
import org.isf.base.service.BaseIoOperation;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;

import java.util.List;
import java.util.Objects;

public class BaseManager<T extends BaseEntity> implements BaseManagerInterface {

	protected final BaseIoOperation<T, ?> service;

	public BaseManager(BaseIoOperation<T, ?> service) {
		this.service = service;
	}

	/**
	 * Returns the list of {@link T}s in DB.
	 *
	 * @return the list of {@link T}s
	 */
	@Override
	public List<T> getAll() {
		return service.findAll();
	}

	/**
	 * Returns a specific {@link T} based on T id.
	 *
	 * @param id the T id.
	 * @return a {@link T}
	 */
	@Override
	public T getById(Integer id) throws OHServiceException {
		T entityFound = service.findById(id).orElse(null);
		if (entityFound == null) {
			throw new OHServiceException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.notfound.msg")));
		}
		return entityFound;
	}

	/**
	 * Inserts a new {@link T} into the DB.
	 *
	 * @param entity the {@link T} object to insert
	 * @return the newly inserted {@link T} object.
	 */
	@Override
	public T create(BaseEntity entity) throws OHDataValidationException {
		validateData((T) entity);
		return service.save((T) entity);
	}

	/**
	 * Updates the specified {@link T} object.
	 *
	 * @param id of {@link T} to update
	 * @param entity the {@link T} object to update.
	 * @return the updated {@link T} object.
	 */
	@Override
	public T update(Integer id, BaseEntity entity) throws OHDataValidationException {
		validateData((T) entity);
		return service.update(id, (T) entity);
	}

	/**
	 * Deletes a {@link T} in the DB.
	 *
	 * @param id of T to delete
	 */
	@Override
	public void delete(Integer id) {
		service.deleteById(id);
	}

	private void validateData(T entity) throws OHDataValidationException {
		T entityFound = service.findByName(entity.getName());

		if (entityFound != null && !Objects.equals(entityFound.getName(),entity.getName()) && !Objects.equals(entityFound.getId(),entity.getId())) {
			throw new OHDataValidationException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.alreadyexit.msg")));
		}

		if (entity.getName().isBlank() || entity.getName().isEmpty()) {
			throw new OHDataValidationException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertavalidname.msg")));
		}
	}
}
