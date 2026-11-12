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
package org.isf.generaldata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class GeneralDataTest {

	@Test
	void getGeneralData_shouldLoadLanguageFromSettings() {
		GeneralData generalData = GeneralData.getGeneralData();
		
		assertThat(generalData).isNotNull();
		
		assertThat(GeneralData.LANGUAGE).isEqualTo("es");
	}

	@Test
	void getSINGLEUSER_shouldReturnFalse_whenSettingIsNo() {
		GeneralData generalData = GeneralData.getGeneralData();

		assertThat(generalData).isNotNull();

		assertThat(generalData.getSINGLEUSER()).isFalse();
	}

	@Test
	void getUSERSLISTLOGIN_shouldReturnTrue_whenSettingIsYes() {
		GeneralData generalData = GeneralData.getGeneralData();

		assertThat(generalData).isNotNull();

		assertThat(generalData.getUSERSLISTLOGIN()).isTrue();
	}

	@Test
	void getGeneralData_shouldReturnInstance_whenCalledAfterReset() {
		// get them one time
		GeneralData generalData = GeneralData.getGeneralData();
		// throw them away
		GeneralData.reset();

		// get it a second time
		generalData = GeneralData.getGeneralData();
		assertThat(generalData).isNotNull();
	}
}
