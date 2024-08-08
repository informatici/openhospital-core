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

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class TestAuditable {

	@Test
	void testSetGet() throws Exception {
		MyAuditable myAuditable = new MyAuditable();

		myAuditable.setCreatedBy("createdBy");
		assertThat(myAuditable.getCreatedBy()).isEqualTo("createdBy");

		LocalDateTime dateTime = LocalDateTime.of(2020, 1, 2, 3, 4, 5);
		myAuditable.setCreatedDate(dateTime);
		assertThat(myAuditable.getCreatedDate()).isEqualTo(dateTime);

		myAuditable.setActive(-99);
		assertThat(myAuditable.getActive()).isEqualTo(-99);

		myAuditable.setLastModifiedBy("asdfg");
		assertThat(myAuditable.getLastModifiedBy()).isEqualTo("asdfg");

		myAuditable.setLastModifiedDate(dateTime);
		assertThat(myAuditable.getLastModifiedDate()).isEqualTo(dateTime);
	}

	class MyAuditable extends Auditable<String> {

	}
}
