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

import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.isf.generaldata.MessageBundle;
import org.isf.medicals.model.Medical;
import org.isf.medicals.service.MedicalsIoOperations;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.model.Movement;
import org.isf.medicalstock.service.LotIoOperationRepository;
import org.isf.medicalstock.service.MedicalStockIoOperations;
import org.isf.medicalstockward.manager.MovWardBrowserManager;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.medicalstockward.model.MovementWard;
import org.isf.medstockmovtype.manager.MedicalDsrStockMovementTypeBrowserManager;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.ward.model.Ward;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MovBrowserManager {

	@Autowired
	private MedicalStockIoOperations ioOperations;
	
	@Autowired
	private LotIoOperationRepository lotRepository;
	
	@Autowired
	private MedicalDsrStockMovementTypeBrowserManager medicalDsrStockMovTypeManager;
	
	@Autowired
	private MedicalsIoOperations medicalsIoOperation;
	
	@Autowired
	private MovWardBrowserManager movWardBrowserManager;

	public MovBrowserManager(MedicalStockIoOperations ioOperations, LotIoOperationRepository lotRepository, MedicalsIoOperations medicalsIoOperation) {
		this.ioOperations = ioOperations;
		this.lotRepository = lotRepository;
		this.medicalsIoOperation = medicalsIoOperation;
	}

	/**
	 * Retrieves all the {@link Movement}s.
	 *
	 * @return the retrieved movements.
	 * @throws OHServiceException
	 */
	public List<Movement> getMovements() throws OHServiceException {
		return ioOperations.getMovements();
	}

	/**
	 * Retrieves all the movement associated to the specified {@link Ward}.
	 *
	 * @param wardId the ward id.
	 * @param dateTo
	 * @param dateFrom
	 * @return the retrieved movements.
	 * @throws OHServiceException
	 */
	public List<Movement> getMovements(String wardId, LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		return ioOperations.getMovements(wardId, dateFrom, dateTo);
	}

	/**
	 * Retrieves all the movement associated to the specified reference number.
	 *
	 * @param refNo the reference number.
	 * @return the retrieved movements.
	 * @throws OHServiceException
	 */
	public List<Movement> getMovementsByReference(String refNo) throws OHServiceException {
		return ioOperations.getMovementsByReference(refNo);
	}

	/**
	 * Retrieves all the {@link Movement}s with the specified criteria.
	 *
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
	 * @return the retrieved movements.
	 * @throws OHServiceException
	 */
	public List<Movement> getMovements(Integer medicalCode, String medicalType,
			String wardId, String movType, LocalDateTime movFrom, LocalDateTime movTo,
			LocalDateTime lotPrepFrom, LocalDateTime lotPrepTo,
			LocalDateTime lotDueFrom, LocalDateTime lotDueTo) throws OHServiceException {

		if (medicalCode == null &&
				medicalType == null &&
				movType == null &&
				movFrom == null &&
				movTo == null &&
				lotPrepFrom == null &&
				lotPrepTo == null &&
				lotDueFrom == null &&
				lotDueTo == null) {
			return getMovements();
		}

		check(movFrom, movTo, "angal.medicalstock.chooseavalidmovementdate.msg");
		check(lotPrepFrom, lotPrepTo, "angal.medicalstock.chooseavalidmovementdate.msg");
		check(lotDueFrom, lotDueTo, "angal.medicalstock.chooseavalidduedate.msg");

		return ioOperations.getMovements(medicalCode, medicalType, wardId, movType, movFrom, movTo, lotPrepFrom, lotPrepTo, lotDueFrom, lotDueTo);
	}

	private void check(LocalDateTime from, LocalDateTime to, String errMsgKey) throws OHDataValidationException {
		if (from == null || to == null) {
			if (!(from == null && to == null)) {
				throw new OHDataValidationException(
						new OHExceptionMessage(MessageBundle.getMessage(errMsgKey)));
			}
		}
	}

	/**
	 * Delete the last Movement in Medical Stock.
	 *
	 * @throws OHServiceException
	 */
	@Transactional
	public void deleteLastMovement() throws OHDataValidationException, OHServiceException {
		
		Movement lastMovement = ioOperations.getLastMovement();
		
		if (lastMovement == null) {
			throw new OHDataValidationException(
							new OHExceptionMessage(MessageBundle.getMessage("angal.medicalstock.lastmovementnotfound.msg")));
		} else {
			MovementType movType = medicalDsrStockMovTypeManager.getMovementType(lastMovement.getType().getCode());
			
			if (movType.getType().equals("+")) {
				
				Lot lot = lastMovement.getLot();
				String lotCode = lot.getCode();
				int code = lastMovement.getCode();
				Medical medical = lastMovement.getMedical();
				int quantity = lastMovement.getQuantity();
				
				medical.setInqty(medical.getInqty() - quantity);
				medicalsIoOperation.updateMedical(medical);
				
				List<Integer> lastMovWithSameLot = ioOperations.getMedicalsFromLot(lotCode);
				
				if (lastMovWithSameLot.size() == 1) {
					lotRepository.deleteById(lotCode);
				}
				
				ioOperations.deleteMovement(code);
				
			} else {
				
				Ward ward = lastMovement.getWard();
				String wardCode = ward.getCode();
				Medical medical = lastMovement.getMedical();
				int code = lastMovement.getCode();
				int quantity = lastMovement.getQuantity();
				int medicalCode = medical.getCode();
				String lotCode = lastMovement.getLot().getCode();
				
				MedicalWard medWard = movWardBrowserManager.getMedicalWardByWardAndMedical(wardCode, medicalCode, lotCode);
				
				medWard.setIn_quantity(medWard.getIn_quantity() - quantity);
				movWardBrowserManager.updateMedicalWard(medWard);
				
				medical.setInqty(medical.getInqty() - quantity);
				medicalsIoOperation.updateMedical(medical);
				
				ioOperations.deleteMovement(code);
			}
		}
	}
}
