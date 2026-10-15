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
package org.isf.stat.manager;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlWriter;

/**
 * Optional automatic compilation of JasperReports templates ({@code .jrxml}) into their compiled form ({@code .jasper}),
 * see OP-1403.
 * <p>
 * Open Hospital ships and loads precompiled {@code .jasper} files. When a {@code .jrxml} is edited on disk but its
 * {@code .jasper} is not regenerated, report generation fails at runtime with a stale (or missing) compiled report. This
 * helper scans the report folders and (re)compiles every template whose {@code .jasper} is missing or older than the
 * source, so that keeping the {@code .jrxml} up to date is enough.
 * <p>
 * It is meant to be triggered at application startup, guarded by a configuration flag
 * ({@code GeneralData.AUTOCOMPILEJRXML}), and it is a no-op when every compiled report is already up to date.
 * <p>
 * <b>Security:</b> JRXML templates can contain executable expressions, so automatic compilation must only be enabled when
 * the report files are trusted (i.e. not writable by untrusted users).
 */
public final class JasperReportCompiler {

	private static final Logger LOGGER = LoggerFactory.getLogger(JasperReportCompiler.class);

	private static final String JRXML_EXTENSION = ".jrxml";
	private static final String JASPER_EXTENSION = ".jasper";

	private JasperReportCompiler() {
	}

	/**
	 * Scans the given folders (recursively) and compiles every report whose {@code .jasper} is missing or older than its
	 * {@code .jrxml}. A template that fails to compile is logged and skipped, so a single broken report does not stop the
	 * others.
	 *
	 * @param folders the report folders to scan (e.g. {@code rpt_base}, {@code rpt_extra}, {@code rpt_stat}); {@code null}
	 *                or non-existent folders are ignored.
	 * @return the number of reports (re)compiled.
	 */
	public static int compileStaleReports(List<String> folders) {
		if (folders == null) {
			return 0;
		}
		int compiled = 0;
		for (String folder : folders) {
			compiled += compileStaleReports(folder == null ? null : new File(folder));
		}
		return compiled;
	}

	/**
	 * Scans a single folder (recursively) and compiles every report whose {@code .jasper} is missing or older than its
	 * {@code .jrxml}.
	 *
	 * @param folder the report folder to scan; when it is {@code null} or not a directory the method does nothing.
	 * @return the number of reports (re)compiled in this folder.
	 */
	public static int compileStaleReports(File folder) {
		if (folder == null || !folder.isDirectory()) {
			LOGGER.debug("Skipping report folder '{}': not an existing directory.", folder);
			return 0;
		}
		int compiled = 0;
		int failed = 0;
		for (File jrxml : listJrxmlFiles(folder)) {
			File jasper = new File(jrxml.getParentFile(), jasperNameOf(jrxml));
			if (isUpToDate(jasper, jrxml)) {
				continue;
			}
			try {
				JasperCompileManager.compileReportToFile(jrxml.getAbsolutePath(), jasper.getAbsolutePath());
				compiled++;
				LOGGER.info("Compiled report '{}'.", jrxml.getPath());
			} catch (Exception exception) {
				failed++;
				LOGGER.warn("Unable to compile report '{}': {}", jrxml.getPath(), exception.getMessage());
			}
		}
		if (compiled > 0 || failed > 0) {
			LOGGER.info("Report compilation in '{}': {} compiled, {} failed.", folder.getPath(), compiled, failed);
		}
		return compiled;
	}

	/**
	 * Migrates a legacy (JasperReports 6) report to the current JasperReports 7 format headless, i.e. without Jaspersoft
	 * Studio. A compiled report already carries a JasperReports 7 model, so this loads the committed {@code .jasper}, writes
	 * it back with {@link JRXmlWriter} as a JasperReports 7 {@code .jrxml} (no legacy namespace) and recompiles it with the
	 * pom's JasperReports version. It is the way to convert the templates Open Hospital still ships in the legacy JR6 XML
	 * format — which {@link JasperCompileManager} cannot compile from source directly — after which
	 * {@link #compileStaleReports(List)} keeps their {@code .jasper} up to date.
	 *
	 * @param compiledReport the committed {@code .jasper} to migrate.
	 * @param outputJrxml    where the JasperReports 7 {@code .jrxml} is written.
	 * @param outputJasper   where the recompiled {@code .jasper} is written.
	 * @throws JRException if the report cannot be loaded or recompiled.
	 * @throws IOException if the {@code .jrxml} cannot be written.
	 */
	public static void migrateToJasperReports7(File compiledReport, File outputJrxml, File outputJasper) throws JRException, IOException {
		JasperReport report = (JasperReport) JRLoader.loadObject(compiledReport);
		String jrxml = JRXmlWriter.writeReport(report, StandardCharsets.UTF_8.name());
		Files.write(outputJrxml.toPath(), jrxml.getBytes(StandardCharsets.UTF_8));
		JasperCompileManager.compileReportToFile(outputJrxml.getAbsolutePath(), outputJasper.getAbsolutePath());
		LOGGER.info("Migrated report '{}' to JasperReports 7: '{}'.", compiledReport.getPath(), outputJrxml.getPath());
	}

	private static List<File> listJrxmlFiles(File folder) {
		List<File> result = new ArrayList<>();
		collectJrxmlFiles(folder, result);
		return result;
	}

	private static void collectJrxmlFiles(File folder, List<File> result) {
		File[] entries = folder.listFiles();
		if (entries == null) {
			return;
		}
		for (File entry : entries) {
			if (entry.isDirectory()) {
				collectJrxmlFiles(entry, result);
			} else if (entry.getName().toLowerCase().endsWith(JRXML_EXTENSION)) {
				result.add(entry);
			}
		}
	}

	private static boolean isUpToDate(File jasper, File jrxml) {
		return jasper.exists() && jasper.lastModified() >= jrxml.lastModified();
	}

	private static String jasperNameOf(File jrxml) {
		String name = jrxml.getName();
		return name.substring(0, name.length() - JRXML_EXTENSION.length()) + JASPER_EXTENSION;
	}
}
