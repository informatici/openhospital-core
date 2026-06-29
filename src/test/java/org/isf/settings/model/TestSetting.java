/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.settings.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHException;
import org.isf.visits.model.Visit;
import org.isf.ward.model.Ward;

public class TestSetting {

	private int id = 1;
	private String code = "TEST_CODE";
	private SettingCategory category = SettingCategory.general;
	private SettingValueType type = SettingValueType.select;
	private String valueOptions = "opt1,opt2,opt3";
	private String defaultValue = "defaultVal";
	private String value = "currentVal";
	private String description = "Test description";
	private boolean needRestart = true;

	public Setting setup(boolean usingSet) {
		Setting setting;

		if (usingSet) {
			setting = new Setting();
			setParameters(setting);
		} else {
			setting = new Setting();
			setting.setId(id);
			setting.setCode(code);
			setting.setCategory(category);
			setting.setType(type);
			setting.setValueOptions(valueOptions);
			setting.setDefaultValue(defaultValue);
			setting.setValue(value);
			setting.setDescription(description);
			setting.setNeedRestart(needRestart);
		}

		return setting;
	}

	public void setParameters(Setting setting) {
		setting.setId(id);
		setting.setCode(code);
		setting.setCategory(category);
		setting.setType(type);
		setting.setValueOptions(valueOptions);
		setting.setDefaultValue(defaultValue);
		setting.setValue(value);
		setting.setDescription(description);
		setting.setNeedRestart(needRestart);
	}

	public void check(Setting setting) {
		assertThat(setting.getId()).isEqualTo(id);
		assertThat(setting.getCode()).isEqualTo(code);
		assertThat(setting.getCategory()).isEqualTo(category);
		assertThat(setting.getType()).isEqualTo(type);
		assertThat(setting.getValueOptions()).isEqualTo(valueOptions);
		assertThat(setting.getDefaultValue()).isEqualTo(defaultValue);
		assertThat(setting.getValue()).isEqualTo(value);
		assertThat(setting.getDescription()).isEqualTo(description);
		assertThat(setting.getNeedRestart()).isEqualTo(needRestart);
	}
}