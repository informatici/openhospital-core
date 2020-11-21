/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.supplier.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.supplier.model.Supplier;
import org.isf.supplier.service.SupplierIoOperationRepository;
import org.isf.supplier.service.SupplierOperations;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class Tests extends OHCoreTestCase {

	private static TestSupplier testSupplier;

	@Autowired
	SupplierOperations supplierIoOperation;
	@Autowired
	SupplierIoOperationRepository supplierIoOperationRepository;

	@BeforeClass
	public static void setUpClass() {
		testSupplier = new TestSupplier();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testSupplierGets() throws Exception {
		int code = _setupTestSupplier(false);
		_checkSupplierIntoDb(code);
	}

	@Test
	public void testSupplierSets() throws Exception {
		int code = _setupTestSupplier(true);
		_checkSupplierIntoDb(code);
	}

	@Test
	public void testSupplierSaveOrUpdate() throws Exception {
		Supplier supplier = testSupplier.setup(true);
		boolean result = supplierIoOperation.saveOrUpdate(supplier);

		assertThat(result).isTrue();
		_checkSupplierIntoDb(supplier.getSupId());
	}

	@Test
	public void testSupplierGetByID() throws Exception {
		int code = _setupTestSupplier(false);
		Supplier foundSupplier = supplierIoOperation.getByID(code);
		_checkSupplierIntoDb(foundSupplier.getSupId());
	}

	@Test
	public void testSupplierGetAll() throws Exception {
		int code = _setupTestSupplier(false);
		Supplier foundSupplier = supplierIoOperation.getByID(code);
		List<Supplier> suppliers = supplierIoOperation.getAll();
		assertThat(suppliers).contains(foundSupplier);
	}

	@Test
	public void testSupplierGetList() throws Exception {
		int code = _setupTestSupplier(false);
		Supplier foundSupplier = supplierIoOperation.getByID(code);
		List<Supplier> suppliers = supplierIoOperation.getList();
		assertThat(suppliers).contains(foundSupplier);
	}

	private int _setupTestSupplier(boolean usingSet) throws OHException {
		Supplier supplier = testSupplier.setup(usingSet);
		supplierIoOperationRepository.saveAndFlush(supplier);
		return supplier.getSupId();
	}

	private void _checkSupplierIntoDb(int code) throws OHServiceException {
		Supplier foundSupplier = supplierIoOperation.getByID(code);
		testSupplier.check(foundSupplier);
	}
}