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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.medicalinventory.model.InventoryStatus;
import org.isf.medicalinventory.model.InventoryType;
import org.isf.medicalinventory.model.MedicalInventory;
import org.isf.medicalinventory.model.MedicalInventoryRow;
import org.isf.medicalinventory.service.MedicalInventoryIoOperation;
import org.isf.medicals.model.Medical;
import org.isf.medicalstock.manager.MovBrowserManager;
import org.isf.medicalstock.manager.MovStockInsertingManager;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.model.Movement;
import org.isf.medstockmovtype.manager.MedicalDsrStockMovementTypeBrowserManager;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.supplier.manager.SupplierBrowserManager;
import org.isf.supplier.model.Supplier;
import org.isf.medicalstockward.manager.MovWardBrowserManager;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.medicalstockward.model.MovementWard;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.isf.utils.time.TimeTools;
import org.isf.utils.validator.DefaultSorter;
import org.isf.ward.manager.WardBrowserManager;
import org.isf.ward.model.Ward;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MedicalInventoryManager {

	private final MedicalInventoryIoOperation ioOperations;

	private final MedicalInventoryRowManager medicalInventoryRowManager;

	private final MovStockInsertingManager movStockInsertingManager;

	private final MovBrowserManager movBrowserManager;

	private final MedicalDsrStockMovementTypeBrowserManager medicalDsrStockMovementTypeBrowserManager;

	private final SupplierBrowserManager supplierManager;

	private final WardBrowserManager wardManager;

	protected Map<String, String> statusHashMap;

	private MovWardBrowserManager movWardBrowserManager;

	public MedicalInventoryManager(MedicalInventoryIoOperation medicalInventoryIoOperation, MedicalInventoryRowManager medicalInventoryRowManager,
			MedicalDsrStockMovementTypeBrowserManager medicalDsrStockMovementTypeBrowserManager,
			SupplierBrowserManager supplierManager, MovStockInsertingManager movStockInsertingManager, WardBrowserManager wardManager,
			MovBrowserManager movBrowserManager, MovWardBrowserManager movWardBrowserManager) {
		this.ioOperations = medicalInventoryIoOperation;
		this.medicalInventoryRowManager = medicalInventoryRowManager;
		this.medicalDsrStockMovementTypeBrowserManager = medicalDsrStockMovementTypeBrowserManager;
		this.supplierManager = supplierManager;
		this.movStockInsertingManager = movStockInsertingManager;
		this.wardManager = wardManager;
		this.movBrowserManager = movBrowserManager;
		this.movWardBrowserManager = movWardBrowserManager;
	}

	/**
	 * Insert a new {@link MedicalInventory}.
	 *
	 * @param medicalInventory the {@link MedicalInventory} to insert.
	 * @return the newly persisted {@link MedicalInventory} object.
	 * @throws OHServiceException
	 */
	public MedicalInventory newMedicalInventory(MedicalInventory medicalInventory) throws OHServiceException {
		validateMedicalInventory(medicalInventory);
		checkReference(medicalInventory);
		return ioOperations.newMedicalInventory(medicalInventory);
	}

	/**
	 * Update an existing {@link MedicalInventory}.
	 *
	 * @param medicalInventory the {@link MedicalInventory} to update.
	 * @return the updated {@link MedicalInventory} object.
	 * @throws OHServiceException
	 */
	public MedicalInventory updateMedicalInventory(MedicalInventory medicalInventory, boolean checkReference) throws OHServiceException {
		validateMedicalInventory(medicalInventory);
		if (checkReference) {
			checkReference(medicalInventory);
		}
		return ioOperations.updateMedicalInventory(medicalInventory);
	}

	/**
	 * Check if the reference number is already used.
	 * 
	 * @param reference the {@link MedicalInventory} reference.
	 * @return {@code true} if the code is already in use, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	public boolean referenceExists(String reference) throws OHServiceException {
		return ioOperations.referenceExists(reference);
	}

	/**
	 * Return a list of {@link MedicalInventory}s for passed params.
	 *
	 * @param status the {@link MedicalInventory} status.
	 * @param wardCode the {@link Ward} code.
	 * @return the list of {@link MedicalInventory}s. It could be {@code empty}.
	 * @throws OHServiceException
	 */
	public List<MedicalInventory> getMedicalInventoryByStatusAndWard(String status, String wardCode) throws OHServiceException {
		return ioOperations.getMedicalInventoryByStatusAndWard(status, wardCode);
	}

	/**
	 * Return a list {@link MedicalInventory}s for passed params.
	 *
	 * @param status the {@link MedicalInventory} status.
	 * @param inventoryType the {@link MedicalInventory} type.
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
	 * @param dateFrom the lowest date for the range.
	 * @param dateTo the highest date for the range.
	 * @param status the {@link MedicalInventory} status.
	 * @param type the {@link MedicalInventory} type.
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
	 * @param dateFrom the lower date for the range.
	 * @param dateTo the highest date for the range.
	 * @param status the {@link MedicalInventory} status.
	 * @param type the {@link MedicalInventory} type.
	 * @param page the page number.
	 * @param size the page size.
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
	 * @param inventoryId the {@link MedicalInventory} id.
	 * @return {@link MedicalInventory}. It could be {@code null}.
	 * @throws OHServiceException
	 */
	public MedicalInventory getInventoryById(int inventoryId) throws OHServiceException {
		return ioOperations.getInventoryById(inventoryId);
	}

	/**
	 * Fetch {@link MedicalInventory} with param.
	 * 
	 * @param reference the {@link MedicalInventory} reference.
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
	 * @param inventory the {@link MedicalInventory}
	 * @param inventoryRowSearchList- The list of {@link MedicalInventory}
	 * @throws OHServiceException
	 */
	public void validateMedicalInventoryRow(MedicalInventory inventory, List<MedicalInventoryRow> inventoryRowSearchList)
		throws OHServiceException {
		LocalDateTime movFrom = inventory.getInventoryDate();
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
		// Get all the lot of the movements
		List<Lot> lotOfMovements = movs.stream().map(Movement::getLot).collect(Collectors.toList());
		// Remove duplicates by converting the list to a set
		Set<Lot> uniqueLots = new HashSet<>(lotOfMovements);
		// Convert the set back to a list
		List<Lot> uniqueLotList = new ArrayList<>(uniqueLots);
		// Cycle fetched movements to see if they impact inventoryRowSearchList
		for (Lot lot : uniqueLotList) {
			String lotCodeOfMovement = lot.getCode();
			String lotExpiringDate = TimeTools.formatDateTime(lot.getDueDate(), TimeTools.DD_MM_YYYY);
			String lotInfo = GeneralData.AUTOMATICLOT_IN ? lotExpiringDate : lotCodeOfMovement;
			Medical medical = lot.getMedical();
			String medicalDesc = medical.getDescription();
			Integer medicalCode = medical.getCode();
			double mainStoreQty = 0.0;
			// Fetch also empty lots because some movements may have discharged them completely
			Optional<Lot> optLot = movStockInsertingManager.getLotByMedical(medical, false).stream().filter(l -> l.getCode().equals(lotCodeOfMovement))
				.findFirst();
			if (optLot.isPresent()) {
				mainStoreQty = optLot.get().getMainStoreQuantity();
			}

			// Search for the specific Lot and Medical in inventoryRowSearchList (Lot should be enough)
			Optional<MedicalInventoryRow> matchingRow = inventoryRowSearchList.stream()
				.filter(row -> row.getLot().getCode().equals(lotCodeOfMovement) && row.getMedical().getCode().equals(medicalCode))
				.findFirst();

			if (matchingRow.isPresent()) {
				MedicalInventoryRow medicalInventoryRow = matchingRow.get();
				double theoQty = medicalInventoryRow.getTheoreticQty();
				if (mainStoreQty != theoQty) {
					lotUpdated = true;
					medDescriptionForLotUpdated
						.append("\n")
						.append(MessageBundle.formatMessage(
							"angal.inventory.theoreticalqtyhavebeenupdatedforsomemedical.detail.fmt.msg",
							medicalDesc, lotInfo, theoQty, mainStoreQty, mainStoreQty - theoQty));

				}
			} else {
				// TODO: to decide if to give control to the user about this
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
	 * Validate the Inventory rows of inventory ward.
	 *
	 * @param inventory the {@link MedicalInventory}
	 * @param inventoryRowSearchList the list of {@link MedicalInventory}
	 * @throws OHServiceException
	 */
	public void validateMedicalWardInventoryRow(MedicalInventory inventory, List<MedicalInventoryRow> inventoryRowSearchList) throws OHServiceException {
		LocalDateTime movFrom = inventory.getInventoryDate();
		LocalDateTime movTo = TimeTools.getNow();
		StringBuilder medDescriptionForLotUpdated = new StringBuilder("\n"); // initial new line
		StringBuilder medDescriptionForNewLot = new StringBuilder("\n"); // initial new line
		StringBuilder medDescriptionForNewMedical = new StringBuilder("\n"); // initial new line
		boolean lotUpdated = false;
		boolean lotAdded = false;
		boolean medicalAdded = false;

		List<MovementWard> movementWards = new ArrayList<>(movWardBrowserManager.getMovementWard(inventory.getWard(), movFrom, movTo));
		List<Movement> movementToWards = new ArrayList<>(movBrowserManager.getMovements(inventory.getWard(), movFrom, movTo));
		List<Medical> inventoryMedicalsList = inventoryRowSearchList.stream().map(MedicalInventoryRow::getMedical).distinct().toList();

		// Get all the lots from ward movements
		List<Lot> lotOfMovements = new ArrayList<>(movementWards.stream().map(MovementWard::getLot).toList());
		// Get all the lots from main store movements
		lotOfMovements.addAll(movementToWards.stream().map(Movement::getLot).toList());
		// Remove duplicates by converting the list to a set
		Set<Lot> uniqueLots = new HashSet<>(lotOfMovements);
		// Convert the set back to a list
		List<Lot> uniqueLotList = new ArrayList<>(uniqueLots);
		// Cycle fetched movements to see if they impact inventoryRowSearchList
		for (Lot lot : uniqueLotList) {
			String lotCode = lot.getCode();
			String lotExpiringDate = TimeTools.formatDateTime(lot.getDueDate(), TimeTools.DD_MM_YYYY);
			String lotInfo = GeneralData.AUTOMATICLOT_IN ? lotExpiringDate : lotCode;
			Medical medical = lot.getMedical();
			String medicalDesc = medical.getDescription();

			Optional<MedicalWard> optMedicalWard = movWardBrowserManager.getMedicalsWard(inventory.getWard(), medical.getCode(), false).stream()
					.filter(m -> m.getLot().getCode().equals(lotCode)).findFirst();

			double wardStoreQty = optMedicalWard.isPresent() ? optMedicalWard.get().getQty() : 0.0;

			// Search for the specific Lot and Medical in inventoryRowSearchList
			Optional<MedicalInventoryRow> matchingRow = inventoryRowSearchList.stream()
				.filter(row -> row.getLot().getCode().equals(lotCode)).findFirst();

			if (matchingRow.isPresent()) {
				MedicalInventoryRow medicalInventoryRow = matchingRow.get();
				double theoQty = medicalInventoryRow.getTheoreticQty();
				if (wardStoreQty != theoQty) {
					lotUpdated = true;
					medDescriptionForLotUpdated
						.append("\n")
						.append(MessageBundle.formatMessage("angal.inventory.theoreticalqtyhavebeenupdatedforsomemedical.detail.fmt.msg",
							medicalDesc, lotInfo, theoQty, wardStoreQty, wardStoreQty - theoQty));
				}
			} else {
				// TODO: to decide if to give control to the user about this
				if (!inventoryMedicalsList.contains(medical)) {
					// New medical
					medicalAdded = true;
					medDescriptionForNewMedical
						.append("\n")
						.append(MessageBundle.formatMessage("angal.inventory.newmedicalshavebeenfound.detail.fmt.msg",
							medicalDesc, lotInfo, wardStoreQty));
				} else {
					// New Lot
					lotAdded = true;
					medDescriptionForNewLot
						.append("\n")
						.append(MessageBundle.formatMessage("angal.inventory.newlotshavebeenaddedforsomemedical.detail.fmt.msg",
							medicalDesc, lotInfo, wardStoreQty));
				}
			}
		}
		List<OHExceptionMessage> errors = new ArrayList<>();
		if (lotUpdated) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.inventory.validate.btn"),
				MessageBundle.formatMessage("angal.inventory.theoreticalqtyhavebeenupdatedforsomemedicalward.fmt.msg", medDescriptionForLotUpdated),
				OHSeverityLevel.INFO));
		}
		if (lotAdded) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.inventory.validate.btn"),
				MessageBundle.formatMessage("angal.inventory.newlotshavebeenaddedforsomemedicalward.fmt.msg", medDescriptionForNewLot),
				OHSeverityLevel.INFO));
		}
		if (medicalAdded) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.inventory.validate.btn"),
				MessageBundle.formatMessage("angal.inventory.newmedicalshavebeenfoundward.fmt.msg", medDescriptionForNewMedical), OHSeverityLevel.INFO));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	/**
	 * Marks an inventory as deleted by changing its status.
	 * 
	 * @param medicalInventory the medicalInventory of the inventory to delete.
	 * @throws OHServiceException if an error occurs during the operation.
	 */
	public void deleteInventory(MedicalInventory medicalInventory) throws OHServiceException {
		int inventoryId = medicalInventory.getId();
		List<MedicalInventoryRow> inventoryRows = medicalInventoryRowManager.getMedicalInventoryRowByInventoryId(inventoryId);
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

	/**
	 * Confirm the Inventory rows of inventory.
	 *
	 * @param inventory the {@link MedicalInventory}
	 * @param inventoryRowSearchList- The list of {@link MedicalInventory}
	 * @return List {@link Movement}. It could be {@code empty}.
	 * @throws OHServiceException
	 */
	@Transactional(rollbackFor = OHServiceException.class)
	public List<Movement> confirmMedicalInventoryRow(MedicalInventory inventory, List<MedicalInventoryRow> inventoryRowSearchList) throws OHServiceException {
		// validate the inventory
		this.validateMedicalInventoryRow(inventory, inventoryRowSearchList);

		// get general info
		String referenceNumber = inventory.getInventoryReference();
		// TODO: to explore the possibility to allow charges and discharges with same referenceNumber
		String chargeReferenceNumber = referenceNumber + "-charge";
		String dischargeReferenceNumber = referenceNumber + "-discharge";
		MovementType chargeType = medicalDsrStockMovementTypeBrowserManager.getMovementType(inventory.getChargeType());
		MovementType dischargeType = medicalDsrStockMovementTypeBrowserManager.getMovementType(inventory.getDischargeType());
		Supplier supplier = supplierManager.getByID(inventory.getSupplier());
		Ward ward = wardManager.findWard(inventory.getDestination());
		LocalDateTime now = TimeTools.getNow();
		// prepare movements
		List<Movement> chargeMovements = new ArrayList<>();
		List<Movement> dischargeMovements = new ArrayList<>();
		for (MedicalInventoryRow medicalInventoryRow : inventoryRowSearchList) {
			double theoQty = medicalInventoryRow.getTheoreticQty();
			double realQty = medicalInventoryRow.getRealQty();
			Double ajustQty = realQty - theoQty;
			Medical medical = medicalInventoryRow.getMedical();
			Lot currentLot = medicalInventoryRow.getLot();
			if (ajustQty > 0) { // charge movement when realQty > theoQty
				Movement movement = new Movement(medical, chargeType, null, currentLot, now, ajustQty.intValue(), supplier, chargeReferenceNumber);
				chargeMovements.add(movement);
			} else if (ajustQty < 0) { // discharge movement when realQty < theoQty
				Movement movement = new Movement(medical, dischargeType, ward, currentLot, now, -ajustQty.intValue(), null, dischargeReferenceNumber);
				dischargeMovements.add(movement);
			} // else ajustQty = 0, continue
		}
		// create movements
		List<Movement> insertedMovements = new ArrayList<>();
		if (!chargeMovements.isEmpty()) {
			insertedMovements.addAll(movStockInsertingManager.newMultipleChargingMovements(chargeMovements, chargeReferenceNumber));
		}
		if (!dischargeMovements.isEmpty()) {
			insertedMovements.addAll(movStockInsertingManager.newMultipleDischargingMovements(dischargeMovements, dischargeReferenceNumber));
		}
		String status = InventoryStatus.done.toString();
		inventory.setStatus(status);
		this.updateMedicalInventory(inventory, false);
		return insertedMovements;
	}

	private void checkReference(MedicalInventory medicalInventory) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();
		String reference = medicalInventory.getInventoryReference();
		String chargeReferenceNumber = reference + "-charge";
		String dischargeReferenceNumber = reference + "-discharge";
		boolean existWithSuffixCharge = movStockInsertingManager.refNoExists(chargeReferenceNumber);
		boolean existWithSuffixDischarge = movStockInsertingManager.refNoExists(dischargeReferenceNumber);
		MedicalInventory inventory = this.getInventoryByReference(reference);
		if (existWithSuffixCharge || existWithSuffixDischarge || inventory != null && inventory.getId() != medicalInventory.getId()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.inventory.referencealreadyused.msg")));
		}
		if (!errors.isEmpty()) {
			throw new OHServiceException(errors);
		}
	}

	/**
	 * Actualize the {@link MedicalInventory}.
	 *
	 * @param inventory the {@link MedicalInventory}
	 * @return {@link MedicalInventory}. It could be {@code null}.
	 * @throws OHServiceException
	 */
	public MedicalInventory actualizeMedicalInventoryRow(MedicalInventory inventory) throws OHServiceException {
		LocalDateTime movFrom = inventory.getInventoryDate();
		LocalDateTime movTo = TimeTools.getNow();
		// Fetch all the inventory row of that inventory
		int id = inventory.getId();
		List<MedicalInventoryRow> inventoryRowList = medicalInventoryRowManager.getMedicalInventoryRowByInventoryId(id);
		// TODO: To decide if to make allMedicals parameter
		boolean allMedicals = true;
		List<Movement> movs = new ArrayList<>();
		List<Medical> inventoryMedicalsList = inventoryRowList.stream()
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
		// Get all the lot of the movements
		List<Lot> lotOfMovements = movs.stream().map(Movement::getLot).collect(Collectors.toList());
		// Remove duplicates by converting the list to a set
		Set<Lot> uniqueLots = new HashSet<>(lotOfMovements);
		// Convert the set back to a list
		List<Lot> uniqueLotList = new ArrayList<>(uniqueLots);
		// Cycle fetched movements to see if they impact inventoryRowSearchList
		for (Lot lot : uniqueLotList) {
			String lotCodeOfMovement = lot.getCode();
			Medical medical = lot.getMedical();
			Integer medicalCode = medical.getCode();
			// Fetch also empty lots because some movements may have discharged them completely
			Optional<Lot> optLot = movStockInsertingManager.getLotByMedical(medical, false).stream().filter(l -> l.getCode().equals(lotCodeOfMovement))
				.findFirst();
			double mainStoreQty = optLot.get().getMainStoreQuantity();

			// Search for the specific Lot and Medical in inventoryRowSearchList (Lot should be enough)
			Optional<MedicalInventoryRow> matchingRow = inventoryRowList.stream()
				.filter(row -> row.getLot().getCode().equals(lotCodeOfMovement) && row.getMedical().getCode().equals(medicalCode))
				.findFirst();

			if (matchingRow.isPresent()) {
				MedicalInventoryRow medicalInventoryRow = matchingRow.get();
				double theoQty = medicalInventoryRow.getTheoreticQty();
				if (mainStoreQty != theoQty) {
					// Update Lot
					medicalInventoryRow.setTheoreticQty(mainStoreQty);
					medicalInventoryRowManager.updateMedicalInventoryRow(medicalInventoryRow);
				}
			} else {
				// TODO: to decide if to give control to the user about this
				double realQty = mainStoreQty;
				MedicalInventoryRow newMedicalInventoryRow = new MedicalInventoryRow(null, mainStoreQty, realQty, inventory, medical,
					lot);
				medicalInventoryRowManager.newMedicalInventoryRow(newMedicalInventoryRow);
			}
		}
		return this.updateMedicalInventory(inventory, true);
	}

	/**
	 * Return the number of inventories by {@link InventoryType}.
	 * 
	 * @param type {@link InventoryType}
	 * @return the number of records or zero.
	 */
	public int getInventoryCount(String type) {
		return ioOperations.getInventoryCount(type);
	}

	private void buildStatusHashMap() {
		statusHashMap = new HashMap<>(4);
		statusHashMap.put("canceled", MessageBundle.getMessage("angal.inventory.status.canceled.txt"));
		statusHashMap.put("draft", MessageBundle.getMessage("angal.inventory.status.draft.txt"));
		statusHashMap.put("done", MessageBundle.getMessage("angal.inventory.status.done.txt"));
		statusHashMap.put("validated", MessageBundle.getMessage("angal.inventory.status.validated.txt"));
	}

	/**
	 * Return a list of status: draft, done, canceled, validated
	 *
	 * @return
	 */
	public List<String> getStatusList() {
		if (statusHashMap == null) {
			buildStatusHashMap();
		}
		List<String> statusList = new ArrayList<>(statusHashMap.values());
		statusList.sort(new DefaultSorter(MessageBundle.getMessage("angal.inventory.status.draft.txt")));
		return statusList;
	}

	/**
	 * Return a value of the key on statusHashMap.
	 * 
	 * @param key - the key of status
	 * @return the value of the key or empty string if key is not on statusHashMap.
	 */
	public String getStatusByKey(String key) {
		if (statusHashMap == null) {
			buildStatusHashMap();
		}
		for (Map.Entry<String, String> entry : statusHashMap.entrySet()) {
			if (entry.getKey().equals(key)) {
				return entry.getValue();
			}
		}
		return "";
	}

	/**
	 * Actualize the {@link MedicalInventory}'s ward.
	 *
	 * @param inventory the {@link MedicalInventory}
	 * @return {@link MedicalInventory}. It could be {@code null}.
	 * @throws OHServiceException
	 */
	public MedicalInventory actualizeMedicalWardInventoryRow(MedicalInventory inventory) throws OHServiceException {
		LocalDateTime movFrom = inventory.getInventoryDate();
		LocalDateTime movTo = TimeTools.getNow();

		List<MovementWard> movementWards = new ArrayList<>(movWardBrowserManager.getMovementWard(inventory.getWard(), movFrom, movTo));
		List<Movement> movementToWards = new ArrayList<>(movBrowserManager.getMovements(inventory.getWard(), movFrom, movTo));
		List<MedicalInventoryRow> inventoryRowList = medicalInventoryRowManager.getMedicalInventoryRowByInventoryId(inventory.getId());

		// Get all the lots from the ward movements
		List<Lot> lotOfMovements = new ArrayList<>(movementWards.stream().map(MovementWard::getLot).toList());
		// Get all the lots from the main store movements
		lotOfMovements.addAll(movementToWards.stream().map(Movement::getLot).toList());
		// Remove duplicates by converting the list to a set
		Set<Lot> uniqueLots = new HashSet<>(lotOfMovements);
		// Convert the set back to a list
		List<Lot> uniqueLotList = new ArrayList<>(uniqueLots);
		// Cycle fetched movements to see if they impact inventoryRowSearchList
		for (Lot lot : uniqueLotList) {
			String lotCode = lot.getCode();
			Medical medical = lot.getMedical();
			Integer medicalCode = medical.getCode();
			// Fetch also empty lots because some movements may have discharged them completely
			Optional<MedicalWard> optMedicalWard = movWardBrowserManager.getMedicalsWard(inventory.getWard(), medical.getCode(), false).stream()
				.filter(m -> m.getLot().getCode().equals(lotCode)).findFirst();
			double wardStoreQty = optMedicalWard.isPresent() ? optMedicalWard.get().getQty() : 0.0;

			// Search for the specific Lot and Medical in inventoryRowSearchList (Lot should be enough)
			Optional<MedicalInventoryRow> matchingRow = inventoryRowList.stream()
				.filter(row -> row.getLot().getCode().equals(lotCode) && row.getMedical().getCode().equals(medicalCode))
				.findFirst();

			if (matchingRow.isPresent()) {
				MedicalInventoryRow medicalInventoryRow = matchingRow.get();
				double theoQty = medicalInventoryRow.getTheoreticQty();
				if (wardStoreQty != theoQty) {
					// Update Lot
					medicalInventoryRow.setTheoreticQty(wardStoreQty);
					medicalInventoryRow.setRealqty(wardStoreQty);
					medicalInventoryRowManager.updateMedicalInventoryRow(medicalInventoryRow);
				}
			} else {
				// TODO: to decide if to give control to the user about this
				double realQty = wardStoreQty;
				MedicalInventoryRow newMedicalInventoryRow = new MedicalInventoryRow(null, wardStoreQty, realQty, inventory, medical, lot);
				medicalInventoryRowManager.newMedicalInventoryRow(newMedicalInventoryRow);
			}
		}
		return this.updateMedicalInventory(inventory, true);
	}
}
