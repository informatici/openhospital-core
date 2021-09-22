/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.accounting.service;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItems;
import org.isf.accounting.model.BillPayments;
import org.isf.patient.model.Patient;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.time.TimeTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persistence class for Accounting module.
 */
@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class AccountingIoOperations {	
	
	@Autowired
	private AccountingBillIoOperationRepository billRepository;
	@Autowired
	private AccountingBillPaymentIoOperationRepository billPaymentRepository;
	@Autowired
	private AccountingBillItemsIoOperationRepository billItemsRepository;
	
	
	/**
	 * Returns all the pending {@link Bill}s for the specified patient.
	 * @param patID the patient id.
	 * @return the list of pending bills.
	 */
	public List<Bill> getPendingBills(int patID) {
		if (patID != 0) {
			return billRepository.findByStatusAndBillPatientCodeOrderByDateDesc("O", patID);
		}
		return billRepository.findByStatusOrderByDateDesc("O");
	}
	
	/**
	 * Get all the {@link Bill}s.
	 * @return a list of bills.
	 */
	public List<Bill> getBills() {
		return billRepository.findAllByOrderByDateDesc();
	}
	
	/**
	 * Get the {@link Bill} with specified billID.
	 * @param billID
	 * @return the {@link Bill}.
	 */
	public Bill getBill(int billID) {
		return billRepository.findOne(billID);
	}

	/**
	 * Returns all user ids from {@link BillPayments}.
	 * @return a list of user id.
	 */
    public List<String> getUsers() {
    	Set<String> accountingUsers = new TreeSet<>(String::compareTo);
    	accountingUsers.addAll(billRepository.findUserDistinctByOrderByUserAsc());
    	accountingUsers.addAll(billPaymentRepository.findUserDistinctByOrderByUserAsc());
		return new ArrayList<>(accountingUsers);
	}

	/**
	 * Returns the {@link BillItems} associated to the specified {@link Bill} id or all 
	 * the stored {@link BillItems} if no id is provided. 
	 * @param billID the bill id or <code>0</code>.
	 * @return a list of {@link BillItems} associated to the bill id or all the stored bill items.
	 */
	public List<BillItems> getItems(int billID) {
		if (billID != 0) {
			return billItemsRepository.findByBill_idOrderByIdAsc(billID);
		}
		return billItemsRepository.findAllByOrderByIdAsc();
	}

	/**
	 * Retrieves all the {@link BillPayments} for the specified date range.
	 * @param dateFrom low endpoint, inclusive, for the date range. 
	 * @param dateTo high endpoint, inclusive, for the date range.
	 * @return a list of {@link BillPayments} for the specified date range.
	 */
	public List<BillPayments> getPayments(GregorianCalendar dateFrom, GregorianCalendar dateTo) {
		return billPaymentRepository.findByDateBetweenOrderByIdAscDateAsc(TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo));
	}

	/**
	 * Retrieves all the {@link BillPayments} for the specified {@link Bill} id, or all 
	 * the stored {@link BillPayments} if no id is indicated.
	 * @param billID the bill id or <code>0</code>.
	 * @return the list of bill payments.
	 */
	public List<BillPayments> getPayments(int billID) {
		if (billID != 0) {
			return billPaymentRepository.findAllWherBillIdByOrderByBillAndDate(billID);
		}
		return billPaymentRepository.findAllByOrderByBillAndDate();
	}

	/**
	 * Stores a new {@link Bill}.
	 * @param newBill the bill to store.
	 * @return the generated {@link Bill} id.
	 */
	public int newBill(Bill newBill) {
		return billRepository.save(newBill).getId();
	}

	/**
	 * Stores a list of {@link BillItems} associated to a {@link Bill}.
	 * @param bill the bill.
	 * @param billItems the bill items to store.
	 * @return <code>true</code> if the {@link BillItems} have been store, <code>false</code> otherwise.
	 */
	public boolean newBillItems(Bill bill, List<BillItems> billItems) {

		// deleteBillsInsideBillItems(bill.getId());
		billItemsRepository.deleteWhereId(bill.getId());

		// insertNewBillInsideBillItems(bill, billItems);
		for (BillItems item : billItems) {
			item.setBill(bill);
			billItemsRepository.save(item);
		}
		return true;
	}

	/**
	 * Stores a list of {@link BillPayments} associated to a {@link Bill}.
	 * @param bill the bill.
	 * @param payItems the bill payments.
	 * @return <code>true</code> if the payment have stored, <code>false</code> otherwise.
	 */
	public boolean newBillPayments(Bill bill, List<BillPayments> payItems) {

		// deleteBillsInsideBillPayments(bill.getId());
		billPaymentRepository.deleteWhereId(bill.getId());

		// insertNewBillInsideBillPayments(bill, payItems);
		for (BillPayments payment : payItems) {
			payment.setBill(bill);
			billPaymentRepository.save(payment);
		}
		return true;
	}

	/**
	 * Updates the specified {@link Bill}.
	 * @param updateBill the bill to update.
	 * @return <code>true</code> if the bill has been updated, <code>false</code> otherwise.
	 */
	public boolean updateBill(Bill updateBill) {
		Bill savedBill = billRepository.save(updateBill);
		return savedBill != null;
	}

	/**
	 * Deletes the specified {@link Bill}.
	 * @param deleteBill the bill to delete.
	 * @return <code>true</code> if the bill has been deleted, <code>false</code> otherwise.
	 */
	public boolean deleteBill(Bill deleteBill) {
		billRepository.updateDeleteWhereId(deleteBill.getId());
		return true;
	}

	/**
	 * Retrieves all the {@link Bill}s for the specified date range.
	 * @param dateFrom the low date range endpoint, inclusive. 
	 * @param dateTo the high date range endpoint, inclusive.
	 * @return a list of retrieved {@link Bill}s.
	 * @deprecated use {@link #getBillsBetweenDates(GregorianCalendar, GregorianCalendar)}
	 */
	@Deprecated
	public List<Bill> getBills(GregorianCalendar dateFrom, GregorianCalendar dateTo) {
		return getBillsBetweenDates(dateFrom, dateTo);
	}

	/**
	 * Retrieves all the {@link Bill}s for the specified date range.
	 * @param dateFrom the low date range endpoint, inclusive.
	 * @param dateTo the high date range endpoint, inclusive.
	 * @return a list of retrieved {@link Bill}s.
	 */
	public List<Bill> getBillsBetweenDates(GregorianCalendar dateFrom, GregorianCalendar dateTo) {
		return billRepository.findByDateBetween(TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo));
	}

	/**
	 * Gets all the {@link Bill}s associated to the passed {@link BillPayments}.
	 * @param payments the {@link BillPayments} associated to the bill to retrieve.
	 * @return a list of {@link Bill} associated to the passed {@link BillPayments}.
	 */
	public List<Bill> getBills(List<BillPayments> payments) {
		Set<Bill> bills = new TreeSet<>((o1, o2) -> o1.getId() == o2.getId() ? 0 : -1);
		for (BillPayments bp : payments) {
			bills.add(bp.getBill());
		}
		return new ArrayList<>(bills);
	}

	/**
	 * Retrieves all the {@link BillPayments} associated to the passed {@link Bill} list.
	 * @param bills the bill list.
	 * @return a list of {@link BillPayments} associated to the passed bill list.
	 */
	public List<BillPayments> getPayments(List<Bill> bills) {
		return billPaymentRepository.findAllByBillIn(bills);
	}
	
	/**
	 * Retrieves all billPayments for a given patient in the period dateFrom -> dateTo
	 * @param dateFrom
	 * @param dateTo
	 * @param patient
	 * @return
	 * @deprecated use {@link #getPaymentsBetweenDatesWherePatient(GregorianCalendar, GregorianCalendar, Patient)}
	 */
	@Deprecated
	public List<BillPayments> getPayments(GregorianCalendar dateFrom, GregorianCalendar dateTo, Patient patient) {
		return getPaymentsBetweenDatesWherePatient(dateFrom, dateTo, patient);
	}

	/**
	 * Retrieves all billPayments for a given patient in the period dateFrom -> dateTo
	 * @param dateFrom
	 * @param dateTo
	 * @param patient
	 * @return
	 */
	public List<BillPayments> getPaymentsBetweenDatesWherePatient(GregorianCalendar dateFrom, GregorianCalendar dateTo, Patient patient) {
		return billPaymentRepository.findByDateAndPatient(dateFrom, dateTo, patient.getCode());
	}

	/**
	 * Retrieves all the bills for a given patient in the period dateFrom -> dateTo
	 * @param dateFrom
	 * @param dateTo
	 * @param patient
	 * @return the bill list
	 * @deprecated use {@link #getBillsBetweenDatesWherePatient(GregorianCalendar, GregorianCalendar, Patient)}
	 */
	@Deprecated
	public List<Bill> getBills(GregorianCalendar dateFrom, GregorianCalendar dateTo, Patient patient) {
		return getBillsBetweenDatesWherePatient(dateFrom, dateTo, patient);
	}

	/**
	 * Retrieves all the bills for a given patient in the period dateFrom -> dateTo
	 * @param dateFrom
	 * @param dateTo
	 * @param patient
	 * @return the bill list
	 */
	public List<Bill> getBillsBetweenDatesWherePatient(GregorianCalendar dateFrom, GregorianCalendar dateTo, Patient patient) {
		return billRepository.findByDateAndPatient(dateFrom, dateTo, patient.getCode());
	}

	/**
	 * 
	 * @param patID
	 * @return
	 */
	public List<Bill> getPendingBillsAffiliate(int patID) {
		return billRepository.findAllPendindBillsByBillPatient(patID);
	}

	/**
	 *
	 * @param patID
	 * @return
	 */
	public List<Bill> getAllPatientsBills(int patID) {
		return billRepository.findByBillPatientCode(patID);
	}

	/**
	 * Return distinct BillItems
	 * added by u2g
	 * @return BillItems list 
	 */
	public List<BillItems> getDistictsBillItems() {
		return billItemsRepository.findAllGroupByDescription();
	}
	
	/**
	 * Return the bill list which date between dateFrom and dateTo and containing given billItem
	 *
	 * added by u2g
	 *
	 * @param dateFrom
	 * @param dateTo
	 * @param billItem
	 * @return the bill list
	 */
	@Deprecated
	public List<Bill> getBills(GregorianCalendar dateFrom, GregorianCalendar dateTo, BillItems billItem) {
		return getBillsBetweenDatesWhereBillItem(dateFrom, dateTo, billItem);
	}

	/**
	 * Return the bill list which date between dateFrom and dateTo and containing given billItem
	 *
	 * @param dateFrom
	 * @param dateTo
	 * @param billItem
	 * @return the bill list
	 */
	public List<Bill> getBillsBetweenDatesWhereBillItem(GregorianCalendar dateFrom, GregorianCalendar dateTo, BillItems billItem) {
		if (billItem == null) {
			return billRepository.findByDateBetween(TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo));
		}
		return billRepository.findAllWhereDatesAndBillItem(TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo), billItem.getItemDescription());
	}
}
