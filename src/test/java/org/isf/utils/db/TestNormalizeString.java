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
package org.isf.utils.db;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TestNormalizeString {

	@Test
	void testNormalizeString() throws Exception {
		assertThat(NormalizeString.normalizeString("touch" + "\u00e9")).isEqualTo("touche");
		assertThat(NormalizeString.normalizeString("a" + "\ufb03" + "ance")).isEqualTo("aance");
		assertThat(NormalizeString.normalizeString("fa" + "\u00e7" + "ade")).isEqualTo("facade");
	}

	@Test
	void testNormalizeCompareTo() throws Exception {
		assertThat(NormalizeString.normalizeCompareTo("touch" + "\u00e9", "fa" + "\u00e7" + "ade")).isGreaterThan(0);
		assertThat(NormalizeString.normalizeCompareTo("touch" + "\u00e9", "touche")).isZero();
	}

	@Test
	void testNormalizeCompareToIgnoreCase() throws Exception {
		assertThat(NormalizeString.normalizeCompareToIgnorecase("touch" + "\u00e9", "fa" + "\u00e7" + "ade")).isGreaterThan(0);
		assertThat(NormalizeString.normalizeCompareToIgnorecase("touch" + "\u00e9", "ToUcHe")).isZero();
	}

	@Test
	void testNormalizeContains() throws Exception {
		assertThat(NormalizeString.normalizeContains("touch" + "\u00e9", "fa" + "\u00e7" + "ade")).isFalse();
		assertThat(NormalizeString.normalizeContains("touch" + "\u00e9", "\u00e9")).isTrue();
	}
}
