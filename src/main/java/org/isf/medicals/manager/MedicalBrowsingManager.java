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
package org.isf.medicals.manager;

import java.util.ArrayList;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.medicals.model.Medical;
import org.isf.medicals.service.MedicalsIoOperations;
import org.isf.medtype.model.MedicalType;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

/**
 * Class that provides gui separation from database operations and gives some
 * useful logic manipulations of the dynamic data (memory)
 *
 * @author bob
 * 19-dec-2005
 * 14-jan-2006
 */
@Component
public class MedicalBrowsingManager {

	private MedicalsIoOperations ioOperations;

	public MedicalBrowsingManager(MedicalsIoOperations medicalsIoOperations) {
		this.ioOperations = medicalsIoOperations;
	}

	/**
	 * Returns the requested medical.
	 *
	 * @param code the medical code.
	 * @return the retrieved medical.
	 * @throws OHServiceException
	 */
	public Medical getMedical(int code) throws OHServiceException {
		return ioOperations.getMedical(code);
	}
	
	/**
	 * Returns the requested medical.
	 *
	 * @param prod_code the medical prod_code.
	 * @return the retrieved medical.
	 * @throws OHServiceException
	 */
	public Medical getMedicalByMedicalCode(String prod_code) throws OHServiceException {
		return ioOperations.getMedicalByMedicalCode(prod_code);
	}

	/**
	 * Returns all the medicals.
	 *
	 * @return all the medicals.
	 * @throws OHServiceException
	 */
	public List<Medical> getMedicals() throws OHServiceException {
		return ioOperations.getMedicals(null, false);
	}
	
	/**
	 * Returns the medicals pageable.
	 *
	 * @param page - the page number.
	 * @param size - the page size.
	 * @return the list of {@link Medical}s pageable. It could be {@code empty}.
	 * @throws OHServiceException
	 */
	public Page<Medical> getMedicalsPageable(int page, int size) throws OHServiceException {
		return ioOperations.getMedicalsPageable(page, size);
	}

	/**
	 * Returns all the medicals sorted by Name.
	 *
	 * @return all the medicals.
	 */
	public List<Medical> getMedicalsSortedByName() throws OHServiceException {
		return ioOperations.getMedicals(null, true);
	}

	/**
	 * Returns all the medicals sorted by code.
	 *
	 * @return all the medicals.
	 */
	public List<Medical> getMedicalsSortedByCode() throws OHServiceException {
		return ioOperations.getMedicals(null, false);
	}

	/**
	 * Returns all the medicals with the specified description.
	 *
	 * @param description the medical description.
	 * @return all the medicals with the specified description.
	 * @throws OHServiceException
	 */
	public List<Medical> getMedicals(String description) throws OHServiceException {
		return ioOperations.getMedicals(description, false);
	}

	/**
	 * Returns all the medicals with the specified description.
	 *
	 * @param type the medical type description.
	 * @return all the medicals with the specified description.
	 * @param nameSorted if {@code true} return the list in alphabetical order, by code otherwise
	 */
	public List<Medical> getMedicals(String type, boolean nameSorted) throws OHServiceException {
		return ioOperations.getMedicals(type, nameSorted);
	}

	/**
	 * Return all the medicals with the specified criteria.
	 *
	 * @param description the medical description or {@code null}
	 * @param type the medical type or {@code null}.
	 * @param critical {@code true} to include only medicals under critical level.
	 * @return the retrieved medicals.
	 * @throws OHServiceException
	 */
	public List<Medical> getMedicals(String description, String type, boolean critical) throws OHServiceException {
		return ioOperations.getMedicals(description, type, critical);
	}

	/**
	 * Saves the specified {@link Medical}. The medical is updated with the generated id.
	 *
	 * @param medical - the medical to store.
	 * @return {@code true} if the medical has been stored, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	public Medical newMedical(Medical medical) throws OHServiceException {
		return newMedical(medical, false);
	}

	/**
	 * Saves the specified {@link Medical}. The medical is updated with the generated id.
	 *
	 * @param medical - the medical to store.
	 * @param ignoreSimilar - if {@code true}, it ignore the warning "similarsFoundWarning".
	 * @return {@code true} if the medical has been stored, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	public Medical newMedical(Medical medical, boolean ignoreSimilar) throws OHServiceException {
		checkMedicalForInsert(medical, ignoreSimilar);
		return ioOperations.newMedical(medical);
	}

	/**
	 * Updates the specified medical.
	 *
	 * @param medical - the medical to update.
	 * @return {@code Medical}
	 * @throws OHServiceException
	 */
	public Medical updateMedical(Medical medical) throws OHServiceException {
		return updateMedical(medical, false);
	}

	/**
	 * Updates the specified medical.
	 *
	 * @param medical - the medical to update.
	 * @param ignoreSimilar - if {@code true}, it ignore the warning "similarsFoundWarning".
	 * @return {@code Medical}
	 * @throws OHServiceException
	 */
	public Medical updateMedical(Medical medical, boolean ignoreSimilar) throws OHServiceException {
		checkMedicalForUpdate(medical, ignoreSimilar);
		return ioOperations.updateMedical(medical);
	}

	/**
	 * Deletes the specified medical.
	 *
	 * @param medical the medical to delete.
	 * @throws OHServiceException
	 */
	public void deleteMedical(Medical medical) throws OHServiceException {
		boolean inStockMovement = ioOperations.isMedicalReferencedInStockMovement(medical.getCode());
		if (inStockMovement) {
			throw new OHDataIntegrityViolationException(
					new OHExceptionMessage(MessageBundle.getMessage("angal.medicals.therearestockmovementsreferredtothismedical.msg")));
		}
		ioOperations.deleteMedical(medical);
	}

	/**
	 * Common checks to validate a {@link Medical} for insert or update.
	 *
	 * @param medical - the {@link Medical} to insert or update
	 * @return list of {@link OHExceptionMessage}
	 */
	private List<OHExceptionMessage> checkMedicalCommon(Medical medical) {
		List<OHExceptionMessage> errors = new ArrayList<>();
		if (medical.getMinqty() < 0) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.medicals.minquantitycannotbelessthan0.msg")));
		}
		if (medical.getPcsperpck() < 0) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.medicals.insertavalidpackaging.msg")));
		}
		if (medical.getDescription().equalsIgnoreCase("")) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertavaliddescription.msg")));
		}
		return errors;
	}

	/**
	 * Perform several checks on the provided medical, useful for insert
	 *
	 * @param medical - the {@link Medical} to check
	 * @param ignoreSimilar - if {@code true}, it will not perform a similarity check.
	 * {@code warning}: same Medical description in the same {@link MedicalType} category is not allowed anyway
	 * @throws OHServiceException
	 */
	private void checkMedicalForInsert(Medical medical, boolean ignoreSimilar) throws OHServiceException {
		checkMedical(medical, ignoreSimilar, false);
	}

	/**
	 * Perform several checks on the provided medical, useful for update
	 *
	 * @param medical - the {@link Medical} to check
	 * @param ignoreSimilar - if {@code true}, it will not perform a similarity check.
	 * {@code warning}: same Medical description in the same {@link MedicalType} category is not allowed anyway
	 * @throws OHServiceException
	 */
	public void checkMedicalForUpdate(Medical medical, boolean ignoreSimilar) throws OHServiceException {
		checkMedical(medical, ignoreSimilar, true);
	}

	/**
	 * Perform several checks on the provided medical, useful for update
	 *
	 * @param medical - the {@link Medical} to check
	 * @param ignoreSimilar - if {@code true}, it will not perform a similarity check.
	 * {@code warning}: same Medical description in the same {@link MedicalType} category is not allowed anyway
	 * @param update - if {@code true}, it will not consider the actual {@link Medical}
	 * @throws OHServiceException
	 */
	public void checkMedical(Medical medical, boolean ignoreSimilar, boolean update) throws OHServiceException {

		//check commons
		List<OHExceptionMessage> errors = new ArrayList<>(checkMedicalCommon(medical));

		//check existing data
		boolean productCodeExists = !medical.getProdCode().isEmpty() && ioOperations.productCodeExists(medical, update);
		boolean medicalExists = ioOperations.medicalExists(medical, update);
		List<Medical> similarMedicals = ioOperations.medicalCheck(medical, update);

		if (productCodeExists) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.thecodeisalreadyinuse.msg")));
		} else if (medicalExists) {
			errors.add(new OHExceptionMessage(
					MessageBundle.formatMessage("angal.medicals.thepairtypemedicalalreadyexists.fmt.msg", medical.getType().getDescription(),
					                            medical.toString())));
		} else if (!ignoreSimilar && !similarMedicals.isEmpty()) {
			StringBuilder message = new StringBuilder(MessageBundle.getMessage("angal.medicals.theinsertedmedicalisalreadyinuse.msg")).append('\n');
			for (Medical med : similarMedicals) {
				message.append('[').append(med.getType().getDescription()).append("] ");
				if (!med.getProdCode().isEmpty()) {
					message.append('[').append(med.getProdCode()).append("] ");
				}
				message.append(med).append('\n');
			}
			errors.add(new OHExceptionMessage(message.toString()));
		}

		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}
}
