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
package org.isf.dicom.service;

import java.util.List;

import org.isf.dicom.model.FileDicom;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class DicomIoOperations {

	private DicomIoOperationRepository repository;

	public DicomIoOperations(DicomIoOperationRepository dicomIoOperationRepository) {
		this.repository = dicomIoOperationRepository;
	}

	/**
	 * Load a list of file id for a series.
	 * 
	 * @param patientID, the patient id
	 * @param seriesNumber, the series number
	 * @return
	 * @throws OHServiceException 
	 */
	public Long[] getSeriesDetail(int patientID, String seriesNumber) throws OHServiceException {
		List<FileDicom> dicomList = repository.findAllWhereIdAndNumberByOrderNameAsc(patientID, seriesNumber);
		Long[] dicomIdArray = new Long[dicomList.size()];

		for (int i = 0; i < dicomList.size(); i++) {
			dicomIdArray[i] = dicomList.get(i).getIdFile();
		}

		return dicomIdArray;
	}

	/**
	 * Delete a series.
	 * 
	 * @param patientID, the id of patient
	 * @param seriesNumber, the series number to delete
	 * @throws OHServiceException 
	 */
	public void deleteSeries(int patientID, String seriesNumber) throws OHServiceException {
		repository.deleteByIdAndNumber(patientID, seriesNumber);
	}

	/**
	 * Load the details of DICOM.
	 * 
	 * @param idFile
	 * @param patientID
	 * @param seriesNumber
	 * @return {@link FileDicom} or {@code null)}
	 * @throws OHServiceException 
	 */
	public FileDicom loadDetails(long idFile, int patientID, String seriesNumber) throws OHServiceException {
		return repository.findById(idFile).orElse(null);
	}

	/**
	 * Load metadata from {@link FileDicom} files stored in database for the patient.
	 * 
	 * @param patientID
	 * @return FileDicom array
	 * @throws OHServiceException 
	 */
	public FileDicom[] loadPatientFiles(int patientID) throws OHServiceException {
		List<FileDicom> dicomList = repository.findAllWhereIdGroupBySeriesInstanceUIDOrderSerDateDesc(patientID);

		FileDicom[] dicoms = new FileDicom[dicomList.size()];	
		for (int i = 0; i < dicomList.size(); i++) {
			int count = repository.countFramesInSeries(dicomList.get(i).getDicomSeriesInstanceUID(), patientID);
			dicoms[i] = dicomList.get(i);
			dicoms[i].setFrameCount(count);
		}
		return dicoms;
	}

	/**
	 * Check if {@link FileDicom} is loaded.
	 *
	 * @param dicom, the detail od dicom
	 * @return true if file exist
	 * @throws OHServiceException 
	 */
	public boolean exist(FileDicom dicom) throws OHServiceException {
		List<FileDicom> dicomList = repository.findAllWhereIdAndFileAndUid(dicom.getPatId(), dicom.getDicomSeriesNumber(), dicom.getDicomInstanceUID());
		return !dicomList.isEmpty();
	}

	/**
	 * Save the {@link FileDicom} and metadata in the database.
	 * 
	 * @param dicom
	 * @returns the persisted {@link FileDicom} object.
	 * @throws OHServiceException 
	 */
	public FileDicom saveFile(FileDicom dicom) throws OHServiceException {
		return repository.save(dicom);
	}

	/**
	 * Checks if the code is already in use.
	 *
	 * @param code - the DICOM code
	 * @return {@code true} if the code is already in use, {@code false} otherwise
	 * @throws OHServiceException 
	 */
	public boolean isCodePresent(Long code) throws OHServiceException {
		return repository.existsById(code);
	}

	/**
	 * Checks if the series number is already in use
	 *
	 * @param dicomSeriesNumber - the series number to check
	 * @return {@code true} if the code is already in use, {@code false} otherwise
	 * @throws OHServiceException
	 */
	public boolean isSeriesPresent(String dicomSeriesNumber) throws OHServiceException {
		return repository.seriesExists(dicomSeriesNumber) > 0;
	}

}
