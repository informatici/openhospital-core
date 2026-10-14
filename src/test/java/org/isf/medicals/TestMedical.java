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
package org.isf.medicals;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.medicals.model.Medical;
import org.isf.medtype.model.MedicalType;
import org.isf.utils.exception.OHException;

public class TestMedical {

	private Integer code;
	private String prod_code = "TP1";
	private String description = "TestDescription";
	private Integer pcsperpck = 11;
	private int inqty = 30;
	private int outqty = 20;
	private int minqty = 40;

	public Medical setup(MedicalType medicalType, boolean usingSet) throws OHException {
		Medical medical;

		if (usingSet) {
			medical = new Medical();
			setParameters(medical, medicalType);
		} else {
			// Create Medical with all parameters
			medical = new Medical(code, medicalType, prod_code, description, pcsperpck, minqty, inqty, outqty);
		}

		return medical;
	}

	public void setParameters(Medical medical, MedicalType medicalType) {
		medical.setDescription(description);
		medical.setInqty(inqty);
		medical.setMinqty(minqty);
		medical.setOutqty(outqty);
		medical.setPcsperpck(pcsperpck);
		medical.setProdCode(prod_code);
		medical.setType(medicalType);
	}

	public void check(Medical medical) {
		assertThat(medical.getDescription()).isEqualTo(description);
		assertThat(medical.getInqty()).isEqualTo(inqty);
		assertThat(medical.getMinqty()).isEqualTo(minqty);
		assertThat(medical.getOutqty()).isEqualTo(outqty);
		assertThat(medical.getPcsperpck()).isEqualTo(pcsperpck);
		assertThat(medical.getProdCode()).isEqualTo(prod_code);
	}
}
