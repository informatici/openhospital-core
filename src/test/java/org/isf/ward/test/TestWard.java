/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.ward.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.utils.exception.OHException;
import org.isf.ward.model.Ward;

public class TestWard {

	private String code = "Z";
	private String maternityCode = "M";
	private String description = "TestDescription";
	private String telephone = "TestTelephone";
	private String fax = "TestFac";
	private String email = "TestEmail@gmail.com";
	private Integer beds = 100;
	private Integer nurs = 101;
	private Integer docs = 102;
	private boolean isOpd = true;
	private boolean isPharmacy = true;
	private boolean isFemale = true;
	private boolean isMale = false;
	private int visitDuration = 30;

	public Ward setup(boolean usingSet) throws OHException {
		return setup(usingSet, false);
	}

	public Ward setup(boolean usingSet, boolean maternity) throws OHException {
		Ward ward;

		if (usingSet) {
			ward = new Ward();
			setParameters(ward, maternity);
		} else {
			// Create Ward with all parameters 
			ward = new Ward(code, description, telephone, fax, email, beds, nurs, docs,
					isOpd, isPharmacy, isMale, isFemale);
		}
		if (maternity) {
			ward.setCode(maternityCode);
		}
		return ward;
	}

	public void setParameters(Ward ward, boolean maternity) {
		ward.setCode(code);
		if (maternity) {
			ward.setCode(maternityCode);
		}
		ward.setBeds(beds);
		ward.setDescription(description);
		ward.setDocs(docs);
		ward.setEmail(email);
		ward.setFax(fax);
		ward.setFemale(isFemale);
		ward.setMale(isMale);
		ward.setNurs(nurs);
		ward.setOpd(isOpd);
		ward.setPharmacy(isPharmacy);
		ward.setTelephone(telephone);
		ward.setVisitDuration(visitDuration);
	}

	public void check(Ward ward) {
		assertThat(ward.getCode()).isEqualTo(code);
		assertThat(ward.getBeds()).isEqualTo(beds);
		assertThat(ward.getDescription()).isEqualTo(description);
		assertThat(ward.getDocs()).isEqualTo(docs);
		assertThat(ward.getEmail()).isEqualTo(email);
		assertThat(ward.getFax()).isEqualTo(fax);
		assertThat(ward.isFemale()).isEqualTo(isFemale);
		assertThat(ward.isMale()).isEqualTo(isMale);
		assertThat(ward.getNurs()).isEqualTo(nurs);
		assertThat(ward.isOpd()).isEqualTo(isOpd);
		assertThat(ward.isPharmacy()).isEqualTo(isPharmacy);
		assertThat(ward.getTelephone()).isEqualTo(telephone);
		assertThat(ward.getVisitDuration()).isEqualTo(visitDuration);
	}
}
