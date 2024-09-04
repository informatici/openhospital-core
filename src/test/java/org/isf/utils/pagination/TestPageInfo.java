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
package org.isf.utils.pagination;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TestPageInfo {

	@Test
	void testConstructor() {
		assertThat(setupPageInfo()).isNotNull();
	}

	@Test
	void testSetGet() {
		PageInfo pageInfo = new PageInfo();

		pageInfo.setSize(-9);
		assertThat(pageInfo.getSize()).isEqualTo(-9);

		pageInfo.setPage(-8);
		assertThat(pageInfo.getPage()).isEqualTo(-8);

		pageInfo.setNbOfElements(-7);
		assertThat(pageInfo.getNbOfElements()).isEqualTo(-7);

		pageInfo.setHasPreviousPage(true);
		assertThat(pageInfo.isHasPreviousPage()).isTrue();

		pageInfo.setHasNextPage(false);
		assertThat(pageInfo.isHasNextPage()).isFalse();

		pageInfo.setTotalNbOfElements(0);
		assertThat(pageInfo.getTotalNbOfElements()).isZero();

		pageInfo.setTotalPages(1);
		assertThat(pageInfo.getTotalPages()).isEqualTo(1);
	}

	protected static PageInfo setupPageInfo() {
		PageInfo pageInfo = new PageInfo(1, 2, 3, 4, 5, false, true);
		return pageInfo;
	}
}
