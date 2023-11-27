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
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.dicom.manager;

import java.io.File;
import java.time.LocalDateTime;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.io.DicomStreamException;

import org.isf.dicom.model.FileDicom;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHDicomException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.file.FileTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import static org.isf.dicom.manager.DicomLoader.getSeriesDateTime;
import static org.isf.dicom.manager.DicomLoader.getStudyDateTime;

/**
 * Manager for DICOM Files
 *
 * @author Pietro Castellucci
 * @version 1.0.0
 */
public class SourceFiles extends Thread {

	private static final Logger LOGGER = LoggerFactory.getLogger(SourceFiles.class);

	private File file;
	private FileDicom fileDicom;
	private int patient;
	private int filesCount;
	private int filesLoaded;
	private AbstractDicomLoader dicomLoader;
	private AbstractThumbnailViewGui thumbnail;

	 static DicomLoader dicomLoaderFrame;


	public SourceFiles(FileDicom fileDicom, File sourceFile, int patient, int filesCount, AbstractThumbnailViewGui thumbnail, AbstractDicomLoader frame) {
		this.patient = patient;
		this.file = sourceFile;
		this.fileDicom = fileDicom;
		this.filesCount = filesCount;
		this.thumbnail = thumbnail;
		this.dicomLoader = frame;
		start();
	}

	@Override
	public void run() {
		try {
			loadDicomDir(fileDicom, file, patient);
		} catch (Exception e) {
			LOGGER.error("loadDicomDir", e);
		}
		dicomLoader.setVisible(false);
		thumbnail.initialize();
	}

	/**
	 * Load a DICOM directory
	 *
	 * @throws Exception
	 */
	private void loadDicomDir(FileDicom fileDicom, File sourceFile, int patient) throws Exception {
		File[] files = sourceFile.listFiles();
		String seriesNumber = fileDicom.getDicomSeriesNumber();
		if (seriesNumber == null || seriesNumber.isEmpty()) {
			try {
				seriesNumber = dicomLoaderFrame.generateSeriesNumber(patient);
				fileDicom.setDicomSeriesNumber(seriesNumber);
			} catch (OHServiceException e1) {
				seriesNumber = "";
			}
		}
		for (File value : files) {

			try {
				Thread.sleep(10);
			} catch (Exception e) {}

			if (!value.isDirectory()) {
				try {
					dicomLoaderFrame.loadDicom(fileDicom, value, patient);
				} catch (OHDicomException ohDicomException) {
					LOGGER.error("loadDicomDir: {}", ohDicomException.getMessages().get(0).getMessage());
				} catch (Exception e) {
					throw e;
				}
				filesLoaded++;
				dicomLoader.setLoaded(filesLoaded);
			} else if (!".".equals(value.getName()) && !"..".equals(value.getName())) {
				loadDicomDir(fileDicom, value, patient);
			}
		}
	}

	public static boolean checkSize(File sourceFile) throws OHDicomException {

		return DicomManagerFactory.getMaxDicomSizeLong() > sourceFile.length();
	}

	public static int countFiles(File sourceFile, int patient) throws OHDicomException {
		int num = 0;

		File[] files = sourceFile.listFiles();

		for (File value : files) {
			if (!value.isDirectory()) {
				if (!checkSize(value)) {
					throw new OHDicomException(new OHExceptionMessage(
							MessageBundle.formatMessage("angal.dicom.afileinthefolderistoobigpleasesetdicommaxsizeindicomproperties.fmt.msg",
							                            DicomManagerFactory.getMaxDicomSize())));
				}
				num++;
			} else if (!".".equals(value.getName()) && !"..".equals(value.getName())) {
				num = num + countFiles(value, patient);
			}
		}
		return num;
	}

	public boolean working() {
		return (filesLoaded < filesCount);
	}

	public int getLoaded() {
		return filesLoaded;
	}

	/**
	 * PreLoad dicom file for validation in gui with some
	 * data from filesystem
	 *
	 * @param sourceFile
	 * @param numfiles
	 */
	public static FileDicom preLoadDicom(File sourceFile, int numfiles) {
		FileDicom dicomFileDetail = new FileDicom();
		try {
			String fileName = sourceFile.getName();
			LocalDateTime seriesDate = null;
			LocalDateTime studyDate = null;
			boolean isJpeg = StringUtils.endsWithIgnoreCase(fileName, ".jpg") || StringUtils.endsWithIgnoreCase(fileName, ".jpeg");
			boolean isDicom = StringUtils.endsWithIgnoreCase(fileName, ".dcm");
			if (isJpeg) {
				studyDate = FileTools.getTimestamp(sourceFile); //get last modified date (creation date)
			} else if (isDicom) {
				DicomInputStream dicomInputStream;
				try {
					dicomInputStream = new DicomInputStream(sourceFile);
				} catch (DicomStreamException dicomStreamException) {
					throw new OHDicomException(new OHExceptionMessage(
							MessageBundle.formatMessage("angal.dicom.thefileisinanunknownformat.fmt.msg", fileName)));
				}
				Attributes attributes = dicomInputStream.readDataset();
				seriesDate = getSeriesDateTime(attributes);
				studyDate = getStudyDateTime(attributes);
				if (attributes.contains(Tag.SeriesNumber)) {
					dicomFileDetail.setDicomSeriesNumber(attributes.getString(Tag.SeriesNumber));
				} else {
					LOGGER.error("DICOM: Unparsable SeriesNumber");
				}
			} else {
				throw new OHDicomException(new OHExceptionMessage(MessageBundle.formatMessage("angal.dicom.dicomformatnotsupported.fmt.msg", fileName)));
			}
			dicomFileDetail.setFrameCount(numfiles);
			dicomFileDetail.setDicomData(sourceFile);
			dicomFileDetail.setFileName(fileName);
			dicomFileDetail.setDicomSeriesDate(seriesDate);
			dicomFileDetail.setDicomStudyDate(studyDate);

			return dicomFileDetail;

		} catch (OHDicomException e) {
			LOGGER.error(e.getMessages().get(0).getMessage());
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
		}
		return dicomFileDetail;
	}
}
