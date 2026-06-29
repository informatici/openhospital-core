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
package org.isf.settings.manager;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.patient.model.Patient;
import org.isf.settings.model.Setting;
import org.isf.settings.model.TestSetting;
import org.isf.settings.service.SettingIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.visits.TestVisit;
import org.isf.visits.model.Visit;
import org.isf.ward.model.Ward;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Setting Manager Tests
 * @author Silevester D.
 * @since v1.15
 */
public class SettingManagerTest extends OHCoreTestCase {

	private static TestSetting testSetting;

	@Autowired
	private SettingManager manager;

	@Autowired
	private SettingIoOperationRepository repository;

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
		executeSQLScript("LoadSettingTable.sql");
		testSetting = new TestSetting();
	}

	private int setupTestSetting(boolean usingSet) throws OHException {
		Setting setting = testSetting.setup(usingSet);
		repository.saveAndFlush(setting);
		return setting.getId();
	}

	private void checkSettingIntoDb(int id) {
		Setting foundSetting = repository.findFirstById(id);
		testSetting.check(foundSetting);
	}

	@Test
	@DisplayName("Test Setting Gets")
	void testSettingGets() throws Exception {
		int id = setupTestSetting(false);
		checkSettingIntoDb(id);
	}

	@Test
	@DisplayName("Test Setting Sets")
	void testSettingSets() throws Exception {
		int id = setupTestSetting(true);
		checkSettingIntoDb(id);
	}


	@Test
	@DisplayName("Get all settings")
	void testGetAllSettings() throws OHServiceException {
		List<Setting> settings = manager.findAll();

		assertThat(settings).isNotNull();
		assertThat(settings).isNotEmpty();
		assertThat(settings.size()).isEqualTo(55);
	}

	@Test
	@DisplayName("Get setting by ID")
	void testGetSettingById() throws OHServiceException {
		Setting setting = manager.getById(1);

		assertThat(setting).isNotNull();
		assertThat(setting.getCode()).isEqualTo("SINGLEUSER");
	}

	@Test
	@DisplayName("Get non existing setting by ID")
	void testGetNonExistingSettingById() throws OHServiceException {
		Setting setting = manager.getById(156);

		assertThat(setting).isNull();
	}

	@Test
	@DisplayName("Get setting by Code")
	void testGetSettingByCode() throws OHServiceException {
		Setting setting = manager.getByCode("SINGLEUSER");

		assertThat(setting).isNotNull();
		assertThat(setting.getId()).isEqualTo(1);
	}

	@Test
	@DisplayName("Get non existing setting by Code")
	void testGetNonExistingSettingByCode() throws OHServiceException {
		Setting setting = manager.getByCode("MODEMODE");

		assertThat(setting).isNull();
	}

	@Test
	@DisplayName("Update setting")
	void testUpdateSetting() throws OHServiceException {
		Setting setting = manager.getById(10);
		String oldValue = setting.getValue();

		setting.setValue(String.valueOf(!Boolean.parseBoolean(oldValue)));
		Setting savedSetting = manager.update(setting);

		assertThat(savedSetting.getDefaultValue()).isEqualTo(setting.getDefaultValue());
		assertThat(savedSetting.getValue()).isNotEqualTo(oldValue);

		setting = manager.getById(10);

		assertThat(savedSetting.getValue()).isEqualTo(setting.getValue());
	}

	@Test
	@DisplayName("Reset all settings to default")
	void testResetAllSettingsToDefault() throws OHServiceException {
		Setting setting = manager.getByCode("USERSLISTLOGIN");

		assertThat(setting.getValue()).isEqualTo("FALSE");

		setting.setValue("TRUE");
		setting = manager.update(setting);
		String oldValue = setting.getValue();

		boolean reset = manager.resetAll();
		Setting resetSetting = manager.getByCode("USERSLISTLOGIN");

		assertThat(reset).isTrue();
		assertThat(oldValue).isNotEqualTo(resetSetting.getValue());
		assertThat(resetSetting.getDefaultValue()).isEqualTo(resetSetting.getValue());
	}
}
