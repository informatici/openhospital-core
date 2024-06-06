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
package org.isf.medicalstock.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.isf.generaldata.GeneralData;
import org.isf.medicals.model.Medical;
import org.isf.medicals.service.MedicalsIoOperationRepository;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.model.Movement;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.medicalstockward.service.MedicalStockWardIoOperationRepository;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.time.TimeTools;
import org.isf.ward.model.Ward;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persistence class for MedicalStock module.
 * 		   modified by alex:
 * 			- reflection from Medicals product code
 * 			- reflection from Medicals pieces per packet
 * 			- added complete Ward and Movement construction in getMovement()
 */
@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class MedicalStockIoOperations {

	private static final Logger LOGGER = LoggerFactory.getLogger(MedicalStockIoOperations.class);

	private MovementIoOperationRepository movRepository;

	private LotIoOperationRepository lotRepository;

	private MedicalsIoOperationRepository medicalRepository;

	private MedicalStockWardIoOperationRepository medicalStockRepository;

	public MedicalStockIoOperations(MovementIoOperationRepository movementIoOperationRepository, LotIoOperationRepository lotIoOperationRepository,
	                                MedicalsIoOperationRepository medicalsIoOperationRepository,
	                                MedicalStockWardIoOperationRepository medicalStockWardIoOperationRepository) {
		this.movRepository = movementIoOperationRepository;
		this.lotRepository = lotIoOperationRepository;
		this.medicalRepository = medicalsIoOperationRepository;
		this.medicalStockRepository = medicalStockWardIoOperationRepository;
	}

	public enum MovementOrder {
		DATE, WARD, PHARMACEUTICAL_TYPE, TYPE
	}

	/**
	 * Checks if we are in automatic lot mode.
	 * @return {@code true} if automatic lot mode, {@code false} otherwise.
	 */
	private boolean isAutomaticLotMode() {
		return GeneralData.AUTOMATICLOT_IN;
	}

	/**
	 * Retrieves all medicals referencing the specified code.
	 * @param lotCode the lot code.
	 * @return the ids of medicals referencing the specified lot.
	 * @throws OHServiceException if an error occurs retrieving the referencing medicals.
	 */
	public List<Integer> getMedicalsFromLot(String lotCode) throws OHServiceException {
		return movRepository.findAllByLot(lotCode);
	}
	
	/**
	 * Retrieves all movements referencing the specified lot.
	 * @param lot - the lot.
	 * @return the movements referencing the specified lot.
	 * @throws OHServiceException if an error occurs retrieving the referencing movement.
	 */
	public List<Movement> getMovementByLot(Lot lot) throws OHServiceException {
		return movRepository.findByLot(lot);
	}

	/**
	 * Store the specified {@link Movement} by using automatically the most old lots
	 * and splitting in more movements if required
	 * @param movement - the {@link Movement} to store
	 * @throws OHServiceException
	 */
	public List<Movement> newAutomaticDischargingMovement(Movement movement) throws OHServiceException {
		List<Movement> dischargingMovements = new ArrayList<>();
		List<Lot> lots = getLotsByMedical(movement.getMedical());
		Medical medical = movement.getMedical();
		int qty = movement.getQuantity(); // movement initial quantity

		if (lots.isEmpty()) {
			LOGGER.warn("No lots with available quantity found for medical {}", medical.getDescription());
			return dischargingMovements;
		}
		for (Lot lot : lots) {
			String lotCode = lot.getCode();
			Movement splitMovement = new Movement(medical, movement.getType(), movement.getWard(),
							null, // lot to be set
							movement.getDate(),
							qty, // quantity can remain the same or changed if greater than lot quantity
							null,
							movement.getRefNo());
			int qtLot = lot.getMainStoreQuantity();
			if (qtLot < qty) {
				splitMovement.setQuantity(qtLot);
				try {
					dischargingMovements.add(storeMovement(splitMovement, lotCode));
					// medical stock movement inserted updates quantity of the medical
					updateStockQuantity(splitMovement);
				} catch (OHServiceException serviceException) {
					throw new OHServiceException(new OHExceptionMessage(serviceException.getMessage()));
				}
				qty = qty - qtLot;
			} else {
				splitMovement.setQuantity(qty);
				try {
					dischargingMovements.add(storeMovement(splitMovement, lotCode));
					// medical stock movement inserted updates quantity of the medical
					updateStockQuantity(splitMovement);
				} catch (OHServiceException serviceException) {
					throw new OHServiceException(new OHExceptionMessage(serviceException.getMessage()));
				}
				break;
			}
		}
		return dischargingMovements;
	}

	/**
	 * Stores the specified {@link Movement}.
	 * @param movement - the movement to store.
	 * @throws OHServiceException if an error occurs during the store operation.
	 */
	public Movement newMovement(Movement movement) throws OHServiceException {
		String lotCode = null;

		if (movement.getLot() != null) {
			lotCode = movement.getLot().getCode();
		}

		// we have to manage the Lot
		if (movement.getType().getType().contains("+")) {
			// if is in automatic lot mode then we have to generate a new lot code
			if (isAutomaticLotMode() || "".equals(lotCode)) {
				lotCode = generateLotCode();
			}

			boolean lotExists = lotExists(lotCode);
			if (!lotExists) {
				storeLot(lotCode, movement.getLot(), movement.getMedical());
			}
		}

		Movement movementStored = storeMovement(movement, lotCode);
		// medical stock movement inserted updates quantity of the medical
		updateStockQuantity(movement);
		return movementStored;
	}

	/**
	 * Prepare the insert of the specified {@link Movement} (no commit)
	 * @param movement - the movement to store.
	 * @return the prepared {@link Movement}.
	 * @throws OHServiceException if an error occurs during the store operation.
	 */
	public Movement prepareChargingMovement(Movement movement) throws OHServiceException {
		return newMovement(movement);
	}

	/**
	 * Prepare the insert of the specified {@link Movement} (no commit)
	 * @param movement - the movement to store.
	 * @return {@code true} if the movement has been stored, {@code false} otherwise.
	 * @throws OHServiceException if an error occurs during the store operation.
	 */
	public Movement prepareDischargingMovement(Movement movement) throws OHServiceException {
		String lotCode = null;

		if (movement.getLot() != null) {
			lotCode = movement.getLot().getCode();
		}

		Movement movementStored = storeMovement(movement, lotCode);

		// medical stock movement inserted
		updateStockQuantity(movement);
		return movementStored;
	}

	/**
	 * Stores the specified {@link Movement}.
	 * @param movement the movement to store.
	 * @param lotCode the {@link Lot} code to use.
	 * @return returns the stored {@link Movement} object.
	 * @throws OHServiceException if an error occurs storing the movement.
	 */
	protected Movement storeMovement(Movement movement, String lotCode) throws OHServiceException {
		Lot lot = lotRepository.findById(lotCode).orElse(null);
		if (lot == null) {
			throw new OHServiceException(new OHExceptionMessage("Lot '" + lotCode + "' not found."));
		}
		movement.setLot(lot);
		return movRepository.save(movement);
	}

	/**
	 * Creates a new unique lot code.
	 * @return the new unique code.
	 * @throws OHServiceException if an error occurs during the code generation.
	 */
	protected String generateLotCode() throws OHServiceException {
		Random random = new Random();
		long candidateCode;
		Lot lot;

		do {
			candidateCode = Math.abs(random.nextLong());
			lot = lotRepository.findById(String.valueOf(candidateCode)).orElse(null);
		} while (lot != null);

		return String.valueOf(candidateCode);
	}

	/**
	 * Checks if the specified {@link Lot} exists.
	 * @param lotCode the lot code.
	 * @return {@code true} if exists, {@code false} otherwise.
	 * @throws OHServiceException if an error occurs during the check.
	 */
	public boolean lotExists(String lotCode) throws OHServiceException {
		return lotRepository.findById(String.valueOf(lotCode)).orElse(null) != null;
	}
	
	/**
	 * Retrieves the {@link Lot}.
	 * @param lotCode the lot code.
	 * @return the retrieved {@link Lot}.
	 * @throws OHServiceException if an error occurs during the check.
	 */
	public Lot getLot(String lotCode) throws OHServiceException {
		return lotRepository.findById(String.valueOf(lotCode)).orElse(null);
	}
	
	/**
	 * Update the {@link Lot}.
	 * @param lot - the lot.
	 * @return the {@link Lot} updated.
	 * @throws OHServiceException if an error occurs during the check.
	 */
	public Lot updateLot(Lot lot) throws OHServiceException {
		return lotRepository.save(lot);
	}

	/**
	 * Stores the specified {@link Lot}.
	 * @param lotCode the {@link Lot} code.
	 * @param lot the lot to store.
	 * @param medical
	 * @return the stored {@link Lot} object.
	 * @throws OHServiceException if an error occurred storing the lot.
	 */
	// TODO: verify why lotCode and medical params are needed
	public Lot storeLot(String lotCode, Lot lot, Medical medical) throws OHServiceException {
		lot.setCode(lotCode);
		lot.setMedical(medical);
		return lotRepository.save(lot);
	}

	/**
	 * Updated {@link Medical} stock quantity for the specified {@link Movement}.
	 * @param movement the movement.
	 * @return {@code true} if the quantity has been updated, {@code false} otherwise.
	 * @throws OHServiceException if an error occurs during the update.
	 */
	protected Medical updateStockQuantity(Movement movement) throws OHServiceException {
		if (movement.getType().getType().contains("+")) {
			// incoming medical stock
			Medical medical = movement.getMedical();
			return updateMedicalIncomingQuantity(medical.getCode(), movement.getQuantity());
		} else {
			// outgoing medical stock
			Medical medical = movement.getMedical();
			try {
				Medical updatedMedical = updateMedicalOutcomingQuantity(medical.getCode(), movement.getQuantity());
				Ward ward = movement.getWard();
				if (ward != null) {
					// updates stock quantity for wards
					updateMedicalWardQuantity(ward, medical, movement.getQuantity(), movement.getLot());
				}
				return updatedMedical;
			} catch (OHServiceException serviceException) {
				throw new OHServiceException(new OHExceptionMessage(serviceException.getMessage()));
			}
		}
	}

	/**
	 * Updates the incoming quantity for the specified medical.
	 * @param medicalCode the medical code.
	 * @param incrementQuantity the quantity to add.
	 * @return the updated {@link Medical} object.
	 * @throws OHServiceException if an error occurs during the update.
	 */
	protected Medical updateMedicalIncomingQuantity(int medicalCode, double incrementQuantity) throws OHServiceException {
		Medical medical = medicalRepository.findById(medicalCode).orElse(null);
		if (medical == null) {
			throw new OHServiceException(new OHExceptionMessage("Medical '" + medicalCode + "' not found."));
		}
		medical.setInqty(medical.getInqty() + incrementQuantity);
		return medicalRepository.save(medical);
	}

	/**
	 * Updates the outcoming quantity for the specified medicinal.
	 * @param medicalCode the medical code.
	 * @param incrementQuantity the quantity to add to the current outcoming quantity.
	 * @return the updated {@link Medical} object.
	 * @throws OHServiceException if an error occurs during the update.
	 */
	protected Medical updateMedicalOutcomingQuantity(int medicalCode, double incrementQuantity) throws OHServiceException {
		Medical medical = medicalRepository.findById(medicalCode).orElse(null);
		if (medical == null) {
			throw new OHServiceException(new OHExceptionMessage("Medical '" + medicalCode + "' not found."));
		}
		medical.setOutqty(medical.getOutqty() + incrementQuantity);
		return medicalRepository.save(medical);
	}

	/**
	 * Updates medical quantity for the specified ward.
	 * @param ward the ward.
	 * @param medical the medical.
	 * @param quantity the quantity to add to the current medical quantity.
	 * @return the updated {@link MedicalWard} object.
	 * @throws OHServiceException if an error occurs during the update.
	 */
	@SuppressWarnings("unchecked")
	protected MedicalWard updateMedicalWardQuantity(Ward ward, Medical medical, int quantity, Lot lot) throws OHServiceException {
		MedicalWard medicalWard = medicalStockRepository.findOneWhereCodeAndMedicalAndLot(ward.getCode(), medical.getCode(), lot.getCode());

		if (medicalWard != null) {
			medicalWard.setIn_quantity(medicalWard.getIn_quantity() + quantity);
			medicalStockRepository.save(medicalWard);
		} else {
			medicalWard = new MedicalWard(ward, medical, quantity, 0, lot);
			medicalStockRepository.insertMedicalWard(ward.getCode(), medical.getCode(), (double) quantity, lot.getCode());
		}
		return medicalStockRepository.save(medicalWard);
	}

	/**
	 * Gets all the stored {@link Movement}.
	 * @return all retrieved movement
	 * @throws OHServiceException if an error occurs retrieving the movements.
	 */
	public List<Movement> getMovements() throws OHServiceException {
		return getMovements(null, null, null);
	}

	/**
	 * Retrieves all the stored {@link Movement}s for the specified {@link Ward}.
	 * @param wardId the ward id.
	 * @param dateTo 
	 * @param dateFrom 
	 * @return the list of retrieved movements.
	 * @throws OHServiceException if an error occurs retrieving the movements.
	 */
	public List<Movement> getMovements(String wardId, LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		List<Movement> pMovement = new ArrayList<>();
		// TODO: investigate whether findMovementWhereDatesAndId() could return a List<Movement> directly
		// to remove the need to fetch the movements later in the loop below
		List<Integer> pMovementCode = movRepository.findMovementWhereDatesAndId(wardId, TimeTools.truncateToSeconds(dateFrom),
						TimeTools.truncateToSeconds(dateTo));
		for (int i = 0; i < pMovementCode.size(); i++) {
			Integer code = pMovementCode.get(i);
			Movement movement = movRepository.findById(code).orElse(null);
			if (movement == null) {
				throw new OHServiceException(new OHExceptionMessage("Movement '" + code + "' not found."));
			}
			pMovement.add(i, movement);
		}
		return pMovement;
	}

	/**
	 * Retrieves all the stored {@link Movement} with the specified criteria.
	 * @param medicalCode the {@link Medical} code (optional).
	 * @param medicalTypeCode the {@link MedicalType} code (optional).
	 * @param wardId the {@link Ward} id (optional).
	 * @param movTypeCode the {@link MovementType} code or {@code "+"}/{@code "-"} for all charge/discharge types (optional).
	 * @param movFrom the lower bound for the movement date range (optional).
	 * @param movTo the upper bound for the movement date range (optional).
	 * @param lotPrepFrom the lower bound for the lot preparation date range (optional).
	 * @param lotPrepTo the upper bound for the lot preparation date range (optional).
	 * @param lotDueFrom the lower bound for the lot due date range (optional).
	 * @param lotDueTo the lower bound for the lot due date range (optional).
	 * @return all the retrieved movements.
	 * @throws OHServiceException
	 */
	public List<Movement> getMovements(
					Integer medicalCode,
					String medicalType,
					String wardId,
					String movType,
					LocalDateTime movFrom,
					LocalDateTime movTo,
					LocalDateTime lotPrepFrom,
					LocalDateTime lotPrepTo,
					LocalDateTime lotDueFrom,
					LocalDateTime lotDueTo) throws OHServiceException {
		List<Movement> pMovement = new ArrayList<>();

		List<Integer> pMovementCode = movRepository.findMovementWhereData(medicalCode, medicalType, wardId, movType,
						TimeTools.truncateToSeconds(movFrom),
						TimeTools.truncateToSeconds(movTo),
						TimeTools.truncateToSeconds(lotPrepFrom),
						TimeTools.truncateToSeconds(lotPrepTo),
						TimeTools.truncateToSeconds(lotDueFrom),
						TimeTools.truncateToSeconds(lotDueTo));
		for (int i = 0; i < pMovementCode.size(); i++) {
			Integer code = pMovementCode.get(i);
			Movement movement = movRepository.findById(code).orElse(null);
			if (movement == null) {
				throw new OHServiceException(new OHExceptionMessage("Movement '" + code + "' not found."));
			}
			pMovement.add(i, movement);
		}
		return pMovement;
	}

	/**
	 * Retrieves {@link Movement}s for printing using specified filtering criteria.
	 * @param medicalDescription the medical description.
	 * @param medicalTypeCode the medical type code.
	 * @param wardId the ward id.
	 * @param movType the movement type.
	 * @param movFrom the lower bound for the movement date range.
	 * @param movTo the upper bound for the movement date range.
	 * @param lotCode the lot code.
	 * @param order the result order.
	 * @return the retrieved movements.
	 * @throws OHServiceException if an error occurs retrieving the movements.
	 */
	public List<Movement> getMovementForPrint(
					String medicalDescription,
					String medicalTypeCode,
					String wardId,
					String movType,
					LocalDateTime movFrom,
					LocalDateTime movTo,
					String lotCode,
					MovementOrder order) throws OHServiceException {
		List<Movement> pMovement = new ArrayList<>();

		List<Integer> pMovementCode = movRepository.findMovementForPrint(medicalDescription, medicalTypeCode, wardId, movType, movFrom, movTo, lotCode, order);
		for (int i = 0; i < pMovementCode.size(); i++) {
			Integer code = pMovementCode.get(i);
			Movement movement = movRepository.findById(code).orElse(null);
			if (movement == null) {
				throw new OHServiceException(new OHExceptionMessage("Movement '" + code + "' not found."));
			}
			pMovement.add(i, movement);
		}
		return pMovement;
	}

	/**
	 * Retrieves lot referred to the specified {@link Medical}, expiring first on top
	 * Lots with zero quantities will be stripped out
	 * @param medical the medical.
	 * @return a list of {@link Lot}.
	 * @throws OHServiceException if an error occurs retrieving the lot list.
	 */
	public List<Lot> getLotsByMedical(Medical medical) throws OHServiceException {
		List<Lot> lots = lotRepository.findByMedicalOrderByDueDate(medical.getCode());

		if (lots.isEmpty()) {
			return Collections.emptyList();
		}

		// Get all lot IDs
		List<String> lotCodes = lots.stream().map(Lot::getCode).collect(Collectors.toList());

		// Retrieve quantities in batch
		List<Object[]> mainStoreQuantities = lotRepository.getMainStoreQuantities(lotCodes);
		List<Object[]> wardsTotalQuantities = lotRepository.getWardsTotalQuantities(lotCodes);

		// Process mainStoreQuantities and update lots
		for (Object[] result : mainStoreQuantities) {
			String lotCode = (String) result[0];
			Integer mainStoreQuantity = ((Long) result[1]).intValue();

			// Find the corresponding lot in the lots list
			Optional<Lot> matchingLot = lots.stream().filter(lot -> lot.getCode().equals(lotCode)).findFirst();

			// Update the lot if found
			matchingLot.ifPresent(lot -> lot.setMainStoreQuantity(mainStoreQuantity));
		}

		// Process wardsTotalQuantities and update lots
		for (Object[] result : wardsTotalQuantities) {
			String lotCode = (String) result[0];
			Double wardsTotalQuantity = (Double) result[1];

			// Find the corresponding lot in the lots list
			Optional<Lot> matchingLot = lots.stream().filter(lot -> lot.getCode().equals(lotCode)).findFirst();

			// Update the lot if found
			matchingLot.ifPresent(lot -> lot.setWardsTotalQuantity(wardsTotalQuantity));
		}

		// Remove empty lots
		lots.removeIf(lot -> lot.getMainStoreQuantity() <= 0);

		return lots;
	}

	/**
	 * Returns the date of the last movement
	 * @return 
	 * @throws OHServiceException
	 */
	public LocalDateTime getLastMovementDate() throws OHServiceException {
		return movRepository.findMaxDate();
	}

	/**
	 * Check if the reference number is already used
	 * @return {@code true} if is already used, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	public boolean refNoExists(String refNo) throws OHServiceException {
		return !movRepository.findAllWhereRefNo(refNo).isEmpty();
	}

	/**
	 * Retrieves all the movement associated to the specified reference number.
	 * In case of error a message error is shown and a {@code null} value is returned.
	 * @param refNo the reference number.
	 * @return the retrieved movements.
	 * @throws OHServiceException 
	 */
	public List<Movement> getMovementsByReference(String refNo) throws OHServiceException {
		return movRepository.findAllByRefNo(refNo);
	}
	
	/**
	 * Retrieves the last movement.
	 * 
	 * @return the retrieved movement.
	 * @throws OHServiceException 
	 */
	public Movement getLastMovement() throws OHServiceException {
		return movRepository.findLastMovement();
	}
	
	/**
	 * Deletes the movement.
	 * 
	 * @param movement - the movement to delete
	 * @throws OHServiceException 
	 */
	public void deleteMovement(Movement movement) throws OHServiceException {
		movRepository.delete(movement);
	}

	/**
	 * Count active {@link Movement}s
	 * 
	 * @return the number of recorded {@link Movement}s
	 * @throws OHServiceException
	 */
	public long countAllActiveMovements() {
		return this.movRepository.countAllActiveMovements();
	}

	public void deleteLot(Lot lot) throws OHServiceException {
		lotRepository.delete(lot);
	}

}
