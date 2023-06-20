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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.medicalstock.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
import org.isf.utils.time.TimeTools;
import org.isf.ward.model.Ward;
import org.springframework.beans.factory.annotation.Autowired;
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
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class MedicalStockIoOperations {

	@Autowired
	private MovementIoOperationRepository movRepository;
	
	@Autowired
	private LotIoOperationRepository lotRepository;
	
	@Autowired
	private MedicalsIoOperationRepository medicalRepository;

	@Autowired
	private MedicalStockWardIoOperationRepository medicalStockRepository;

	public enum MovementOrder {
		DATE, WARD, PHARMACEUTICAL_TYPE, TYPE
	}

	/**
	 * Checks if we are in automatic lot mode.
	 * @return <code>true</code> if automatic lot mode, <code>false</code> otherwise.
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
	 * Store the specified {@link Movement} by using automatically the most old lots
	 * and splitting in more movements if required
	 * @param movement - the {@link Movement} to store
	 * @return <code>true</code> if the movement has been stored, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public boolean newAutomaticDischargingMovement(Movement movement) throws OHServiceException {
		boolean result = false;

		List<Lot> lots = getLotsByMedical(movement.getMedical());

		int qty = movement.getQuantity(); // movement initial quantity
		for (Lot lot : lots) {
			Movement splitMovement = new Movement(movement.getMedical(), movement.getType(), movement.getWard(),
					null, // lot to be set
					movement.getDate(),
					qty, // quantity can remain the same or changed if greater than lot quantity
					null,
					movement.getRefNo());
			int qtLot = lot.getMainStoreQuantity();
			if (qtLot < qty) {
				splitMovement.setQuantity(qtLot);
				result = storeMovement(splitMovement, lot.getCode());
				if (result) {
					//medical stock movement inserted updates quantity of the medical
					result = updateStockQuantity(splitMovement);
				}
				qty = qty - qtLot;
			} else {
				splitMovement.setQuantity(qty);
				result = storeMovement(splitMovement, lot.getCode());
				if (result) {
					//medical stock movement inserted updates quantity of the medical
					result = updateStockQuantity(splitMovement);
				}
				break;
			}
		}

		return result;
	}
		
	/**
	 * Stores the specified {@link Movement}.
	 * @param movement - the movement to store.
	 * @return <code>true</code> if the movement has been stored, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs during the store operation.
	 */
	public boolean newMovement(Movement movement) throws OHServiceException {
		String lotCode = null;

		if (movement.getLot() != null) {
			lotCode = movement.getLot().getCode();
		}

		//we have to manage the Lot
		if (movement.getType().getType().contains("+")) {
			//if is in automatic lot mode then we have to generate a new lot code
			if (isAutomaticLotMode() || lotCode.equals("")) {
				lotCode = generateLotCode();
			}

			boolean lotExists = lotExists(lotCode);
			if (!lotExists) {
				boolean lotStored = storeLot(lotCode, movement.getLot(), movement.getMedical());
				if (!lotStored) {
					return false;
				}
			}
		}

		boolean movementStored = storeMovement(movement, lotCode);
		if (movementStored) {
			//medical stock movement inserted updates quantity of the medical
			return updateStockQuantity(movement);
		}

		//something is failed
		return false;
	}
	
	/**
	 * Prepare the insert of the specified {@link Movement} (no commit)
	 * @param movement - the movement to store.
	 * @return <code>true</code> if the movement has been stored, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs during the store operation.
	 */
	public boolean prepareChargingMovement(Movement movement) throws OHServiceException {
		return newMovement(movement);
	}
	
	/**
	 * Prepare the insert of the specified {@link Movement} (no commit)
	 * @param movement - the movement to store.
	 * @return <code>true</code> if the movement has been stored, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs during the store operation.
	 */
	public boolean prepareDischargingMovement(Movement movement) throws OHServiceException {
		String lotCode = null;

		if (movement.getLot() != null) {
			lotCode = movement.getLot().getCode();
		}

		boolean movementStored = storeMovement(movement, lotCode);

		//medical stock movement inserted
		if (movementStored) {
			// updates quantity of the medical
			return updateStockQuantity(movement);
		}

		//something is failed
		return false;
	}

	/**
	 * Stores the specified {@link Movement}.
	 * @param movement the movement to store.
	 * @param lotCode the {@link Lot} code to use.
	 * @return <code>true</code> if the movement has stored, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs storing the movement.
	 */
	protected boolean storeMovement(Movement movement, String lotCode) throws OHServiceException {
		Lot lot = lotRepository.findById(lotCode).orElse(null);
		movement.setLot(lot);
		return movRepository.save(movement) != null;
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
	 * @return <code>true</code> if exists, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs during the check.
	 */
	public boolean lotExists(String lotCode) throws OHServiceException {
		return lotRepository.findById(String.valueOf(lotCode)).orElse(null) != null;
	}

	/**
	 * Stores the specified {@link Lot}.
	 * @param lotCode the {@link Lot} code.
	 * @param lot the lot to store.
	 * @param medical
	 * @return <code>true</code> if the lot has been stored, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurred storing the lot.
	 */
	public boolean storeLot(String lotCode, Lot lot, Medical medical) throws OHServiceException {
		lot.setCode(lotCode);
		lot.setMedical(medical);
		lotRepository.save(lot);
		return true;
	}

	/**
	 * Updated {@link Medical} stock quantity for the specified {@link Movement}.
	 * @param movement the movement.
	 * @return <code>true</code> if the quantity has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs during the update.
	 */
	protected boolean updateStockQuantity(Movement movement) throws OHServiceException {
		if (movement.getType().getType().contains("+")) {
			//incoming medical stock
			Medical medical = movement.getMedical();
			return updateMedicalIncomingQuantity(medical.getCode(), movement.getQuantity());
		} else {
			//outgoing medical stock
			Medical medical = movement.getMedical();
			boolean updated = updateMedicalOutcomingQuantity(medical.getCode(), movement.getQuantity());
			if (!updated) {
				return false;
			} else {
				Ward ward = movement.getWard();
				if (ward != null) {
					//updates stock quantity for wards
					return updateMedicalWardQuantity(ward, medical, movement.getQuantity(), movement.getLot());
				} else {
					return true;
				}
			}
		}
	}

	/**
	 * Updates the incoming quantity for the specified medical.
	 * @param medicalCode the medical code.
	 * @param incrementQuantity the quantity to add.
	 * @return <code>true</code> if the quantity has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs during the update.
	 */
	protected boolean updateMedicalIncomingQuantity(int medicalCode, double incrementQuantity) throws OHServiceException {
		Medical medical = medicalRepository.findById(medicalCode).orElse(null);
		medical.setInqty(medical.getInqty() + incrementQuantity);
		medicalRepository.save(medical);
		return true;
	}

	/**
	 * Updates the outcoming quantity for the specified medicinal.
	 * @param medicalCode the medical code.
	 * @param incrementQuantity the quantity to add to the current outcoming quantity.
	 * @return <code>true</code> if the outcoming quantity has been updated <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs during the update.
	 */
	protected boolean updateMedicalOutcomingQuantity(int medicalCode, double incrementQuantity) throws OHServiceException {
		Medical medical = medicalRepository.findById(medicalCode).orElse(null);
		medical.setOutqty(medical.getOutqty() + incrementQuantity);
		medicalRepository.save(medical);
		return true;
	}

	/**
	 * Updates medical quantity for the specified ward.
	 * @param ward the ward.
	 * @param medical the medical.
	 * @param quantity the quantity to add to the current medical quantity.
	 * @return <code>true</code> if the quantity has been updated/inserted, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs during the update.
	 */
	@SuppressWarnings("unchecked")
	protected boolean updateMedicalWardQuantity(Ward ward, Medical medical, int quantity, Lot lot) throws OHServiceException {
		MedicalWard medicalWard = medicalStockRepository.findOneWhereCodeAndMedicalAndLot(ward.getCode(), medical.getCode(), lot.getCode());

		if (medicalWard != null) {
			medicalWard.setIn_quantity(medicalWard.getIn_quantity() + quantity);
			medicalStockRepository.save(medicalWard);
		} else {
			medicalWard = new MedicalWard(ward, medical, quantity, 0, lot);
			medicalStockRepository.insertMedicalWard(ward.getCode(), medical.getCode(), (double)quantity, lot.getCode());
		}
		medicalStockRepository.save(medicalWard);
		return true;
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

		List<Integer> pMovementCode = movRepository.findMovementWhereDatesAndId(wardId, TimeTools.truncateToSeconds(dateFrom),
		                                                                        TimeTools.truncateToSeconds(dateTo));
		for (int i = 0; i < pMovementCode.size(); i++) {
			Integer code = pMovementCode.get(i);
			Movement movement = movRepository.findById(code).orElse(null);
			pMovement.add(i, movement);
		}
		return pMovement;
	}

	/**
	 * Retrieves all the stored {@link Movement} with the specified criteria.
	 * @param medicalCode the medical code.
	 * @param medicalType the medical type.
	 * @param wardId the ward type.
	 * @param movType the movement type.
	 * @param movFrom the lower bound for the movement date range.
	 * @param movTo the upper bound for the movement date range.
	 * @param lotPrepFrom the lower bound for the lot preparation date range.
	 * @param lotPrepTo the upper bound for the lot preparation date range.
	 * @param lotDueFrom the lower bound for the lot due date range.
	 * @param lotDueTo the lower bound for the lot due date range.
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
		//retrieve quantities
		lots.forEach(lot -> {
			lot.setMainStoreQuantity(lotRepository.getMainStoreQuantity(lot));
			lot.setWardsTotalQuantity(lotRepository.getWardsTotalQuantity(lot));
		});
		// remove empty lots
		return lots.stream().filter(lot -> lot.getMainStoreQuantity() > 0).collect(Collectors.toList());
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
	 * @return <code>true</code> if is already used, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public boolean refNoExists(String refNo) throws OHServiceException {
		return !movRepository.findAllWhereRefNo(refNo).isEmpty();
	}

	/**
	 * Retrieves all the movement associated to the specified reference number.
	 * In case of error a message error is shown and a <code>null</code> value is returned.
	 * @param refNo the reference number.
	 * @return the retrieved movements.
	 * @throws OHServiceException 
	 */
	public List<Movement> getMovementsByReference(String refNo) throws OHServiceException {
		return movRepository.findAllByRefNo(refNo);
	}

}
