package org.isf.hospital.manager;

import org.isf.hospital.model.Hospital;
import org.isf.hospital.service.HospitalIoOperations;
import org.isf.utils.exception.OHServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Class that provides gui separation from database operations and gives some
 * useful logic manipulations of the dinamic data (memory)
 * 
 * @author bob
 * 
 */
@Component
public class HospitalBrowsingManager {
	
	private final Logger logger = LoggerFactory.getLogger(HospitalBrowsingManager.class);
	
	@Autowired
	private HospitalIoOperations ioOperations;

	/**
	 * Reads from database hospital informations
	 * 
	 * @return {@link Hospital} object
	 * @throws OHServiceException 
	 */
	public Hospital getHospital() throws OHServiceException {
		return ioOperations.getHospital();
	}
	/**
	 * Reads from database currency cod
	 * @return currency cod
	 * @throws OHServiceException 
	 */
	public String getHospitalCurrencyCod() throws OHServiceException {
		return ioOperations.getHospitalCurrencyCod();
	}

	/**
	 * updates hospital informations
	 * 
	 * @return <code>true</code> if the hospital informations have been updated, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean updateHospital(Hospital hospital) throws OHServiceException {
		return ioOperations.updateHospital(hospital);
    }	
}
	
