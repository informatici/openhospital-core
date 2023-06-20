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
package org.isf.medicalstockward.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.isf.medicals.model.Medical;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.utils.exception.OHException;
import org.isf.ward.model.Ward;

public class TestMedicalWard {

	private float in_quantity = (float) 100.100;
	private float out_quantity = (float) 30.30;

	public MedicalWard setup(Medical medical, Ward ward, Lot lot, boolean usingSet) throws OHException {
		MedicalWard medicalward;

		if (usingSet) {
			medicalward = new MedicalWard();
			setParameters(medicalward, medical, ward, lot);
		} else {
			// Create MedicalWard with all parameters 
			medicalward = new MedicalWard(ward, medical, in_quantity, out_quantity, lot);
		}

		return medicalward;
	}

	public void setParameters(MedicalWard medicalward, Medical medical, Ward ward, Lot lot) {
		medicalward.setMedical(medical);
		medicalward.setWard(ward);
		medicalward.setIn_quantity(in_quantity);
		medicalward.setOut_quantity(out_quantity);
		medicalward.setLot(lot);
	}

	public void check(MedicalWard medicalward) {
		assertThat(medicalward.getIn_quantity()).isCloseTo(in_quantity, within(0.1F));
		assertThat(medicalward.getOut_quantity()).isCloseTo(out_quantity, within(0.1F));
	}
}
