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
package org.isf.dicom.manager;

import org.isf.dicom.model.FileDicom;
import org.isf.dicom.service.DicomIoOperations;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Interface for definitions IO for Dicom acquired files
 *
 * @author Pietro Castellucci
 * @version 1.0.0
 */
@Component
public class SqlDicomManager implements DicomManagerInterface {

	@Autowired
	private DicomIoOperations ioOperations;

	public SqlDicomManager() {
	}

	/**
	 * Load a list of id file for series
	 *
	 * @param patientID, the patient id
	 * @param seriesNumber, the series number
	 * @return
	 * @throws OHServiceException
	 */
	@Override
	public Long[] getSerieDetail(int patientID, String seriesNumber) throws OHServiceException {
		return ioOperations.getSerieDetail(patientID, seriesNumber);
	}

	/**
	 * Delete series
	 *
	 * @param patientID, the id of patient
	 * @param seriesNumber, the series number to delete
	 * @return true if success
	 * @throws OHServiceException
	 */
	@Override
	public boolean deleteSerie(int patientID, String seriesNumber) throws OHServiceException {
		return ioOperations.deleteSerie(patientID, seriesNumber);
	}

	/**
	 * Check if dicom is loaded
	 *
	 * @param dicom - the detail of the dicom
	 * @return true if file exist
	 * @throws OHServiceException
	 */
	@Override
	public boolean exist(FileDicom dicom) throws OHServiceException {
		return ioOperations.exist(dicom);
	}

	/**
	 * Check if series number does already exist
	 *
	 * @param patientID, the id of patient
	 * @param seriesNumber,
	 * @return true if file exist
	 * @throws OHServiceException
	 */
	@Override
	public boolean exist(int patientID, String seriesNumber) throws OHServiceException {
		return ioOperations.isSeriesPresent(seriesNumber);
	}

	/**
	 * Load the Detail of DICOM
	 *
	 * @param idFile
	 * @param patientID
	 * @param seriesNumber
	 * @return FileDicom
	 * @throws OHServiceException
	 */
	@Override
	public FileDicom loadDetails(Long idFile, int patientID, String seriesNumber) throws OHServiceException {
		return ioOperations.loadDetails(idFile, patientID, seriesNumber);
	}

	/**
	 * Load detail
	 *
	 * @param idFile
	 * @param patientID
	 * @param seriesNumber
	 * @return FileDicom
	 * @throws OHServiceException
	 */
	@Override
	public FileDicom loadDetails(long idFile, int patientID, String seriesNumber) throws OHServiceException {
		return ioOperations.loadDetails(idFile, patientID, seriesNumber);
	}

	/**
	 * Load metadata from DICOM files of the patient
	 *
	 * @param patientID
	 * @return
	 * @throws OHServiceException
	 */
	@Override
	public FileDicom[] loadPatientFiles(int patientID) throws OHServiceException {
		return ioOperations.loadPatientFiles(patientID);
	}

	/**
	 * Save the DICOM file and metadata
	 *
	 * @param dicom
	 * @throws OHServiceException
	 */
	@Override
	public void saveFile(FileDicom dicom) throws OHServiceException {
		ioOperations.saveFile(dicom);
	}

}
