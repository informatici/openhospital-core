/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.settings.manager;

import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.settings.model.Setting;
import org.isf.settings.service.SettingIoOperations;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;

@Component
public class SettingManager {

	private final SettingIoOperations operations;

	public SettingManager(SettingIoOperations settingIoOperations) {
		this.operations = settingIoOperations;
	}

	public List<Setting> findAll() throws OHServiceException {
		return operations.findAll();
	}

	public Setting update(Setting setting) throws OHServiceException {
		Setting existingSetting = operations.getByCode(setting.getCode());

		if (!existingSetting.getEditable()) {
			throw new OHDataValidationException(
							new OHExceptionMessage(MessageBundle.getMessage("settings.settingnoteditable"))
			);
		}

		return operations.save(setting);
	}

	public Setting getByCode(String code) {
		return operations.getByCode(code);
	}

	public Setting getById(int id) {
		return operations.getById(id);
	}

	public boolean resetAll() throws OHServiceException {
		List<Setting> settings = operations.findAll();

		for (var setting: settings) {
			setting.setValue(setting.getDefaultValue());
		}

		try {
			operations.saveAll(settings);
			return true;
		} catch (OHServiceException e) {
			return false;
		}
	}
}
