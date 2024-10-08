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
package org.isf.settings.service;

import java.util.List;

import org.isf.settings.model.Setting;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Setting Core Service
 * @author Silevester D.
 * @since v1.15
 */
@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class SettingIoOperations {

	private final SettingIoOperationRepository repository;

	public SettingIoOperations(SettingIoOperationRepository settingIoOperationRepository) {
		this.repository = settingIoOperationRepository;
	}

	public List<Setting> findAll() throws OHServiceException {
		return repository.findAllByDeleted(false);
	}

	public Setting save(Setting setting) throws OHServiceException {
		return repository.save(setting);
	}

	public Setting getByCode(String code) throws OHServiceException {
		return repository.findFirstByCodeAndDeleted(code, false);
	}

	public Setting getById(int id) throws OHServiceException {
		return repository.findFirstByIdAndDeleted(id, false);
	}

	public void saveAll(List<Setting> settings) throws OHServiceException {
		repository.saveAll(settings);
	}
}
