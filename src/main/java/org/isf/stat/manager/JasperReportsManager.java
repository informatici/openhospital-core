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
package org.isf.stat.manager;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.hospital.manager.HospitalBrowsingManager;
import org.isf.hospital.model.Hospital;
import org.isf.medicals.model.Medical;
import org.isf.patient.model.Patient;
import org.isf.stat.dto.JasperReportResultDto;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.db.UTF8Control;
import org.isf.utils.excel.ExcelExporter;
import org.isf.utils.exception.OHReportException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.isf.utils.time.TimeTools;
import org.isf.ward.model.Ward;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpressionChunk;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRQuery;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.base.JRBaseSubreport;
import net.sf.jasperreports.engine.util.JRLoader;

@Component
public class JasperReportsManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(JasperReportsManager.class);

	private static final String YYYY_MM_DD = "yyyy-MM-dd";
	private static final String DD_MM_YYYY = "dd/MM/yyyy";
	private static final String YYYY_M_MDD = "yyyyMMdd";
	private static final String E_D_MMMM_YYYY = "E d, MMMM yyyy";
	private static final String COMMON_ERROR_TITLE = "angal.common.error.title";
	private static final String STAT_REPORTERROR_MSG = "angal.stat.reporterror.msg";

	private static final String RPT_BASE = "rpt_base";

	@Autowired
	private HospitalBrowsingManager hospitalManager;

	@Autowired
	private DataSource dataSource;

	public JasperReportResultDto getExamsListPdf() throws OHServiceException {

		try {
			final Map<String, Object> parameters = new HashMap<>();
			Hospital hospital = hospitalManager.getHospital();

			parameters.put("hospital", hospital.getDescription());

			String jasperFileName = "examslist";
			String pdfFilename = compilePDFFilename(RPT_BASE, jasperFileName, null, "pdf");

			JasperReportResultDto result = generateJasperReport(compileJasperFilename(RPT_BASE, jasperFileName), pdfFilename, parameters);
			JasperExportManager.exportReportToPdfFile(result.getJasperPrint(), pdfFilename);
			return result;
		} catch (Exception e) {
			LOGGER.error("", e);
			throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage(COMMON_ERROR_TITLE), MessageBundle.getMessage(STAT_REPORTERROR_MSG),
							OHSeverityLevel.ERROR));
		}
	}

	public JasperReportResultDto getDiseasesListPdf() throws OHServiceException {

		try {
			Map<String, Object> parameters = new HashMap<>();
			Hospital hospital = hospitalManager.getHospital();
			parameters.put("hospital", hospital.getDescription());

			String jasperFileName = "diseaseslist";
			String pdfFilename = compilePDFFilename(RPT_BASE, jasperFileName, null, "pdf");

			JasperReportResultDto result = generateJasperReport(compileJasperFilename(RPT_BASE, jasperFileName), pdfFilename, parameters);
			JasperExportManager.exportReportToPdfFile(result.getJasperPrint(), pdfFilename);
			return result;
		} catch (Exception e) {
			LOGGER.error("", e);
			throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage(COMMON_ERROR_TITLE), MessageBundle.getMessage(STAT_REPORTERROR_MSG),
							OHSeverityLevel.ERROR));
		}
	}

	public JasperReportResultDto getOperationsListPdf() throws OHServiceException {

		try {
			Map<String, Object> parameters = new HashMap<>();
			Hospital hospital = hospitalManager.getHospital();
			parameters.put("hospital", hospital.getDescription());

			String jasperFileName = "operationslist";
			String pdfFilename = compilePDFFilename(RPT_BASE, jasperFileName, null, "pdf");

			JasperReportResultDto result = generateJasperReport(compileJasperFilename(RPT_BASE, jasperFileName), pdfFilename, parameters);
			JasperExportManager.exportReportToPdfFile(result.getJasperPrint(), pdfFilename);
			return result;
		} catch (Exception e) {
			LOGGER.error("", e);
			throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage(COMMON_ERROR_TITLE), MessageBundle.getMessage(STAT_REPORTERROR_MSG),
							OHSeverityLevel.ERROR));
		}
	}

	public JasperReportResultDto getGenericReportAdmissionPdf(int admID, int patID, String jasperFileName) throws OHServiceException {

		try {
			HashMap<String, Object> parameters = getHospitalParameters();
			addBundleParameter(RPT_BASE, jasperFileName, parameters);

			parameters.put("admID", String.valueOf(admID)); // real param
			parameters.put("patientID", String.valueOf(patID)); // real param

			String pdfFilename = compilePDFFilename(RPT_BASE, jasperFileName, Arrays.asList(String.valueOf(patID)), "pdf");

			JasperReportResultDto result = generateJasperReport(compileJasperFilename(RPT_BASE, jasperFileName), pdfFilename.toString(), parameters);
			JasperExportManager.exportReportToPdfFile(result.getJasperPrint(), pdfFilename.toString());
			return result;
		} catch (Exception e) {
			LOGGER.error("", e);
			throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage(COMMON_ERROR_TITLE), MessageBundle.getMessage(STAT_REPORTERROR_MSG),
							OHSeverityLevel.ERROR));
		}
	}

	public JasperReportResultDto getGenericReportBillZPL(Integer billID, String jasperFileName, boolean show, boolean askForPrint) throws OHServiceException {

		try {
			HashMap<String, Object> parameters = getHospitalParameters();

			StringBuilder sbTxtFilename = new StringBuilder(jasperFileName).append("Txt");
			addBundleParameter(RPT_BASE, sbTxtFilename.toString(), parameters);

			parameters.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
			parameters.put("billID", String.valueOf(billID)); // real param

			String filename = compileJasperFilename(RPT_BASE, sbTxtFilename.toString());
			String txtFilename = compilePDFFilename(RPT_BASE, jasperFileName, Arrays.asList(String.valueOf(billID)), "txt");

			return generateJasperReport(filename, txtFilename, parameters);
		} catch (Exception e) {
			LOGGER.error("", e);
			throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage(COMMON_ERROR_TITLE), MessageBundle.getMessage(STAT_REPORTERROR_MSG),
							OHSeverityLevel.ERROR));
		}
	}

	public JasperReportResultDto getGenericReportBillTxt(Integer billID, String jasperFileName, boolean show, boolean askForPrint) throws OHServiceException {

		try {
			HashMap<String, Object> parameters = getHospitalParameters();
			StringBuilder sbTxtFilename = new StringBuilder(jasperFileName).append("Txt");
			addBundleParameter(RPT_BASE, sbTxtFilename.toString(), parameters);

			parameters.put("billID", String.valueOf(billID)); // real param

			String filename = compileJasperFilename(RPT_BASE, sbTxtFilename.toString());
			String txtFilename = compilePDFFilename(RPT_BASE, jasperFileName, Arrays.asList(String.valueOf(billID)), "txt");

			return generateJasperReport(filename, txtFilename, parameters);
		} catch (Exception e) {
			LOGGER.error("", e);
			throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage(COMMON_ERROR_TITLE), MessageBundle.getMessage(STAT_REPORTERROR_MSG),
							OHSeverityLevel.ERROR));
		}
	}

	public JasperReportResultDto getGenericReportBillPdf(Integer billID, String jasperFileName, boolean show, boolean askForPrint) throws OHServiceException {

		try {
			HashMap<String, Object> parameters = getHospitalParameters();
			addBundleParameter(RPT_BASE, jasperFileName, parameters);

			parameters.put("billID", String.valueOf(billID)); // real param

			String pdfFilename = compilePDFFilename(RPT_BASE, jasperFileName, Arrays.asList(String.valueOf(billID)), "pdf");

			JasperReportResultDto result = generateJasperReport(compileJasperFilename(RPT_BASE, jasperFileName), pdfFilename, parameters);
			JasperExportManager.exportReportToPdfFile(result.getJasperPrint(), pdfFilename);
			return result;
		} catch (Exception e) {
			LOGGER.error("", e);
			throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage(COMMON_ERROR_TITLE), MessageBundle.getMessage(STAT_REPORTERROR_MSG),
							OHSeverityLevel.ERROR));
		}
	}

	public JasperReportResultDto getGenericReportBillGroupedPdf(Integer billID, String jasperFileName, Patient patient, List<Integer> billListId,
					String dateFrom, String dateTo, boolean show, boolean askForPrint) throws OHServiceException {

		try {
			HashMap<String, Object> parameters = getHospitalParameters();
			addBundleParameter(RPT_BASE, jasperFileName, parameters);

			parameters.put("billID", String.valueOf(billID)); // real param
			parameters.put("collectionbillsId", billListId); // real param

			String pdfFilename = compilePDFFilename(RPT_BASE, jasperFileName, Arrays.asList(String.valueOf(billID)), "pdf");

			JasperReportResultDto result = generateJasperReport(compileJasperFilename(RPT_BASE, jasperFileName), pdfFilename, parameters);
			JasperExportManager.exportReportToPdfFile(result.getJasperPrint(), pdfFilename);
			return result;
		} catch (Exception e) {
			LOGGER.error("", e);
			throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage(COMMON_ERROR_TITLE), MessageBundle.getMessage(STAT_REPORTERROR_MSG),
							OHSeverityLevel.ERROR));
		}
	}

	public JasperReportResultDto getGenericReportBillGroupedTxt(Integer billID, String jasperFileName, Patient patient, List<Integer> billListId,
					String dateFrom, String dateTo, boolean show, boolean askForPrint) throws OHServiceException {

		try {
			HashMap<String, Object> parameters = getHospitalParameters();

			StringBuilder sbTxtFilename = new StringBuilder(jasperFileName).append("Txt");
			addBundleParameter(RPT_BASE, sbTxtFilename.toString(), parameters);

			parameters.put("billID", String.valueOf(billID)); // real param
			parameters.put("collectionbillsId", billListId); // real param

			String filename = compileJasperFilename(RPT_BASE, sbTxtFilename.toString());
			String txtFilename = compilePDFFilename(RPT_BASE, jasperFileName, Arrays.asList(String.valueOf(billID)), "txt");

			return generateJasperReport(filename, txtFilename, parameters);
		} catch (Exception e) {
			LOGGER.error("", e);
			throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage(COMMON_ERROR_TITLE), MessageBundle.getMessage(STAT_REPORTERROR_MSG),
							OHSeverityLevel.ERROR));
		}
	}

	public JasperReportResultDto getGenericReportOpdPdf(int opdID, int patID, String jasperFileName) throws OHServiceException {

		try {
			HashMap<String, Object> parameters = getHospitalParameters();
			addBundleParameter(RPT_BASE, jasperFileName, parameters);

			parameters.put("opdID", String.valueOf(opdID)); // real param
			parameters.put("patientID", String.valueOf(patID)); // real param

			String pdfFilename = compilePDFFilename(RPT_BASE, jasperFileName, Arrays.asList(String.valueOf(opdID)), "pdf");

			JasperReportResultDto result = generateJasperReport(compileJasperFilename(RPT_BASE, jasperFileName), pdfFilename, parameters);
			JasperExportManager.exportReportToPdfFile(result.getJasperPrint(), pdfFilename);
			return result;
		} catch (Exception e) {
			LOGGER.error("", e);
			throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage(COMMON_ERROR_TITLE), MessageBundle.getMessage(STAT_REPORTERROR_MSG),
							OHSeverityLevel.ERROR));
		}
	}

	public JasperReportResultDto getGenericReportPatientExaminationPdf(Integer patientID, Integer examId, String jasperFileName) throws OHServiceException {

		try {
			HashMap<String, Object> parameters = new HashMap<>();
			addBundleParameter(RPT_BASE, jasperFileName, parameters);

			parameters.put("examId", examId);

			String pdfFilename = compilePDFFilename(RPT_BASE, jasperFileName, Arrays.asList(String.valueOf(patientID)), "pdf");

			JasperReportResultDto result = generateJasperReport(compileJasperFilename(RPT_BASE, jasperFileName), pdfFilename, parameters);
			JasperExportManager.exportReportToPdfFile(result.getJasperPrint(), pdfFilename);
			return result;
		} catch (Exception e) {
			LOGGER.error("", e);
			throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage(COMMON_ERROR_TITLE), MessageBundle.getMessage(STAT_REPORTERROR_MSG),
							OHSeverityLevel.ERROR));
		}
	}

	public JasperReportResultDto getGenericReportPatientPdf(Integer patientID, String jasperFileName) throws OHServiceException {

		try {
			HashMap<String, Object> parameters = getHospitalParameters();
			addBundleParameter(RPT_BASE, jasperFileName, parameters);

			parameters.put("patientID", String.valueOf(patientID)); // real param

			String pdfFilename = compilePDFFilename(RPT_BASE, jasperFileName, Arrays.asList(String.valueOf(patientID)), "pdf");

			JasperReportResultDto result = generateJasperReport(compileJasperFilename(RPT_BASE, jasperFileName), pdfFilename, parameters);
			JasperExportManager.exportReportToPdfFile(result.getJasperPrint(), pdfFilename);
			return result;
		} catch (Exception e) {
			LOGGER.error("", e);
			throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage(COMMON_ERROR_TITLE), MessageBundle.getMessage(STAT_REPORTERROR_MSG),
							OHSeverityLevel.ERROR));
		}
	}

	public JasperReportResultDto getGenericReportWardVisitPdf(String wardID, LocalDateTime date, String jasperFileName) throws OHServiceException {

		try {
			HashMap<String, Object> parameters = getHospitalParameters();
			addBundleParameter(RPT_BASE, jasperFileName, parameters);

			parameters.put("wardID", String.valueOf(wardID)); // real param
			parameters.put("date", toDate(date)); // real param

			String pdfFilename = compilePDFFilename(RPT_BASE, jasperFileName, Arrays.asList(String.valueOf(wardID), TimeTools.formatDateTime(date, YYYY_M_MDD)),
							"pdf");

			JasperReportResultDto result = generateJasperReport(compileJasperFilename(RPT_BASE, jasperFileName), pdfFilename, parameters);
			JasperExportManager.exportReportToPdfFile(result.getJasperPrint(), pdfFilename);
			return result;
		} catch (OHServiceException e) {
			// Already managed, ready to return OHServiceException
			throw e;
		} catch (Exception e) {
			LOGGER.error("", e);
			throw new OHServiceException(e, new OHExceptionMessage(MessageBundle.getMessage(COMMON_ERROR_TITLE), MessageBundle.getMessage(STAT_REPORTERROR_MSG),
							OHSeverityLevel.ERROR));
		}
	}

	public JasperReportResultDto getGenericReportPatientVersion2Pdf(Integer patientID, String parametersString, LocalDateTime dateFrom, LocalDateTime dateTo,
					String jasperFileName) throws OHServiceException {

		try {
			HashMap<String, Object> parameters = getHospitalParameters();
			addBundleParameter(RPT_BASE, jasperFileName, parameters);

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern(YYYY_MM_DD);
			dateFrom = dateFrom.minusDays(1);
			dateTo = dateTo.plusDays(1);
			String dateFromQuery = dateFrom.format(dtf);
			String dateToQuery = dateTo.format(dtf);

			parameters.put("patientID", String.valueOf(patientID));
			parameters.put("All", parametersString.contains("All"));
			parameters.put("Drugs", parametersString.contains("Drugs"));
			parameters.put("Examination", parametersString.contains("Examination"));
			parameters.put("Admission", parametersString.contains("Admission"));
			parameters.put("Opd", parametersString.contains("Opd"));
			parameters.put("Laboratory", parametersString.contains("Laboratory"));
			parameters.put("Operations", parametersString.contains("Operations"));
			parameters.put("Date_from", dateFromQuery);
			parameters.put("Date_to", dateToQuery);

			String pdfFilename = compilePDFFilename(RPT_BASE, jasperFileName, Arrays.asList(String.valueOf(patientID)), "pdf");

			JasperReportResultDto result = generateJasperReport(compileJasperFilename(RPT_BASE, jasperFileName), pdfFilename, parameters);
			JasperExportManager.exportReportToPdfFile(result.getJasperPrint(), pdfFilename);
			return result;
		} catch (OHServiceException e) {
			// Already managed, ready to return OHServiceException
			throw e;
		} catch (Exception e) {
			LOGGER.error("", e);
			throw new OHServiceException(e, new OHExceptionMessage(MessageBundle.getMessage(COMMON_ERROR_TITLE), MessageBundle.getMessage(STAT_REPORTERROR_MSG),
							OHSeverityLevel.ERROR));
		}
	}

	public JasperReportResultDto getGenericReportPharmaceuticalOrderPdf(String jasperFileName) throws OHServiceException {

		try {
			HashMap<String, Object> parameters = getHospitalParameters();
			addBundleParameter(RPT_BASE, jasperFileName, parameters);

			LocalDateTime date = TimeTools.getNow();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(E_D_MMMM_YYYY);
			String todayReport = formatter.format(date);
			formatter = DateTimeFormatter.ofPattern(YYYY_M_MDD);
			String todayFile = formatter.format(date);
			parameters.put("Date", todayReport);

			String pdfFilename = compilePDFFilename(RPT_BASE, jasperFileName, Arrays.asList(todayFile), "pdf");

			JasperReportResultDto result = generateJasperReport(compileJasperFilename(RPT_BASE, jasperFileName), pdfFilename, parameters);
			JasperExportManager.exportReportToPdfFile(result.getJasperPrint(), pdfFilename);
			return result;
		} catch (Exception e) {
			LOGGER.error("", e);
			throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage(COMMON_ERROR_TITLE), MessageBundle.getMessage(STAT_REPORTERROR_MSG),
							OHSeverityLevel.ERROR));
		}
	}

	public JasperReportResultDto getGenericReportPharmaceuticalStockPdf(LocalDateTime date, String jasperFileName, String filter, String groupBy, String sortBy)
					throws OHServiceException {

		try {
			HashMap<String, Object> parameters = getHospitalParameters();
			addBundleParameter(RPT_BASE, jasperFileName, parameters);

			if (date == null) {
				date = TimeTools.getNow();
			}
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(E_D_MMMM_YYYY);
			String dateReport = formatter.format(date);
			formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD);
			String dateQuery = formatter.format(date);
			formatter = DateTimeFormatter.ofPattern(YYYY_M_MDD);
			String dateFile = formatter.format(date);

			parameters.put("Date", dateReport);
			parameters.put("todate", dateQuery);
			if (groupBy != null) {
				parameters.put("groupBy", groupBy);
			}
			if (sortBy != null) {
				parameters.put("sortBy", sortBy);
			}
			if (filter != null) {
				parameters.put("filter", filter);
			}

			String pdfFilename = compilePDFFilename(RPT_BASE, jasperFileName, Arrays.asList(dateFile), "pdf");

			JasperReportResultDto result = generateJasperReport(compileJasperFilename(RPT_BASE, jasperFileName), pdfFilename, parameters);
			JasperExportManager.exportReportToPdfFile(result.getJasperPrint(), pdfFilename);
			return result;
		} catch (Exception e) {
			LOGGER.error("", e);
			throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage(COMMON_ERROR_TITLE), MessageBundle.getMessage(STAT_REPORTERROR_MSG),
							OHSeverityLevel.ERROR));
		}
	}

	public void getGenericReportPharmaceuticalStockExcel(LocalDateTime date, String jasperFileName, String exportFilename, String filter, String groupBy,
					String sortBy) throws OHServiceException {

		try {
			if (date == null) {
				date = TimeTools.getNow();
			}
			String dateQuery = TimeTools.formatDateTime(date, YYYY_MM_DD);
			File jasperFile = new File(compileJasperFilename(RPT_BASE, jasperFileName));

			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperFile);
			JRQuery query = jasperReport.getMainDataset().getQuery();

			String queryString = query.getText();
			queryString = queryString.replace("$P{todate}", "'" + dateQuery + "'");
			if (groupBy != null) {
				queryString = queryString.replace("$P{groupBy}", "'" + groupBy + "'");
			}
			if (sortBy != null) {
				queryString = queryString.replace("$P!{sortBy}", "'" + sortBy + "'");
			}
			if (filter != null) {
				queryString = queryString.replace("$P{filter}", "'" + filter + "'");
			}

			DbQueryLogger dbQuery = new DbQueryLogger();
			ResultSet resultSet = dbQuery.getData(queryString, true);

			File exportFile = new File(exportFilename);
			ExcelExporter xlsExport = new ExcelExporter();
			if (exportFile.getName().endsWith(".xls")) {
				xlsExport.exportResultsetToExcelOLD(resultSet, exportFile);
			} else {
				xlsExport.exportResultsetToExcel(resultSet, exportFile);
			}
		} catch (Exception e) {
			LOGGER.error("", e);
			throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage(COMMON_ERROR_TITLE), MessageBundle.getMessage(STAT_REPORTERROR_MSG),
							OHSeverityLevel.ERROR));
		}
	}

	public JasperReportResultDto getGenericReportPharmaceuticalStockCardPdf(String jasperFileName, String exportFileName, LocalDateTime dateFrom,
					LocalDateTime dateTo, Medical medical, Ward ward) throws OHServiceException {

		try {
			if (dateFrom == null) {
				dateFrom = TimeTools.getNow();
			}
			if (dateTo == null) {
				dateTo = TimeTools.getNow();
			}

			HashMap<String, Object> parameters = getHospitalParameters();
			addBundleParameter(RPT_BASE, jasperFileName, parameters);

			parameters.put("fromdate", toDate(dateFrom));
			parameters.put("todate", toDate(dateTo));
			if (medical != null) {
				parameters.put("productID", String.valueOf(medical.getCode()));
			}
			if (ward != null) {
				parameters.put("WardCode", String.valueOf(ward.getCode()));
				parameters.put("WardName", String.valueOf(ward.getDescription()));
			}

			String pdfFilename = compilePDFFilename(RPT_BASE, jasperFileName, null, "pdf");

			JasperReportResultDto result = generateJasperReport(compileJasperFilename(RPT_BASE, jasperFileName), pdfFilename, parameters);
			JasperExportManager.exportReportToPdfFile(result.getJasperPrint(), pdfFilename);
			return result;
		} catch (Exception e) {
			LOGGER.error("", e);
			throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage(COMMON_ERROR_TITLE), MessageBundle.getMessage(STAT_REPORTERROR_MSG),
							OHSeverityLevel.ERROR));
		}
	}

	public void getGenericReportPharmaceuticalStockCardExcel(String jasperFileName, String exportFileName, LocalDateTime dateFrom, LocalDateTime dateTo,
					Medical medical, Ward ward) throws OHServiceException {

		try {
			if (dateFrom == null) {
				dateFrom = TimeTools.getNow();
			}
			if (dateTo == null) {
				dateTo = TimeTools.getNow();
			}
			String dateFromQuery = TimeTools.formatDateTime(dateFrom, YYYY_MM_DD);
			String dateToQuery = TimeTools.formatDateTime(dateTo, YYYY_MM_DD);

			File jasperFile = new File(compileJasperFilename(RPT_BASE, jasperFileName));

			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperFile);
			JRQuery query = jasperReport.getMainDataset().getQuery();

			String queryString = query.getText();
			queryString = queryString.replace("$P{fromdate}", "'" + dateFromQuery + "'");
			queryString = queryString.replace("$P{todate}", "'" + dateToQuery + "'");
			if (medical != null) {
				queryString = queryString.replace("$P{productID}", "'" + medical.getCode() + "'");
			}
			if (ward != null) {
				queryString = queryString.replace("$P{WardCode}", "'" + ward.getCode() + "'");
			}

			DbQueryLogger dbQuery = new DbQueryLogger();
			ResultSet resultSet = dbQuery.getData(queryString, true);

			File exportFile = new File(exportFileName);
			ExcelExporter xlsExport = new ExcelExporter();
			if (exportFile.getName().endsWith(".xls")) {
				xlsExport.exportResultsetToExcelOLD(resultSet, exportFile);
			} else {
				xlsExport.exportResultsetToExcel(resultSet, exportFile);
			}

		} catch (Exception e) {
			LOGGER.error("", e);
			throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage(COMMON_ERROR_TITLE), MessageBundle.getMessage(STAT_REPORTERROR_MSG),
							OHSeverityLevel.ERROR));
		}
	}

	public JasperReportResultDto getGenericReportPharmaceuticalStockWardPdf(LocalDateTime date, String jasperFileName, Ward ward) throws OHServiceException {

		try {
			HashMap<String, Object> parameters = getHospitalParameters();
			addBundleParameter(RPT_BASE, jasperFileName, parameters);

			if (date == null) {
				date = TimeTools.getNow();
			}
			String dateReport = date.format(DateTimeFormatter.ofPattern(E_D_MMMM_YYYY));
			String dateQuery = date.format(DateTimeFormatter.ofPattern(YYYY_MM_DD));
			String dateFile = date.format(DateTimeFormatter.ofPattern(YYYY_M_MDD));

			parameters.put("Date", dateQuery);
			parameters.put("DateReport", dateReport);
			parameters.put("Ward", ward.getDescription());
			parameters.put("WardCode", ward.getCode());

			String pdfFilename = compilePDFFilename(RPT_BASE, jasperFileName, Arrays.asList(dateFile), "pdf");

			JasperReportResultDto result = generateJasperReport(compileJasperFilename(RPT_BASE, jasperFileName), pdfFilename, parameters);
			JasperExportManager.exportReportToPdfFile(result.getJasperPrint(), pdfFilename);
			return result;
		} catch (Exception e) {
			LOGGER.error("", e);
			throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage(COMMON_ERROR_TITLE), MessageBundle.getMessage(STAT_REPORTERROR_MSG),
							OHSeverityLevel.ERROR));
		}
	}

	public JasperReportResultDto getGenericReportUserInDatePdf(String fromDate, String toDate, String aUser, String jasperFileName) throws OHServiceException {

		try {
			HashMap<String, Object> parameters = compileGenericReportUserInDateParameters(fromDate, toDate, aUser);
			addBundleParameter(RPT_BASE, jasperFileName, parameters);

			String date = new SimpleDateFormat(YYYY_M_MDD).format(new Date());
			String pdfFilename = compilePDFFilename(RPT_BASE, jasperFileName, Arrays.asList(aUser, date), "pdf");

			JasperReportResultDto result = generateJasperReport(compileJasperFilename(RPT_BASE, jasperFileName), pdfFilename, parameters);
			JasperExportManager.exportReportToPdfFile(result.getJasperPrint(), pdfFilename);
			return result;
		} catch (Exception e) {
			LOGGER.error("", e);
			throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage(COMMON_ERROR_TITLE), MessageBundle.getMessage(STAT_REPORTERROR_MSG),
							OHSeverityLevel.ERROR));
		}
	}

	public JasperReportResultDto getGenericReportUserInDateTxt(String fromDate, String toDate, String aUser, String jasperFileName) throws OHServiceException {

		try {
			HashMap<String, Object> parameters = compileGenericReportUserInDateParameters(fromDate, toDate, aUser);

			StringBuilder sbTxtFilename = new StringBuilder(jasperFileName).append("Txt");
			addBundleParameter(RPT_BASE, sbTxtFilename.toString(), parameters);

			String filename = compileJasperFilename(RPT_BASE, jasperFileName);

			String date = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
			String txtFilename = compilePDFFilename(RPT_BASE, jasperFileName, Arrays.asList(aUser, date), "txt");

			return generateJasperReport(filename, txtFilename, parameters);
		} catch (Exception e) {
			LOGGER.error("", e);
			throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage(COMMON_ERROR_TITLE), MessageBundle.getMessage(STAT_REPORTERROR_MSG),
							OHSeverityLevel.ERROR));
		}
	}

	public JasperReportResultDto getGenericReportDischargePdf(int admID, int patID, String jasperFileName) throws OHServiceException {

		try {
			HashMap<String, Object> parameters = getHospitalParameters();
			addBundleParameter(RPT_BASE, jasperFileName, parameters);

			parameters.put("admID", String.valueOf(admID)); // real param
			parameters.put("patientID", String.valueOf(patID)); // real param

			String pdfFilename = compilePDFFilename(RPT_BASE, jasperFileName, Arrays.asList(String.valueOf(admID)), "pdf");

			JasperReportResultDto result = generateJasperReport(compileJasperFilename(RPT_BASE, jasperFileName), pdfFilename, parameters);
			JasperExportManager.exportReportToPdfFile(result.getJasperPrint(), pdfFilename);
			return result;
		} catch (Exception e) {
			LOGGER.error("", e);
			throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage(COMMON_ERROR_TITLE), MessageBundle.getMessage(STAT_REPORTERROR_MSG),
							OHSeverityLevel.ERROR));
		}
	}

	public JasperReportResultDto getGenericReportFromDateToDatePdf(LocalDate fromDate, LocalDate toDate, String jasperFileFolder, String jasperFileName)
					throws OHServiceException {

		try {
			HashMap<String, Object> parameters = compileGenericReportFromDateToDateParameters(fromDate, toDate);
			addBundleParameter(jasperFileFolder, jasperFileName, parameters);

			String pdfFilename = compilePDFFilename(jasperFileFolder, jasperFileName, null, "pdf");
			String filename = compileJasperFilename(jasperFileFolder, jasperFileName);

			JasperReportResultDto result = generateJasperReport(filename, pdfFilename, parameters);
			JasperExportManager.exportReportToPdfFile(result.getJasperPrint(), pdfFilename);
			return result;
		} catch (Exception e) {
			LOGGER.error("", e);
			throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage(COMMON_ERROR_TITLE), MessageBundle.getMessage(STAT_REPORTERROR_MSG),
							OHSeverityLevel.ERROR));
		}
	}

	public JasperReportResultDto getGenericReportFromDateToDatePdf(String fromDate, String toDate, String jasperFileFolder, String jasperFileName)
					throws OHServiceException {

		try {
			HashMap<String, Object> parameters = compileGenericReportFromDateToDateParameters(fromDate, toDate);
			addBundleParameter(jasperFileFolder, jasperFileName, parameters);

			String pdfFilename = compilePDFFilename(jasperFileFolder, jasperFileName, null, "pdf");
			String filename = compileJasperFilename(jasperFileFolder, jasperFileName);

			JasperReportResultDto result = generateJasperReport(filename, pdfFilename, parameters);
			JasperExportManager.exportReportToPdfFile(result.getJasperPrint(), pdfFilename);
			return result;
		} catch (Exception e) {
			LOGGER.error("", e);
			throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage(COMMON_ERROR_TITLE), MessageBundle.getMessage(STAT_REPORTERROR_MSG),
							OHSeverityLevel.ERROR));
		}
	}

	public void getGenericReportFromDateToDateExcel(LocalDate fromDate, LocalDate toDate, String jasperFileFolder, String jasperFileName, String exportFilename)
					throws OHServiceException {

		try {
			String filename = compileJasperFilename(jasperFileFolder, jasperFileName);
			File jasperFile = new File(filename);

			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperFile);
			JRQuery query = jasperReport.getMainDataset().getQuery();
			String queryString = query.getText();

			queryString = queryString.replace("$P{fromdate}", "'" + java.sql.Date.valueOf(fromDate).toString() + "'");
			queryString = queryString.replace("$P{todate}", "'" + java.sql.Date.valueOf(toDate).toString() + "'");

			DbQueryLogger dbQuery = new DbQueryLogger();
			ResultSet resultSet = dbQuery.getData(queryString, true);

			File exportFile = new File(exportFilename);
			ExcelExporter xlsExport = new ExcelExporter();
			if (exportFile.getName().endsWith(".xls")) {
				xlsExport.exportResultsetToExcelOLD(resultSet, exportFile);
			} else {
				xlsExport.exportResultsetToExcel(resultSet, exportFile);
			}
		} catch (Exception exception) {
			throw new OHReportException(exception, new OHExceptionMessage(MessageBundle.getMessage(COMMON_ERROR_TITLE),
							MessageBundle.getMessage(STAT_REPORTERROR_MSG), OHSeverityLevel.ERROR));
		}
	}

	public void getGenericReportFromDateToDateExcel(String fromDate, String toDate, String jasperFileFolder, String jasperFileName, String exportFilename)
					throws OHServiceException {

		try {
			File jasperFile = new File(compileJasperFilename(jasperFileFolder, jasperFileName));
			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperFile);
			JRQuery query = jasperReport.getMainDataset().getQuery();
			String queryString = query.getText();

			String dateFromQuery = TimeTools.formatDateTime(TimeTools.getDate(fromDate, DD_MM_YYYY), YYYY_MM_DD);
			String dateToQuery = TimeTools.formatDateTime(TimeTools.getDate(toDate, DD_MM_YYYY), YYYY_MM_DD);

			queryString = queryString.replace("$P{fromdate}", "'" + dateFromQuery + "'");
			queryString = queryString.replace("$P{todate}", "'" + dateToQuery + "'");

			DbQueryLogger dbQuery = new DbQueryLogger();
			ResultSet resultSet = dbQuery.getData(queryString, true);

			File exportFile = new File(exportFilename);
			ExcelExporter xlsExport = new ExcelExporter();
			if (exportFile.getName().endsWith(".xls")) {
				xlsExport.exportResultsetToExcelOLD(resultSet, exportFile);
			} else {
				xlsExport.exportResultsetToExcel(resultSet, exportFile);
			}
		} catch (Exception exception) {
			throw new OHReportException(exception, new OHExceptionMessage(MessageBundle.getMessage(COMMON_ERROR_TITLE),
							MessageBundle.getMessage(STAT_REPORTERROR_MSG), OHSeverityLevel.ERROR));
		}
	}

	public JasperReportResultDto getGenericReportMYPdf(Integer month, Integer year, String jasperFileFolder, String jasperFileName) throws OHServiceException {

		try {
			Map<String, Object> parameters = compileGenericReportMYParameters(month, year, jasperFileFolder, jasperFileName);
			String pdfFilename = compilePDFFilename(jasperFileFolder, jasperFileName, Arrays.asList(String.valueOf(year), String.valueOf(month)), "pdf");

			JasperReportResultDto result = generateJasperReport(compileJasperFilename(jasperFileFolder, jasperFileName), pdfFilename, parameters);
			JasperExportManager.exportReportToPdfFile(result.getJasperPrint(), pdfFilename);
			return result;
		} catch (Exception e) {
			LOGGER.error("", e);
			throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage(COMMON_ERROR_TITLE), MessageBundle.getMessage(STAT_REPORTERROR_MSG),
							OHSeverityLevel.ERROR));
		}
	}

	public void getGenericReportMYExcel(Integer month, Integer year, String jasperFileFolder, String jasperFileName, String exportFilename)
					throws OHServiceException {

		try {
			File jasperFile = new File(compileJasperFilename(jasperFileFolder, jasperFileName));
			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperFile);
			JRQuery query = jasperReport.getMainDataset().getQuery();
			String queryString = query.getText();
			queryString = queryString.replace("$P{year}", "'" + year + "'");
			queryString = queryString.replace("$P{month}", "'" + month + "'");

			DbQueryLogger dbQuery = new DbQueryLogger();
			ResultSet resultSet = dbQuery.getData(queryString, true);

			File exportFile = new File(exportFilename);
			ExcelExporter xlsExport = new ExcelExporter();
			if (exportFile.getName().endsWith(".xls")) {
				xlsExport.exportResultsetToExcelOLD(resultSet, exportFile);
			} else {
				xlsExport.exportResultsetToExcel(resultSet, exportFile);
			}
		} catch (Exception e) {
			LOGGER.error("", e);
			throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage(COMMON_ERROR_TITLE), MessageBundle.getMessage(STAT_REPORTERROR_MSG),
							OHSeverityLevel.ERROR));
		}
	}

	private HashMap<String, Object> compileGenericReportUserInDateParameters(String fromDate, String toDate, String aUser) throws OHServiceException {
		HashMap<String, Object> parameters = getHospitalParameters();

		LocalDateTime fromDateQuery = TimeTools.parseDate(fromDate, null, false);
		LocalDateTime toDateQuery = TimeTools.parseDate(toDate, null, false);

		parameters.put("fromdate", toDate(fromDateQuery)); // real param
		parameters.put("todate", toDate(toDateQuery)); // real param
		parameters.put("user", aUser + ""); // real param
		return parameters;
	}

	private HashMap<String, Object> compileGenericReportFromDateToDateParameters(String fromDate, String toDate) throws OHServiceException {
		HashMap<String, Object> parameters = getHospitalParameters();

		LocalDateTime fromDateQuery = TimeTools.parseDate(fromDate, DD_MM_YYYY, true);
		LocalDateTime toDateQuery = TimeTools.parseDate(toDate, DD_MM_YYYY, true);

		parameters.put("fromdate", toDate(fromDateQuery)); // real param
		parameters.put("todate", toDate(toDateQuery)); // real param
		return parameters;
	}

	private HashMap<String, Object> compileGenericReportMYParameters(Integer month, Integer year, String jasperFileFolder, String jasperFileName)
					throws OHServiceException {
		HashMap<String, Object> parameters = getHospitalParameters();
		addBundleParameter(jasperFileFolder, jasperFileName, parameters);

		parameters.put("year", String.valueOf(year)); // real param
		parameters.put("month", String.valueOf(month)); // real param
		return parameters;
	}

	private HashMap<String, Object> compileGenericReportFromDateToDateParameters(LocalDate fromDate, LocalDate toDate) throws OHServiceException {
		HashMap<String, Object> parameters = getHospitalParameters();

		parameters.put("fromdate", toDate(fromDate)); // real param
		parameters.put("todate", toDate(toDate)); // real param
		return parameters;
	}

	private HashMap<String, Object> getHospitalParameters() throws OHServiceException {
		HashMap<String, Object> parameters = new HashMap<>();

		Hospital hosp = hospitalManager.getHospital();

		parameters.put("Hospital", hosp.getDescription());
		parameters.put("Address", hosp.getAddress());
		parameters.put("City", hosp.getCity());
		parameters.put("Email", hosp.getEmail());
		parameters.put("Telephone", hosp.getTelephone());
		parameters.put("Currency", hosp.getCurrencyCod());
		return parameters;
	}

	private void addBundleParameter(String jasperFileFolder, String jasperFileName, HashMap<String, Object> parameters) {

		/*
		 * Some reports use pre-formatted dates, that need to be localized as well (days, months, etc...) For this reason we pass the same Locale used in the
		 * application (otherwise it would use the Locale used on the user client machine)
		 */
		parameters.put(JRParameter.REPORT_LOCALE, new Locale(GeneralData.LANGUAGE));

		/*
		 * Jasper Report seems failing to decode resource bundles in UTF-8 encoding. For this reason we pass also the resource for the specific report read with
		 * UTF8Control()
		 */
		addReportBundleParameter(JRParameter.REPORT_RESOURCE_BUNDLE, jasperFileFolder, jasperFileName, parameters);

		/*
		 * Jasper Reports may contain subreports and we should pass also those. The parent report must contain parameters like:
		 * 
		 * SUBREPORT_RESOURCE_BUNDLE_1 SUBREPORT_RESOURCE_BUNDLE_2 SUBREPORT_RESOURCE_BUNDLE_...
		 * 
		 * and pass them as REPORT_RESOURCE_BUNDLE to each related subreport.
		 * 
		 * If nothing is passed, subreports still work, but REPORT_LOCALE will be used (if passed to the subreport) and corresponding bundle (UTF-8 decoding not
		 * available)
		 */
		try {
			LOGGER.debug("Search subreports for {}...", jasperFileName);
			addSubReportsBundleParameters(jasperFileFolder, jasperFileName, parameters);
		} catch (JRException e) {
			LOGGER.error(">> error loading subreport bundle, default will be used");
			LOGGER.error(e.getMessage());
		}
	}

	private void addSubReportsBundleParameters(String jasperFileFolder, String jasperFileName, HashMap<String, Object> parameters) throws JRException {
		File jasperFile = new File(compileJasperFilename(jasperFileFolder, jasperFileName));
		final JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperFile);
		JRBand[] bands = jasperReport.getAllBands(); // Get all bands
		for (JRBand band : bands) {
			List<JRChild> elements = band.getChildren(); // Get all children
			for (JRChild child : elements) {
				int index = 1;
				if (child instanceof JRBaseSubreport) { // This is a subreport
					JRBaseSubreport subreport = (JRBaseSubreport) child;
					String expression = ""; // Lets find out the expression used
					JRExpressionChunk[] chunks = subreport.getExpression().getChunks();
					for (JRExpressionChunk c : chunks) {
						expression += c.getText();
					}

					/*
					 * add indexed subreport bundle
					 */
					Pattern pattern = Pattern.compile("\"(.*)\"");
					Matcher matcher = pattern.matcher(expression);
					if (matcher.find()) {
						String subreportName = matcher.group(1).split("\\.")[0];
						LOGGER.debug("found a subreport: {}", subreportName);
						addReportBundleParameter("SUBREPORT_RESOURCE_BUNDLE_" + index, jasperFileFolder, subreportName, parameters);
					} else {
						LOGGER.error(">> unexpected subreport expression {}", expression);
					}
				}
			}
		}
	}

	private void addReportBundleParameter(String jasperParameter, String jasperFileFolder, String jasperFileName, Map<String, Object> parameters) {
		try {
			ResourceBundle resourceBundle = ResourceBundle.getBundle(jasperFileFolder + File.separator + jasperFileName, new Locale(GeneralData.LANGUAGE),
							new UTF8Control());
			parameters.put(jasperParameter, resourceBundle);

		} catch (MissingResourceException e) {
			LOGGER.error(">> no resource bundle for language '{}' found for report {}", GeneralData.LANGUAGE, jasperFileName);
			LOGGER.info(">> switch to default language '{}'", Locale.getDefault());
			parameters.put(jasperParameter, ResourceBundle.getBundle(jasperFileName, Locale.getDefault()));
			parameters.put(JRParameter.REPORT_LOCALE, Locale.getDefault());
		}
	}

	private JasperReportResultDto generateJasperReport(String jasperFilename, String filename, Map<String, Object> parameters)
					throws JRException, SQLException {
		File jasperFile = new File(jasperFilename);
		final JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperFile);
		Connection connection = dataSource.getConnection();
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);
		connection.close();
		return new JasperReportResultDto(jasperPrint, jasperFilename, filename);
	}

	private String compileJasperFilename(String folderName, String jasperFileName) {
		StringBuilder sbFilename = new StringBuilder();
		sbFilename.append(folderName);
		sbFilename.append(File.separator);
		sbFilename.append(jasperFileName);
		sbFilename.append(".jasper");
		return sbFilename.toString();
	}

	private String compileFilename(String folderName, String jasperFileName, List<String> params, String ext) {
		StringBuilder sbFilename = new StringBuilder();
		sbFilename.append(folderName);
		sbFilename.append(File.separator);
		sbFilename.append(jasperFileName);
		if (params != null) {
			params.forEach(p -> {
				sbFilename.append("_");
				sbFilename.append(p);
			});
		}
		sbFilename.append(".");
		sbFilename.append(ext);
		return sbFilename.toString();
	}

	private String compilePDFFilename(String folderName, String jasperFileName, List<String> params, String ext) {
		StringBuilder sbFilename = new StringBuilder();
		sbFilename.append(folderName);
		sbFilename.append(File.separator);
		sbFilename.append("PDF");
		sbFilename.append(File.separator);
		sbFilename.append(jasperFileName);
		if (params != null) {
			params.forEach(p -> {
				sbFilename.append("_");
				sbFilename.append(p);
			});
		}
		sbFilename.append(".");
		sbFilename.append(ext);
		return sbFilename.toString();
	}

	public String compileDefaultFilename(String defaultFileName) {
		StringBuilder sbFilename = new StringBuilder();
		sbFilename.append("PDF");
		sbFilename.append(File.separator);
		sbFilename.append(defaultFileName);
		return sbFilename.toString();
	}

	/**
	 * Converts a {@link LocalDateTime} to a {@link Date}.
	 * 
	 * @param calendar
	 *            the calendar to convert.
	 * @return the converted value or <code>null</code> if the passed value is <code>null</code>.
	 */
	private static Date toDate(LocalDateTime localDateTime) {
		return Optional.ofNullable(localDateTime).map(ldt -> Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant())).orElse(null);
	}

	private static Date toDate(LocalDate localDate) {
		return Optional.ofNullable(localDate).map(t -> Date.from(t.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())).orElse(null);
	}

}
