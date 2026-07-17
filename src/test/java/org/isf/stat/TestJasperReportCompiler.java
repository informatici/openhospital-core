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

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import org.isf.stat.manager.JasperReportCompiler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TestJasperReportCompiler {

	/**
	 * A minimal, valid JasperReports 7 template: no XML namespace and band children wrapped in {@code <element kind="…">}.
	 */
	private static final String VALID_JRXML = """
			<?xml version="1.0" encoding="UTF-8"?>
			<jasperReport name="TestAutoCompile" pageWidth="595" pageHeight="842" columnWidth="555" leftMargin="20" rightMargin="20" topMargin="20" bottomMargin="20" uuid="1a2b3c4d-0000-0000-0000-000000000001">
				<detail>
					<band height="20">
						<element kind="staticText" x="0" y="0" width="555" height="20" uuid="1a2b3c4d-0000-0000-0000-000000000002">
							<text><![CDATA[OP-1403 auto-compile test]]></text>
						</element>
					</band>
				</detail>
			</jasperReport>
			""";

	/**
	 * A legacy JasperReports 6 template: it declares the old XML namespace and therefore cannot be compiled by
	 * JasperReports 7.
	 */
	private static final String LEGACY_JRXML = """
			<?xml version="1.0" encoding="UTF-8"?>
			<jasperReport xmlns="http://jasperreports.sourceforge.net/jasperreports" name="Legacy" pageWidth="595" pageHeight="842" columnWidth="555" uuid="1a2b3c4d-0000-0000-0000-000000000003"/>
			""";

	@TempDir
	File reportFolder;

	@Test
	void testCompilesMissingReport() throws Exception {
		write("report.jrxml", VALID_JRXML);
		File jasper = new File(reportFolder, "report.jasper");
		assertThat(jasper).doesNotExist();

		int compiled = JasperReportCompiler.compileStaleReports(List.of(reportFolder.getPath()));

		assertThat(compiled).isEqualTo(1);
		assertThat(jasper).exists();
	}

	@Test
	void testSkipsUpToDateReport() throws Exception {
		write("report.jrxml", VALID_JRXML);
		assertThat(JasperReportCompiler.compileStaleReports(List.of(reportFolder.getPath()))).isEqualTo(1);

		// a second run finds the .jasper not older than the .jrxml and recompiles nothing
		assertThat(JasperReportCompiler.compileStaleReports(List.of(reportFolder.getPath()))).isZero();
	}

	@Test
	void testRecompilesStaleReport() throws Exception {
		File jrxml = write("report.jrxml", VALID_JRXML);
		File jasper = new File(reportFolder, "report.jasper");
		JasperReportCompiler.compileStaleReports(List.of(reportFolder.getPath()));
		assertThat(jasper).exists();

		// make the compiled report older than its source
		assertThat(jasper.setLastModified(1_000L)).isTrue();
		assertThat(jrxml.setLastModified(2_000L)).isTrue();

		assertThat(JasperReportCompiler.compileStaleReports(List.of(reportFolder.getPath()))).isEqualTo(1);
		assertThat(jasper.lastModified()).isGreaterThan(1_000L);
	}

	@Test
	void testFailingReportIsSkippedAndDoesNotStopTheOthers() throws Exception {
		write("legacy.jrxml", LEGACY_JRXML);
		write("good.jrxml", VALID_JRXML);

		int compiled = JasperReportCompiler.compileStaleReports(List.of(reportFolder.getPath()));

		// only the valid report is compiled; the legacy one fails (logged) without stopping the scan or throwing
		assertThat(compiled).isEqualTo(1);
		assertThat(new File(reportFolder, "good.jasper")).exists();
		assertThat(new File(reportFolder, "legacy.jasper")).doesNotExist();
	}

	@Test
	void testNullAndMissingFoldersAreIgnored() {
		assertThat(JasperReportCompiler.compileStaleReports((List<String>) null)).isZero();
		assertThat(JasperReportCompiler.compileStaleReports(List.of(new File(reportFolder, "does-not-exist").getPath()))).isZero();
	}

	private File write(String name, String content) throws Exception {
		File file = new File(reportFolder, name);
		Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
		return file;
	}
}
