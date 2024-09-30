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
package org.isf.serviceprinting;

import static org.assertj.core.api.Fail.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

import java.io.File;
import java.sql.Connection;
import java.util.HashMap;

import org.isf.OHCoreTestCase;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.serviceprinting.manager.PrintLabels;
import org.isf.utils.db.DbSingleJpaConn;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

class TestPrintLabels extends OHCoreTestCase {

	@Mock
	private JasperReport jasperReport;
	@Mock
	private JasperPrint jasperPrint;
	@Mock
	private Connection connection;

	@Autowired
	private PatientIoOperationRepository patientIoOperationRepository;

	private static TestPatient testPatient;

	private AutoCloseable closeable;

	@BeforeAll
	static void setUpClass() {
		testPatient = new TestPatient();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
		closeable = MockitoAnnotations.openMocks(this);
	}

	@AfterEach
	void closeService() throws Exception {
		closeable.close();
	}

	@Test
	void testPrintLabels() throws Exception {
		try (MockedStatic<DbSingleJpaConn> mockedDbSingleJpaConn = mockStatic(DbSingleJpaConn.class);
			 MockedStatic<JRLoader> mockedJRLoader = mockStatic(JRLoader.class);
			 MockedStatic<JasperFillManager> mockedJasperFillManager = mockStatic(JasperFillManager.class);
			 MockedStatic<JasperPrintManager> mockedJasperPrintManager = mockStatic(JasperPrintManager.class)) {
			mockedDbSingleJpaConn.when(() -> DbSingleJpaConn.getConnection()).thenReturn(connection);
			mockedJRLoader.when(() -> JRLoader.loadObject(any(File.class))).thenReturn(jasperReport);
			mockedJasperFillManager.when(() -> JasperFillManager.fillReport(any(JasperReport.class), any(HashMap.class), any(Connection.class)))
							.thenReturn(jasperPrint);
			// returns a void so there is no need for this instruction
			//mockedJasperPrintManager.when(() -> JasperPrintManager.printReport(jasperPrint, true));
			Integer patId = setupTestPatient(false);
			new PrintLabels("LabelForSamples", patId);
		} catch(Exception exception) {
			fail("testPrintLabels() should not generate an exception");
		}
	}

	private Integer setupTestPatient(boolean usingSet) throws OHException {
		Patient patient = testPatient.setup(usingSet);
		patientIoOperationRepository.saveAndFlush(patient);
		return patient.getCode();
	}

}
