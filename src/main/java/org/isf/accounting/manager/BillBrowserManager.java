/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.accounting.manager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItems;
import org.isf.accounting.model.BillPayments;
import org.isf.accounting.service.AccountingIoOperations;
import org.isf.generaldata.MessageBundle;
import org.isf.patient.model.Patient;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.time.TimeTools;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BillBrowserManager {

	private final AccountingIoOperations ioOperations;

	public BillBrowserManager(AccountingIoOperations accountingIoOperations) {
		this.ioOperations = accountingIoOperations;
	}

	/**
	 * Validates structural and business rules that can be checked <em>before</em> any DB operation: dates, patient name, non-empty item list.
	 *
	 * <p>
	 * The balance-vs-status check is intentionally absent here because {@code BLL_BALANCE} is only authoritative after {@link #recalculateTotals}. Call
	 * {@link #validateClosedBillBalance} after recalculation.
	 *
	 * @param bill the bill to validate
	 * @param billItems effective item lines — must not be empty
	 * @param billPayments payment lines used for date-ordering checks
	 * @throws OHDataValidationException if any rule is violated
	 */
	protected void validateBill(Bill bill, List<BillItems> billItems, List<BillPayments> billPayments) throws OHDataValidationException {
		List<OHExceptionMessage> errors = new ArrayList<>();

		LocalDateTime today = TimeTools.getNow();
		LocalDateTime upDate;
		LocalDateTime firstPay = today;
		LocalDateTime lastPay = today;

		LocalDateTime billDate = bill.getDate();
		if (!billPayments.isEmpty()) {
			firstPay = billPayments.get(0).getDate();
			lastPay = billPayments.get(billPayments.size() - 1).getDate(); // most recent payment
			upDate = lastPay;
		} else {
			upDate = billDate;
		}
		bill.setUpdate(upDate);

		if (billItems.isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.newbill.abillmustcontainatleastoneitem.msg")));
		}
		if (billItems.stream().anyMatch(item -> item.getItemAmount() == null || item.getItemAmount().compareTo(BigDecimal.ZERO) < 0)) {
			errors.add(new OHExceptionMessage("Bill item amounts must be zero or greater."));
		}
		if (billItems.stream().anyMatch(item -> item.getItemQuantity() < 0)) {
			errors.add(new OHExceptionMessage("Bill item quantities must be zero or greater."));
		}
		if (billDate.isAfter(today)) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.newbill.billsinthefuturearenotallowed.msg")));
		}
		if (lastPay.isAfter(today)) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.newbill.payementsinthefuturearenotallowed.msg")));
		}
		if (billDate.isAfter(firstPay)) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.newbill.billdateaisfterthefirstpayment.msg")));
		}
		if (bill.getPatName().isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.newbill.pleaseinsertanameforthepatient.msg")));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	/**
	 * Validates that a bill marked as closed ({@code status = "C"}) carries a zero balance. Must be called <em>after</em> {@link #recalculateTotals} so that
	 * {@code BLL_BALANCE} reflects the authoritative computed value.
	 *
	 * @param bill the bill whose balance to check
	 * @throws OHDataValidationException if the bill is closed with a non-zero balance
	 */
	private void validateClosedBillBalance(Bill bill) throws OHDataValidationException {
		if (bill.getStatus().equals("C")
						&& bill.getBalance().setScale(2, RoundingMode.HALF_UP).compareTo(BigDecimal.ZERO) != 0) {
			throw new OHDataValidationException(List.of(
							new OHExceptionMessage(MessageBundle.getMessage("angal.newbill.abillwithanoutstandingbalancecannotbeclosed.msg"))));
		}
	}

	/**
	 * Retrieves all the {@link BillItems} associated to the passed {@link Bill} id.
	 * 
	 * @param billID the bill id.
	 * @return a list of {@link BillItems} or {@code null} if an error occurred.
	 * @throws OHServiceException
	 */
	public List<BillItems> getItems(int billID) throws OHServiceException {
		if (billID == 0) {
			return new ArrayList<>();
		}
		return ioOperations.getItems(billID);
	}

	/**
	 * Retrieves all the bills of a given patient between dateFrom and datTo
	 * 
	 * @param dateFrom
	 * @param dateTo
	 * @param patient
	 * @return the bills list
	 * @throws OHServiceException
	 */
	public List<Bill> getBills(LocalDateTime dateFrom, LocalDateTime dateTo, Patient patient) throws OHServiceException {
		return ioOperations.getBillsBetweenDatesWherePatient(dateFrom, dateTo, patient);
	}

	/**
	 * Retrieves all the billPayments for a given patient between dateFrom and dateTo
	 * 
	 * @param dateFrom
	 * @param dateTo
	 * @param patient
	 * @return the list of payments
	 * @throws OHServiceException
	 */
	public List<BillPayments> getPayments(LocalDateTime dateFrom, LocalDateTime dateTo, Patient patient) throws OHServiceException {
		return ioOperations.getPaymentsBetweenDatesWherePatient(dateFrom, dateTo, patient);
	}

	/**
	 * Gets all the {@link BillPayments} for the specified {@link Bill}.
	 * 
	 * @param billID the bill id.
	 * @return a list of {@link BillPayments}
	 * @throws OHServiceException
	 */
	public List<BillPayments> getPayments(int billID) throws OHServiceException {
		return ioOperations.getPayments(billID);
	}

	/**
	 * Stores a new {@link Bill} along with all its {@link BillItems} and {@link BillPayments}
	 * 
	 * @param bill the bill to store.
	 * @param billItems the list of bill's items
	 * @param billPayments the list of bill's payments
	 * @returns the persisted Bill object
	 * @throws OHServiceException
	 */
	@Transactional(rollbackFor = OHServiceException.class)
	@TranslateOHServiceException
	public Bill newBill(
					Bill bill,
					List<BillItems> billItems,
					List<BillPayments> billPayments) throws OHServiceException {
		validateBill(bill, billItems, billPayments);
		Bill newBill = newBill(bill);
		int billId = newBill.getId();
		if (!billItems.isEmpty()) {
			newBillItems(billId, billItems);
		}
		if (!billPayments.isEmpty()) {
			newBillPayments(billId, billPayments);
		}
		// Recompute totals, then check closed-bill balance constraint.
		recalculateTotals(newBill, billItems, billPayments);
		validateClosedBillBalance(newBill);
		return ioOperations.updateBill(newBill);
	}

	/**
	 * Stores a new {@link Bill}.
	 * 
	 * @param newBill the bill to store.
	 * @return the persisted Bill object
	 * @throws OHServiceException
	 */
	private Bill newBill(Bill newBill) throws OHServiceException {
		return ioOperations.newBill(newBill);
	}

	/**
	 * Stores a list of {@link BillItems} associated to a {@link Bill}.
	 * 
	 * @param billID the bill id.
	 * @param billItems the bill items to store.
	 * @throws OHServiceException
	 */
	private void newBillItems(int billID, List<BillItems> billItems) throws OHServiceException {
		ioOperations.newBillItems(ioOperations.getBill(billID), billItems);
	}

	/**
	 * Stores a list of {@link BillPayments} associated to a {@link Bill}.
	 * 
	 * @param billID the bill id.
	 * @param payItems the bill payments.
	 * @throws OHServiceException
	 */
	private void newBillPayments(int billID, List<BillPayments> payItems) throws OHServiceException {
		ioOperations.newBillPayments(ioOperations.getBill(billID), payItems);
	}

	/**
	 * Updates the specified {@link Bill} along with all its {@link BillItems} and {@link BillPayments}
	 * 
	 * @param updateBill the bill to update.
	 * @param billItems the list of bill's items
	 * @param billPayments the list of bill's payments
	 * @return the updated Bill object
	 * @throws OHServiceException
	 */
	@Transactional(rollbackFor = OHServiceException.class)
	@TranslateOHServiceException
	public Bill updateBill(Bill updateBill,
					List<BillItems> billItems,
					List<BillPayments> billPayments) throws OHServiceException {
		// Resolve effective items before touching the DB:
		// - empty list means payment-only update → read current items from DB
		// - non-empty list means the caller is replacing items → use as-is
		List<BillItems> effectiveItems = billItems.isEmpty()
						? ioOperations.getItems(updateBill.getId())
						: billItems;
		validateBill(updateBill, effectiveItems, billPayments);
		Bill updatedBill = updateBill(updateBill);
		if (!billItems.isEmpty()) {
			newBillItems(updateBill.getId(), billItems);
		}
		newBillPayments(updateBill.getId(), billPayments);
		// Recompute authoritative totals, then check closed-bill balance constraint.
		recalculateTotals(updatedBill, effectiveItems, billPayments);
		validateClosedBillBalance(updatedBill);
		return ioOperations.updateBill(updatedBill);
	}

	/**
	 * Updates the specified {@link Bill}.
	 * 
	 * @param updateBill the bill to update.
	 * @return the updated Bill object
	 * @throws OHServiceException
	 */
	private Bill updateBill(Bill updateBill) throws OHServiceException {
		return ioOperations.updateBill(updateBill);
	}

	/**
	 * Returns all the pending {@link Bill}s for the specified patient.
	 * 
	 * @param patID the patient id.
	 * @return the list of pending bills or {@code null} if an error occurred.
	 * @throws OHServiceException
	 */
	public List<Bill> getPendingBills(int patID) throws OHServiceException {
		return ioOperations.getPendingBills(patID);
	}

	/**
	 * Recomputes {@code BLL_AMOUNT} and {@code BLL_BALANCE} from the supplied item and payment lists, updating the {@link Bill} object in-place.
	 *
	 * @param bill the bill to update in-place
	 * @param items item lines (may be empty, not null)
	 * @param payments payment lines (may be empty, not null)
	 */
	private void recalculateTotals(Bill bill, List<BillItems> items, List<BillPayments> payments) {
		Map<String, BigDecimal[]> groups = new LinkedHashMap<>();
		for (BillItems item : items) {
			String key = item.getPriceID() + "|" + item.getItemDescription();
			BigDecimal[] g = groups.get(key);
			if (g == null) {
				g = new BigDecimal[] {
						BigDecimal.ZERO,
						BigDecimal.ZERO,
						item.getItemAmount()
				};
				groups.put(key, g);
			}
			BigDecimal qty = new BigDecimal(item.getItemQuantity());
			BigDecimal amt = item.getItemAmount();
			g[0] = g[0].add(qty);
			g[1] = g[1].add(amt.multiply(qty));
		}

		BigDecimal amount = BigDecimal.ZERO;
		BigDecimal bigTotal = BigDecimal.ZERO;
		for (BigDecimal[] g : groups.values()) {
			bigTotal = bigTotal.add(g[1]);
			if (g[2].compareTo(BigDecimal.ZERO) > 0 && g[0].compareTo(BigDecimal.ZERO) > 0) {
				amount = amount.add(g[1]);
			}
		}

		BigDecimal paid = BigDecimal.ZERO;
		for (BillPayments payment : payments) {
			paid = paid.add(payment.getAmount());
		}

		bill.setAmount(amount.setScale(2, RoundingMode.HALF_UP));
		bill.setBalance(bigTotal.subtract(paid).setScale(2, RoundingMode.HALF_UP));
	}

	/**
	 * Get the {@link Bill} with specified billID
	 * 
	 * @param billID
	 * @return the {@link Bill} or {@code null} if an error occurred.
	 * @throws OHServiceException
	 */
	public Bill getBill(int billID) throws OHServiceException {
		return ioOperations.getBill(billID);
	}

	/**
	 * Returns all user ids related to a {@link BillPayments}.
	 * 
	 * @return a list of user id or {@code null} if an error occurred.
	 * @throws OHServiceException
	 */
	public List<String> getUsers() throws OHServiceException {
		return ioOperations.getUsers();
	}

	/**
	 * Deletes the specified {@link Bill}. If the argument is NULL then an error is thrown. If the Bill is not found it is silently ignored.
	 * 
	 * @param deleteBill the bill to delete.
	 * @throws OHServiceException
	 */
	public void deleteBill(Bill deleteBill) throws OHServiceException {
		ioOperations.deleteBill(deleteBill);
	}

	/**
	 * Retrieves all the {@link Bill}s for the specified date range.
	 * 
	 * @param dateFrom the low date range endpoint, inclusive.
	 * @param dateTo the high date range endpoint, inclusive.
	 * @return a list of retrieved {@link Bill}s or {@code null} if an error occurred.
	 * @throws OHServiceException
	 */
	public List<Bill> getBills(LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		return ioOperations.getBillsBetweenDates(dateFrom, dateTo);
	}

	/**
	 * Gets all the {@link Bill}s associated to the passed {@link BillPayments}.
	 * 
	 * @param billPayments the {@link BillPayments} associated to the bill to retrieve.
	 * @return a list of {@link Bill} associated to the passed {@link BillPayments} or {@code null} if an error occurred.
	 * @throws OHServiceException
	 */
	public List<Bill> getBills(List<BillPayments> billPayments) throws OHServiceException {
		if (billPayments.isEmpty()) {
			return new ArrayList<>();
		}
		return ioOperations.getBills(billPayments);
	}

	/**
	 * Retrieves all the {@link BillPayments} for the specified date range.
	 * 
	 * @param dateFrom low endpoint, inclusive, for the date range.
	 * @param dateTo high endpoint, inclusive, for the date range.
	 * @return a list of {@link BillPayments} for the specified date range or {@code null} if an error occurred.
	 * @throws OHServiceException
	 */
	public List<BillPayments> getPayments(LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		return ioOperations.getPayments(dateFrom, dateTo);
	}

	/**
	 * Retrieves all the {@link BillPayments} associated to the passed {@link Bill} list.
	 * 
	 * @param billArray the bill array list of {@link Bill}s.
	 * @return a list of {@link BillPayments} associated to the passed bill list or {@code null} if an error occurred.
	 * @throws OHServiceException
	 */
	public List<BillPayments> getPayments(List<Bill> billArray) throws OHServiceException {
		return ioOperations.getPayments(billArray);
	}

	/**
	 * Retrieves all the {@link Bill}s associated to the specified {@link Patient}.
	 * 
	 * @param patID the Patient's ID
	 * @return the list of {@link Bill}s
	 * @throws OHServiceException
	 */
	public List<Bill> getPendingBillsAffiliate(int patID) throws OHServiceException {
		return ioOperations.getPendingBillsAffiliate(patID);
	}

	/**
	 * Returns all the distinct stored {@link BillItems}.
	 * 
	 * @return a list of distinct {@link BillItems} or null if an error occurs.
	 * @throws OHServiceException
	 */
	public List<BillItems> getDistinctItems() throws OHServiceException {
		return ioOperations.getDistictsBillItems();
	}

	/**
	 * Get the bills list with a given billItem
	 * 
	 * @param dateFrom
	 * @param dateTo
	 * @param billItem
	 * @return
	 * @throws OHServiceException
	 */
	public List<Bill> getBills(LocalDateTime dateFrom, LocalDateTime dateTo, BillItems billItem) throws OHServiceException {
		return ioOperations.getBillsBetweenDatesWhereBillItem(dateFrom, dateTo, billItem);
	}
}
