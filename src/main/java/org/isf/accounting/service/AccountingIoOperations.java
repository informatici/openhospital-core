/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.accounting.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItems;
import org.isf.accounting.model.BillPayments;
import org.isf.patient.model.Patient;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.time.TimeTools;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persistence class for Accounting module.
 */
@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class AccountingIoOperations {

	private AccountingBillIoOperationRepository billRepository;
	private AccountingBillPaymentIoOperationRepository billPaymentRepository;
	private AccountingBillItemsIoOperationRepository billItemsRepository;

	public AccountingIoOperations(AccountingBillIoOperationRepository accountingBillIoOperationRepository,
		AccountingBillPaymentIoOperationRepository accountingBillPaymentIoOperationRepository,
		AccountingBillItemsIoOperationRepository accountingBillItemsIoOperationRepository) {
		this.billRepository = accountingBillIoOperationRepository;
		this.billPaymentRepository = accountingBillPaymentIoOperationRepository;
		this.billItemsRepository = accountingBillItemsIoOperationRepository;
	}

	/**
	 * Returns all the pending {@link Bill}s for the specified patient.
	 * 
	 * @param patID the patient id.
	 * @return the list of pending bills.
	 * @throws OHServiceException if an error occurs retrieving the pending bills.
	 */
	public List<Bill> getPendingBills(int patID) throws OHServiceException {
		if (patID != 0) {
			return billRepository.findByStatusAndBillPatientCodeOrderByDateDesc("O", patID);
		}
		return billRepository.findByStatusOrderByDateDesc("O");
	}

	/**
	 * Get all the {@link Bill}s.
	 * 
	 * @return a list of bills.
	 * @throws OHServiceException if an error occurs retrieving the bills.
	 */
	public List<Bill> getBills() throws OHServiceException {
		return billRepository.findAllByOrderByDateDesc();
	}

	/**
	 * Get the {@link Bill} with specified billID.
	 * 
	 * @param billID
	 * @return the {@link Bill}.
	 * @throws OHServiceException if an error occurs retrieving the bill.
	 */
	public Bill getBill(int billID) throws OHServiceException {
		return billRepository.findById(billID).orElse(null);
	}

	/**
	 * Returns all user ids from {@link BillPayments}.
	 * 
	 * @return a list of user id.
	 * @throws OHServiceException if an error occurs retrieving the users list.
	 */
	public List<String> getUsers() throws OHServiceException {
		Set<String> accountingUsers = new TreeSet<>(String::compareTo);
		accountingUsers.addAll(billRepository.findUserDistinctByOrderByUserAsc());
		accountingUsers.addAll(billPaymentRepository.findUserDistinctByOrderByUserAsc());
		return new ArrayList<>(accountingUsers);
	}

	/**
	 * Returns the {@link BillItems} associated to the specified {@link Bill} id or all the stored {@link BillItems} if no id is provided.
	 * 
	 * @param billID the bill id or {@code 0}.
	 * @return a list of {@link BillItems} associated to the bill id or all the stored bill items.
	 * @throws OHServiceException if an error occurs retrieving the bill items.
	 */
	public List<BillItems> getItems(int billID) throws OHServiceException {
		if (billID != 0) {
			return billItemsRepository.findByBill_idOrderByIdAsc(billID);
		}
		return billItemsRepository.findAllByOrderByIdAsc();
	}

	/**
	 * Retrieves all the {@link BillPayments} for the specified date range.
	 * 
	 * @param dateFrom low endpoint, inclusive, for the date range.
	 * @param dateTo high endpoint, inclusive, for the date range.
	 * @return a list of {@link BillPayments} for the specified date range.
	 * @throws OHServiceException if an error occurs retrieving the bill payments.
	 */
	public List<BillPayments> getPayments(LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		return billPaymentRepository.findByDateBetweenOrderByIdAscDateAsc(TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo));
	}

	/**
	 * Retrieves all the {@link BillPayments} for the specified {@link Bill} id, or all the stored {@link BillPayments} if no id is indicated.
	 * 
	 * @param billID the bill id or {@code 0}.
	 * @return the list of bill payments.
	 * @throws OHServiceException if an error occurs retrieving the bill payments.
	 */
	public List<BillPayments> getPayments(int billID) throws OHServiceException {
		if (billID != 0) {
			return billPaymentRepository.findAllWherBillIdByOrderByBillAndDate(billID);
		}
		return billPaymentRepository.findAllByOrderByBillAndDate();
	}

	/**
	 * Stores a new {@link Bill}.
	 * 
	 * @param newBill the bill to store.
	 * @return the persisted Bill object
	 * @throws OHServiceException if the bill already exists or an error occurs storing the bill.
	 */
	public Bill newBill(Bill newBill) throws OHServiceException {
		if (newBill.getId() != 0) {
			throw new OHServiceException(new OHExceptionMessage("Bill '" + newBill.getId() + "' already exists."));
		}
		return billRepository.save(newBill);
	}

	/**
	 * Synchronizes the {@link BillItems} of the specified {@link Bill} with the passed list. Items with a matching id are updated in place, items with id
	 * {@code 0} are inserted as new rows and stored items missing from the list are deleted (orphan removal).
	 *
	 * @param bill the bill.
	 * @param billItems the bill items to store.
	 * @throws OHServiceException if the bill is not found or an error occurs during the store operation.
	 */
	public void newBillItems(Bill bill, List<BillItems> billItems) throws OHServiceException {
		Bill managedBill = billRepository.findById(bill.getId()).orElse(null);
		if (managedBill == null) {
			throw new OHServiceException(new OHExceptionMessage("Bill '" + bill.getId() + "' not found."));
		}
		List<BillItems> currentItems = managedBill.getItems();
		Set<Integer> incomingIds = new HashSet<>();
		for (BillItems item : billItems) {
			if (item.getId() != 0) {
				incomingIds.add(item.getId());
			}
		}
		currentItems.removeIf(currentItem -> !incomingIds.contains(currentItem.getId()));
		Set<Integer> mergedIds = new HashSet<>();
		for (BillItems item : billItems) {
			BillItems managedItem = null;
			if (item.getId() != 0 && mergedIds.add(item.getId())) {
				managedItem = findBillItem(currentItems, item.getId());
			}
			if (managedItem != null) {
				managedItem.setPrice(item.isPrice());
				managedItem.setPriceID(item.getPriceID());
				managedItem.setItemDescription(item.getItemDescription());
				managedItem.setItemAmount(item.getItemAmount());
				managedItem.setItemQuantity(item.getItemQuantity());
			} else if (item.getId() == 0) {
				item.setBill(managedBill);
				currentItems.add(item);
			} else {
				currentItems.add(new BillItems(0, managedBill, item.isPrice(), item.getPriceID(), item.getItemDescription(), item.getItemAmount(),
					item.getItemQuantity()));
			}
		}
		billRepository.flush();
	}

	/**
	 * Synchronizes the {@link BillPayments} of the specified {@link Bill} with the passed list. Payments with a matching id are updated in place, payments with
	 * id {@code 0} are inserted as new rows and stored payments missing from the list are deleted (orphan removal).
	 *
	 * @param bill the bill.
	 * @param payItems the bill payments.
	 * @throws OHServiceException if the bill is not found or an error occurs during the store procedure.
	 */
	public void newBillPayments(Bill bill, List<BillPayments> payItems) throws OHServiceException {
		Bill managedBill = billRepository.findById(bill.getId()).orElse(null);
		if (managedBill == null) {
			throw new OHServiceException(new OHExceptionMessage("Bill '" + bill.getId() + "' not found."));
		}
		List<BillPayments> currentPayments = managedBill.getPayments();
		Set<Integer> incomingIds = new HashSet<>();
		for (BillPayments payment : payItems) {
			if (payment.getId() != 0) {
				incomingIds.add(payment.getId());
			}
		}
		currentPayments.removeIf(currentPayment -> !incomingIds.contains(currentPayment.getId()));
		Set<Integer> mergedIds = new HashSet<>();
		for (BillPayments payment : payItems) {
			BillPayments managedPayment = null;
			if (payment.getId() != 0 && mergedIds.add(payment.getId())) {
				managedPayment = findBillPayment(currentPayments, payment.getId());
			}
			if (managedPayment != null) {
				managedPayment.setDate(payment.getDate());
				managedPayment.setAmount(payment.getAmount());
				managedPayment.setUser(payment.getUser());
			} else if (payment.getId() == 0) {
				payment.setBill(managedBill);
				currentPayments.add(payment);
			} else {
				currentPayments.add(new BillPayments(0, managedBill, payment.getDate(), payment.getAmount(), payment.getUser()));
			}
		}
		billRepository.flush();
	}

	private BillItems findBillItem(List<BillItems> items, int id) {
		for (BillItems item : items) {
			if (item.getId() == id) {
				return item;
			}
		}
		return null;
	}

	private BillPayments findBillPayment(List<BillPayments> payments, int id) {
		for (BillPayments payment : payments) {
			if (payment.getId() == id) {
				return payment;
			}
		}
		return null;
	}

	/**
	 * Updates the specified {@link Bill}. The scalar fields of the stored bill are updated from the passed object; the associated {@link BillItems} and
	 * {@link BillPayments} are left untouched (use {@link #newBillItems} and {@link #newBillPayments} to update them).
	 *
	 * @param updateBill the bill to update.
	 * @return the updated Bill object
	 * @throws OHServiceException if an error occurs during the update.
	 */
	public Bill updateBill(Bill updateBill) throws OHServiceException {
		Bill managedBill = billRepository.findById(updateBill.getId()).orElse(null);
		if (managedBill == null) {
			return billRepository.save(updateBill);
		}
		managedBill.setDate(updateBill.getDate());
		managedBill.setUpdate(updateBill.getUpdate());
		managedBill.setIsList(updateBill.isList());
		managedBill.setPriceList(updateBill.getPriceList());
		managedBill.setListName(updateBill.getListName());
		managedBill.setIsPatient(updateBill.isPatient());
		managedBill.setBillPatient(updateBill.getBillPatient());
		managedBill.setPatName(updateBill.getPatName());
		managedBill.setStatus(updateBill.getStatus());
		managedBill.setAmount(updateBill.getAmount());
		managedBill.setBalance(updateBill.getBalance());
		managedBill.setUser(updateBill.getUser());
		managedBill.setAdmission(updateBill.getAdmission());
		return billRepository.save(managedBill);
	}

	/**
	 * Deletes the specified {@link Bill}. If the argument is NULL then an error is thrown. If the Bill is not found it is silently ignored.
	 * 
	 * @param deleteBill the bill to delete.
	 * @throws OHServiceException if an error occurs deleting the bill.
	 */
	public void deleteBill(Bill deleteBill) throws OHServiceException {
		billRepository.deleteById(deleteBill.getId());
	}

	/**
	 * Retrieves all the {@link Bill}s for the specified date range.
	 * 
	 * @param dateFrom the low date range endpoint, inclusive.
	 * @param dateTo the high date range endpoint, inclusive.
	 * @return a list of retrieved {@link Bill}s.
	 * @throws OHServiceException if an error occurs retrieving the bill list.
	 */
	public List<Bill> getBillsBetweenDates(LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		return billRepository.findByDateBetween(TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo));
	}

	/**
	 * Gets all the {@link Bill}s associated to the passed {@link BillPayments}.
	 * 
	 * @param payments the {@link BillPayments} associated to the bill to retrieve.
	 * @return a list of {@link Bill} associated to the passed {@link BillPayments}.
	 * @throws OHServiceException if an error occurs retrieving the bill list.
	 */
	public List<Bill> getBills(List<BillPayments> payments) throws OHServiceException {
		Set<Bill> bills = new TreeSet<>((o1, o2) -> o1.getId() == o2.getId() ? 0 : -1);
		for (BillPayments bp : payments) {
			bills.add(bp.getBill());
		}
		return new ArrayList<>(bills);
	}

	/**
	 * Retrieves all the {@link BillPayments} associated to the passed {@link Bill} list.
	 * 
	 * @param bills the bill list.
	 * @return a list of {@link BillPayments} associated to the passed bill list.
	 * @throws OHServiceException if an error occurs retrieving the payments.
	 */
	public List<BillPayments> getPayments(List<Bill> bills) throws OHServiceException {
		return billPaymentRepository.findAllByBillIn(bills);
	}

	/**
	 * Retrieves all billPayments for a given patient in the period dateFrom -> dateTo
	 * 
	 * @param dateFrom
	 * @param dateTo
	 * @param patient
	 * @return
	 * @throws OHServiceException
	 */
	public List<BillPayments> getPaymentsBetweenDatesWherePatient(LocalDateTime dateFrom, LocalDateTime dateTo, Patient patient)
		throws OHServiceException {
		return billPaymentRepository.findByDateAndPatient(TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo), patient.getCode());
	}

	/**
	 * Retrieves all the bills for a given patient in the period dateFrom -> dateTo
	 * 
	 * @param dateFrom
	 * @param dateTo
	 * @param patient
	 * @return the bill list
	 * @throws OHServiceException
	 */
	public List<Bill> getBillsBetweenDatesWherePatient(LocalDateTime dateFrom, LocalDateTime dateTo, Patient patient) throws OHServiceException {
		return billRepository.findByDateAndPatient(TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo), patient.getCode());
	}

	/**
	 * 
	 * @param patID
	 * @return
	 * @throws OHServiceException
	 */
	public List<Bill> getPendingBillsAffiliate(int patID) throws OHServiceException {
		return billRepository.findAllPendindBillsByBillPatient(patID);
	}

	/**
	 *
	 * @param patID
	 * @return
	 * @throws OHServiceException
	 */
	public List<Bill> getAllPatientsBills(int patID) throws OHServiceException {
		return billRepository.findByBillPatientCode(patID);
	}

	/**
	 * Return distinct BillItems
	 * 
	 * @return BillItems list
	 * @throws OHServiceException
	 */
	public List<BillItems> getDistictsBillItems() throws OHServiceException {
		return billItemsRepository.findAllGroupByDescription();
	}

	/**
	 * Return the bill list which date between dateFrom and dateTo and containing given billItem
	 *
	 * @param dateFrom
	 * @param dateTo
	 * @param billItem
	 * @return the bill list
	 * @throws OHServiceException
	 */
	public List<Bill> getBillsBetweenDatesWhereBillItem(LocalDateTime dateFrom, LocalDateTime dateTo, BillItems billItem) throws OHServiceException {
		if (billItem == null) {
			return billRepository.findByDateBetween(TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo));
		}
		return billRepository.findAllWhereDatesAndBillItem(TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo),
			billItem.getItemDescription());
	}

	/**
	 * Count active {@link Bill}s
	 * 
	 * @return the number of recorded {@link Bill}s
	 * @throws OHServiceException
	 */
	public long countAllActiveBills() {
		return this.billRepository.countAllActiveBills();
	}

}
