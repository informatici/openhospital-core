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
package org.isf.priceslist.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.isf.generaldata.MessageBundle;
import org.isf.priceslist.model.Price;
import org.isf.priceslist.model.PriceList;
import org.isf.priceslist.service.PricesListIoOperations;
import org.isf.serviceprinting.print.PriceForPrint;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PriceListManager {

	@Autowired
	private PricesListIoOperations ioOperations;

	/**
	 * Return the list of {@link PriceList}s in the DB
	 *
	 * @return the list of {@link PriceList}s
	 * @throws OHServiceException
	 */
	public List<PriceList> getLists() throws OHServiceException {
		return ioOperations.getLists();
	}

	/**
	 * Return the list of {@link Price}s in the DB
	 *
	 * @return the list of {@link Price}s
	 * @throws OHServiceException
	 */
	public List<Price> getPrices() throws OHServiceException {
		return ioOperations.getPrices();
	}

	/**
	 * Updates all {@link Price}s in the specified {@link PriceList}
	 *
	 * @param list - the {@link PriceList}
	 * @param prices - the list of {@link Price}s
	 * @return <code>true</code> if the list has been replaced, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	public boolean updatePrices(PriceList list, List<Price> prices) throws OHServiceException {
		return ioOperations.updatePrices(list, prices);
	}

	/**
	 * Insert a new {@link PriceList} in the DB
	 *
	 * @param list - the {@link PriceList}
	 * @return <code>true</code> if the list has been inserted, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	public PriceList newList(PriceList list) throws OHServiceException {
		validatePriceList(list);
		return ioOperations.newList(list);
	}

	/**
	 * Update a {@link PriceList} in the DB
	 *
	 * @param updateList - the {@link PriceList} to update
	 * @return <code>true</code> if the list has been updated, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	public PriceList updateList(PriceList updateList) throws OHServiceException {
		validatePriceList(updateList);
		return ioOperations.updateList(updateList);
	}

	/**
	 * Delete a {@link PriceList} in the DB
	 *
	 * @param deleteList - the {@link PriceList} to delete
	 * @return <code>true</code> if the list has been deleted, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	public boolean deleteList(PriceList deleteList) throws OHServiceException {
		return ioOperations.deleteList(deleteList);
	}

	/**
	 * Duplicate specified {@link PriceList}
	 *
	 * @param list
	 * @return <code>true</code> if the list has been duplicated, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	public boolean copyList(PriceList list) throws OHServiceException {
		return copyList(list, 1., 0.);
	}

	/**
	 * Duplicate {@link PriceList} multiplying by <code>factor</code> and rounding by <code>step</code>
	 *
	 * @param list - the {@link PriceList} to be duplicated
	 * @param factor - the multiplying factor
	 * @param step - the rounding step
	 * @return <code>true</code> if the list has been duplicated, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	public boolean copyList(PriceList list, double factor, double step) throws OHServiceException {
		return ioOperations.copyList(list, factor, step);
	}

	public List<PriceForPrint> convertPrice(PriceList listSelected, Iterable<Price> prices) {
		List<PriceForPrint> pricePrint = new ArrayList<>();
		for (Price price : prices) {
			if (price.getList().getId() == listSelected.getId() && price.getPrice() != 0.) {
				PriceForPrint price4print = new PriceForPrint();
				price4print.setList(listSelected.getName());
				price4print.setCurrency(listSelected.getCurrency());
				price4print.setDesc(price.getDesc());
				price4print.setGroup(price.getGroup());
				price4print.setPrice(price.getPrice());
				pricePrint.add(price4print);
			}
			Collections.sort(pricePrint);
		}
		return pricePrint;
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 *
	 * @param priceList
	 * @throws OHDataValidationException
	 */
	protected void validatePriceList(PriceList priceList) throws OHDataValidationException {
		java.util.List<OHExceptionMessage> errors = new ArrayList<>();

		if (StringUtils.isEmpty(priceList.getCode())) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.common.pleaseinsertacode.msg"),
					OHSeverityLevel.ERROR));
		}
		if (StringUtils.isEmpty(priceList.getName())) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.priceslist.pleaseinsertanameforthelist.msg"),
					OHSeverityLevel.ERROR));
		}
		if (StringUtils.isEmpty(priceList.getDescription())) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.common.pleaseinsertavaliddescription.msg"),
					OHSeverityLevel.ERROR));
		}
		if (StringUtils.isEmpty(priceList.getCurrency())) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.priceslist.pleaseinsertacurrency.msg"),
					OHSeverityLevel.ERROR));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

}
