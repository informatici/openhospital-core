/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.dlvrtype.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.dlvrtype.model.DeliveryType;
import org.isf.utils.exception.OHException;

public class TestDeliveryType {

	private String code = "Z";
	private String description = "TestDescription";

	public DeliveryType setup(boolean usingSet) throws OHException {
		DeliveryType deliveryType;

		if (usingSet) {
			deliveryType = new DeliveryType();
			setParameters(deliveryType);
		} else {
			// Create DeliveryType with all parameters 
			deliveryType = new DeliveryType(code, description);
		}

		return deliveryType;
	}

	public void setParameters(DeliveryType deliveryType) {
		deliveryType.setCode(code);
		deliveryType.setDescription(description);
	}

	public void check(DeliveryType deliveryType) {
		assertThat(deliveryType.getCode()).isEqualTo(code);
		assertThat(deliveryType.getDescription()).isEqualTo(description);
	}
}
