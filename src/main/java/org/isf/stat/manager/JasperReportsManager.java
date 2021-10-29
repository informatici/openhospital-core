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
package org.isf.stat.manager;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
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

            StringBuilder pdfFilename = new StringBuilder();
            pdfFilename.append("rpt");
            pdfFilename.append(File.separator);
            pdfFilename.append("PDF");
            pdfFilename.append(File.separator);
            pdfFilename.append(jasperFileName);
            pdfFilename.append(".pdf");

            JasperReportResultDto result = generateJasperReport(compileJasperFilename(jasperFileName), pdfFilename.toString(), parameters);
            JasperExportManager.exportReportToPdfFile(result.getJasperPrint(), pdfFilename.toString());
            return result;
        } catch(Exception e) {
            //Any exception
        	LOGGER.error("", e);
            throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
                    MessageBundle.getMessage("angal.stat.reporterror.msg"), OHSeverityLevel.ERROR));
        }
    }

    public JasperReportResultDto getDiseasesListPdf() throws OHServiceException {

        try {
            Map<String, String> parameters = new HashMap<>();
            Hospital hospital = hospitalManager.getHospital();
            parameters.put("hospital", hospital.getDescription());

            String jasperFileName = "diseaseslist";
            StringBuilder pdfFilename = new StringBuilder();
            pdfFilename.append("rpt");
            pdfFilename.append(File.separator);
            pdfFilename.append("PDF");
            pdfFilename.append(File.separator);
            pdfFilename.append(jasperFileName);
            pdfFilename.append(".pdf");

            JasperReportResultDto result = generateJasperReport(compileJasperFilename(jasperFileName), pdfFilename.toString(), parameters);
            JasperExportManager.exportReportToPdfFile(result.getJasperPrint(), pdfFilename.toString());
            return result;
        } catch(Exception e) {
            //Any exception
        	LOGGER.error("", e);
            throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
                    MessageBundle.getMessage("angal.stat.reporterror.msg"), OHSeverityLevel.ERROR));
        }
    }

    public JasperReportResultDto getGenericReportAdmissionPdf(int admID, int patID, String jasperFileName) throws OHServiceException {

        try {
            HashMap<String, Object> parameters = getHospitalParameters();
            addBundleParameter(jasperFileName, parameters);
            
            parameters.put("admID", String.valueOf(admID)); // real param
            parameters.put("patientID", String.valueOf(patID)); // real param

            String pdfFilename = "rpt/PDF/"+jasperFileName + "_" + admID +".pdf";

            JasperReportResultDto result = generateJasperReport(compileJasperFilename(jasperFileName), pdfFilename, parameters);
            JasperExportManager.exportReportToPdfFile(result.getJasperPrint(), pdfFilename);
            return result;
        } catch(Exception e) {
            //Any exception
        	LOGGER.error("", e);
            throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
                    MessageBundle.getMessage("angal.stat.reporterror.msg"), OHSeverityLevel.ERROR));
        }
    }
    
    public JasperReportResultDto getGenericReportBillZPL(Integer billID, String jasperFileName, boolean show, boolean askForPrint) throws OHServiceException {

        try {
            HashMap<String, Object> parameters = getHospitalParameters();
            
            StringBuilder sbTxtFilename = new StringBuilder(jasperFileName).append("Txt");
            addBundleParameter(sbTxtFilename.toString(), parameters);
            
            parameters.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
            parameters.put("billID", String.valueOf(billID)); // real param

            StringBuilder sbFilename = new StringBuilder();
            sbFilename.append("rpt");
            sbFilename.append(File.separator);
            sbFilename.append(sbTxtFilename);
            sbFilename.append(".jasper");

            String txtFilename = "rpt/PDF/" + jasperFileName + "_" + billID + ".txt";
            JasperReportResultDto result = generateJasperReport(sbFilename.toString(), txtFilename, parameters);
            return result;
        } catch(Exception e) {
            throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
                    MessageBundle.getMessage("angal.stat.reporterror.msg"),
		            OHSeverityLevel.ERROR));
        }
    }

    public JasperReportResultDto getGenericReportBillTxt(Integer billID, String jasperFileName, boolean show, boolean askForPrint) throws OHServiceException {

        try {
            HashMap<String, Object> parameters = getHospitalParameters();
            StringBuilder sbTxtFilename = new StringBuilder(jasperFileName).append("Txt");
            addBundleParameter(sbTxtFilename.toString(), parameters);
            
            parameters.put("billID", String.valueOf(billID)); // real param

            StringBuilder sbFilename = new StringBuilder();
            sbFilename.append("rpt");
            sbFilename.append(File.separator);
            sbFilename.append(sbTxtFilename);
            sbFilename.append(".jasper");

            String txtFilename = "rpt/PDF/" + jasperFileName + "_" + billID + ".txt";
            JasperReportResultDto result = generateJasperReport(sbFilename.toString(), txtFilename, parameters);
            return result;
        } catch(Exception e) {
            throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
                    MessageBundle.getMessage("angal.stat.reporterror.msg"), OHSeverityLevel.ERROR));
        }
    }

    public JasperReportResultDto getGenericReportBillPdf(Integer billID, String jasperFileName, boolean show, boolean askForPrint) throws OHServiceException {

        try {
            HashMap<String, Object> parameters = getHospitalParameters();
            addBundleParameter(jasperFileName, parameters);
            
            parameters.put("billID", String.valueOf(billID)); // real param

            String pdfFilename = "rpt/PDF/" + jasperFileName + "_" + billID + ".pdf";

            JasperReportResultDto result = generateJasperReport(compileJasperFilename(jasperFileName), pdfFilename, parameters);
            JasperExportManager.exportReportToPdfFile(result.getJasperPrint(), pdfFilename);
            return result;
        } catch(Exception e) {

            throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
                    MessageBundle.getMessage("angal.stat.reporterror.msg"), OHSeverityLevel.ERROR));
        }
    }
    
    public JasperReportResultDto getGenericReportBillGroupedPdf(Integer billID, String jasperFileName, Patient patient, List<Integer> billListId, String dateFrom, String dateTo, boolean show, boolean askForPrint) throws OHServiceException {

		try {
			HashMap<String, Object> parameters = getHospitalParameters();
			addBundleParameter(jasperFileName, parameters);

			parameters.put("billID", String.valueOf(billID)); // real param
			parameters.put("collectionbillsId", billListId); // real param

			String pdfFilename = "rpt/PDF/" + jasperFileName + "_" + billID + ".pdf";

			JasperReportResultDto result = generateJasperReport(compileJasperFilename(jasperFileName), pdfFilename, parameters);
			JasperExportManager.exportReportToPdfFile(result.getJasperPrint(), pdfFilename);
			return result;
		} catch(Exception e) {
            //Any exception
			LOGGER.error("", e);
            throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
                    MessageBundle.getMessage("angal.stat.reporterror.msg"), OHSeverityLevel.ERROR));
        }
    }
    
    public JasperReportResultDto getGenericReportBillGroupedTxt(Integer billID, String jasperFileName, Patient patient, List<Integer> billListId, String dateFrom, String dateTo, boolean show, boolean askForPrint) throws OHServiceException {

        try {
            HashMap<String, Object> parameters = getHospitalParameters();
            
            StringBuilder sbTxtFilename = new StringBuilder(jasperFileName).append("Txt");
            addBundleParameter(sbTxtFilename.toString(), parameters);
            
            parameters.put("billID", String.valueOf(billID)); // real param
            parameters.put("collectionbillsId", billListId); // real param

            StringBuilder sbFilename = new StringBuilder();
            sbFilename.append("rpt");
            sbFilename.append(File.separator);
            sbFilename.append(sbTxtFilename);
            sbFilename.append(".jasper");

            String txtFilename = "rpt/PDF/" + jasperFileName + "_" + billID + ".txt";
            JasperReportResultDto result = generateJasperReport(sbFilename.toString(), txtFilename, parameters);
            return result;
        } catch(Exception e) {
            //Any exception
        	LOGGER.error("", e);
            throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
                    MessageBundle.getMessage("angal.stat.reporterror.msg"), OHSeverityLevel.ERROR));
        }
    }
    
    
    

    public JasperReportResultDto getGenericReportOpdPdf(int opdID, int patID, String jasperFileName) throws OHServiceException {

        try {
            HashMap<String, Object> parameters = getHospitalParameters();
            addBundleParameter(jasperFileName, parameters);
            
            parameters.put("opdID", String.valueOf(opdID)); // real param
            parameters.put("patientID", String.valueOf(patID)); // real param

            String pdfFilename = "rpt/PDF/"+jasperFileName + "_" + opdID +".pdf";

            JasperReportResultDto result = generateJasperReport(compileJasperFilename(jasperFileName), pdfFilename, parameters);
            JasperExportManager.exportReportToPdfFile(result.getJasperPrint(), pdfFilename);
            return result;
        } catch(Exception e) {
            //Any exception
        	LOGGER.error("", e);
            throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
                    MessageBundle.getMessage("angal.stat.reporterror.msg"), OHSeverityLevel.ERROR));
        }
    }

    
    public JasperReportResultDto getGenericReportPatientExaminationPdf(Integer patientID, Integer examId, String jasperFileName) throws OHServiceException {

        try {
        	HashMap<String, Object> parameters = new HashMap<>();
        	addBundleParameter(jasperFileName, parameters);
            
            parameters.put("examId", examId); 
            String pdfFilename = "rpt/PDF/"+jasperFileName + "_" + patientID +".pdf";

            JasperReportResultDto result = generateJasperReport(compileJasperFilename(jasperFileName), pdfFilename, parameters);
            JasperExportManager.exportReportToPdfFile(result.getJasperPrint(), pdfFilename);
            return result;
        } catch(Exception e) {
            //Any exception
        	LOGGER.error("", e);
            throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
                    MessageBundle.getMessage("angal.stat.reporterror.msg"), OHSeverityLevel.ERROR));
        }
    }
    public JasperReportResultDto getGenericReportPatientPdf(Integer patientID, String jasperFileName) throws OHServiceException {

        try {
            HashMap<String, Object> parameters = getHospitalParameters();
            addBundleParameter(jasperFileName, parameters);
            
            parameters.put("patientID", String.valueOf(patientID)); // real param

            String pdfFilename = "rpt/PDF/"+jasperFileName + "_" + patientID +".pdf";

            JasperReportResultDto result = generateJasperReport(compileJasperFilename(jasperFileName), pdfFilename, parameters);
            JasperExportManager.exportReportToPdfFile(result.getJasperPrint(), pdfFilename);
            return result;
        } catch(Exception e) {
            //Any exception
        	LOGGER.error("", e);
            throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
                    MessageBundle.getMessage("angal.stat.reporterror.msg"), OHSeverityLevel.ERROR));
        }
    }
    public JasperReportResultDto getGenericReportWardVisitPdf(String wardID, Date date, String jasperFileName) throws OHServiceException {

        try {
            HashMap<String, Object> parameters = getHospitalParameters();
            addBundleParameter(jasperFileName, parameters);
            
            parameters.put("wardID", String.valueOf(wardID)); // real param
            parameters.put("date", date); // real param
            String pdfFilename = "rpt/PDF/"+jasperFileName + "_" + String.valueOf(wardID)+"_"+TimeTools.formatDateTime(date, "yyyyMMdd")+".pdf";

            JasperReportResultDto result = generateJasperReport(compileJasperFilename(jasperFileName), pdfFilename, parameters);
            JasperExportManager.exportReportToPdfFile(result.getJasperPrint(), pdfFilename);
            return result;
        } catch(OHServiceException e) {
            //Already managed, ready to return OHServiceException
            throw e;
        } catch(Exception e) {
            //Any exception
            LOGGER.error("", e);
            throw new OHServiceException(e, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
                    MessageBundle.getMessage("angal.stat.reporterror.msg"), OHSeverityLevel.ERROR));
        }
    }
    
    
    public JasperReportResultDto getGenericReportPatientVersion2Pdf(Integer patientID, String parametersString, Date date_From, Date date_To, String jasperFileName) throws OHServiceException {

        try {
            HashMap<String, Object> parameters = getHospitalParameters();
            addBundleParameter(jasperFileName, parameters);
            
    		Format formatter = new SimpleDateFormat("yyyy-MM-dd");
		    String dateFromQuery = formatter.format(date_From);
		    String dateToQuery = formatter.format(date_To);
	
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
            String pdfFilename = "rpt/PDF/"+jasperFileName + "_" + String.valueOf(patientID)+".pdf";

            JasperReportResultDto result = generateJasperReport(compileJasperFilename(jasperFileName), pdfFilename, parameters);
            JasperExportManager.exportReportToPdfFile(result.getJasperPrint(), pdfFilename);
            return result;
        } catch(OHServiceException e) {
            //Already managed, ready to return OHServiceException
            throw e;
        } catch(Exception e) {
            //Any exception
            LOGGER.error("", e);
            throw new OHServiceException(e, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
                    MessageBundle.getMessage("angal.stat.reporterror.msg"), OHSeverityLevel.ERROR));
        }
    }
    
    public JasperReportResultDto getGenericReportPharmaceuticalOrderPdf(String jasperFileName) throws OHServiceException {

		try {
			HashMap<String, Object> parameters = getHospitalParameters();
			addBundleParameter(jasperFileName, parameters);

			Date date = new Date();
			Format formatter;
			formatter = new SimpleDateFormat("E d, MMMM yyyy");
			String todayReport = formatter.format(date);
			formatter = new SimpleDateFormat("yyyyMMdd");
			String todayFile = formatter.format(date);
			parameters.put("Date", todayReport);

			String pdfFilename = "rpt/PDF/" + jasperFileName + "_" + todayFile + ".pdf";

			JasperReportResultDto result = generateJasperReport(compileJasperFilename(jasperFileName), pdfFilename, parameters);
			JasperExportManager.exportReportToPdfFile(result.getJasperPrint(), pdfFilename);
			return result;
		} catch(Exception e) {
            //Any exception
			LOGGER.error("", e);
            throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
                    MessageBundle.getMessage("angal.stat.reporterror.msg"), OHSeverityLevel.ERROR));
        }
    }
    
    public JasperReportResultDto getGenericReportPharmaceuticalStockPdf(Date date, String jasperFileName, String filter, String groupBy, String sortBy) throws OHServiceException {
    	
    	try {
    		HashMap<String, Object> parameters = getHospitalParameters();
            addBundleParameter(jasperFileName, parameters);
            
    		
    		if (date == null)
				date = new Date();
			Format formatter;
			formatter = new SimpleDateFormat("E d, MMMM yyyy");
		    String dateReport = formatter.format(date);
		    formatter = new SimpleDateFormat("yyyy-MM-dd");
		    String dateQuery = formatter.format(date);
		    formatter = new SimpleDateFormat("yyyyMMdd");
		    String dateFile = formatter.format(date);
            
            parameters.put("Date", dateReport);
			parameters.put("todate", dateQuery);
			if (groupBy != null) parameters.put("groupBy", groupBy);
			if (sortBy != null) parameters.put("sortBy", sortBy);
			if (filter != null) parameters.put("filter", filter);

            String pdfFilename = "rpt/PDF/"+jasperFileName + "_" + dateFile +".pdf";

            JasperReportResultDto result = generateJasperReport(compileJasperFilename(jasperFileName), pdfFilename, parameters);
            JasperExportManager.exportReportToPdfFile(result.getJasperPrint(), pdfFilename);
            return result;
    	} catch(Exception e) {
            //Any exception
    		LOGGER.error("", e);
            throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
                    MessageBundle.getMessage("angal.stat.reporterror.msg"), OHSeverityLevel.ERROR));
        }
    }
    
    public void getGenericReportPharmaceuticalStockExcel(Date date, String jasperFileName, String exportFilename, String filter, String groupBy, String sortBy) throws OHServiceException {

        try {
        	if (date == null)
				date = new Date();
		    String dateQuery = TimeTools.formatDateTime(date, "yyyy-MM-dd");
            File jasperFile = new File(compileJasperFilename(jasperFileName));
            
            JasperReport jasperReport = (JasperReport)JRLoader.loadObject(jasperFile);
            JRQuery query = jasperReport.getMainDataset().getQuery();
            
            String queryString = query.getText();
            queryString = queryString.replace("$P{todate}", "'" + dateQuery + "'");
			if (groupBy != null) queryString = queryString.replace("$P{groupBy}", "'" + groupBy + "'");
			if (sortBy != null) queryString = queryString.replace("$P!{sortBy}", "'" + sortBy + "'");
			if (filter != null) queryString = queryString.replace("$P{filter}", "'" + filter + "'");

            DbQueryLogger dbQuery = new DbQueryLogger();
            ResultSet resultSet = dbQuery.getData(queryString, true);

            File exportFile = new File(exportFilename);
            ExcelExporter xlsExport = new ExcelExporter();
			if (exportFile.getName().endsWith(".xls"))
				xlsExport.exportResultsetToExcelOLD(resultSet, exportFile);
			else
				xlsExport.exportResultsetToExcel(resultSet, exportFile);

        } catch(Exception e) {
            //Any exception
        	LOGGER.error("", e);
            throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
                    MessageBundle.getMessage("angal.stat.reporterror.msg"), OHSeverityLevel.ERROR));
        }
    }
    
    public JasperReportResultDto getGenericReportPharmaceuticalStockCardPdf(String jasperFileName, String exportFileName, Date dateFrom, Date dateTo, Medical medical, Ward ward) throws OHServiceException {
    	
    	try {
    		if (dateFrom == null) {
    			dateFrom = new Date();
    		}
    		if (dateTo == null) {
    			dateTo = new Date();
    		}

			HashMap<String, Object> parameters = getHospitalParameters();
			addBundleParameter(jasperFileName, parameters);
			
			parameters.put("fromdate", dateFrom);
			parameters.put("todate", dateTo);
			if (medical != null) parameters.put("productID", String.valueOf(medical.getCode()));
			if (ward != null) {
				parameters.put("WardCode", String.valueOf(ward.getCode()));
				parameters.put("WardName", String.valueOf(ward.getDescription()));
			}

            String pdfFilename = "rpt/PDF/"+ exportFileName +".pdf";

            JasperReportResultDto result = generateJasperReport(compileJasperFilename(jasperFileName), pdfFilename, parameters);
            JasperExportManager.exportReportToPdfFile(result.getJasperPrint(), pdfFilename);
            return result;
    	} catch(Exception e) {
            //Any exception
    		LOGGER.error("", e);
            throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
                    MessageBundle.getMessage("angal.stat.reporterror.msg"), OHSeverityLevel.ERROR));
        }
    }

    public void getGenericReportPharmaceuticalStockCardExcel(String jasperFileName, String exportFileName, Date dateFrom, Date dateTo, Medical medical, Ward ward) throws OHServiceException {

        try {
        	if (dateFrom == null) {
    			dateFrom = new Date();
    		}
    		if (dateTo == null) {
    			dateTo = new Date();
    		}
		    String dateFromQuery = TimeTools.formatDateTime(dateFrom, "yyyy-MM-dd");
		    String dateToQuery = TimeTools.formatDateTime(dateTo, "yyyy-MM-dd");
		    
            File jasperFile = new File(compileJasperFilename(jasperFileName));
            
            JasperReport jasperReport = (JasperReport)JRLoader.loadObject(jasperFile);
            JRQuery query = jasperReport.getMainDataset().getQuery();
            
            String queryString = query.getText();
            queryString = queryString.replace("$P{fromdate}", "'" + dateFromQuery + "'");
			queryString = queryString.replace("$P{todate}", "'" + dateToQuery + "'");
			if (medical != null) queryString = queryString.replace("$P{productID}", "'" + medical.getCode() + "'");
			if (ward != null) queryString = queryString.replace("$P{WardCode}", "'" + ward.getCode() + "'");

            DbQueryLogger dbQuery = new DbQueryLogger();
            ResultSet resultSet = dbQuery.getData(queryString, true);

            File exportFile = new File(exportFileName);
            ExcelExporter xlsExport = new ExcelExporter();
			if (exportFile.getName().endsWith(".xls"))
				xlsExport.exportResultsetToExcelOLD(resultSet, exportFile);
			else
				xlsExport.exportResultsetToExcel(resultSet, exportFile);

        } catch(Exception e) {
            //Any exception
        	LOGGER.error("", e);
            throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
                    MessageBundle.getMessage("angal.stat.reporterror.msg"), OHSeverityLevel.ERROR));
        }
    }
    
    public JasperReportResultDto getGenericReportPharmaceuticalStockWardPdf(Date date, String jasperFileName, Ward ward) throws OHServiceException {
    	
		try {
			HashMap<String, Object> parameters = getHospitalParameters();
			addBundleParameter(jasperFileName, parameters);

			if (date == null)
				date = new Date();
			Format formatter;
			formatter = new SimpleDateFormat("E d, MMMM yyyy");
			String dateReport = formatter.format(date);
			formatter = new SimpleDateFormat("yyyy-MM-dd");
			String dateQuery = formatter.format(date);
			formatter = new SimpleDateFormat("yyyyMMdd");
			String dateFile = formatter.format(date);

			parameters.put("Date", dateQuery);
			parameters.put("DateReport", dateReport);
			parameters.put("Ward", ward.getDescription());
			parameters.put("WardCode", ward.getCode());

			String pdfFilename = "rpt/PDF/" + jasperFileName + "_" + dateFile + ".pdf";

			JasperReportResultDto result = generateJasperReport(compileJasperFilename(jasperFileName), pdfFilename, parameters);
			JasperExportManager.exportReportToPdfFile(result.getJasperPrint(), pdfFilename);
			return result;
		} catch(Exception e) {
            //Any exception
			
            throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
                    MessageBundle.getMessage("angal.stat.reporterror.msg"), OHSeverityLevel.ERROR));
        }
    }

    public JasperReportResultDto getGenericReportUserInDatePdf(String fromDate, String toDate, String aUser, String jasperFileName) throws OHServiceException {

		try {
			HashMap<String, Object> parameters = compileGenericReportUserInDateParameters(fromDate, toDate, aUser);
			addBundleParameter(jasperFileName, parameters);

			String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
			String pdfFilename = "rpt/PDF/" + jasperFileName + "_" + aUser + "_" + date + ".pdf";

			JasperReportResultDto result = generateJasperReport(compileJasperFilename(jasperFileName), pdfFilename, parameters);
			JasperExportManager.exportReportToPdfFile(result.getJasperPrint(), pdfFilename);
			return result;
		} catch(Exception e) {
            //Any exception
			LOGGER.error("", e);
            throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
                    MessageBundle.getMessage("angal.stat.reporterror.msg"), OHSeverityLevel.ERROR));
        }
    }

    public JasperReportResultDto getGenericReportUserInDateTxt(String fromDate, String toDate, String aUser, String jasperFileName) throws OHServiceException {

        try {
            HashMap<String, Object> parameters = compileGenericReportUserInDateParameters(fromDate, toDate, aUser);
            
            StringBuilder sbTxtFilename = new StringBuilder(jasperFileName).append("Txt");
            addBundleParameter(sbTxtFilename.toString(), parameters);

            StringBuilder sbFilename = new StringBuilder();
            sbFilename.append("rpt");
            sbFilename.append(File.separator);
            sbFilename.append(sbTxtFilename);
            sbFilename.append(".jasper");

            String date = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String txtFilename = "rpt/PDF/" + jasperFileName + "_" + aUser + "_" + date + ".txt";
            JasperReportResultDto result = generateJasperReport(sbFilename.toString(), txtFilename, parameters);
            return result;
        } catch(Exception e) {
            //Any exception
        	LOGGER.error("", e);
            throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
                    MessageBundle.getMessage("angal.stat.reporterror.msg"), OHSeverityLevel.ERROR));
        }
    }

    public JasperReportResultDto getGenericReportDischargePdf(int admID, int patID, String jasperFileName) throws OHServiceException {

        try {
            HashMap<String, Object> parameters = getHospitalParameters();
            addBundleParameter(jasperFileName, parameters);
            
            parameters.put("admID", String.valueOf(admID)); // real param
            parameters.put("patientID", String.valueOf(patID)); // real param
            String pdfFilename = "rpt/PDF/"+jasperFileName + "_" + admID +".pdf";

            JasperReportResultDto result = generateJasperReport(compileJasperFilename(jasperFileName), pdfFilename, parameters);
            JasperExportManager.exportReportToPdfFile(result.getJasperPrint(), pdfFilename);
            return result;
        } catch(Exception e) {
            //Any exception
        	LOGGER.error("", e);
            throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
                    MessageBundle.getMessage("angal.stat.reporterror.msg"), OHSeverityLevel.ERROR));
        }
    }

    public JasperReportResultDto getGenericReportFromDateToDatePdf(String fromDate, String toDate, String jasperFileName) throws OHServiceException {

        try {
            HashMap<String, Object> parameters = compileGenericReportFromDateToDateParameters(fromDate, toDate);
            addBundleParameter(jasperFileName, parameters);
            
            String pdfFilename = "rpt/PDF/"+jasperFileName+".pdf";

            JasperReportResultDto result = generateJasperReport(compileJasperFilename(jasperFileName), pdfFilename, parameters);
            JasperExportManager.exportReportToPdfFile(result.getJasperPrint(), pdfFilename);
            return result;
        } catch(Exception e) {
            //Any exception
        	LOGGER.error("", e);
            throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
                    MessageBundle.getMessage("angal.stat.reporterror.msg"), OHSeverityLevel.ERROR));
        }
    }

    public void getGenericReportFromDateToDateExcel(String fromDate, String toDate, String jasperFileName, String exportFilename) throws OHServiceException {

        try {
            File jasperFile = new File(compileJasperFilename(jasperFileName));
            JasperReport jasperReport = (JasperReport)JRLoader.loadObject(jasperFile);
            JRQuery query = jasperReport.getMainDataset().getQuery();
            String queryString = query.getText();
            
            String dateFromQuery = TimeTools.formatDateTime(TimeTools.getDate(fromDate, "dd/MM/yyyy"), "yyyy-MM-dd");
            String dateToQuery = TimeTools.formatDateTime(TimeTools.getDate(toDate, "dd/MM/yyyy"), "yyyy-MM-dd");
            
            queryString = queryString.replace("$P{fromdate}", "'" + dateFromQuery + "'");
            queryString = queryString.replace("$P{todate}", "'" +  dateToQuery + "'");

            DbQueryLogger dbQuery = new DbQueryLogger();
            ResultSet resultSet = dbQuery.getData(queryString, true);

            File exportFile = new File(exportFilename);
            ExcelExporter xlsExport = new ExcelExporter();
			if (exportFile.getName().endsWith(".xls"))
				xlsExport.exportResultsetToExcelOLD(resultSet, exportFile);
			else
				xlsExport.exportResultsetToExcel(resultSet, exportFile);

        } catch (Exception exception) {
        	throw new OHReportException(exception, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
			        MessageBundle.getMessage("angal.stat.reporterror.msg"), OHSeverityLevel.ERROR));
    	}
    }

    public JasperReportResultDto getGenericReportMYPdf(Integer month, Integer year, String jasperFileName) throws OHServiceException {

        try {
            Map<String, Object> parameters = compileGenericReportMYParameters(month, year, jasperFileName);
            String pdfFilename = "rpt/PDF/"+jasperFileName+"_"+year+"_"+month+".pdf";

            JasperReportResultDto result = generateJasperReport(compileJasperFilename(jasperFileName), pdfFilename, parameters);
            JasperExportManager.exportReportToPdfFile(result.getJasperPrint(), pdfFilename);
            return result;
        } catch(Exception e) {
            //Any exception
        	LOGGER.error("", e);
            throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
                    MessageBundle.getMessage("angal.stat.reporterror.msg"), OHSeverityLevel.ERROR));
        }
    }

    public void getGenericReportMYExcel(Integer month, Integer year, String jasperFileName, String exportFilename) throws OHServiceException {

        try {
            File jasperFile = new File(compileJasperFilename(jasperFileName));
            JasperReport jasperReport = (JasperReport)JRLoader.loadObject(jasperFile);
            JRQuery query = jasperReport.getMainDataset().getQuery();
            String queryString = query.getText();
            queryString = queryString.replace("$P{year}", "'" + year + "'");
            queryString = queryString.replace("$P{month}", "'" + month + "'");

            DbQueryLogger dbQuery = new DbQueryLogger();
            ResultSet resultSet = dbQuery.getData(queryString, true);

            File exportFile = new File(exportFilename);
            ExcelExporter xlsExport = new ExcelExporter();
            if (exportFile.getName().endsWith(".xls"))
				xlsExport.exportResultsetToExcelOLD(resultSet, exportFile);
			else
				xlsExport.exportResultsetToExcel(resultSet, exportFile);

        } catch(Exception e) {
            //Any exception
        	LOGGER.error("", e);
            throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
                    MessageBundle.getMessage("angal.stat.reporterror.msg"), OHSeverityLevel.ERROR));
        }
    }

    private HashMap<String,Object> compileGenericReportMYParameters(Integer month, Integer year, String jasperFileName) throws OHServiceException {
        HashMap<String, Object> parameters = getHospitalParameters();
        addBundleParameter(jasperFileName, parameters);
		
        parameters.put("year", String.valueOf(year)); // real param
        parameters.put("month", String.valueOf(month)); // real param
        return  parameters;
    }

    private HashMap<String,Object> compileGenericReportUserInDateParameters(String fromDate, String toDate, String aUser) throws OHServiceException {
        HashMap<String, Object> parameters = getHospitalParameters();
		
        Date fromDateQuery;
		Date toDateQuery;
        try {
			fromDateQuery = TimeTools.parseDate(fromDate, null, false).getTime();
		} catch (ParseException e) {
	        LOGGER.error("Error parsing '{}' to a Date using pattern: 'yyyy-MM-dd HH:mm:ss'", fromDate);
			throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.stat.reporterror.msg"), OHSeverityLevel.ERROR));
		}

        try {
        	toDateQuery = TimeTools.parseDate(toDate, null, false).getTime();
		} catch (ParseException e) {
	        LOGGER.error("Error parsing '{}' to a Date using pattern: 'yyyy-MM-dd HH:mm:ss'", toDate);
			throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.stat.reporterror.msg"), OHSeverityLevel.ERROR));
		}

		parameters.put("fromdate", fromDateQuery); // real param
		parameters.put("todate", toDateQuery); // real param
		parameters.put("user", aUser + ""); // real param
		return parameters;

    }

    private HashMap<String,Object> compileGenericReportFromDateToDateParameters(String fromDate, String toDate) throws OHServiceException {
        HashMap<String, Object> parameters = getHospitalParameters();
        
		Date fromDateQuery;
		Date toDateQuery;
		try {
			fromDateQuery = TimeTools.parseDate(fromDate, "dd/MM/yyyy", false).getTime();
		} catch (ParseException e) {
			LOGGER.error("Error parsing '{}' to a Date using pattern: 'dd/MM/yyyy'", fromDate);
			throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.stat.reporterror.msg"), OHSeverityLevel.ERROR));
		}

		try {
			toDateQuery = TimeTools.parseDate(toDate, "dd/MM/yyyy", false).getTime();
		} catch (ParseException e) {
			LOGGER.error("Error parsing '{}' to a Date using pattern: 'dd/MM/yyyy'", toDate);
			throw new OHReportException(e, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.stat.reporterror.msg"), OHSeverityLevel.ERROR));
		}

        parameters.put("fromdate", fromDateQuery); // real param
        parameters.put("todate", toDateQuery); // real param
        return parameters;
    }

    private HashMap<String,Object> getHospitalParameters() throws OHServiceException {
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
    
	private void addBundleParameter(String jasperFileName, HashMap<String, Object> parameters) {
		
		/*
		 * Some reports use pre-formatted dates, that need to be localized as
		 * well (days, months, etc...) For this reason we pass the same Locale
		 * used in the application (otherwise it would use the Locale used on
		 * the user client machine)
		 */
		parameters.put(JRParameter.REPORT_LOCALE, new Locale(GeneralData.LANGUAGE));
		
		/*
		 * Jasper Report seems failing to decode resource bundles in UTF-8
		 * encoding. For this reason we pass also the resource for the specific
		 * report read with UTF8Control()
		 */
		addReportBundleParameter(JRParameter.REPORT_RESOURCE_BUNDLE, jasperFileName, parameters);

		/*
		 * Jasper Reports may contain subreports and we should pass also those.
		 * The parent report must contain parameters like:
		 * 
		 * SUBREPORT_RESOURCE_BUNDLE_1 
		 * SUBREPORT_RESOURCE_BUNDLE_2
		 * SUBREPORT_RESOURCE_BUNDLE_...
		 * 
		 * and pass them as REPORT_RESOURCE_BUNDLE to each related subreport.
		 * 
		 * If nothing is passed, subreports still work, but REPORT_LOCALE will be used 
		 * (if passed to the subreport) and corresponding bundle (UTF-8 decoding not available) 
		 */
		try {
			LOGGER.debug("Search subreports for {}...", jasperFileName);
			addSubReportsBundleParameters(jasperFileName, parameters);
		} catch (JRException e) {
			LOGGER.error(">> error loading subreport bundle, default will be used");
			LOGGER.error(e.getMessage());
		}
	}

	private void addSubReportsBundleParameters(String jasperFileName, HashMap<String, Object> parameters) throws JRException {
		File jasperFile = new File(compileJasperFilename(jasperFileName));
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
						addReportBundleParameter("SUBREPORT_RESOURCE_BUNDLE_" + index, subreportName, parameters);
					} else {
						LOGGER.error(">> unexpected subreport expression {}", expression);
					}
				}
			}
		}
	}

	private void addReportBundleParameter(String jasperParameter, String jasperFileName, Map<String, Object> parameters) {
		try {
			ResourceBundle resourceBundle = ResourceBundle.getBundle(
						jasperFileName, 
						new Locale(GeneralData.LANGUAGE), 
						new UTF8Control());
			parameters.put(jasperParameter, resourceBundle);
			
		} catch (MissingResourceException e) {
			LOGGER.error(">> no resource bundle for language '{}' found for report {}", GeneralData.LANGUAGE, jasperFileName);
			LOGGER.info(">> switch to default language '{}'", Locale.getDefault());
			parameters.put(jasperParameter, ResourceBundle.getBundle(jasperFileName, Locale.getDefault()));
			parameters.put(JRParameter.REPORT_LOCALE, Locale.getDefault());
		}
	}

    private JasperReportResultDto generateJasperReport(String jasperFilename, String filename, Map parameters) throws JRException, SQLException {
        File jasperFile = new File(jasperFilename);
        final JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperFile);
        final Map localParameters = parameters;
        Connection connection = dataSource.getConnection();
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, localParameters, connection);
        connection.close();
        return new JasperReportResultDto(jasperPrint, jasperFilename, filename);
    }

    private String compileJasperFilename(String jasperFileName) {
        StringBuilder sbFilename = new StringBuilder();
        sbFilename.append("rpt");
        sbFilename.append(File.separator);
        sbFilename.append(jasperFileName);
        sbFilename.append(".jasper");
        return  sbFilename.toString();
    }
    
    public String compileDefaultFilename(String defaultFileName) {
    	StringBuilder sbFilename = new StringBuilder();
		sbFilename.append("PDF");
		sbFilename.append(File.separator);
		sbFilename.append(defaultFileName);
        return  sbFilename.toString();
    }
}
