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
package org.isf.medicalstock.manager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.medicals.model.Medical;
import org.isf.medicals.service.MedicalsIoOperations;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.model.Movement;
import org.isf.medicalstock.service.MedicalStockIoOperations;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.time.TimeTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MovStockInsertingManager {

	@Autowired
	private MedicalStockIoOperations ioOperations;
	@Autowired
	private MedicalsIoOperations ioOperationsMedicals;

	public MovStockInsertingManager() {
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 *
	 * @param movement - the movement to validate
	 * @param checkReference - if {@code true} it will use {@link #checkReferenceNumber(String) checkReferenceNumber}
	 * @throws OHServiceException
	 */
	protected void validateMovement(Movement movement, boolean checkReference) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();

		// Check the Date
		LocalDateTime today = TimeTools.getNow();
		LocalDateTime movDate = movement.getDate();
		LocalDateTime lastDate = getLastMovementDate();
		if (movDate.isAfter(today)) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.medicalstock.multiplecharging.adateinthefutureisnotallowed.msg")));
		}
		if (lastDate != null && movDate.isBefore(lastDate)) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.medicalstock.multiplecharging.datecannotbebeforelastmovementdate.msg")));
		}

		// Check the RefNo
		if (checkReference) {
			String refNo = movement.getRefNo();
			errors.addAll(checkReferenceNumber(refNo));
		}

		// Check Movement Type
		boolean chargingType = false;
		if (movement.getType() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.medicalstock.pleasechooseatype.msg")));
		} else {
			chargingType = movement.getType().getType().contains("+"); //else discharging

			// Check supplier
			if (chargingType) {
				Object supplier = movement.getSupplier();
				if (null == supplier) {
					errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.medicalstock.multiplecharging.pleaseselectasupplier.msg")));
				}
			} else {
				Object ward = movement.getWard();
				if (null == ward) {
					errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.medicalstock.multipledischarging.pleaseselectaward.msg")));
				}
			}
		}

		// Check quantity
		if (movement.getQuantity() == 0) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.medicalstock.thequantitymustnotbezero.msg")));
		}

		// Check Medical
		if (movement.getMedical() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.medicalstock.chooseamedical.msg")));
		}

		// Check Lot
		if (!isAutomaticLotOut()) {
			Lot lot = movement.getLot();
			if (lot != null) {

				if (lot.getCode().length() >= 50) {
					errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.medicalstock.thelotidistoolongmax50chars.msg")));
				}

				if (lot.getDueDate() == null) {
					errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.medicalstock.insertavalidduedate.msg")));
				}

				if (lot.getPreparationDate() == null) {
					errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.medicalstock.insertavalidpreparationdate.msg")));
				}

				if (lot.getPreparationDate() != null && lot.getDueDate() != null && lot.getPreparationDate().isAfter(lot.getDueDate())) {
					errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.medicalstock.thepreparationdatecannotbyaftertheduedate.msg")));
				}
			}

			if (movement.getType() != null && !chargingType && movement.getQuantity() > lot.getMainStoreQuantity()) {
				errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.medicalstock.movementquantityisgreaterthanthequantityof.msg")));
			}

			List<Integer> medicalIds = ioOperations.getMedicalsFromLot(lot.getCode());
			if (movement.getMedical() != null && !(medicalIds.isEmpty() || medicalIds.size() == 1 && medicalIds.get(0).intValue() == movement
					.getMedical().getCode().intValue())) {
				errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.medicalstock.thislotreferstoanothermedical.msg")));
			}
			if (GeneralData.LOTWITHCOST && chargingType) {
				BigDecimal cost = lot.getCost();
				if (cost == null || cost.doubleValue() <= 0.0) {
					errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.medicalstock.multiplecharging.zerocostsarenotallowed.msg")));
				}
			}
		}

		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	/**
	 * Verify if the referenceNumber is valid for CRUD and return a list of errors, if any
	 *
	 * @param referenceNumber - the lot to validate
	 * @return list of {@link OHExceptionMessage}s
	 * @throws OHServiceException
	 */
	protected List<OHExceptionMessage> checkReferenceNumber(String referenceNumber) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();
		if (referenceNumber == null || referenceNumber.isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.medicalstock.multiplecharging.pleaseinsertareferencenumber.msg")));
		} else {
			if (refNoExists(referenceNumber)) {
				errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.medicalstock.multiplecharging.theinsertedreferencenumberalreadyexists.msg")));
			}
		}
		return errors;
	}

	private boolean isAutomaticLotOut() {
		return GeneralData.AUTOMATICLOT_OUT;
	}

	/**
	 * Retrieves all the {@link Lot} associated to the specified {@link Medical}, expiring first on top
	 *
	 * @param medical the medical.
	 * @return the list of retrieved {@link Lot}s.
	 * @throws OHServiceException
	 */
	public List<Lot> getLotByMedical(Medical medical) throws OHServiceException {
		if (medical == null) {
			return new ArrayList<>();
		}
		return ioOperations.getLotsByMedical(medical);
	}

	/**
	 * Checks if the provided quantity is under the medical limits.
	 *
	 * @param medicalSelected the selected medical.
	 * @param specifiedQuantity the quantity provided by the user.
	 * @return {@code true} if is under the limit, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	public boolean alertCriticalQuantity(Medical medicalSelected, int specifiedQuantity) throws OHServiceException {
		Medical medical = ioOperationsMedicals.getMedical(medicalSelected.getCode());
		double totalQuantity = medical.getTotalQuantity();
		double residual = totalQuantity - specifiedQuantity;
		return residual < medical.getMinqty();
	}

	/**
	 * Returns the date of the last movement.
	 *
	 * @return
	 * @throws OHServiceException
	 */
	public LocalDateTime getLastMovementDate() throws OHServiceException {
		return ioOperations.getLastMovementDate();
	}

	/**
	 * Check if the reference number is already used.
	 *
	 * @return {@code true} if is already used,{@code false} otherwise.
	 * @throws OHServiceException
	 */
	public boolean refNoExists(String refNo) throws OHServiceException {
		return ioOperations.refNoExists(refNo);
	}

	/**
	 * Insert a list of charging {@link Movement}s and related {@link Lot}s
	 *
	 * @param movements - the list of {@link Movement}s
	 * @param referenceNumber - the reference number to be set for all movements
	 * if {@link null}, each movements must have a different referenceNumber
	 * @return a list of inserted {@link Movement}s.
	 * @throws OHServiceException
	 */
	@Transactional(rollbackFor = OHServiceException.class)
	@TranslateOHServiceException
	public List<Movement> newMultipleChargingMovements(List<Movement> movements, String referenceNumber) throws OHServiceException {

		boolean checkReference = referenceNumber == null; // referenceNumber == null, each movement should have referenceNumber set
		if (!checkReference) {
			// referenceNumber != null, all movement will have same referenceNumber, we check only once for all
			List<OHExceptionMessage> errors = checkReferenceNumber(referenceNumber);
			if (!errors.isEmpty()) {
				throw new OHDataValidationException(errors);
			}
		}
		List<Movement> insertedMovements = new ArrayList<>();
		for (Movement mov : movements) {
			try {
				insertedMovements.add(prepareChargingMovement(mov, checkReference));
			} catch (OHServiceException e) {
				List<OHExceptionMessage> errors = e.getMessages();
				errors.add(new OHExceptionMessage(
						mov.getMedical() != null ? mov.getMedical().getDescription() : MessageBundle.getMessage("angal.medicalstock.nodescription.txt")));
				throw new OHDataValidationException(errors);
			}
		}
		return insertedMovements;
	}

	/**
	 * Prepare the insert of the specified charging {@link Movement}.
	 *
	 * @param movement - the movement to store.
	 * @param checkReference - if {@code true} every movement must have unique reference number
	 * @return the prepared {@link Movement}.
	 * @throws OHServiceException
	 */
	@Transactional(rollbackFor = OHServiceException.class)
	protected Movement prepareChargingMovement(Movement movement, boolean checkReference) throws OHServiceException {
		validateMovement(movement, checkReference);
		return ioOperations.prepareChargingMovement(movement);
	}

	/**
	 * Stores the specified {@link Lot}.
	 * @param lotCode the {@link Lot} code.
	 * @param lot the lot to store.
	 * @param medical
	 * @return the stored {@link Lot} object.
	 * @throws OHServiceException if an error occurred storing the lot.
	 */
	public Lot storeLot(String lotCode, Lot lot, Medical medical) throws OHServiceException {
		return ioOperations.storeLot(lotCode, lot, medical);
	}

	/**
	 * Insert a list of discharging {@link Movement}s
	 *
	 * @param movements - the list of {@link Movement}s
	 * @param referenceNumber - the reference number to be set for all movements
	 * if {@link null}, each movements must have a different referenceNumber
	 * @return a list of {@Link Movement}s.
	 * @throws OHServiceException
	 */
	@Transactional(rollbackFor = OHServiceException.class)
	public List<Movement> newMultipleDischargingMovements(List<Movement> movements, String referenceNumber) throws OHServiceException {

		boolean checkReference = referenceNumber == null; // referenceNumber == null, each movement should have referenceNumber set
		if (!checkReference) {
			// referenceNumber != null, all movement will have same referenceNumber, we check only once for all
			List<OHExceptionMessage> errors = checkReferenceNumber(referenceNumber);
			if (!errors.isEmpty()) {
				throw new OHDataValidationException(errors);
			}
		}
		List<Movement> dischargingMovements = new ArrayList<>();
		for (Movement mov : movements) {
			try {
				dischargingMovements.addAll(prepareDishargingMovement(mov, checkReference));
			} catch (OHServiceException e) {
				List<OHExceptionMessage> errors = e.getMessages();
				errors.add(new OHExceptionMessage(mov.getMedical().getDescription()));
				throw new OHDataValidationException(errors);
			}
		}
		return dischargingMovements;
	}

	/**
	 * Prepare the insert of the specified {@link Movement}
	 *
	 * @param movement - the movement to store.
	 * @param checkReference - if {@code true} every movement must have unique reference number
	 * @throws OHServiceException
	 */
	private List<Movement> prepareDishargingMovement(Movement movement, boolean checkReference) throws OHServiceException {
		validateMovement(movement, checkReference);
		if (isAutomaticLotOut()) {
			return ioOperations.newAutomaticDischargingMovement(movement);
		} else {
			List<Movement> dischargeMovement = new ArrayList<>();
			dischargeMovement.add(ioOperations.prepareDischargingMovement(movement));
			return dischargeMovement;
		}
	}
}
