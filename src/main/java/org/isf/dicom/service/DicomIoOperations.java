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
package org.isf.dicom.service;

import java.util.List;

import org.isf.dicom.model.FileDicom;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Manager for hibernate database communication
 * 
 * @author Pietro Castellucci
 * @version 1.0.0
 */
/*------------------------------------------
 * Dicom - IO operations for the DICOM entity
 * -----------------------------------------
 * modification history
 * ? -  Pietro Castellucci - first version 
 * 29/08/2016 - Antonio - ported to JPA
 * 
 *------------------------------------------*/
@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class DicomIoOperations 
{
	@Autowired
	private DicomIoOperationRepository repository;
	
	/**
	 * Load a list of id file for series
	 * 
	 * @param patientID, the patient id
	 * @param seriesNumber, the series number
	 * @return
	 * @throws OHServiceException 
	 */
	public Long[] getSerieDetail(
			int patientID,
			String seriesNumber) throws OHServiceException
	{
		List<FileDicom> dicomList  = repository.findAllWhereIdAndNumberByOrderNameAsc(patientID, seriesNumber);
		Long[] dicomIdArray = new Long[dicomList.size()];	
		
		
		for (int i=0; i<dicomList.size(); i++)
		{
			dicomIdArray[i] = dicomList.get(i).getIdFile();
		}
		
		return dicomIdArray;
	}

	/**
	 * Delete series from DB
	 * 
	 * @param patientID, the id of patient
	 * @param seriesNumber, the series number to delete
	 * @return true if success
	 * @throws OHServiceException 
	 */
	public boolean deleteSerie(
			int patientID,
			String seriesNumber) throws OHServiceException
	{
		boolean result = true;
        

		repository.deleteByIdAndNumber(patientID, seriesNumber);
				
        return result;
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
	public FileDicom loadDetails(
			Long idFile, 
			int patientID,
			String seriesNumber) throws OHServiceException
	{
		if (idFile == null)
			return null;
		else
			return loadDetails(idFile.longValue(), patientID, seriesNumber);
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
	public FileDicom loadDetails(
			long idFile, 
			int patientID,
			String seriesNumber) throws OHServiceException
	{
		FileDicom dicom = repository.findOne(idFile);
				
		return dicom;
	}

	/**
	 * Load metadata from DICOM files stored in database fot the patient
	 * 
	 * @param patientID
	 * @return FileDicom array
	 * @throws OHServiceException 
	 */
	public FileDicom[] loadPatientFiles(
			int patientID) throws OHServiceException
	{
		List<FileDicom> dicomList = repository.findAllWhereIdGroupBySeriesInstanceUIDOrderSerDateDesc(patientID);

		FileDicom[] dicoms = new FileDicom[dicomList.size()];	
		for (int i=0; i<dicomList.size(); i++)
		{
			int count = repository.countFramesinSeries(dicomList.get(i).getDicomSeriesInstanceUID());
			dicoms[i] = dicomList.get(i);
			dicoms[i].setFrameCount(count);
		}
		
		return dicoms;
	}

	/**
	 * Check if dicom is loaded
	 *
	 * @param dicom, the detail od dicom
	 * @return true if file exist
	 * @throws OHServiceException 
	 */
	public boolean exist(
			FileDicom dicom) throws OHServiceException 
	{
		List<FileDicom> dicomList = repository.findAllWhereIdAndFileAndUid(dicom.getPatId(), dicom.getDicomSeriesNumber(), dicom.getDicomInstanceUID());
	
		return (dicomList.size() > 0);
	}

	/**
	 * Save the DICOM file and metadata in the database
	 * 
	 * @param dicom
	 * @throws OHServiceException 
	 */
	public void saveFile(
			FileDicom dicom) throws OHServiceException 
	{
		repository.save(dicom);
		
		return;
	}

	/**
	 * Checks if the code is already in use
	 *
	 * @param code - the DICOM code
	 * @return <code>true</code> if the code is already in use, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean isCodePresent(
			Long code) throws OHServiceException
	{
		boolean result = true;
	
		
		result = repository.exists(code);
		
		return result;	
	}

	/**
	 * Checks if the series number is already in use
	 *
	 * @param dicomSeriesNumber - the series number to check
	 * @return <code>true</code> if the code is already in use, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	public boolean isSeriePresent(String dicomSeriesNumber) throws OHServiceException
	{
		List<FileDicom> result;


		result = repository.findAllWhereDicomSeriesNumber(dicomSeriesNumber);

		return !result.isEmpty();
	}
}
