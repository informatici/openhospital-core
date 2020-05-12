package org.isf.dicom.manager;

import org.isf.dicom.model.FileDicom;
import org.isf.dicom.service.DicomIoOperations;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Interface for definitions IO for Dicom acquired files
 * @author Pietro Castellucci
 * @version 1.0.0 
 */
@Component
public class SqlDicomManager implements DicomManagerInterface{   
	
	@Autowired
	private DicomIoOperations ioOperations;
	/**
	 * Constructor
	 */
	public SqlDicomManager() {
	}
	
    /**
     * Load a list of id file for series
     * @param patientID, the patient id
     * @param seriesNumber, the series number
     * @return
     * @throws OHServiceException 
     */
    public Long[] getSerieDetail(int patientID, String seriesNumber) throws OHServiceException
    {
        return ioOperations.getSerieDetail(patientID, seriesNumber);
    }

    /**
     * delete series 
     * @param patientID, the id of patient
     * @param seriesNumber, the series number to delete
     * @return true if success
     * @throws OHServiceException 
     */
    public boolean deleteSerie(int patientID, String seriesNumber) throws OHServiceException 
    {
    	return ioOperations.deleteSerie(patientID, seriesNumber);
    }
    
    /**
    * check if dicom is loaded
    * @param dicom - the detail of the dicom
    * @return true if file exist
     * @throws OHServiceException 
    */
    public boolean exist(FileDicom dicom) throws OHServiceException
    {
    	return ioOperations.exist(dicom);
    }
    
    /**
     * check if series number does already exist
     * @param patientID, the id of patient
     * @param seriesNumber, 
     * @return true if file exist
      * @throws OHServiceException 
     */
     public boolean exist(int patientID, String seriesNumber) throws OHServiceException
     {
     	return ioOperations.isSeriePresent(seriesNumber);
     }

    /**
     * load the Detail of DICOM
     * @param idFile
     * @param patientID
     * @param seriesNumber
     * @return FileDicom
     * @throws OHServiceException 
     */
    public FileDicom loadDetails(Long idFile,int patientID, String seriesNumber) throws OHServiceException
    {
    	return  ioOperations.loadDetails(idFile, patientID, seriesNumber);
    }
    
    /**
     * Load detail
     * @param idFile
     * @param patientID
     * @param seriesNumber
     * @return FileDicom
     * @throws OHServiceException 
     */
    public FileDicom loadDetails(long idFile,int patientID, String seriesNumber) throws OHServiceException
    {
    	return  ioOperations.loadDetails(idFile, patientID, seriesNumber);
    }

    /**
     * load metadata from DICOM files of the patient
     * @param patientID
     * @return
     * @throws OHServiceException 
     */
    public FileDicom[] loadPatientFiles(int patientID) throws OHServiceException
    {
    	return  ioOperations.loadPatientFiles(patientID);
    }

    /**
     * save the DICOM file and metadata
     * @param dicom
     * @throws OHServiceException 
     */
    public void saveFile(FileDicom dicom) throws OHServiceException
    {
			ioOperations.saveFile(dicom);
    }

}
