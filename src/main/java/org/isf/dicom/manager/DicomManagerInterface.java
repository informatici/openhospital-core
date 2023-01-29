/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
import org.isf.utils.exception.OHServiceException;

/**
 * Interface for definitions IO for Dicom acquired files
 *
 * @author Pietro Castellucci
 * @version 1.0.0
 */
public interface DicomManagerInterface {

	/**
	 * Load a list of idfile for series
	 *
	 * @param patientID, the patient id
	 * @param seriesNumber, the series number
	 * @return
	 * @throws OHServiceException
	 */
	Long[] getSerieDetail(int patientID, String seriesNumber) throws OHServiceException;

	/**
	 * Delete series
	 *
	 * @param patientID, the id of patient
	 * @param seriesNumber, the series number to delete
	 * @return true if success
	 * @throws OHServiceException
	 */
	boolean deleteSerie(int patientID, String seriesNumber) throws OHServiceException;

	/**
	 * Check if dicom is loaded
	 *
	 * @param dicom, the detail of dicom
	 * @return true if file exist
	 * @throws OHServiceException
	 */
	boolean exist(FileDicom dicom) throws OHServiceException;

	/**
	 * Check if dicom is loaded
	 *
	 * @param patientID, the id of patient
	 * @param seriesNumber, the series number
	 * @return true if file exist
	 * @throws OHServiceException
	 */
	boolean exist(int patientID, String seriesNumber) throws OHServiceException;

	/**
	 * Load the Detail of DICOM
	 *
	 * @param idFile
	 * @param patientID
	 * @param seriesNumber
	 * @return FileDicom
	 * @throws OHServiceException
	 */
	FileDicom loadDetails(Long idFile, int patientID, String seriesNumber) throws OHServiceException;

	/**
	 * Load detail
	 *
	 * @param idFile
	 * @param patientID
	 * @param seriesNumber
	 * @return FileDicom
	 * @throws OHServiceException
	 */
	FileDicom loadDetails(long idFile, int patientID, String seriesNumber) throws OHServiceException;

	/**
	 * Load metadata from DICOM files of the patient
	 *
	 * @param patientID
	 * @return
	 * @throws OHServiceException
	 */
	FileDicom[] loadPatientFiles(int patientID) throws OHServiceException;

	/**
	 * Save the DICOM file and metadata
	 *
	 * @param dicom
	 * @throws OHServiceException
	 */
	void saveFile(FileDicom dicom) throws OHServiceException;
}
