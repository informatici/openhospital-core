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
package org.isf.supplier.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.isf.supplier.model.Supplier;
import org.isf.supplier.service.SupplierOperations;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SupplierBrowserManager {

	@Autowired
	private SupplierOperations ioOperations;

	public Supplier saveOrUpdate(Supplier supplier) throws OHServiceException {
		return ioOperations.saveOrUpdate(supplier);
	}

	public Supplier getByID(int id) throws OHServiceException {
		return ioOperations.getByID(id);
	}

	public List<Supplier> getAll() throws OHServiceException {
		return ioOperations.getAll();
	}

	public List<Supplier> getList() throws OHServiceException {
		return ioOperations.getList();
	}

	/**
	 * Returns the {@link HashMap} of all {@link Supplier}s
	 *
	 * @param all - if <code>true</code> it will returns deleted ones also
	 * @return the {@link HashMap} of all {@link Supplier}s
	 * @throws OHServiceException
	 */
	public Map<Integer, String> getHashMap(boolean all) throws OHServiceException {
		List<Supplier> supList;
		if (all) {
			supList = ioOperations.getAll();
		} else {
			supList = ioOperations.getList();
		}
		Map<Integer, String> supMap = new HashMap<>();
		for (Supplier sup : supList) {
			supMap.put(sup.getSupId(), sup.getSupName());
		}
		return supMap;
	}
}
