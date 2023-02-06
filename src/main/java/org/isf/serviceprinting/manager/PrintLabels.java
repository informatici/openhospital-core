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
package org.isf.serviceprinting.manager;

import java.io.File;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.isf.utils.db.DbSingleJpaConn;
import org.isf.utils.exception.OHServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

public class PrintLabels {

	private static final Logger LOGGER = LoggerFactory.getLogger(PrintLabels.class);

	public PrintLabels(String filename, Integer patId) throws OHServiceException {
		try {
			Map<String, Object> parameters = new HashMap<>();

			parameters.put("patientID", String.valueOf(patId == null ? "" : patId));

			StringBuilder sbFilename = new StringBuilder();
			sbFilename.append("rpt_base");
			sbFilename.append(File.separator);

			sbFilename.append(filename);
			sbFilename.append(".jasper");
			File jasperFile = new File(sbFilename.toString());
			Connection conn = DbSingleJpaConn.getConnection();
			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperFile);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);
			JasperPrintManager.printReport(jasperPrint, true);

		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
		}

	}
}
