/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
import org.isf.medicalstock.service.LotIoOperationRepository;
import org.isf.medicalstock.service.MedicalStockIoOperations;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.time.TimeTools;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MovStockInsertingManager {

	private MedicalStockIoOperations ioOperations;

	private MedicalsIoOperations ioOperationsMedicals;

	private LotIoOperationRepository ioOperationsLots;

	public MovStockInsertingManager(MedicalStockIoOperations medicalStockIoOperations, MedicalsIoOperations medicalsIoOperations,
		LotIoOperationRepository ioOperationsLots) {
		this.ioOperations = medicalStockIoOperations;
		this.ioOperationsMedicals = medicalsIoOperations;
		this.ioOperationsLots = ioOperationsLots;
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 * 
	 * TODO: verify the need of checkReference param
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
		boolean isCharge = false;
		if (movement.getType() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.medicalstock.pleasechooseatype.msg")));
		} else {
			isCharge = movement.getType().getType().contains("+"); // else discharging

			// Check supplier
			if (isCharge) {
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

		/*
		 * Check Lot: it should be not null, but with some informations.
		 * 
		 * Depending on the operation type and the modality AUTOMATICLOT_IN/AUTOMATICLOT_OUT, some properties will be checked.
		 * 
		 * - charge:
		 * 
		 * AUTOMATICLOT_IN=no, Lot(String, GregorianCalendar, GregorianCalendar) check everything
		 * 
		 * AUTOMATICLOT_IN=yes, Lot("", GregorianCalendar, GregorianCalendar) check everything but the code, it will be generated
		 * 
		 * - discharge:
		 * 
		 * AUTOMATICLOT_OUT=no, Lot(String, GregorianCalendar, GregorianCalendar) check everything
		 * 
		 * AUTOMATICLOT_OUT=yes, Lot("", null, null) no checks, the lot will be automatically selected
		 * 
		 */
		Lot lot = movement.getLot();
		if (lot != null) {

			if (isCharge && !isAutomaticLotIn() || !isCharge && !isAutomaticLotOut()) {
				// check everything
				validateLot(errors, lot, true);
			}

			if (isCharge && isAutomaticLotIn()) {
				// check everything but the code
				validateLot(errors, lot, false);
			}

			/*
			 * Check Lot code: it must be unique per Lot-Medical
			 * 
			 */
			List<Integer> medicalIds = ioOperations.getMedicalsFromLot(lot.getCode());
			if (movement.getMedical() != null && !(medicalIds.isEmpty() || medicalIds.size() == 1 && medicalIds.get(0).intValue() == movement
				.getMedical().getCode().intValue())) {
				errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.medicalstock.thislotreferstoanothermedical.msg")));
			}

			/*
			 * Check cost.
			 * 
			 * - charge:
			 * 
			 * LOTWITHCOSTS=yes, Lot(..., cost) with cost > 0.0, otherwise error
			 * 
			 * LOTWITHCOSTS=no, no check: with or without cost, the cost will be ignored
			 * 
			 */
			if (isCharge && GeneralData.LOTWITHCOST) {
				BigDecimal cost = lot.getCost();
				if (cost == null || cost.doubleValue() <= 0.0) {
					errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.medicalstock.multiplecharging.zerocostsarenotallowed.msg")));
				}
			}

			/*
			 * Check quantity.
			 * 
			 * AUTOMATICLOT_OUT=no, specified quantity must be equal or lower than the lot quantity
			 * 
			 * AUTOMATICLOT_OUT=yes, no check: the quantity will be split automatically between available lots
			 */
			if (!isAutomaticLotOut()) {

				if (movement.getType() != null && !isCharge && movement.getQuantity() > lot.getMainStoreQuantity()) {
					errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.medicalstock.movementquantityisgreaterthanthequantityof.msg")));
				}
			}

		} else {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.medicalstock.choosealot.msg")));
		}

		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	private void validateLot(List<OHExceptionMessage> errors, Lot lot, boolean checkCode) {
		if (checkCode && lot.getCode().length() >= 50) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.medicalstock.thelotidistoolongmax50chars.msg")));
		}
		if (lot.getPreparationDate() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.medicalstock.insertavalidpreparationdate.msg")));
		}
		if (lot.getDueDate() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.medicalstock.insertavalidduedate.msg")));
		}
		if (lot.getPreparationDate() != null && lot.getDueDate() != null && lot.getPreparationDate().compareTo(lot.getDueDate()) > 0) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.medicalstock.thepreparationdatecannotbyaftertheduedate.msg")));
		}
	}

	/**
	 * Checks if the specified {@link Lot} exists.
	 * 
	 * @param lotCode the lot code.
	 * @return {@code true} if exists, {@code false} otherwise.
	 * @throws OHServiceException if an error occurs during the check.
	 */
	public boolean lotExists(String lotCode) throws OHServiceException {
		return ioOperationsLots.findById(String.valueOf(lotCode)).orElse(null) != null;
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

	private boolean isAutomaticLotIn() {
		return GeneralData.AUTOMATICLOT_IN;
	}

	private boolean isAutomaticLotOut() {
		return GeneralData.AUTOMATICLOT_OUT;
	}

	/**
	 * Retrieves all the {@link Lot} associated to the specified {@link Medical}, expiring first on top, zero quantities will be stripped out.
	 * 
	 * @param medical the medical.
	 * @return the list of retrieved {@link Lot}s.
	 * @throws OHServiceException
	 */
	public List<Lot> getLotByMedical(Medical medical) throws OHServiceException {
		return ioOperations.getLotsByMedical(medical, true);
	}

	/**
	 * Retrieves all the {@link Lot} associated to the specified {@link Medical}, expiring first on top, zero quantities will be stripped out if
	 * {@code removeEmtpy} is set to true.
	 *
	 * @param medical the medical.
	 * @param removeEmpty
	 * @return the list of retrieved {@link Lot}s.
	 * @throws OHServiceException
	 */
	public List<Lot> getLotByMedical(Medical medical, boolean removeEmpty) throws OHServiceException {
		if (medical == null) {
			return new ArrayList<>();
		}
		return ioOperations.getLotsByMedical(medical, removeEmpty);
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
	 * @param referenceNumber - the reference number to be set for all movements if {@link null}, each movements must have a different referenceNumber
	 * @return a list of inserted {@link Movement}s.
	 * @throws OHServiceException
	 */
	@Transactional(rollbackFor = OHServiceException.class)
	@TranslateOHServiceException
	public List<Movement> newMultipleChargingMovements(List<Movement> movements, String referenceNumber) throws OHServiceException {

		// TODO: verify the need of this checkReference in the whole class
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
					mov.getMedical() != null ? mov.getMedical().getDescription()
						: MessageBundle.getMessage("angal.medicalstock.nodescription.txt")));
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
	 * Retrieves the {@link Lot}.
	 * 
	 * @param lotCode - the lot code.
	 * @return the retrieved {@link Lot}.
	 * @throws OHServiceException if an error occurs during the check.
	 */
	public Lot getLot(String lotCode) throws OHServiceException {
		return ioOperations.getLot(lotCode);
	}

	/**
	 * Update the list of {@link Lot}s.
	 * 
	 * @param lots - the list of lots.
	 * @return the list of {@link Lot}s updated.
	 * @throws OHServiceException if an error occurs during the check.
	 */
	@Transactional
	public List<Lot> updateLot(List<Lot> lots) throws OHServiceException {
		List<Lot> updatedLots = new ArrayList<>();
		for (Lot lot : lots) {
			Lot updatedLot = updateLot(lot);
			updatedLots.add(updatedLot);
		}
		return updatedLots;
	}

	/**
	 * Update the {@link Lot}.
	 * 
	 * @param lot - the lot.
	 * @return the {@link Lot} updated.
	 * @throws OHServiceException if an error occurs during the check.
	 */
	public Lot updateLot(Lot lot) throws OHServiceException {
		return ioOperations.updateLot(lot);
	}

	/**
	 * Insert a list of discharging {@link Movement}s
	 *
	 * @param movements - the list of {@link Movement}s
	 * @param referenceNumber - the reference number to be set for all movements if {@link null}, each movements must have a different referenceNumber
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
	 * Stores the specified {@link Lot}.
	 * 
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

	/**
	 * Deletes the specified {@link Lot}.
	 *
	 * @param lot the lot to delete.
	 * @throws OHServiceException
	 */
	public void deleteLot(Lot lot) throws OHServiceException {
		ioOperations.deleteLot(lot);
	}

	/**
	 * Retrieves all medicals referencing the specified code.
	 * 
	 * @param code the lot code.
	 * @return the ids of medicals referencing the specified lot.
	 * @throws OHServiceException if an error occurs retrieving the referencing medicals.
	 */
	public List<Integer> getMedicalsFromLot(String code) throws OHServiceException {
		return ioOperations.getMedicalsFromLot(code);
	}
}
