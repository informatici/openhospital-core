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
package org.isf.medicalinventory.manager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.medicalinventory.model.MedicalInventory;
import org.isf.medicalinventory.model.MedicalInventoryRow;
import org.isf.medicalinventory.service.MedicalInventoryIoOperation;
import org.isf.medicals.model.Medical;
import org.isf.medicalstock.manager.MovBrowserManager;
import org.isf.medicalstock.manager.MovStockInsertingManager;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.model.Movement;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.isf.utils.time.TimeTools;
import org.isf.ward.model.Ward;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MedicalInventoryManager {

	private MedicalInventoryIoOperation ioOperations;

	private MedicalInventoryRowManager medicalInventoryRowManager;

	private MovStockInsertingManager movStockInsertingManager;

	private MovBrowserManager movBrowserManager;

	public MedicalInventoryManager(MedicalInventoryIoOperation medicalInventoryIoOperation, MedicalInventoryRowManager medicalInventoryRowManager,
					MovStockInsertingManager movStockInsertingManager,
					MovBrowserManager movBrowserManager) {
		this.ioOperations = medicalInventoryIoOperation;
		this.medicalInventoryRowManager = medicalInventoryRowManager;
		this.movStockInsertingManager = movStockInsertingManager;
		this.movBrowserManager = movBrowserManager;
	}

	/**
	 * Insert a new {@link MedicalInventory}.
	 *
	 * @param medicalInventory - the {@link MedicalInventory} to insert.
	 * @return the newly persisted {@link MedicalInventory} object.
	 * @throws OHServiceException
	 */
	public MedicalInventory newMedicalInventory(MedicalInventory medicalInventory) throws OHServiceException {
		validateMedicalInventory(medicalInventory);
		return ioOperations.newMedicalInventory(medicalInventory);
	}

	/**
	 * Update an existing {@link MedicalInventory}.
	 *
	 * @param medicalInventory - the {@link MedicalInventory} to update.
	 * @return the updated {@link MedicalInventory} object.
	 * @throws OHServiceException
	 */
	public MedicalInventory updateMedicalInventory(MedicalInventory medicalInventory) throws OHServiceException {
		validateMedicalInventory(medicalInventory);
		return ioOperations.updateMedicalInventory(medicalInventory);
	}

	/**
	 * Delete the specified {@link MedicalInventory}.
	 * 
	 * @param medicalInventory - the {@link MedicalInventory} to delete.
	 * @throws OHServiceException
	 */
	@Transactional(rollbackFor = OHServiceException.class)
	public void deleteMedicalInventory(MedicalInventory medicalInventory) throws OHServiceException {
		List<MedicalInventoryRow> inventoryRowList = medicalInventoryRowManager.getMedicalInventoryRowByInventoryId(medicalInventory.getId());
		for (MedicalInventoryRow invRow : inventoryRowList) {
			medicalInventoryRowManager.deleteMedicalInventoryRow(invRow);
		}
		ioOperations.deleteMedicalInventory(medicalInventory);
	}

	/**
	 * Check if the reference number is already used.
	 * 
	 * @param reference - the {@link MedicalInventory} reference.
	 * @return {@code true} if the code is already in use, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	public boolean referenceExists(String reference) throws OHServiceException {
		return ioOperations.referenceExists(reference);
	}

	/**
	 * Return a list of {@link MedicalInventory}s for passed params.
	 *
	 * @param status - the {@link MedicalInventory} status.
	 * @param wardCode - the {@link Ward} code.
	 * @return the list of {@link MedicalInventory}s. It could be {@code empty}.
	 * @throws OHServiceException
	 */
	public List<MedicalInventory> getMedicalInventoryByStatusAndWard(String status, String wardCode) throws OHServiceException {
		return ioOperations.getMedicalInventoryByStatusAndWard(status, wardCode);
	}

	/**
	 * Return a list {@link MedicalInventory}s for passed params.
	 *
	 * @param status - the {@link MedicalInventory} status.
	 * @param inventoryType - the {@link MedicalInventory} type.
	 * @return the list of {@link MedicalInventory}s. It could be {@code empty}.
	 * @throws OHServiceException
	 */
	public List<MedicalInventory> getMedicalInventoryByStatusAndInventoryType(String status, String inventoryType) throws OHServiceException {
		return ioOperations.getMedicalInventoryByStatusAndInventoryType(status, inventoryType);
	}

	/**
	 * Return a list of results {@link MedicalInventory}s for passed params.
	 *
	 * @return the list of {@link MedicalInventory}s. It could be {@code empty}.
	 * @throws OHServiceException
	 */
	public List<MedicalInventory> getMedicalInventory() throws OHServiceException {
		return ioOperations.getMedicalInventory();
	}

	/**
	 * Return a list of {@link MedicalInventory}s for passed params.
	 * 
	 * @param dateFrom - the lowest date for the range.
	 * @param dateTo - the highest date for the range.
	 * @param status - the {@link MedicalInventory} status.
	 * @param type - the {@link MedicalInventory} type.
	 * @return the list of {@link MedicalInventory}s. It could be {@code empty}.
	 * @throws OHServiceException
	 */
	public List<MedicalInventory> getMedicalInventoryByParams(LocalDateTime dateFrom, LocalDateTime dateTo, String status, String type)
					throws OHServiceException {
		dateFrom = TimeTools.getBeginningOfDay(dateFrom);
		dateTo = TimeTools.getBeginningOfNextDay(dateTo);
		return ioOperations.getMedicalInventoryByParams(dateFrom, dateTo, status, type);
	}

	/**
	 * Return a list of {@link MedicalInventory}s for passed params.
	 * 
	 * @param dateFrom - the lower date for the range.
	 * @param dateTo - the highest date for the range.
	 * @param status - the {@link MedicalInventory} status.
	 * @param type - the {@link MedicalInventory} type.
	 * @param page - the page number.
	 * @param size - the page size.
	 * @return the list of {@link MedicalInventory}s. It could be {@code empty}.
	 * @throws OHServiceException
	 */
	public Page<MedicalInventory> getMedicalInventoryByParamsPageable(LocalDateTime dateFrom, LocalDateTime dateTo, String status, String type, int page,
					int size) throws OHServiceException {
		dateFrom = TimeTools.getBeginningOfDay(dateFrom);
		dateTo = TimeTools.getBeginningOfNextDay(dateTo);
		return ioOperations.getMedicalInventoryByParamsPageable(dateFrom, dateTo, status, type, page, size);
	}

	/**
	 * Fetch {@link MedicalInventory} with param.
	 * 
	 * @param inventoryId - the {@link MedicalInventory} id.
	 * @return {@link MedicalInventory}. It could be {@code null}.
	 * @throws OHServiceException
	 */
	public MedicalInventory getInventoryById(int inventoryId) throws OHServiceException {
		return ioOperations.getInventoryById(inventoryId);
	}

	/**
	 * Fetch {@link MedicalInventory} with param.
	 * 
	 * @param reference - the {@link MedicalInventory} reference.
	 * @return {@link MedicalInventory}. It could be {@code null}.
	 * @throws OHServiceException
	 */
	public MedicalInventory getInventoryByReference(String reference) throws OHServiceException {
		return ioOperations.getInventoryByReference(reference);
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any.
	 *
	 * @param medInventory
	 * @throws OHDataValidationException
	 */
	private void validateMedicalInventory(MedicalInventory medInventory) throws OHDataValidationException {
		List<OHExceptionMessage> errors = new ArrayList<>();
		LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
		String reference = medInventory.getInventoryReference();
		if (medInventory.getInventoryDate() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.inventory.pleaseinsertavalidinventorydate.msg")));
		}
		if (medInventory.getInventoryDate() != null && medInventory.getInventoryDate().isAfter(tomorrow)) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.inventory.notdateinfuture.msg")));
		}
		if (reference == null || reference.equals("")) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.inventory.mustenterareference.msg")));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	/**
	 * Validate the Inventory rows of inventory.
	 *
	 * @param inventory - The {@link MedicalInventory}
	 * @param inventoryRowSearchList- The list of {@link MedicalInventory}
	 * @throws OHDataValidationException
	 */
	public void validateMedicalInventoryRow(MedicalInventory inventory, List<MedicalInventoryRow> inventoryRowSearchList)
					throws OHDataValidationException, OHServiceException {
		LocalDateTime movFrom = inventory.getLastModifiedDate();
		LocalDateTime movTo = TimeTools.getNow();
		StringBuilder medDescriptionForLotUpdated = new StringBuilder("\n"); // initial new line
		StringBuilder medDescriptionForNewLot = new StringBuilder("\n"); // initial new line
		StringBuilder medDescriptionForNewMedical = new StringBuilder("\n"); // initial new line
		boolean lotUpdated = false;
		boolean lotAdded = false;
		boolean medicalAdded = false;

		// TODO: To decide if to make allMedicals parameter
		boolean allMedicals = true;
		List<Movement> movs = new ArrayList<>();
		List<Medical> inventoryMedicalsList = inventoryRowSearchList.stream()
						.map(MedicalInventoryRow::getMedical)
						.distinct()
						.collect(Collectors.toList());
		if (allMedicals) {
			// Fetch all movements without filtering by medical code
			movs.addAll(movBrowserManager.getMovements(null, null, null, null, movFrom, movTo, null, null, null, null));
		} else {
			// Fetch only movements concerning inventoryRowSearchList list
			for (Medical medical : inventoryMedicalsList) {
				movs.addAll(movBrowserManager.getMovements(medical.getCode(), null, null, null, movFrom, movTo, null, null, null, null));
			}
		}

		// Cycle fetched movements to see if they impact inventoryRowSearchList
		for (Movement mov : movs) {
			Lot movLot = mov.getLot();
			String lotCodeOfMovement = movLot.getCode();
			String lotExpiringDate = TimeTools.formatDateTime(movLot.getDueDate(), TimeTools.DD_MM_YYYY);
			String lotInfo = GeneralData.AUTOMATICLOT_IN ? lotExpiringDate : lotCodeOfMovement;
			Medical medical = mov.getMedical();
			String medicalDesc = medical.getDescription();
			Integer medicalCode = medical.getCode();

			// Fetch also empty lots because some movements may have discharged them completely
			Optional<Lot> lot = movStockInsertingManager.getLotByMedical(medical, false).stream().filter(l -> l.getCode().equals(lotCodeOfMovement))
							.findFirst();
			double mainStoreQty = lot.get().getMainStoreQuantity();

			// Search for the specific Lot and Medical in inventoryRowSearchList (Lot should be enough)
			Optional<MedicalInventoryRow> matchingRow = inventoryRowSearchList.stream()
							.filter(row -> row.getLot().getCode().equals(lotCodeOfMovement) && row.getMedical().getCode().equals(medicalCode))
							.findFirst();

			if (matchingRow.isPresent()) {
				MedicalInventoryRow medicalInventoryRow = matchingRow.get();
				double theoQty = medicalInventoryRow.getTheoreticQty();
				if (mainStoreQty != theoQty) {
					// Update Lot
					medicalInventoryRow.setTheoreticQty(mainStoreQty);
					MedicalInventoryRow updatedRow = medicalInventoryRowManager.updateMedicalInventoryRow(medicalInventoryRow);
					if (updatedRow != null) {
						lotUpdated = true;
						medDescriptionForLotUpdated
										.append("\n")
										.append(MessageBundle.formatMessage(
														"angal.inventory.theoreticalqtyhavebeenupdatedforsomemedical.detail.fmt.msg",
														medicalDesc, lotInfo, theoQty, mainStoreQty, mainStoreQty - theoQty));
					}
				}
			} else {
				// TODO: to decide if to give control to the user about this
				double realQty = mainStoreQty;
				MedicalInventoryRow newMedicalInventoryRow = new MedicalInventoryRow(null, mainStoreQty, realQty, inventory, medical,
								mov.getLot());
				medicalInventoryRowManager.newMedicalInventoryRow(newMedicalInventoryRow);
				inventoryRowSearchList.add(newMedicalInventoryRow);

				if (!inventoryMedicalsList.contains(medical)) {
					// New medical
					medicalAdded = true;
					medDescriptionForNewMedical
									.append("\n")
									.append(MessageBundle.formatMessage(
													"angal.inventory.newmedicalshavebeenfound.detail.fmt.msg",
													medicalDesc, lotInfo, mainStoreQty));
				} else {
					// New Lot
					lotAdded = true;
					medDescriptionForNewLot
									.append("\n")
									.append(MessageBundle.formatMessage(
													"angal.inventory.newlotshavebeenaddedforsomemedical.detail.fmt.msg",
													medicalDesc, lotInfo, mainStoreQty));
				}
			}
		}
		List<OHExceptionMessage> errors = new ArrayList<>();
		if (lotUpdated) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.inventory.validate.btn"),
							MessageBundle.formatMessage("angal.inventory.theoreticalqtyhavebeenupdatedforsomemedical.fmt.msg", medDescriptionForLotUpdated),
							OHSeverityLevel.INFO));
		}
		if (lotAdded) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.inventory.validate.btn"),
							MessageBundle.formatMessage("angal.inventory.newlotshavebeenaddedforsomemedical.fmt.msg", medDescriptionForNewLot),
							OHSeverityLevel.INFO));
		}
		if (medicalAdded) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.inventory.validate.btn"),
							MessageBundle.formatMessage("angal.inventory.newmedicalshavebeenfound.fmt.msg", medDescriptionForNewMedical),
							OHSeverityLevel.INFO));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	/**
	 * Marks an inventory as deleted by changing its status.
	 * 
	 * @param medicalInventory - the medicalInventory of the inventory to delete.
	 * @throws OHServiceException if an error occurs during the operation.
	 */
	public void deleteInventory(MedicalInventory medicalInventory) throws OHServiceException {
		int invenotyId = medicalInventory.getId();
		List<MedicalInventoryRow> inventoryRows = medicalInventoryRowManager.getMedicalInventoryRowByInventoryId(invenotyId);
		for (MedicalInventoryRow invRow : inventoryRows) {
			boolean isNewLot = invRow.isNewLot();
			Lot lot = invRow.getLot();
			if (isNewLot && lot != null) {
				invRow.setLot(null);
				medicalInventoryRowManager.updateMedicalInventoryRow(invRow);
				movStockInsertingManager.deleteLot(lot);
			}
		}
		ioOperations.deleteInventory(medicalInventory);
	}
}
