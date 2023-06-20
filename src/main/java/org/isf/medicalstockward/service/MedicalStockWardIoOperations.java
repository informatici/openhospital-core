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
package org.isf.medicalstockward.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.isf.medicals.model.Medical;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.model.Movement;
import org.isf.medicalstock.service.LotIoOperationRepository;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.medicalstockward.model.MovementWard;
import org.isf.patient.model.Patient;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.time.TimeTools;
import org.isf.ward.model.Ward;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author mwithi
 */
@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class MedicalStockWardIoOperations 
{

	@Autowired
	private MedicalStockWardIoOperationRepository repository;
	@Autowired
	private MovementWardIoOperationRepository movementRepository;
	@Autowired
	private LotIoOperationRepository lotRepository;
	
	/**
	 * Get all {@link MovementWard}s with the specified criteria.
	 * @param wardId the ward id.
	 * @param dateFrom the lower bound for the movement date range.
	 * @param dateTo the upper bound for the movement date range.
	 * @return the retrieved movements.
	 * @throws OHServiceException if an error occurs retrieving the movements.
	 */
	public List<MovementWard> getWardMovements(String wardId, LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		List<MovementWard> pMovementWard = new ArrayList<>();

		List<Integer> pMovementWardCode = new ArrayList<>(repository.findAllWardMovement(wardId, TimeTools.truncateToSeconds(dateFrom),
		                                                                                 TimeTools.truncateToSeconds(dateTo)));
		for (Integer code : pMovementWardCode) {
			MovementWard movementWard = movementRepository.findById(code).orElse(null);
			pMovementWard.add(movementWard);
		}
		return pMovementWard;
	}
        
    /**
	 * Get all {@link MovementWard}s with the specified criteria.
	 * @param idwardTo the target ward id.
	 * @param dateFrom the lower bound for the movement date range.
	 * @param dateTo the upper bound for the movement date range.
	 * @return the retrieved movements.
	 * @throws OHServiceException if an error occurs retrieving the movements.
	 */
    public List<MovementWard> getWardMovementsToWard(String idwardTo, LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
	    return movementRepository.findWardMovements(idwardTo, TimeTools.truncateToSeconds(dateFrom), TimeTools.truncateToSeconds(dateTo));
    }

	/**
	 * Gets the current quantity for the specified {@link Medical} and specified {@link Ward}.
	 * @param ward - if {@code null} the quantity is counted for the whole hospital
	 * @return the total quantity.
	 * @throws OHServiceException if an error occurs retrieving the quantity.
	 */
	public int getCurrentQuantityInWard(Ward ward, Medical medical) throws OHServiceException {
		Double mainQuantity;
		if (ward != null) {
			mainQuantity = repository.findQuantityInWardWhereMedicalAndWard(medical.getCode(), ward.getCode());
		} else {
			mainQuantity = repository.findQuantityInWardWhereMedical(medical.getCode());
		}
		return (int) (mainQuantity != null ? mainQuantity : 0.0);
	}
	
	/**
	 * Gets the current quantity for the specified {@link Ward} and {@link Lot}.
	 * @param ward - if {@code null} the quantity is counted for the whole hospital
	 * @param lot - the {@link Lot} to be counted
	 * @return the total quantity.
	 * @throws OHServiceException if an error occurs retrieving the quantity.
	 */
	public int getCurrentQuantityInWard(Ward ward, Lot lot) throws OHServiceException {
		Double quantity;
		if (ward != null) {
			quantity = lotRepository.getQuantityByWard(lot, ward);
		} else {
			quantity = repository.findQuantityInWardWhereMedical(lot.getMedical().getCode());
		}
		return (int) (quantity == null ? 0 : quantity);
	}

	/**
	 * Stores the specified {@link Movement}.
	 *
	 * @param movement the movement to store.
	 * @throws OHServiceException if an error occurs.
	 */
	public void newMovementWard(MovementWard movement) throws OHServiceException {
		MovementWard savedMovement = movementRepository.save(movement);
		if (savedMovement.getWardTo() != null) {
			// We have to register also the income movement for the destination Ward
			MovementWard destinationWardIncomeMovement = new MovementWard();
			destinationWardIncomeMovement.setDate(savedMovement.getDate());
			destinationWardIncomeMovement.setDescription(savedMovement.getWard().getDescription());
			destinationWardIncomeMovement.setMedical(savedMovement.getMedical());
			destinationWardIncomeMovement.setQuantity(-savedMovement.getQuantity());
			destinationWardIncomeMovement.setUnits(savedMovement.getUnits());
			destinationWardIncomeMovement.setWard(savedMovement.getWardTo());
			destinationWardIncomeMovement.setWardFrom(savedMovement.getWard());
			destinationWardIncomeMovement.setlot(savedMovement.getLot());
			movementRepository.save(destinationWardIncomeMovement);
		}
		updateStockWardQuantity(movement);
	}

	/**
	 * Stores the specified {@link Movement} list.
	 * @param movements the movement to store.
	 * @throws OHServiceException if an error occurs.
	 */
	public void newMovementWard(List<MovementWard> movements) throws OHServiceException {
		for (MovementWard movement : movements) {
			newMovementWard(movement);
		}
	}

	/**
	 * Updates the specified {@link MovementWard}.
	 * @param movement the movement ward to update.
	 * @return <code>true</code> if has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs during the update.
	 */
	public boolean updateMovementWard(MovementWard movement) throws OHServiceException {
		return movementRepository.save(movement) != null;
	}

	/**
	 * Deletes the specified {@link MovementWard}.
	 * @param movement the movement ward to delete.
	 * @return <code>true</code> if the movement has been deleted, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs during the delete.
	 */
	public boolean deleteMovementWard(MovementWard movement) throws OHServiceException {
		movementRepository.delete(movement);
		return true;
	}

	/**
	 * Updates the quantity for the specified movement ward.
	 * @param movement the movement ward to update.
	 * @return <code>true</code> if has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs during the update.
	 */
	protected boolean updateStockWardQuantity(MovementWard movement) throws OHServiceException {
		Double qty = movement.getQuantity();
		String ward = movement.getWard().getCode();
		String lot = movement.getLot().getCode();
		String wardTo = null;
		if (movement.getWardTo() != null) {
			// in case of a mvnt from the ward movement.getWard() to the ward movement.getWardTO()
			wardTo = movement.getWardTo().getCode();
		}
		Integer medical = movement.getMedical().getCode();
		boolean result = true;

		if (wardTo != null) {
			MedicalWard medicalWardTo = repository.findOneWhereCodeAndMedicalAndLot(wardTo, medical, lot);
			if (medicalWardTo != null) {
				repository.updateInQuantity(Math.abs(qty), wardTo, medical, lot);
			} else {
				MedicalWard medicalWard = new MedicalWard();
				medicalWard.setWard(movement.getWardTo());
				medicalWard.setMedical(movement.getMedical());
				medicalWard.setIn_quantity((float) Math.abs(qty));
				medicalWard.setOut_quantity(0.0f);
				medicalWard.setLot(movement.getLot());
				repository.save(medicalWard);
			}
			repository.updateOutQuantity(Math.abs(qty), ward, medical, lot);
			return result;
		}

		MedicalWard medicalWard = repository.findOneWhereCodeAndMedicalAndLot(ward, medical, lot);
		if (medicalWard == null) {
			medicalWard = new MedicalWard();
			medicalWard.setWard(movement.getWard());
			medicalWard.setMedical(movement.getMedical());
			medicalWard.setIn_quantity((float) -qty);
			medicalWard.setOut_quantity(0.0f);
			medicalWard.setLot(movement.getLot());
			repository.save(medicalWard);
		} else {
			if (qty < 0) {
				repository.updateInQuantity(-qty, ward, medical, lot); // TODO: change to jpa
			} else {
				repository.updateOutQuantity(qty, ward, medical, lot); // TODO: change to jpa
			}
		}
		return result;
	}

	/**
	 * Gets all the {@link Medical}s associated to specified {@link Ward}.
	 * @param wardId the ward id.
	 * @param stripeEmpty - if <code>true</code>, stripes the empty lots
	 * @return the retrieved medicals.
	 * @throws OHServiceException if an error occurs during the medical retrieving.
	 */
	public List<MedicalWard> getMedicalsWard(char wardId, boolean stripeEmpty) throws OHServiceException {
		List<MedicalWard> medicalWards = repository.findAllWhereWard(wardId);
		for (int i = 0; i < medicalWards.size(); i++) {
			double qty = medicalWards.get(i).getIn_quantity() - medicalWards.get(i).getOut_quantity();
			medicalWards.get(i).setQty(qty);

			if (stripeEmpty && qty == 0) {
				medicalWards.remove(i);
				i = i - 1;
			}
		}

		return medicalWards;
	}

	public List<MovementWard> findAllForPatient(Patient patient) {
		return movementRepository.findByPatient_code(patient.getCode());
	}

	public List<MovementWard> getWardMovementsToPatient(Integer patId) {
		return movementRepository.findWardMovementPat(patId);
	}


	/**
	 * Gets all the {@link MedicalWard}s associated to the specified ward summarized by lot
	 * (total quantity, regardless the lot)
	 * @param wardId
	 * @return the retrieved medicals.
	 * @throws OHServiceException
	 */
	public List<MedicalWard> getMedicalsWardTotalQuantity(char wardId) throws OHServiceException {
		String wardID = String.valueOf(wardId);
		List<MedicalWard> medicalWards = getMedicalsWard(wardId, true);

		List<MedicalWard> medicalWardsQty = new ArrayList<>();

		for (MedicalWard medicalWard : medicalWards) {

			if (!medicalWardsQty.contains(medicalWard)) {
				Double qty = repository.findQuantityInWardWhereMedicalAndWard(medicalWard.getId().getMedical().getCode(), wardID);
				medicalWard.setQty(qty);
				medicalWardsQty.add(medicalWard);
			}
		}
		return medicalWardsQty;
	}
}
