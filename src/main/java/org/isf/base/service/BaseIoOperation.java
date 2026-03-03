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
package org.isf.base.service;

import org.isf.base.model.BaseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public abstract class BaseIoOperation<T extends BaseEntity, R extends BaseIoOperationRepository<T>>  {
	protected final R repository;

	public BaseIoOperation(R repository) {
		this.repository = repository;
	}

	@Transactional(readOnly = true)
	public List<T> findAll() {
		return repository.findAll();
	}

	@Transactional(readOnly = true)
	public Optional<T> findById(Integer id) {
		return repository.findById(id);
	}

	@Transactional
	public T save(T entity) {
		return repository.save(entity);
	}

	@Transactional
	public void deleteById(Integer id) {
		repository.deleteById(id);
	}

	@Transactional
	public T findByName(String name) {
		return repository.findByName(name);
	}

	@Transactional
	public T update(Integer id, T updatedEntity) {
		return findById(id).map(entity -> {
			entity.setName(updatedEntity.getName());
			return repository.save(entity);
		}).orElseThrow(() -> new RuntimeException("Entity not found"));
	}
}
