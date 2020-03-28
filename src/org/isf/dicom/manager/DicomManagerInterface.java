package org.isf.dicom.manager;

import org.isf.dicom.model.FileDicom;
import org.isf.utils.exception.OHServiceException;

/**
 * Interface for definitions IO for Dicom acquired files
 * @author Pietro Castellucci
 * @version 1.0.0 
 */
public interface DicomManagerInterface 
{   
    /**
     * Load a list of idfile for series
     * @param patientID, the patient id
     * @param seriesNumber, the series number
     * @return
     * @throws OHServiceException 
     */
    public Long[] getSerieDetail(int patientID, String seriesNumber) throws OHServiceException;

    /**
     * Delete series 
     * @param patientID, the id of patient
     * @param seriesNumber, the series number to delete
     * @return true if success
     * @throws OHServiceException 
     */
    boolean deleteSerie(int patientID, String seriesNumber) throws OHServiceException ;
    
    /**
    * Check if dicom is loaded
    * @param dicom, the detail of dicom
    * @return true if file exist
     * @throws OHServiceException 
    */
    public boolean exist(FileDicom dicom) throws OHServiceException;
    
    /**
     * Check if dicom is loaded
     * @param patientID, the id of patient
     * @param seriesNumber, the series number
     * @return true if file exist
      * @throws OHServiceException 
     */
     public boolean exist(int patientID, String seriesNumber) throws OHServiceException;

    /**
     * Load the Detail of DICOM
     * @param idFile
     * @param patientID
     * @param seriesNumber
     * @return FileDicom
     * @throws OHServiceException 
     */
    public FileDicom loadDetails(Long idFile, int patientID, String seriesNumber) throws OHServiceException;

    /**
     * Load detail
     * @param idFile
     * @param patientID
     * @param seriesNumber
     * @return FileDicom
     * @throws OHServiceException 
     */
    public FileDicom loadDetails(long idFile, int patientID, String seriesNumber) throws OHServiceException;

    /**
     * load metadata from DICOM files of the patient
     * @param patientID
     * @return
     * @throws OHServiceException 
     */
    public FileDicom[] loadPatientFiles(int patientID) throws OHServiceException;

    /**
     * save the DICOM file and metadata
     * @param dicom
     * @throws OHServiceException 
     */
    public void saveFile(FileDicom dicom) throws OHServiceException;
}
