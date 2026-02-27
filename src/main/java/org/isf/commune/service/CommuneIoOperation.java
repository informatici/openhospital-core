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
package org.isf.commune.service;

import org.isf.commune.model.Commune;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class CommuneIoOperation {
	private final CommuneIoOperationRepository communeIoOperationRepository;

	public CommuneIoOperation(CommuneIoOperationRepository communeIoOperationRepository) {
		this.communeIoOperationRepository = communeIoOperationRepository;
	}

	public List<Commune> getCommunes() {
		return communeIoOperationRepository.findAll();
	}

	public Commune getCommuneById(Integer id) {
		return communeIoOperationRepository.findById(id).orElse(null);
	}

	public Commune save(Commune commune) {
		return communeIoOperationRepository.save(commune);
	}

	public Commune update(Commune commune) {
		return communeIoOperationRepository.save(commune);
	}

	public void delete(Commune commune) {
		communeIoOperationRepository.delete(commune);
	}
}
