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
package org.isf.priceslist.service;

import java.util.List;

import org.isf.priceslist.model.Price;
import org.isf.priceslist.model.PriceList;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class PricesListIoOperations {

	@Autowired
	private PricesListIoOperationRepository repository;
	
	@Autowired
	private PriceIoOperationRepository priceRepository;
	
	/**
	 * Return the list of {@link PriceList}s in the DB
	 * 
	 * @return the list of {@link PriceList}s
	 * @throws OHServiceException 
	 */
	public List<PriceList> getLists() throws OHServiceException {
		return repository.findAll();
	}
	
	/**	
	 * Return the list of {@link Price}s in the DB
	 * 
	 * @return the list of {@link Price}s
	 * @throws OHServiceException 
	 */
	public List<Price> getPrices() throws OHServiceException {
		return priceRepository.findAllByOrderByDescriptionAsc();
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
		boolean result = true;

		result = _deletePricesInsideList(list.getId());

		result &= _insertNewPricesInsideList(list, prices);

		return result;
	}
	
	private boolean _deletePricesInsideList(int id) throws OHServiceException {
		priceRepository.deleteByListId(id);

		return true;
	}
	
	private boolean _insertNewPricesInsideList(PriceList list, List<Price> prices) throws OHServiceException {
		for (Price price : prices) {
			price.setList(list);
			priceRepository.save(price);
		}

		return true;
	}

	/**
	 * Insert a new {@link PriceList} in the DB
	 * 
	 * @param list - the {@link PriceList}
	 * @return <code>true</code> if the list has been inserted, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public PriceList newList(PriceList list) throws OHServiceException {
		return repository.save(list);
	}
	
	/**
	 * Update a {@link PriceList} in the DB
	 * 
	 * @param list - the {@link PriceList} to update
	 * @return <code>true</code> if the list has been updated, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public PriceList updateList(PriceList list) throws OHServiceException {
		return repository.save(list);
	}
	
	/**
	 * Delete a {@link PriceList} in the DB
	 * 
	 * @param list - the {@link PriceList} to delete
	 * @return <code>true</code> if the list has been deleted, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean deleteList(PriceList list) throws OHServiceException {
		boolean result = true;

		result = _deletePricesInsideList(list.getId());

		result &= _deletePriceList(list.getId());

		return result;
	}
	
	private boolean _deletePriceList(int id) throws OHServiceException {
		boolean result = true;

		repository.deleteById(id);

		return result;
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
		PriceList newList = insertNewPriceList(list);
		boolean result = true;

		List<Price> Prices = (List<Price>) priceRepository.findByList_id(list.getId());
		for (Price price : Prices) {
			Price newPrice = new Price();

			newPrice.setList(newList);
			newPrice.setGroup(price.getGroup());
			newPrice.setDesc(price.getDesc());
			if (step > 0) {
				newPrice.setPrice(Math.round((price.getPrice() * factor) / step) * step);
			} else {
				newPrice.setPrice(price.getPrice() * factor);
			}
			newPrice.setItem(price.getItem());
			priceRepository.save(newPrice);
		}

		return result;
	}

	private PriceList insertNewPriceList(PriceList list) throws OHServiceException {
		PriceList newList = new PriceList();

		newList.setCode(list.getCode());
		newList.setName(list.getName());
		newList.setDescription(list.getDescription());
		newList.setCurrency(list.getCurrency());
		repository.save(newList);

		return newList;
	}
}