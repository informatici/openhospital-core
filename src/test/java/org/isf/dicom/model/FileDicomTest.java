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
package org.isf.dicom.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;

import org.isf.OHCoreTestCase;
import org.isf.dicom.TestDicom;
import org.isf.dicom.service.DicomIoOperationRepository;
import org.isf.dicomtype.TestDicomType;
import org.isf.dicomtype.model.DicomType;
import org.isf.dicomtype.service.DicomTypeIoOperationRepository;
import org.isf.menu.manager.Context;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

class FileDicomTest extends OHCoreTestCase {

	private static TestDicom testFileDicom;
	private static TestDicomType testDicomType;

	@Autowired
	DicomIoOperationRepository dicomIoOperationRepository;
	@Autowired
	DicomTypeIoOperationRepository dicomTypeIoOperationRepository;
	@Autowired
	private ApplicationContext applicationContext;

	@BeforeAll
	public static void setUpClass() throws ParseException {
		testFileDicom = new TestDicom();
		testDicomType = new TestDicomType();
	}

	@BeforeEach
	void setUp() throws Exception {
		cleanH2InMemoryDb();
		Context.setApplicationContext(applicationContext);
	}

	@Test
	void constructor_shouldSetFields() throws Exception {
		long code = setupTestFileDicom(false);
		checkFileDicomIntoDb(code);
	}

	@Test
	void setters_shouldSetFields() throws Exception {
		long code = setupTestFileDicom(true);
		checkFileDicomIntoDb(code);
	}

	@Test
	void equals_shouldCompareIdFile() throws Exception {
		DicomType dicomType = testDicomType.setup(false);
		FileDicom fileDicom = testFileDicom.setup(dicomType, true);

		assertThat(fileDicom)
				.isNotNull()
				.isEqualTo(fileDicom)
				.isNotEqualTo("someString");

		FileDicom fileDicom2 = testFileDicom.setup(dicomType, true);
		fileDicom2.setIdFile(99L);
		assertThat(fileDicom).isNotEqualTo(fileDicom2);

		fileDicom2.setIdFile(fileDicom.getIdFile());
		assertThat(fileDicom).isEqualTo(fileDicom2);
	}

	@Test
	void hashCode_shouldBeComputedFromIdFile() throws Exception {
		DicomType dicomType = testDicomType.setup(true);
		FileDicom fileDicom = testFileDicom.setup(dicomType, false);
		// compute value
		int hashCode = fileDicom.hashCode();
		assertThat(hashCode).isEqualTo(23 * 133 + fileDicom.getIdFile());
		// used computed value
		assertThat(fileDicom.hashCode()).isEqualTo(hashCode);
	}

	@Test
	void getDicomType_shouldReturnDicomType() throws Exception {
		DicomType dicomType = testDicomType.setup(true);
		FileDicom fileDicom = testFileDicom.setup(dicomType, true);
		assertThat(fileDicom.getDicomType()).isEqualTo(dicomType);
	}

	@Test
	void getDicomThumbnailAsImage_shouldReturnNull_whenThumbnailIsNotAnImage() throws Exception {
		DicomType dicomType = testDicomType.setup(false);
		FileDicom fileDicom = testFileDicom.setup(dicomType, false);
		assertThat(fileDicom.getDicomThumbnailAsImage()).isNull();
	}

	private long setupTestFileDicom(boolean usingSet) throws OHException {
		DicomType dicomType = testDicomType.setup(true);
		FileDicom dicom = testFileDicom.setup(dicomType, usingSet);
		dicomTypeIoOperationRepository.saveAndFlush(dicomType);
		dicomIoOperationRepository.saveAndFlush(dicom);
		return dicom.getIdFile();
	}

	private void checkFileDicomIntoDb(long code) throws Exception {
		FileDicom foundFileDicom = dicomIoOperationRepository.findById(code).orElse(null);
		assertThat(foundFileDicom).isNotNull();
		testFileDicom.check(foundFileDicom);
	}

}
