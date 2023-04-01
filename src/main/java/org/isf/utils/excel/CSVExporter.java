package org.isf.utils.excel;

import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHException;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class CSVExporter {
	private Locale currentLocale;
	private CharsetEncoder encoder;

	public CSVExporter() {
		encoder = StandardCharsets.UTF_8.newEncoder();
		encoder.onMalformedInput(CodingErrorAction.REPORT);
		encoder.onUnmappableCharacter(CodingErrorAction.REPORT);
		currentLocale = Locale.getDefault();
	}

	/**
	 * Export a jTable to CSV file with a semi-column (;) as list separator
	 *
	 * @param jtable
	 * @param file
	 * @throws IOException
	 * @deprecated use exportTableToExcel method
	 */
	@Deprecated
	public void exportTableToCSV(JTable jtable, File file) throws IOException {
		exportTableToCSV(jtable, file, ";");
	}

	/**
	 * Export a jTable to CSV file format
	 *
	 * @param jtable
	 * @param file
	 * @param separator - the character to use as separator (usually ',' or ';')
	 * @throws IOException
	 */
	private void exportTableToCSV(JTable jtable, File file, String separator) throws IOException {
		TableModel model = jtable.getModel();
		FileOutputStream fileStream = new FileOutputStream(file);
		writeBOM(fileStream);

		try (BufferedWriter outFile = new BufferedWriter(new OutputStreamWriter(fileStream, encoder))) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

			int colCount = model.getColumnCount();
			for (int i = 0; i < colCount; i++) {
				if (i == colCount - 1) {
					outFile.write(model.getColumnName(i));
				} else {
					outFile.write(model.getColumnName(i) + separator);
				}
			}
			outFile.write("\n");

			int rowCount = model.getColumnCount();
			for (int i = 0; i < rowCount; i++) {
				for (int j = 0; j < colCount; j++) {
					String strVal;
					Object objVal = model.getValueAt(i, j);
					if (objVal != null) {
						if (objVal instanceof Integer) {
							Integer val = (Integer) objVal;
							NumberFormat format = NumberFormat.getInstance(currentLocale);
							strVal = format.format(val);
						} else if (objVal instanceof Double) {
							Double val = (Double) objVal;
							NumberFormat format = NumberFormat.getInstance(currentLocale);
							strVal = format.format(val);
						} else if (objVal instanceof Timestamp) {
							Timestamp val = (Timestamp) objVal;
							strVal = sdf.format(val);
						} else {
							strVal = objVal.toString();
						}
					} else {
						strVal = " ";
					}
					if (j == colCount - 1) {
						outFile.write(strVal);
					} else {
						outFile.write(strVal + separator);
					}
				}
				outFile.write("\n");
			}
		}
	}

	/**
	 * Export a {@link ResultSet} to CSV file with a semi-column (;) as list separator
	 *
	 * @param resultSet
	 * @param exportFile
	 * @throws IOException
	 * @throws OHException
	 * @deprecated use exportTableToExcel method
	 */
	@Deprecated
	public void exportResultsetToCSV(ResultSet resultSet, File exportFile) throws IOException, OHException {
		exportResultsetToCSV(resultSet, exportFile, ";");
	}

	/**
	 * Export a {@link ResultSet} to CSV file
	 *
	 * @param resultSet
	 * @param exportFile
	 * @param separator - the character to use as separator (usually ',' or ';')
	 * @throws IOException
	 * @throws OHException
	 */
	private void exportResultsetToCSV(ResultSet resultSet, File exportFile, String separator) throws IOException, OHException {

		/*
		 * write BOM for Excel UTF-8 automatic handling
		 */
		FileOutputStream fileStream = new FileOutputStream(exportFile);
		writeBOM(fileStream);

		try (BufferedWriter output = new BufferedWriter(new OutputStreamWriter(fileStream, encoder))) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			NumberFormat numFormat = NumberFormat.getInstance(currentLocale);

			try {
				ResultSetMetaData rsmd = resultSet.getMetaData();

				int colCount = rsmd.getColumnCount();
				for (int i = 1; i <= colCount; i++) {
					if (i == colCount - 1) {
						output.write(rsmd.getColumnName(i));
					} else {
						output.write(rsmd.getColumnName(i) + separator);
					}
				}
				output.write("\n");

				while (resultSet.next()) {

					String strVal;
					for (int i = 1; i <= colCount; i++) {
						Object objVal = resultSet.getObject(i);
						if (objVal != null) {
							if (objVal instanceof Double) {

								Double val = (Double) objVal;
								strVal = numFormat.format(val);
							} else if (objVal instanceof Timestamp) {

								Timestamp val = (Timestamp) objVal;
								strVal = sdf.format(val);
							} else {

								strVal = objVal.toString();
							}
						} else {
							strVal = " ";
						}
						if (i == colCount - 1) {
							output.write(strVal);
						} else {
							output.write(strVal + separator);
						}

					}
					output.write("\n");

				}
			} catch (SQLException e) {
				throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), e);
			}
		}
	}

	/**
	 * Export a {@link Collection} to CSV using Apache POI library
	 *
	 * @param data
	 * @param exportFile
	 * @throws IOException
	 * @throws OHException
	 * @deprecated use exportDataToExcel
	 */
	@Deprecated
	public void exportDataToCSV(Collection data, File exportFile) throws IOException, OHException {

		try (FileWriter outFile = new FileWriter(exportFile)) {
			boolean header = false;
			for (Object map : data) {
				Map thisMap = ((Map) map);
				if (!header) {
					Set columns = thisMap.keySet();
					for (Object column : columns) {
						outFile.write(column.toString() + ";");
					}
					outFile.write("\n");
					header = true;
				}

				String strVal;
				Collection values = thisMap.values();
				for (Object value : values) {
					strVal = convertValue(value);
					outFile.write(strVal + ";");
				}
				outFile.write("\n");
			}
		}
	}

	private String convertValue(Object value) {
		String strVal;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		if (value != null) {
			if (value instanceof BigDecimal) {

				BigDecimal val = (BigDecimal) value;
				NumberFormat format = NumberFormat.getInstance(currentLocale);
				strVal = format.format(val);
			} else if (value instanceof Double) {

				Double val = (Double) value;
				NumberFormat format = NumberFormat.getInstance(currentLocale);
				strVal = format.format(val);
			} else if (value instanceof Timestamp) {

				Timestamp val = (Timestamp) value;
				strVal = sdf.format(val);
			} else {

				strVal = value.toString();
			}
		} else {
			strVal = " ";
		}
		return strVal;
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

}
