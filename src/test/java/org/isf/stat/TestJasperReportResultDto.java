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

import org.isf.stat.dto.JasperReportResultDto;
import org.isf.utils.exception.OHException;

import net.sf.jasperreports.engine.JasperPrint;

class TestJasperReportResultDto {

	JasperPrint jasperPrint = new JasperPrint();
	String jasperFile = "JasperFile";
	String filename = "FileName";

	public JasperReportResultDto setup(boolean usingSet) throws OHException {
		JasperReportResultDto jasperReportResultDto;

		if (usingSet) {
			jasperReportResultDto = new JasperReportResultDto();
			setParameters(jasperReportResultDto);
		} else {
			// Create JasperReportResultDto with all parameters
			jasperReportResultDto = new JasperReportResultDto(jasperPrint, jasperFile, filename);
		}
		return jasperReportResultDto;
	}

	public void setParameters(JasperReportResultDto jasperReportResultDto) {
		jasperReportResultDto.setJasperPrint(jasperPrint);
		jasperReportResultDto.setJasperFile(jasperFile);
		jasperReportResultDto.setFilename(filename);
	}

	public void check(JasperReportResultDto jasperReportResultDto) {
		assertThat(jasperReportResultDto.getJasperPrint()).isEqualTo(jasperPrint);
		assertThat(jasperReportResultDto.getJasperFile()).isEqualTo(jasperFile);
		assertThat(jasperReportResultDto.getFilename()).isEqualTo(filename);
	}
}
