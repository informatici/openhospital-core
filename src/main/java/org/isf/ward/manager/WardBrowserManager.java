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
package org.isf.ward.manager;

import java.util.ArrayList;
import java.util.List;

import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHOperationNotAllowedException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.isf.utils.validator.EmailValidator;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperations;
import org.springframework.stereotype.Component;

/**
 * Class that provides gui separation from database operations and gives some
 * useful logic manipulations of the dinamic data (memory)
 *
 * @author Rick
 */
@Component
public class WardBrowserManager {

	private AdmissionBrowserManager admManager;
	
	private WardIoOperations ioOperations;

	public WardBrowserManager(AdmissionBrowserManager admissionBrowserManager, WardIoOperations wardIoOperations) {
		this.admManager = admissionBrowserManager;
		this.ioOperations = wardIoOperations;
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any.
	 * @param ward the {@link Ward} object to validate.
	 * @param insert {@code true} or updated {@code false}
	 * @throws OHServiceException 
	 */
	protected void validateWard(Ward ward, boolean insert) throws OHServiceException {
		String key = ward.getCode();
		String description = ward.getDescription();
		List<OHExceptionMessage> errors = new ArrayList<>();
		if (key.isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertacode.msg")));
		}
		if (key.equals("OPD") && !ward.isOpd()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.ward.opdwardmusthaveopdservicechecked.msg")));
		}
		if (key.length() > 3) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.thecodeistoolongmax1char.msg")));
		}
		if (description.isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertavaliddescription.msg")));
		}
		if (ward.getBeds() < 0) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.ward.thenumberofbedsmustbepositive.msg")));
		}
		if (ward.getNurs() < 0) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.ward.thenumberofnursesmustbepositive.msg")));
		}
		if (ward.getDocs() < 0) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.ward.thenumberofdoctorsmustbepositive.msg")));
		}
		if (!EmailValidator.isValid(ward.getEmail())) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.ward.theemailmustbevalid.msg")));
		}
		if (insert && isCodePresent(ward.getCode())) {
			throw new OHDataIntegrityViolationException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.thecodeisalreadyinuse.msg")));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}
	
	/**
	 * Returns all stored {@link Ward}s.
	 * In case of error a message error is shown and a {@code null} value is returned.
	 * @return the stored wards.
	 * @throws OHServiceException 
	 */
	public List<Ward> getWards() throws OHServiceException {
		return ioOperations.getWards(null);
	}
	
	/**
	 * Retrieve all stored {@link Ward}s with beds > {@code 0}
	 * @return the list of wards
	 */
	public List<Ward> getIpdWards() {
		return ioOperations.getIpdWards();
	}
	
	/**
	 * Retrieve all stored {@link Ward}s with isOpd = {@code true}
	 * @return the list of wards
	 */
	public List<Ward> getOpdWards() {
		return ioOperations.getOpdWards();
	}

	//TODO: remove this method, findWard(String code) should be enough
	public List<Ward> getWards(Ward ward) throws OHServiceException {
		return ioOperations.getWards(ward.getCode());
	}
	/**
	 * Returns all the stored {@link Ward}s with maternity flag equal to {@code false}.
	 * In case of error a message error is shown and a {@code null} value is returned.
	 * @return the stored {@link Ward}s with maternity flag false.
	 * @throws OHServiceException 
	 */
	public List<Ward> getWardsNoMaternity() throws OHServiceException {
		return ioOperations.getWardsNoMaternity();
	}

	/**
	 * Stores the specified {@link Ward}. 
	 * @param ward the ward to store.
	 * @return the {@link Ward} object that has been stored.
	 * @throws OHServiceException 
	 */
	public Ward newWard(Ward ward) throws OHServiceException {
		validateWard(ward, true);
		return ioOperations.newWard(ward);
	}

	/**
	 * Updates the specified {@link Ward}.
	 * @param ward the {@link Ward} to update.
	 * @return the {@link Ward} object that was updated.
	 * @throws OHServiceException 
	 */
	public Ward updateWard(Ward ward) throws OHServiceException {
		validateWard(ward, false);
		return ioOperations.updateWard(ward);
	}

	/**
	 * Mark as deleted the specified {@link Ward}.
	 * @param ward the ward to mark as deleted.
	 * @throws OHServiceException
	 */
	public void deleteWard(Ward ward) throws OHServiceException {
		if (ward.getCode().equals("M")) {
			throw new OHOperationNotAllowedException(new OHExceptionMessage(MessageBundle.getMessage("angal.ward.cannotdeletematernityward.msg")));
		}
		if (ward.getCode().equals("OPD")) {
			throw new OHOperationNotAllowedException(new OHExceptionMessage(MessageBundle.getMessage("angal.ward.cannotdeleteopdward.msg")));
		}
		int noPatients = admManager.getUsedWardBed(ward.getCode());

		if (noPatients > 0) {
			List<OHExceptionMessage> messages = new ArrayList<>(2);
			messages.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.info.title"),
			                                    MessageBundle.formatMessage("angal.ward.theselectedwardhaspatients.fmt.msg", noPatients),
			                                    OHSeverityLevel.INFO));
			messages.add(new OHExceptionMessage(MessageBundle.getMessage("angal.ward.pleasecheckinadmissionpatients.msg")));
			throw new OHOperationNotAllowedException(messages);
		}
		ioOperations.deleteWard(ward);
	}
	
	/**
	 * Check if the specified code is used by another {@link Ward}.
	 * In case of error a message error is shown and a {@code false} value is returned.
	 * @param code the code to check.
	 * @return {@code true} if it is already used, {@code false} otherwise.
	 * @throws OHServiceException 
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return ioOperations.isCodePresent(code);
	}
	
	/**
	 * Create default Maternity {@link Ward} as follows:
	 * {'code' : "M",
	 * 'Description' : MessageBundle.getMessage("angal.ward.maternity.txt"),
	 * 'Telephone' : "234/52544",
	 * 'Fax' : "54324/5424",
	 * 'Mail' :  "maternity@stluke.org",
	 * 'Beds' : 20,
	 * 'Nurses' : 3,
	 * 'Doctors' : 2,
	 * 'isOpd' : false,
	 * 'isPharmacy' : false,
	 * 'isMale' : false,
	 * 'isFemale' : true,
	 * 'LOCK (version)' : 0,
	 * }
	 * 
	 * @return maternity ward
	 */
	private Ward getDefaultMaternityWard() {
		return new Ward(
				"M",
				MessageBundle.getMessage("angal.ward.maternity.txt").toUpperCase(),
				"234/52544", //Telephone
				"54324/5424", //Fax
				"maternity@stluke.org",
				20, //Beds
				3, //Nurses
				2, //Doctors
				false, //isOpd
				false, //isPharmacy
				false, //isMale
				true); //isFemale
	}
	
	/**
	 * Create default OPD {@link Ward} as follows:
	 * {'code' : "M",
	 * 'Description' : MessageBundle.getMessage("angal.ward.maternity.txt"),
	 * 'Telephone' : "235/52544",
	 * 'Fax' : "54325/5424",
	 * 'Mail' :  "opd@stluke.org",
	 * 'Beds' : 0,
	 * 'Nurses' : 1,
	 * 'Doctors' : 1,
	 * 'isOpd' : true,
	 * 'isPharmacy' : false,
	 * 'isMale' : true,
	 * 'isFemale' : true,
	 * 'LOCK (version)' : 0,
	 * }
	 * 
	 * @return OPD ward
	 */
	private Ward getDefaultOPDWard() {
		return new Ward(
				"OPD",
				MessageBundle.getMessage("angal.ward.opd.txt").toUpperCase(),
				"235/52544", //Telephone
				"54325/5424", //Fax
				"opd@stluke.org",
				0, //Beds
				1, //Nurses
				1, //Doctors
				true, //isOpd
				false, //isPharmacy
				true, //isMale
				true); //isFemale
	}
	
	/**
	 * Check if the Maternity {@link Ward} with code "M" exists or not.
	 * @param createIfNotExists - if {@code true} it will create the missing {@link Ward} (with default values)
	 * 	and will return {@link true} 
	 * @return {@code true} if the Maternity {@link Ward} exists, {@code false} otherwise.
	 * @throws OHServiceException 
	 */
	public boolean maternityControl(boolean createIfNotExists) throws OHServiceException {
		boolean exists = ioOperations.isMaternityPresent();
		if (!exists && createIfNotExists) {
			Ward maternity = getDefaultMaternityWard();
			newWard(maternity);
			return true;
		} else {
			return exists;
		}
	}
	
	/**
	 * Check if the OPD {@link Ward} with code "OPD" exists or not.
	 * @param createIfNotExists - if {@code true} it will create the missing {@link Ward} (with default values)
	 * 	and will return {@link true} 
	 * @return {@code true} if the OPD {@link Ward} exists, {@code false} otherwise.
	 * @throws OHServiceException 
	 */
	public boolean opdControl(boolean createIfNotExists) throws OHServiceException {
		boolean exists = ioOperations.isOpdPresent();
		if (!exists && createIfNotExists) {
			Ward opd = getDefaultOPDWard();
			newWard(opd);
			return true;
		} else {
			return exists;
		}
	}
	
	/**
	 * Retrieves the number of patients currently admitted in the {@link Ward}.
	 * @param ward - the ward
	 * @return the number of patients currently admitted, {@code -1} if an error occurs
	 * @throws OHServiceException 
	 */
	public int getCurrentOccupation(Ward ward) throws OHServiceException {
		return ioOperations.getCurrentOccupation(ward);
	}

	/**
	 * Returns the {@link Ward} based on ward code,
	 *
	 * @param code - the {@link Ward} code.
	 * @return the {@link Ward} or {@code null} if not found
	 */
	public Ward findWard(String code) throws OHServiceException {
		return ioOperations.findWard(code);
	}

}
