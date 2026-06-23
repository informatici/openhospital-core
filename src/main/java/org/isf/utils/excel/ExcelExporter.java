/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.utils.excel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHException;

public class ExcelExporter {

	private CharsetEncoder encoder;
	private Locale currentLocale;
	private Workbook workbook;
	private CellStyle doubleStyle;
	private CellStyle dateStyle;
	private CellStyle dateTimeStyle;
	private CellStyle bigDecimalStyle;
	private CellStyle headerStyle;
	private CreationHelper createHelper;

	private static final String CSV_SEPARATOR = ";";
	private static final String CSV_LINE_TERMINATOR = "\r\n";

	public ExcelExporter() {
		encoder = StandardCharsets.UTF_8.newEncoder();
		encoder.onMalformedInput(CodingErrorAction.REPORT);
		encoder.onUnmappableCharacter(CodingErrorAction.REPORT);
		currentLocale = Locale.getDefault();
	}

	private void initStyles() {

		headerStyle = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setFontHeightInPoints((short) 10);
		font.setBold(true);
		headerStyle.setFont(font);

		short doubleFormat = workbook.createDataFormat().getFormat("#,##0.00");
		doubleStyle = workbook.createCellStyle();
		doubleStyle.setDataFormat(doubleFormat);

		short dateFormat = workbook.createDataFormat().getFormat("yyyy-mm-dd");
		dateStyle = workbook.createCellStyle();
		dateStyle.setDataFormat(dateFormat);

		short dateTimeFormat = workbook.createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss");
		dateTimeStyle = workbook.createCellStyle();
		dateTimeStyle.setDataFormat(dateTimeFormat);

		short bigDecimalFormat = workbook.createDataFormat().getFormat("#,##0");
		bigDecimalStyle = workbook.createCellStyle();
		bigDecimalStyle.setDataFormat(bigDecimalFormat);

	}

	/**
	 * Writes BOM for Excel UTF-8 automatic handling
	 *
	 * @param fileStream - the filestream to use
	 * @throws IOException
	 */
	private void writeBOM(FileOutputStream fileStream) throws IOException {
		byte[] bom = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
		fileStream.write(bom);
	}

	/**
	 * Export a {@link JTable} to a CSV (RFC 4180) file, UTF-8 encoded, using a semicolon ({@code ;}) as list separator.
	 *
	 * @param jtable
	 * @param file
	 * @throws IOException
	 */
	public void exportTableToCSV(JTable jtable, File file) throws IOException {
		exportTableToCSV(jtable, file, -1);
	}

	/**
	 * Export a {@link JTable} to a CSV (RFC 4180) file, UTF-8 encoded, using a semicolon ({@code ;}) as list separator.
	 *
	 * @param jtable
	 * @param file
	 * @param columnCount if -1 use the table model column count; otherwise export only the first {@code columnCount} columns
	 * @throws IOException
	 */
	public void exportTableToCSV(JTable jtable, File file, int columnCount) throws IOException {
		TableModel model = jtable.getModel();
		int colCount = columnCount == -1 ? model.getColumnCount() : columnCount;

		FileOutputStream fileStream = new FileOutputStream(file);
		writeBOM(fileStream);

		try (BufferedWriter outFile = new BufferedWriter(new OutputStreamWriter(fileStream, encoder))) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

			List<String> header = new ArrayList<>(colCount);
			for (int i = 0; i < colCount; i++) {
				header.add(model.getColumnName(i));
			}
			writeCSVRow(outFile, header);

			int rowCount = model.getRowCount();
			for (int i = 0; i < rowCount; i++) {
				List<String> row = new ArrayList<>(colCount);
				for (int j = 0; j < colCount; j++) {
					row.add(formatValue(model.getValueAt(i, j), sdf));
				}
				writeCSVRow(outFile, row);
			}
		}
	}

	/**
	 * Export a {@link ResultSet} to a CSV (RFC 4180) file, UTF-8 encoded, using a semicolon ({@code ;}) as list separator.
	 *
	 * @param resultSet
	 * @param exportFile
	 * @throws IOException
	 * @throws OHException
	 */
	public void exportResultsetToCSV(ResultSet resultSet, File exportFile) throws IOException, OHException {

		FileOutputStream fileStream = new FileOutputStream(exportFile);
		writeBOM(fileStream);

		try (BufferedWriter output = new BufferedWriter(new OutputStreamWriter(fileStream, encoder))) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

			try {
				ResultSetMetaData rsmd = resultSet.getMetaData();
				int colCount = rsmd.getColumnCount();

				List<String> header = new ArrayList<>(colCount);
				for (int i = 1; i <= colCount; i++) {
					header.add(rsmd.getColumnName(i));
				}
				writeCSVRow(output, header);

				while (resultSet.next()) {
					List<String> row = new ArrayList<>(colCount);
					for (int i = 1; i <= colCount; i++) {
						row.add(formatValue(resultSet.getObject(i), sdf));
					}
					writeCSVRow(output, row);
				}
			} catch (SQLException e) {
				throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), e);
			}
		}
	}

	/**
	 * Export a {@link Collection} of {@link Map} rows to a CSV (RFC 4180) file, UTF-8 encoded, using a semicolon ({@code ;}) as list separator.
	 *
	 * @param data
	 * @param exportFile
	 * @throws IOException
	 * @throws OHException
	 */
	public void exportDataToCSV(Collection data, File exportFile) throws IOException, OHException {

		FileOutputStream fileStream = new FileOutputStream(exportFile);
		writeBOM(fileStream);

		try (BufferedWriter outFile = new BufferedWriter(new OutputStreamWriter(fileStream, encoder))) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			boolean header = false;
			for (Object map : data) {
				Map thisMap = ((Map) map);
				if (!header) {
					List<String> columns = new ArrayList<>();
					for (Object column : thisMap.keySet()) {
						columns.add(column == null ? "" : column.toString());
					}
					writeCSVRow(outFile, columns);
					header = true;
				}

				List<String> values = new ArrayList<>();
				for (Object value : thisMap.values()) {
					values.add(formatValue(value, sdf));
				}
				writeCSVRow(outFile, values);
			}
		}
	}

	/**
	 * Writes a single CSV row, escaping each field per RFC 4180 and terminating the line with CRLF.
	 */
	private void writeCSVRow(BufferedWriter out, List<String> fields) throws IOException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < fields.size(); i++) {
			if (i > 0) {
				sb.append(CSV_SEPARATOR);
			}
			sb.append(escapeCSV(fields.get(i)));
		}
		sb.append(CSV_LINE_TERMINATOR);
		out.write(sb.toString());
	}

	/**
	 * Escapes a field per RFC 4180: a field that contains the separator, a double quote, CR or LF is wrapped in double
	 * quotes and any embedded double quote is doubled.
	 */
	private static String escapeCSV(String value) {
		if (value == null) {
			return "";
		}
		boolean mustQuote = value.contains(CSV_SEPARATOR) || value.indexOf('"') >= 0
						|| value.indexOf('\n') >= 0 || value.indexOf('\r') >= 0;
		if (mustQuote) {
			return '"' + value.replace("\"", "\"\"") + '"';
		}
		return value;
	}

	/**
	 * Renders a cell value as text for CSV output: numbers use the current locale, timestamps use {@code dd/MM/yyyy HH:mm:ss},
	 * {@code null} becomes an empty field.
	 */
	private String formatValue(Object value, SimpleDateFormat sdf) {
		if (value == null) {
			return "";
		}
		if (value instanceof Integer || value instanceof Long) {
			// integral identifiers/counts: keep them verbatim, without locale grouping separators
			return value.toString();
		} else if (value instanceof Double || value instanceof BigDecimal) {
			NumberFormat numberFormat = NumberFormat.getInstance(currentLocale);
			numberFormat.setGroupingUsed(false);
			return numberFormat.format(value);
		} else if (value instanceof Timestamp val) {
			return sdf.format(val);
		}
		return value.toString();
	}

	/**
	 * Export a {@link JTable} to Excel using Apache POI library
	 *
	 * @param jtable
	 * @param file
	 * @throws IOException
	 */
	public void exportTableToExcel(JTable jtable, File file) throws IOException {
		exportTableToExcel(jtable, file, -1);
	}

	/**
	 * Export a {@link JTable} to Excel using Apache POI library
	 *
	 * @param jtable
	 * @param file
	 * @param columnCount if -1 then get the column count from the table model; if specified use that number for the column count
	 * @throws IOException
	 */
	public void exportTableToExcel(JTable jtable, File file, int columnCount) throws IOException {
		TableModel model = jtable.getModel();
		FileOutputStream fileStream = new FileOutputStream(file);

		workbook = new XSSFWorkbook();
		createHelper = workbook.getCreationHelper();

		Sheet worksheet = workbook.createSheet();
		initStyles();

		Row headers = worksheet.createRow((short) 0);
		int colCount;
		if (columnCount == -1) {
			colCount = model.getColumnCount();
		} else {
			colCount = columnCount;
		}
		for (int i = 0; i < colCount; i++) {
			Cell cell = headers.createCell((short) i);
			RichTextString value = createHelper.createRichTextString(model.getColumnName(i));
			cell.setCellStyle(headerStyle);
			cell.setCellValue(value);
		}

		int rowCount = model.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			int index = i + 1;
			Row row = worksheet.createRow(index);

			for (int j = 0; j < colCount; j++) {
				Cell cell = row.createCell((short) j);
				Object value = model.getValueAt(i, j);
				setValueForExcel(cell, value);
			}
		}
		workbook.write(fileStream);
		fileStream.flush();
		fileStream.close();
	}

	/**
	 * Export a {@link ResultSet} to Excel using Apache POI library
	 *
	 * @param resultSet
	 * @param exportFile
	 * @throws IOException
	 * @throws OHException
	 */
	public void exportResultsetToExcel(ResultSet resultSet, File exportFile) throws IOException, OHException {
		try (FileOutputStream fileStream = new FileOutputStream(exportFile)) {

			workbook = new XSSFWorkbook();
			createHelper = workbook.getCreationHelper();

			Sheet worksheet = workbook.createSheet();
			initStyles();

			Row headers = worksheet.createRow((short) 0);
			try {
				ResultSetMetaData rsmd = resultSet.getMetaData();

				int colCount = rsmd.getColumnCount();
				for (int i = 0; i < colCount; i++) {
					Cell cell = headers.createCell((short) i);
					RichTextString value = createHelper.createRichTextString(rsmd.getColumnName(i + 1));
					cell.setCellStyle(headerStyle);
					cell.setCellValue(value);
				}

				int index = 1;
				while (resultSet.next()) {
					Row row = worksheet.createRow(index);

					for (int j = 0; j < colCount; j++) {
						Object value = resultSet.getObject(j + 1);
						Cell cell = row.createCell((short) j);
						setValueForExcel(cell, value);
					}
					index++;
				}
				workbook.write(fileStream);
				fileStream.flush();
			} catch (FileNotFoundException e) {
				throw new OHException(e.getLocalizedMessage());
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), e);
		}
	}

	/**
	 * Export a {@link ResultSet} to Excel using Apache POI library
	 *
	 * @param data
	 * @param exportFile
	 * @throws IOException
	 * @throws OHException
	 */
	public void exportDataToExcel(Collection data, File exportFile) throws IOException, OHException {
		FileOutputStream fileStream = new FileOutputStream(exportFile);

		workbook = new XSSFWorkbook();
		createHelper = workbook.getCreationHelper();
		Sheet worksheet = workbook.createSheet();
		initStyles();

		Row headers = worksheet.createRow((short) 0);
		boolean header = false;
		int index = 1;
		for (Object map : data) {
			Map thisMap = ((Map) map);
			if (!header) {
				Set columns = thisMap.keySet();
				int h = 0;
				for (Object column : columns) {
					Cell cell = headers.createCell((short) h);
					RichTextString value = createHelper.createRichTextString(column.toString());
					cell.setCellStyle(headerStyle);
					cell.setCellValue(value);
					h++;
				}
				header = true;
				continue;
			}

			Row row = worksheet.createRow(index);
			Collection values = thisMap.values();
			int j = 0;
			for (Object value : values) {
				Cell cell = row.createCell((short) j);
				setValueForExcel(cell, value);
				j++;
			}
			index++;
		}
		workbook.write(fileStream);
		fileStream.flush();
		fileStream.close();
	}

	private void setValueForExcel(Cell cell, Object value) {

		if (value != null) {
			if (value instanceof Integer val) {
				cell.setCellValue(val);
			} else if (value instanceof Double val) {
				cell.setCellType(CellType.NUMERIC);
				cell.setCellStyle(doubleStyle);
				cell.setCellValue(val);
			} else if (value instanceof Timestamp val) {
				cell.setCellStyle(dateTimeStyle);
				cell.setCellValue(val);
			} else if (value instanceof Date) {
				Timestamp val = new Timestamp(((Date) value).getTime());
				cell.setCellStyle(dateStyle);
				cell.setCellValue(val);
			} else if (value instanceof BigDecimal val) {
				cell.setCellType(CellType.NUMERIC);
				cell.setCellStyle(bigDecimalStyle);
				cell.setCellValue(val.doubleValue());
			} else if (value instanceof Long val) {
				cell.setCellValue(val);
			} else {
				RichTextString val = createHelper.createRichTextString(value.toString());
				cell.setCellValue(val);
			}
		}
	}

	/**
	 * Export a {@link JTable} to Excel 97-2003 using Apache POI library
	 *
	 * @param jtable
	 * @param file
	 * @throws IOException
	 */
	public void exportTableToExcelOLD(JTable jtable, File file) throws IOException {
		exportTableToExcelOLD(jtable, file, -1);
	}

	/**
	 * Export a {@link JTable} to Excel 97-2003 using Apache POI library
	 *
	 * @param jtable
	 * @param file
	 * @param columnCount if -1 then get the column count from the table model; othereise use the specfied number for the column count
	 * @throws IOException
	 */
	public void exportTableToExcelOLD(JTable jtable, File file, int columnCount) throws IOException {
		TableModel model = jtable.getModel();
		FileOutputStream fileStream = new FileOutputStream(file);

		workbook = new HSSFWorkbook();
		HSSFSheet worksheet = (HSSFSheet) workbook.createSheet();
		initStyles();

		HSSFRow headers = worksheet.createRow((short) 0);
		int colCount;
		if (columnCount == -1) {
			colCount = model.getColumnCount();
		} else {
			colCount = columnCount;
		}
		for (int i = 0; i < colCount; i++) {
			HSSFCell cell = headers.createCell((short) i);
			HSSFRichTextString value = new HSSFRichTextString(model.getColumnName(i));
			cell.setCellStyle(headerStyle);
			cell.setCellValue(value);
		}

		int rowCount = model.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			int index = i + 1;
			HSSFRow row = worksheet.createRow((short) index);

			for (int j = 0; j < colCount; j++) {
				HSSFCell cell = row.createCell((short) j);
				Object value = model.getValueAt(i, j);
				setValueForExcelOLD(cell, value);
			}
		}
		workbook.write(fileStream);
		fileStream.flush();
		fileStream.close();
	}

	/**
	 * Export a {@link ResultSet} to Excel 97-2003 using Apache POI library
	 *
	 * @param resultSet
	 * @param exportFile
	 * @throws IOException
	 * @throws OHException
	 */
	public void exportResultsetToExcelOLD(ResultSet resultSet, File exportFile) throws IOException, OHException {
		try (FileOutputStream fileStream = new FileOutputStream(exportFile)) {

			workbook = new HSSFWorkbook();
			HSSFSheet worksheet = (HSSFSheet) workbook.createSheet();
			initStyles();

			HSSFRow headers = worksheet.createRow((short) 0);
			try {
				ResultSetMetaData rsmd = resultSet.getMetaData();

				int colCount = rsmd.getColumnCount();
				for (int i = 0; i < colCount; i++) {
					HSSFCell cell = headers.createCell((short) i);
					HSSFRichTextString value = new HSSFRichTextString(rsmd.getColumnName(i + 1));
					cell.setCellStyle(headerStyle);
					cell.setCellValue(value);
				}

				int index = 1;
				while (resultSet.next()) {
					HSSFRow row = worksheet.createRow((short) index);

					for (int j = 0; j < colCount; j++) {
						Object value = resultSet.getObject(j + 1);
						HSSFCell cell = row.createCell((short) j);
						setValueForExcelOLD(cell, value);
					}
					index++;
				}
				workbook.write(fileStream);
				fileStream.flush();
			} catch (SQLException e) {
				throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), e);
			}
		}
	}

	/**
	 * Export a {@link ResultSet} to Excel 97-2003 using Apache POI library
	 *
	 * @param data
	 * @param exportFile
	 * @throws IOException
	 * @throws OHException
	 */
	public void exportDataToExcelOLD(Collection data, File exportFile) throws IOException, OHException {
		FileOutputStream fileStream = new FileOutputStream(exportFile);

		workbook = new HSSFWorkbook();
		HSSFSheet worksheet = (HSSFSheet) workbook.createSheet();
		initStyles();

		HSSFRow headers = worksheet.createRow((short) 0);
		boolean header = false;
		int index = 1;
		for (Object map : data) {
			Map thisMap = ((Map) map);
			if (!header) {
				Set columns = thisMap.keySet();
				int h = 0;
				for (Object column : columns) {
					HSSFCell cell = headers.createCell((short) h);
					HSSFRichTextString value = new HSSFRichTextString(column.toString());
					cell.setCellStyle(headerStyle);
					cell.setCellValue(value);
					h++;
				}
				header = true;
				continue;
			}

			HSSFRow row = worksheet.createRow((short) index);
			Collection values = thisMap.values();
			int j = 0;
			for (Object value : values) {
				HSSFCell cell = row.createCell((short) j);
				setValueForExcelOLD(cell, value);
				j++;
			}
			index++;
		}
		workbook.write(fileStream);
		fileStream.flush();
		fileStream.close();
	}

	private void setValueForExcelOLD(HSSFCell cell, Object value) {

		if (value != null) {
			if (value instanceof Integer val) {
				cell.setCellValue(val);
			} else if (value instanceof Double val) {
				cell.setCellType(CellType.NUMERIC);
				cell.setCellStyle(doubleStyle);
				cell.setCellValue(val);
			} else if (value instanceof Timestamp val) {
				cell.setCellStyle(dateTimeStyle);
				cell.setCellValue(val);
			} else if (value instanceof Date) {
				Timestamp val = new Timestamp(((Date) value).getTime());
				cell.setCellStyle(dateStyle);
				cell.setCellValue(val);
			} else if (value instanceof BigDecimal val) {
				cell.setCellType(CellType.NUMERIC);
				cell.setCellStyle(bigDecimalStyle);
				cell.setCellValue(val.doubleValue());
			} else if (value instanceof Long val) {
				cell.setCellValue(val);
			} else {
				HSSFRichTextString val = new HSSFRichTextString(value.toString());
				cell.setCellValue(val);
			}
		}
	}

	/**
	 * Export a {@link JTable} to the file format implied by the file extension: {@code .csv} (open format), {@code .xls}
	 * (Excel 97-2003) or, by default, {@code .xlsx}.
	 *
	 * @param jtable
	 * @param file
	 * @param columnCount if -1 use the table model column count; otherwise export only the first {@code columnCount} columns
	 * @throws IOException
	 */
	public void exportTable(JTable jtable, File file, int columnCount) throws IOException {
		String name = file.getName().toLowerCase(Locale.ROOT);
		if (name.endsWith(".csv")) {
			exportTableToCSV(jtable, file, columnCount);
		} else if (name.endsWith(".xls")) {
			exportTableToExcelOLD(jtable, file, columnCount);
		} else {
			exportTableToExcel(jtable, file, columnCount);
		}
	}

	/**
	 * Export a {@link JTable} to the file format implied by the file extension: {@code .csv} (open format), {@code .xls}
	 * (Excel 97-2003) or, by default, {@code .xlsx}.
	 *
	 * @param jtable
	 * @param file
	 * @throws IOException
	 */
	public void exportTable(JTable jtable, File file) throws IOException {
		exportTable(jtable, file, -1);
	}

	/**
	 * Export a {@link ResultSet} to the file format implied by the file extension: {@code .csv} (open format), {@code .xls}
	 * (Excel 97-2003) or, by default, {@code .xlsx}.
	 *
	 * @param resultSet
	 * @param exportFile
	 * @throws IOException
	 * @throws OHException
	 */
	public void exportResultset(ResultSet resultSet, File exportFile) throws IOException, OHException {
		String name = exportFile.getName().toLowerCase(Locale.ROOT);
		if (name.endsWith(".csv")) {
			exportResultsetToCSV(resultSet, exportFile);
		} else if (name.endsWith(".xls")) {
			exportResultsetToExcelOLD(resultSet, exportFile);
		} else {
			exportResultsetToExcel(resultSet, exportFile);
		}
	}

	public static JFileChooser getJFileChooserExcel(File defaultFilename) {
		JFileChooser fcExcel = new JFileChooser();
		FileNameExtensionFilter csvFilter = new FileNameExtensionFilter("CSV - open format (*.csv)", "csv");
		FileNameExtensionFilter excelFilter = new FileNameExtensionFilter("Excel (*.xlsx)", "xlsx");
		FileNameExtensionFilter excelFilter2003 = new FileNameExtensionFilter("Excel 97-2003 (*.xls)", "xls");
		fcExcel.setAcceptAllFileFilterUsed(false);
		fcExcel.addChoosableFileFilter(csvFilter);
		fcExcel.addChoosableFileFilter(excelFilter);
		fcExcel.addChoosableFileFilter(excelFilter2003);
		fcExcel.setFileFilter(csvFilter);
		fcExcel.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fcExcel.setSelectedFile(defaultFilename);
		return fcExcel;
	}
}
