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
package org.isf.stat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.File;
import java.sql.Connection;
import java.util.HashMap;

import javax.sql.DataSource;

import org.isf.OHCoreTestCase;
import org.isf.hospital.manager.HospitalBrowsingManager;
import org.isf.hospital.model.Hospital;
import org.isf.stat.dto.JasperReportResultDto;
import org.isf.stat.manager.JasperReportsManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

class Tests extends OHCoreTestCase {

	private static TestJasperReportResultDto testJasperReportResultDto;

	@Mock
	HospitalBrowsingManager hospitalBrowsingManager;
	@Mock
	DataSource dataSource;
	@Mock
	Hospital hospital;
	@Mock
	JasperReport jasperReport;
	@Mock
	JasperPrint jasperPrint;
	@Mock
	Connection connection;

	private AutoCloseable closeable;

	@BeforeAll
	static void setUpClass() {
		testJasperReportResultDto = new TestJasperReportResultDto();
	}

	@BeforeEach
	void setUp() {
		closeable = MockitoAnnotations.openMocks(this);
	}

	@AfterEach
	void closeService() throws Exception {
		closeable.close();
	}

	@Test
	void testSetGet() throws Exception {
		JasperReportResultDto jasperReportResultDto = testJasperReportResultDto.setup(true);
		assertThat(jasperReportResultDto).isNotNull();
		testJasperReportResultDto.check(jasperReportResultDto);

		jasperReportResultDto = testJasperReportResultDto.setup(false);
		testJasperReportResultDto.check(jasperReportResultDto);
	}

	@Test
	void testGetExamsListPdf() throws Exception {
		try (MockedStatic<JRLoader> mockedJRLoader = mockStatic(JRLoader.class);
						MockedStatic<JasperFillManager> mockedJasperFillManager = mockStatic(JasperFillManager.class);
						MockedStatic<JasperExportManager> mockedJasperExportManager = mockStatic(JasperExportManager.class)) {
			JasperReportsManager jasperReportsManager = new JasperReportsManager(hospitalBrowsingManager, dataSource);

			when(hospitalBrowsingManager.getHospital()).thenReturn(hospital);
			when(hospital.getDescription()).thenReturn("Description");

			mockedJRLoader.when(() -> JRLoader.loadObject(any(File.class))).thenReturn(jasperReport);
			when(dataSource.getConnection()).thenReturn(connection);
			mockedJasperFillManager.when(() -> JasperFillManager.fillReport(any(JasperReport.class), any(HashMap.class), any(Connection.class)))
							.thenReturn(jasperPrint);
			mockedJasperExportManager.when(() -> JasperExportManager.exportReportToPdfFile(any(JasperPrint.class), any(String.class)))
							.thenAnswer((Answer<Void>) invocation -> null);

			JasperReportResultDto jasperReportResultDto = jasperReportsManager.getExamsListPdf();
			assertThat(jasperReportResultDto).isNotNull();
			assertThat(jasperReportResultDto.getFilename()).containsAnyOf("rpt_base/PDF/examslist.pdf", "rpt_base\\PDF\\examslist.pdf");
			assertThat(jasperReportResultDto.getJasperFile()).containsAnyOf("rpt_base/examslist.jasper", "rpt_base\\examslist.jasper");
		}
	}

	@Test
	void testGetDiseasesListPdf() throws Exception {
		try (MockedStatic<JRLoader> mockedJRLoader = mockStatic(JRLoader.class);
						MockedStatic<JasperFillManager> mockedJasperFillManager = mockStatic(JasperFillManager.class);
						MockedStatic<JasperExportManager> mockedJasperExportManager = mockStatic(JasperExportManager.class)) {
			JasperReportsManager jasperReportsManager = new JasperReportsManager(hospitalBrowsingManager, dataSource);

			when(hospitalBrowsingManager.getHospital()).thenReturn(hospital);
			when(hospital.getDescription()).thenReturn("Description");

			mockedJRLoader.when(() -> JRLoader.loadObject(any(File.class))).thenReturn(jasperReport);
			when(dataSource.getConnection()).thenReturn(connection);
			mockedJasperFillManager.when(() -> JasperFillManager.fillReport(any(JasperReport.class), any(HashMap.class), any(Connection.class)))
							.thenReturn(jasperPrint);
			mockedJasperExportManager.when(() -> JasperExportManager.exportReportToPdfFile(any(JasperPrint.class), any(String.class)))
							.thenAnswer((Answer<Void>) invocation -> null);

			JasperReportResultDto jasperReportResultDto = jasperReportsManager.getDiseasesListPdf();
			assertThat(jasperReportResultDto).isNotNull();
			assertThat(jasperReportResultDto.getFilename()).containsAnyOf("rpt_base/PDF/diseaseslist.pdf", "rpt_base\\PDF\\diseaseslist.pdf");
			assertThat(jasperReportResultDto.getJasperFile()).containsAnyOf("rpt_base/diseaseslist.jasper", "rpt_base\\diseaseslist.jasper");
		}
	}

	@Test
	void testGetOperationsListPdf() throws Exception {
		try (MockedStatic<JRLoader> mockedJRLoader = mockStatic(JRLoader.class);
						MockedStatic<JasperFillManager> mockedJasperFillManager = mockStatic(JasperFillManager.class);
						MockedStatic<JasperExportManager> mockedJasperExportManager = mockStatic(JasperExportManager.class)) {
			JasperReportsManager jasperReportsManager = new JasperReportsManager(hospitalBrowsingManager, dataSource);

			when(hospitalBrowsingManager.getHospital()).thenReturn(hospital);
			when(hospital.getDescription()).thenReturn("Description");

			mockedJRLoader.when(() -> JRLoader.loadObject(any(File.class))).thenReturn(jasperReport);
			when(dataSource.getConnection()).thenReturn(connection);
			mockedJasperFillManager.when(() -> JasperFillManager.fillReport(any(JasperReport.class), any(HashMap.class), any(Connection.class)))
							.thenReturn(jasperPrint);
			mockedJasperExportManager.when(() -> JasperExportManager.exportReportToPdfFile(any(JasperPrint.class), any(String.class)))
							.thenAnswer((Answer<Void>) invocation -> null);

			JasperReportResultDto jasperReportResultDto = jasperReportsManager.getOperationsListPdf();
			assertThat(jasperReportResultDto).isNotNull();
			assertThat(jasperReportResultDto.getFilename()).containsAnyOf("rpt_base/PDF/operationslist.pdf", "rpt_base\\PDF\\operationslist.pdf");
			assertThat(jasperReportResultDto.getJasperFile()).containsAnyOf("rpt_base/operationslist.jasper", "rpt_base\\operationslist.jasper");
		}
	}
}
