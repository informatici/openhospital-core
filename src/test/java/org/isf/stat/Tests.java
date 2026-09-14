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
package org.isf.stat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.isf.OHCoreTestCase;
import org.isf.generaldata.GeneralData;
import org.isf.hospital.manager.HospitalBrowsingManager;
import org.isf.hospital.model.Hospital;
import org.isf.stat.dto.JasperReportResultDto;
import org.isf.stat.dto.ReportLauncherDto;
import org.isf.stat.manager.JasperReportsManager;
import org.isf.ward.manager.WardBrowserManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

class Tests extends OHCoreTestCase {

	private static final String RPT_STAT = "rpt_stat";
	private static final String RPT_EXTRA = "rpt_extra";

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
	@Mock
	WardBrowserManager wardBrowserManager;

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
			JasperReportsManager jasperReportsManager = new JasperReportsManager(hospitalBrowsingManager, dataSource, wardBrowserManager);

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
			JasperReportsManager jasperReportsManager = new JasperReportsManager(hospitalBrowsingManager, dataSource, wardBrowserManager);

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
			JasperReportsManager jasperReportsManager = new JasperReportsManager(hospitalBrowsingManager, dataSource, wardBrowserManager);

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

	@Test
	void testGetReportsList() throws Exception {
		String previousLanguage = GeneralData.LANGUAGE;
		Path statFolder = Path.of(RPT_STAT);
		Path extraFolder = Path.of(RPT_EXTRA);
		try (MockedStatic<JRLoader> mockedJRLoader = mockStatic(JRLoader.class)) {
			GeneralData.LANGUAGE = "en";

			// rpt_stat report with a localized title and two prompt parameters
			Files.createDirectories(statFolder.resolve("en"));
			Files.createFile(statFolder.resolve("POI_ByAgeBySex.jasper"));
			Files.writeString(statFolder.resolve("en").resolve("POI_ByAgeBySex.properties"), "jTitle=Patients by age and sex\n");

			// rpt_extra report whose title comes from the default (non-localized) properties
			Files.createDirectories(extraFolder);
			Files.createFile(extraFolder.resolve("Custom_Report.jasper"));
			Files.writeString(extraFolder.resolve("Custom_Report.properties"), "jTitle=Custom report\n");

			// a report without any title must be skipped
			Files.createFile(statFolder.resolve("NoTitle.jasper"));

			JRParameter systemParameter = mock(JRParameter.class);
			when(systemParameter.isSystemDefined()).thenReturn(true);
			JRParameter monthParameter = mock(JRParameter.class);
			when(monthParameter.isSystemDefined()).thenReturn(false);
			when(monthParameter.isForPrompting()).thenReturn(true);
			when(monthParameter.getName()).thenReturn("month");
			JRParameter yearParameter = mock(JRParameter.class);
			when(yearParameter.isSystemDefined()).thenReturn(false);
			when(yearParameter.isForPrompting()).thenReturn(true);
			when(yearParameter.getName()).thenReturn("year");

			when(jasperReport.getParameters()).thenReturn(new JRParameter[] { systemParameter, monthParameter, yearParameter });
			mockedJRLoader.when(() -> JRLoader.loadObject(any(File.class))).thenReturn(jasperReport);

			JasperReportsManager jasperReportsManager = new JasperReportsManager(hospitalBrowsingManager, dataSource, wardBrowserManager);
			List<ReportLauncherDto> reports = jasperReportsManager.getReportsList();

			// NoTitle.jasper is skipped, the remaining reports are sorted by title
			assertThat(reports).hasSize(2);
			assertThat(reports).extracting(ReportLauncherDto::getTitle).containsExactly("Custom report", "Patients by age and sex");

			ReportLauncherDto statReport = reports.stream().filter(r -> "POI_ByAgeBySex".equals(r.getFileName())).findFirst().orElseThrow();
			assertThat(statReport.getFolder()).isEqualTo(RPT_STAT);
			assertThat(statReport.getTitle()).isEqualTo("Patients by age and sex");
			// the system-defined parameter is excluded, only the prompt parameters remain
			assertThat(statReport.getUserInputParameters()).containsExactly("month", "year");

			ReportLauncherDto extraReport = reports.stream().filter(r -> "Custom_Report".equals(r.getFileName())).findFirst().orElseThrow();
			assertThat(extraReport.getFolder()).isEqualTo(RPT_EXTRA);
		} finally {
			GeneralData.LANGUAGE = previousLanguage;
			deleteRecursively(statFolder);
			deleteRecursively(extraFolder);
		}
	}

	@Test
	void testGetReportsListWithCountrySpecificLanguage() throws Exception {
		String previousLanguage = GeneralData.LANGUAGE;
		Path statFolder = Path.of(RPT_STAT);
		try (MockedStatic<JRLoader> mockedJRLoader = mockStatic(JRLoader.class)) {
			// the languages shipped with a country (am_ET, sv_SE, zh_CN) name their folder exactly as configured
			GeneralData.LANGUAGE = "am_ET";

			Files.createDirectories(statFolder.resolve("am_ET"));
			Files.createFile(statFolder.resolve("POI_ByAgeBySex.jasper"));
			Files.writeString(statFolder.resolve("POI_ByAgeBySex.properties"), "jTitle=Patients by age and sex\n");
			Files.writeString(statFolder.resolve("am_ET").resolve("POI_ByAgeBySex.properties"), "jTitle=localized title\n");

			when(jasperReport.getParameters()).thenReturn(new JRParameter[] {});
			mockedJRLoader.when(() -> JRLoader.loadObject(any(File.class))).thenReturn(jasperReport);

			JasperReportsManager jasperReportsManager = new JasperReportsManager(hospitalBrowsingManager, dataSource, wardBrowserManager);
			List<ReportLauncherDto> reports = jasperReportsManager.getReportsList();

			// the localized title wins over the one sitting next to the report
			assertThat(reports).extracting(ReportLauncherDto::getTitle).containsExactly("localized title");
		} finally {
			GeneralData.LANGUAGE = previousLanguage;
			deleteRecursively(statFolder);
		}
	}

	private static void deleteRecursively(Path root) throws Exception {
		if (!Files.exists(root)) {
			return;
		}
		try (var paths = Files.walk(root)) {
			paths.sorted(Comparator.reverseOrder()).forEach(path -> path.toFile().delete());
		}
	}
}
