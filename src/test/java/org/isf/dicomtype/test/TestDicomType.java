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
package org.isf.dicomtype.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.Random;

import javax.sql.rowset.serial.SerialBlob;

import org.isf.dicomtype.model.DicomType;
import org.isf.utils.exception.OHException;

public class TestDicomType {

	private String dicomTypeId = "ZZZ";
	private String dicomTypeDescription = "TestDicomTypeDescription";

	public DicomType setup(boolean usingSet) throws OHException {
		DicomType dicomType;

		if (usingSet) {
			dicomType = new DicomType();
			setParameters(dicomType);
		} else {
			// Create FileDicom with all parameters 
			dicomType = new DicomType(dicomTypeId, dicomTypeDescription);
		}

		return dicomType;
	}

	public void setParameters(DicomType dicomType) {
		dicomType.setDicomTypeID(dicomTypeId);
		dicomType.setDicomTypeDescription(dicomTypeDescription);
	}

	public void check(DicomType dicomType) {
		assertThat(dicomType.getDicomTypeID()).isEqualTo(dicomTypeId);
		assertThat(dicomType.getDicomTypeDescription()).isEqualTo(dicomTypeDescription);
	}

	public Blob createRandomBlob(int byteCount) {
		Blob blob = null;
		byte[] data;

		data = new byte[byteCount];
		new Random().nextBytes(data);
		try {
			blob = new SerialBlob(data);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return blob;
	}
}
