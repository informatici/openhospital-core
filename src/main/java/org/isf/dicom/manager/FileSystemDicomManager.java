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
package org.isf.dicom.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Properties;
import java.util.Vector;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.isf.dicom.model.FileDicom;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHDicomException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.isf.utils.time.TimeTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Manager for filesystem
 * 
 * @author Pietro Castellucci
 * @version 1.0.0
 */
@Component
public class FileSystemDicomManager implements DicomManagerInterface {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemDicomManager.class);
	private static String DICOM_DATE_FORMAT_ZONED = "EEE MMM dd HH:mm:ss z yyyy";
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DICOM_DATE_FORMAT_ZONED, new Locale("en"));

	public FileSystemDicomManager() {
	}

	/**
	 * Root dir for data storage
	 */
	private File dir;
	private FilterSerieDetail dsf = new FilterSerieDetail();

	/**
	 * Constructor
	 * @throws OHDicomException 
	 */
	public FileSystemDicomManager(Properties externalPrp) throws OHDicomException {
		try {
			dir = new File(externalPrp.getProperty("dicom.storage.filesystem"));
			recourse(dir);
		} catch(Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
			throw new OHDicomException(exception, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.formatMessage("angal.dicommanager.genericerror.fmt.msg", exception.getMessage()),
					OHSeverityLevel.ERROR));
		}
	}
	
	/**
	 * @param externalPrp - the properties to set
	 */
	public void setDir(Properties externalPrp) {
		this.dir = new File(externalPrp.getProperty("dicom.storage.filesystem"));
	}

	/**
	 * Load a list of id file for series
	 * 
	 * @param patientID, the patient id
	 * @param seriesNumber, the series number
	 * @return
	 * @throws OHDicomException 
	 */
	@Override
	public Long[] getSerieDetail(int patientID, String seriesNumber) throws OHDicomException {
		try {
			// seriesNumber cannot be null, so it must return null
			if (seriesNumber == null || seriesNumber.trim().length() == 0 || seriesNumber.equalsIgnoreCase("null")) {
				return null;
			}
			File df = getSerieDir(patientID, seriesNumber, false);

			File[] files = df.listFiles(dsf);

			long[] _longs = new long[files.length];

			for (int i = 0; i < _longs.length; i++) {

				try {
					_longs[i] = Long.parseLong(files[i].getName().substring(0, files[i].getName().indexOf(".")));
				} catch (Exception e) {
				}
			}

			Arrays.sort(_longs);

			Long[] _Longs = new Long[_longs.length];

			for (int i = 0; i < _Longs.length; i++) {
				_Longs[i] = _longs[i];
			}

			return _Longs;
		} catch (Exception exception) {
			throw new OHDicomException(exception, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.formatMessage("angal.dicommanager.genericerror.fmt.msg", exception.getMessage()),
					OHSeverityLevel.ERROR));
		}
	}
	
	/**
	 * Delete series
	 * 
	 * @param patientId, the id of patient
	 * @param seriesNumber, the series number to delete
	 * @return true if success
	 * @throws OHDicomException 
	 */
	@Override
	public boolean deleteSerie(int patientId, String seriesNumber) throws OHDicomException {
		try {
			// seriesNumber cannot be null, so it must return false
			if (seriesNumber == null || seriesNumber.trim().length() == 0 || seriesNumber.equalsIgnoreCase("null")) {
				return false;
			}
			File deleteFolder = getSerieDir(patientId, seriesNumber, false);
			File[] f = deleteFolder.listFiles();
			boolean deleted = true;

			for (File file : f) {
				deleted = deleted && file.delete();
			}
			return deleted && deleteFolder.delete();

		} catch (Exception exception) {
			throw new OHDicomException(exception, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.formatMessage("angal.dicommanager.genericerror.fmt.msg", exception.getMessage()),
					OHSeverityLevel.ERROR));
		}
	}

	/**
	 * Load the Detail of DICOM
	 * 
	 * @param idFile
	 * @return FileDicom
	 * @throws OHDicomException 
	 */
	@Override
	public FileDicom loadDetails(Long idFile, int patientId, String seriesNumber) throws OHDicomException {
		// seriesNumber cannot be null, so it must return null
		if (seriesNumber == null || seriesNumber.trim().length() == 0 || seriesNumber.equalsIgnoreCase("null")) {
			return null;
		}
		if (idFile == null) {
			return null;
		}
		return loadDetails(idFile.longValue(), patientId, seriesNumber);
	}

	/**
	 * Load detail
	 * 
	 * @param idFile
	 * @return details
	 * @throws OHDicomException 
	 */
	@Override
	public FileDicom loadDetails(long idFile, int patientId, String seriesNumber) throws OHDicomException {
		try {
			return loadData(idFile, patientId, seriesNumber);
		} catch (Exception exception) {
			throw new OHDicomException(exception, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.formatMessage("angal.dicommanager.genericerror.fmt.msg", exception.getMessage()),
					OHSeverityLevel.ERROR));
		}
	}

	/**
	 * Load metadata from DICOM files fo the patient
	 * 
	 * @param patientId
	 * @return
	 * @throws OHDicomException 
	 */
	@Override
	public FileDicom[] loadPatientFiles(int patientId) throws OHDicomException {
		try {
			File df = getPatientDir(patientId);
			File[] series = df.listFiles();
			FileDicom[] db = new FileDicom[series.length];

			for (int i = 0; i < series.length; i++) {
				long idFile = getFirst(series[i]);
				db[i] = loadMetadata(idFile, patientId, series[i].getName());
			}

			db = compact(db);
			return db;
		} catch (Exception exception) {
			throw new OHDicomException(exception, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.formatMessage("angal.dicommanager.genericerror.fmt.msg", exception.getMessage()),
					OHSeverityLevel.ERROR));
		}
	}

	/**
	 * Save the DICOM file and metadata
	 * 
	 * @param dicom
	 * @throws OHDicomException 
	 */
	@Override
	public void saveFile(FileDicom dicom) throws OHDicomException {
		if (exist(dicom)) {
			return;
		}
		try {
			int patId = dicom.getPatId();
			String seriesNumber = dicom.getDicomSeriesNumber();
			String dicomInstanceUID = dicom.getDicomInstanceUID();

			// some times this number could be null, it's wrong, but I add
			// line to avoid exception
			if (seriesNumber == null || seriesNumber.trim().length() == 0 || seriesNumber.equalsIgnoreCase("null")) {
				seriesNumber = SourceFiles.generateSeriesNumber(patId);
				dicom.setDicomSeriesNumber(seriesNumber);
				dicom.setDicomSeriesInstanceUID("<org_root>."+seriesNumber);
			}

			long idFile = nextId();
			// dicomInstanceUID is used to identify a unique file in the series (like DM_FILE_ID in the DB)
			// so cannot be empty and will be used only for this cycle
			if (dicomInstanceUID == null || dicomInstanceUID.isEmpty()) {
				dicomInstanceUID = seriesNumber + "." + idFile;
				dicom.setDicomInstanceUID(dicomInstanceUID);
			}

			File df = getSerieDir(patId, seriesNumber, true);
			File properties = new File(df, idFile + ".properties");
			try (FileOutputStream fos = new FileOutputStream(properties, false);	PrintStream ps = new PrintStream(fos)) {
				ps.println("idFile =" + idFile);
				ps.println("patId =" + patId);
				ps.println("fileName =" + dicom.getFileName());
				ps.println("dicomAccessionNumber =" + dicom.getDicomAccessionNumber());
				ps.println("dicomInstitutionName =" + dicom.getDicomInstitutionName());
				ps.println("dicomPatientID =" + dicom.getDicomPatientID());
				ps.println("dicomPatientName =" + dicom.getDicomPatientName());
				ps.println("dicomPatientAddress =" + dicom.getDicomPatientAddress());
				ps.println("dicomPatientAge =" + dicom.getDicomPatientAge());
				ps.println("dicomPatientSex =" + dicom.getDicomPatientSex());
				ps.println("dicomPatientBirthDate =" + dicom.getDicomPatientBirthDate());
				ps.println("dicomStudyId =" + dicom.getDicomStudyId());
				ps.println("dicomStudyDate =" + dicom.getDicomStudyDate().atZone(ZoneId.systemDefault()).format(DATE_TIME_FORMATTER));
				ps.println("dicomStudyDescription =" + dicom.getDicomStudyDescription());
				ps.println("dicomSeriesUID =" + dicom.getDicomSeriesUID());
				ps.println("dicomSeriesInstanceUID =" + dicom.getDicomSeriesInstanceUID());
				ps.println("dicomSeriesNumber =" + dicom.getDicomSeriesNumber());
				ps.println("dicomSeriesDescriptionCodeSequence =" + dicom.getDicomSeriesDescriptionCodeSequence());
				ps.println("dicomSeriesDate =" + dicom.getDicomSeriesDate().atZone(ZoneId.systemDefault()).format(DATE_TIME_FORMATTER));
				ps.println("dicomSeriesDescription =" + dicom.getDicomSeriesDescription());
				// dicomInstanceUID is used to identify a unique file in the series
				// so cannot be empty and will be used only for this cycle
				ps.println("dicomInstanceUID =" + dicomInstanceUID);
				ps.println("modality =" + dicom.getModality());
				ps.flush();
			}
			File data = new File(df, idFile + ".data");
			Blob blob = dicom.getDicomData();
			int blobLength = (int) blob.length();
			byte[] blobAsBytes = blob.getBytes(1, blobLength);
			save(data, blobAsBytes);
			File thumn = new File(df, idFile + ".thumn");
			blob = dicom.getDicomThumbnail();
			blobLength = (int) blob.length();
			blobAsBytes = blob.getBytes(1, blobLength);
			save(thumn, blobAsBytes);
		} catch (Exception exception) {
			throw new OHDicomException(exception, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.formatMessage("angal.dicommanager.genericerror.fmt.msg", exception.getMessage()),
					OHSeverityLevel.ERROR));
		}
	}

	/*
	 * Load DICOM data + Thumbnail
	 */
	private FileDicom loadMetadata(long idFile, int patientId, String series) throws IOException, SQLException {
		// Series must exists, so we need to check it and return null in case
		if (series == null || series.trim().length() == 0 || series.equalsIgnoreCase("null")) {
			return null;
		}
		FileDicom rv = new FileDicom();
		File sd = getSerieDir(patientId, series, false);
		rv.setFrameCount(getFramesCount(patientId, series));
		parseDicomProperties(idFile, rv, sd);
		rv.setDicomThumbnail(loadThumbnail(sd, idFile));
		return rv;
	}

	/*
	* Load DICOM data + Image
	*/
	private FileDicom loadData(long idFile, int patientId, String series) throws IOException, SQLException, OHDicomException  {
		// Series must exists, so we need to check it and return null in case
		if (series == null || series.trim().length() == 0 || series.equalsIgnoreCase("null")) {
			return null;
		}
		FileDicom rv = new FileDicom();
		File sd = getSerieDir(patientId, series, false);
		parseDicomProperties(idFile, rv, sd);
		rv.setDicomData(loadDicomData(sd, idFile));
		return rv;
	}

	private void parseDicomProperties(long idFile, FileDicom rv, File sd) throws IOException {
		Properties p = loadMetadata(sd, idFile);
		try {
			rv.setIdFile(Long.parseLong(p.getProperty("idFile")));
		} catch (Exception e) {
			LOGGER.debug("Unparsable 'idFile': {}", p.getProperty("idFile"));
		}
		try {
			rv.setPatId(Integer.parseInt(p.getProperty("patId")));
		} catch (Exception e) {
			LOGGER.debug("Unparsable 'patId': {}", p.getProperty("patId"));
		}
		rv.setFileName(p.getProperty("fileName"));
		rv.setDicomAccessionNumber(p.getProperty("dicomAccessionNumber"));
		rv.setDicomInstitutionName(p.getProperty("dicomInstitutionName"));
		rv.setDicomPatientID(p.getProperty("dicomPatientID"));
		rv.setDicomPatientName(p.getProperty("dicomPatientName"));
		rv.setDicomPatientAddress(p.getProperty("dicomPatientAddress"));
		rv.setDicomPatientAge(p.getProperty("dicomPatientAge"));
		rv.setDicomPatientSex(p.getProperty("dicomPatientSex"));
		rv.setDicomPatientBirthDate(p.getProperty("dicomPatientBirthDate"));
		rv.setDicomStudyId(p.getProperty("dicomStudyId"));
		try {
			rv.setDicomStudyDate(LocalDateTime.parse(p.getProperty("dicomStudyDate"), DATE_TIME_FORMATTER));
		} catch (DateTimeParseException dateTimeParseException) {
			LOGGER.debug("1. example: {}", TimeTools.getNow().format(DATE_TIME_FORMATTER));
			LOGGER.debug("1. Unparsable 'dicomStudyDate': {}", p.getProperty("dicomStudyDate"));
		}
		rv.setDicomStudyDescription(p.getProperty("dicomStudyDescription"));
		rv.setDicomSeriesUID(p.getProperty("dicomSeriesUID"));
		rv.setDicomSeriesInstanceUID(p.getProperty("dicomSeriesInstanceUID"));
		rv.setDicomSeriesNumber(p.getProperty("dicomSeriesNumber"));
		rv.setDicomSeriesDescriptionCodeSequence(p.getProperty("dicomSeriesDescriptionCodeSequence"));
		try {
			rv.setDicomSeriesDate(LocalDateTime.parse(p.getProperty("dicomSeriesDate"), DATE_TIME_FORMATTER));
		} catch (DateTimeParseException dateTimeParseException) {
			LOGGER.debug("1. example: {}", TimeTools.getNow().format(DATE_TIME_FORMATTER));
			LOGGER.debug("2. Unparsable 'dicomSeriesDate': {}", p.getProperty("dicomSeriesDate"));
		}
		rv.setDicomSeriesDescription(p.getProperty("dicomSeriesDescription"));
		rv.setDicomInstanceUID(p.getProperty("dicomInstanceUID"));
		rv.setModality(p.getProperty("modality"));
	}

	/**
	 * Load image for thumbnail
	 * @throws SQLException 
	 * @throws SerialException 
	 */
	private Blob loadThumbnail(File sd, long idFile) throws IOException, SerialException, SQLException {
		File fdc = new File(sd, idFile + ".thumn");
		byte[] byteArray;
		try (FileInputStream fis = new FileInputStream(fdc)) {
			byteArray = new byte[fis.available()];
			fis.read(byteArray);
		}
		return new SerialBlob(byteArray);
	}

	/**
	 * Load DICOM image
	 * @throws SQLException 
	 * @throws SerialException 
	 */
	private Blob loadDicomData(File sd, long idFile) throws IOException, SQLException {
		File fdc = new File(sd, idFile + ".data");
		byte[] byteArray;
		try (FileInputStream fis = new FileInputStream(fdc)) {
			byteArray = new byte[fis.available()];
			fis.read(byteArray);
		}
		return new SerialBlob(byteArray);
	}

	@Override
	public boolean exist(FileDicom dicom) throws OHDicomException {
		boolean rv = false;
		try {
			int patId = dicom.getPatId();
			String serieNumber = dicom.getDicomSeriesNumber();
			String diuid = dicom.getDicomInstanceUID();
			if (serieNumber == null || serieNumber.trim().length() == 0 || serieNumber.equalsIgnoreCase("null")) {
				return false;
			}
			if (diuid == null || diuid.trim().isEmpty() || diuid.equalsIgnoreCase("null")) {
				return false;
			}
			File df = getSerieDir(patId, serieNumber, true);
			File[] files = df.listFiles();
			int i = 0;
			while (!rv && i < files.length) {
				String nf = files[i].getName();
				if (nf.endsWith(".properties")) {
					Properties p = loadMetadata(files[i]);
					String vl = p.getProperty("dicomInstanceUID");
					rv = diuid.equals(vl);
				}
				i++;
			}
		} catch (Exception exception) {
			throw new OHDicomException(exception, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.formatMessage("angal.dicommanager.genericerror.fmt.msg", exception.getMessage()),
					OHSeverityLevel.ERROR));
		}
		return rv;
	}

	private Properties loadMetadata(File sd, long idFile) throws IOException {
		Properties p = new Properties();
		try (FileReader fr = new FileReader(new File(sd, idFile + ".properties"))) {
			p.load(fr);
		}
		return p;
	}

	private Properties loadMetadata(File mdf) throws IOException {
		Properties p = new Properties();
		try (FileReader fr = new FileReader(mdf)) {
			p.load(fr);
		}
		return p;
	}

	/**
	 * frames counter
	 */
	private int getFramesCount(int patID, String serieNumber) throws IOException {
		File df = getSerieDir(patID, serieNumber, false);
		File[] files = df.listFiles(dsf);
		return files.length;
	}

	/**
	 * emulate SQL sequence on filesystem
	 */
	private synchronized long nextId() throws IOException {
		long rv = 0;

		try {
			File i = new File(dir, "dicom.storage");
			if (i.exists()) {
				try (FileInputStream fis = new FileInputStream(i)) {
					try (ObjectInputStream ois = new ObjectInputStream(fis)) {
						rv = ois.readLong();
					}
				}
			}

			i.delete();

			i = new File(dir, "dicom.storage");

			rv++;

			try (FileOutputStream fos = new FileOutputStream(i)) {
				try (ObjectOutputStream oos = new ObjectOutputStream(fos)) {
					oos.writeLong(rv);
					oos.flush();
					fos.flush();
				}
			}
		} catch (Exception exc) {

		}
		return rv;
	}

	/**
	 * retrieve patient's series folder
	 */
	private File getSerieDir(int patId, String serie, boolean recourse) throws IOException {
		File fm = getPatientDir(patId);
		File f = new File(fm, serie);

		if (recourse) {
			recourse(f);
		}
		return f;
	}

	/**
	 * retrieve patient folder
	 */
	private File getPatientDir(int patId) throws IOException {
		File f = new File(dir, "" + patId);
		recourse(f);
		return f;
	}

	/**
	 * 
	 * recorsive function for create folder structure if missing
	 */
	private void recourse(File f) throws IOException {
		if (f.exists()) {
			return;
		}
		File fp = f.getParentFile();

		if (fp != null)
			recourse(fp);

		if (!f.exists())
			f.mkdir();
	}

	/**
	 * Save content in specified file
	 * 
	 * @param outFile - the file to write into
	 * @param content - byte vector to write
	 */
	private void save(File outFile, byte[] content) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(outFile)) {
			fos.write(content);
			fos.flush();
		}
	}

	/**
	 * Return the first idFile in the serie
	 */
	private long getFirst(File df) {
		File[] files = df.listFiles(dsf);

		long[] _longs = new long[files.length];

		for (int i = 0; i < _longs.length; i++) {
			try {
				_longs[i] = Long.parseLong(files[i].getName().substring(0, files[i].getName().indexOf(".")));
			} catch (Exception e) {
			}
		}

		Arrays.sort(_longs);

		if (_longs.length > 0) {
			return _longs[0];
		}
		return -1;
	}

	private FileDicom[] compact(FileDicom[] db) {
		Vector<FileDicom> rv = new Vector<>(0);

		for (FileDicom fileDicom : db) {
			if (fileDicom != null) {
				rv.addElement(fileDicom);
			}
		}
		FileDicom[] ret = new FileDicom[rv.size()];
		rv.sort(new DicomDateComparator());
		Collections.reverse(rv);
		rv.copyInto(ret);

		return ret;
	}
	
	public class DicomDateComparator implements Comparator<FileDicom> {
		
		@Override
		public int compare(FileDicom object1, FileDicom object2) {
			if (object2.getDicomStudyDate() == null) {
				return -1;
			}
			return object1.getDicomStudyDate().compareTo(object2.getDicomStudyDate());
		}
	}

	public class DicomTypeDateComparator implements Comparator<FileDicom> {

		@Override
		public int compare(FileDicom object1, FileDicom object2) {

			//default comparing
			int result = object1.getDicomStudyDate().compareTo(object2.getDicomStudyDate());

			if (object1.getDicomType() == null || object2.getDicomType() == null) {
				return result;
			}
			result = object1.getDicomType().getDicomTypeDescription().compareTo(object2.getDicomType().getDicomTypeDescription());
			if (result != 0) {
				return result;
			}
			return object1.getDicomStudyDate().compareTo(object2.getDicomStudyDate());
		}
	}

	/**
	 * Filter for files .thumn
	 */
	class FilterSerieDetail implements FilenameFilter {

		@Override
		public boolean accept(File dir, String name) {
			if (name == null) {
				return false;
			}
			return name.endsWith(".thumn");
		}
	}

	@Override
	public boolean exist(int patientId, String seriesNumber) throws OHServiceException {
		File seriesFolder = null;
		try {
			seriesFolder = getSerieDir(patientId, seriesNumber, false);
		} catch (IOException e) {
		}
		return seriesFolder.exists();
	}

}
