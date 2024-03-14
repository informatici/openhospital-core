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
package org.isf.supplier;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.isf.OHCoreTestCase;
import org.isf.supplier.manager.SupplierBrowserManager;
import org.isf.supplier.model.Supplier;
import org.isf.supplier.service.SupplierIoOperationRepository;
import org.isf.supplier.service.SupplierOperations;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class Tests extends OHCoreTestCase {

	private static TestSupplier testSupplier;

	@Autowired
	SupplierOperations supplierIoOperation;
	@Autowired
	SupplierIoOperationRepository supplierIoOperationRepository;
	@Autowired
	SupplierBrowserManager supplierBrowserManager;

	@BeforeAll
	static void setUpClass() {
		testSupplier = new TestSupplier();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void testSupplierGets() throws Exception {
		int code = setupTestSupplier(false);
		checkSupplierIntoDb(code);
	}

	@Test
	void testSupplierSets() throws Exception {
		int code = setupTestSupplier(true);
		checkSupplierIntoDb(code);
	}

	@Test
	void testIoSupplierSaveOrUpdate() throws Exception {
		Supplier supplier = testSupplier.setup(true);
		Supplier newSupplier = supplierIoOperation.saveOrUpdate(supplier);
		checkSupplierIntoDb(newSupplier.getSupId());
	}

	@Test
	void testIoSupplierGetByID() throws Exception {
		int code = setupTestSupplier(false);
		Supplier foundSupplier = supplierIoOperation.getByID(code);
		assertThat(foundSupplier).isNotNull();
		checkSupplierIntoDb(foundSupplier.getSupId());
	}

	@Test
	void testIoSupplierGetAll() throws Exception {
		int code = setupTestSupplier(false);
		Supplier foundSupplier = supplierIoOperation.getByID(code);
		assertThat(foundSupplier).isNotNull();
		List<Supplier> suppliers = supplierBrowserManager.getAll();
		assertThat(suppliers).contains(foundSupplier);
	}

	@Test
	void testIoSupplierGetList() throws Exception {
		int code = setupTestSupplier(false);
		Supplier foundSupplier = supplierIoOperation.getByID(code);
		assertThat(foundSupplier).isNotNull();
		List<Supplier> suppliers = supplierBrowserManager.getList();
		assertThat(suppliers).contains(foundSupplier);
	}

	@Test
	void testMgrSupplierSaveOrUpdate() throws Exception {
		Supplier supplier = testSupplier.setup(true);
		Supplier newSupplier = supplierBrowserManager.saveOrUpdate(supplier);
		checkSupplierIntoDb(newSupplier.getSupId());
	}

	@Test
	void testMgrSupplierGetByID() throws Exception {
		int code = setupTestSupplier(false);
		Supplier foundSupplier = supplierBrowserManager.getByID(code);
		assertThat(foundSupplier).isNotNull();
		checkSupplierIntoDb(foundSupplier.getSupId());
	}

	@Test
	void testMgrSupplierGetAll() throws Exception {
		int code = setupTestSupplier(false);
		Supplier foundSupplier = supplierBrowserManager.getByID(code);
		assertThat(foundSupplier).isNotNull();
		List<Supplier> suppliers = supplierBrowserManager.getAll();
		assertThat(suppliers).contains(foundSupplier);
	}

	@Test
	void testMgrSupplierGetList() throws Exception {
		int code = setupTestSupplier(false);
		Supplier foundSupplier = supplierBrowserManager.getByID(code);
		assertThat(foundSupplier).isNotNull();
		List<Supplier> suppliers = supplierBrowserManager.getList();
		assertThat(suppliers).contains(foundSupplier);
	}

	@Test
	void testMgrGetHashMap() throws Exception {
		int code = setupTestSupplier(false);
		Supplier foundSupplier = supplierBrowserManager.getByID(code);
		assertThat(foundSupplier).isNotNull();
		// get all (including deleted)
		Map<Integer, String> allSuppliers = supplierBrowserManager.getHashMap(true);
		// get all (not including deleted)
		Map<Integer, String> suppliers = supplierBrowserManager.getHashMap(false);
		assertThat(allSuppliers).isEqualTo(suppliers);
		// "delete" a supplier
		foundSupplier.setSupDeleted('Y');
		supplierBrowserManager.saveOrUpdate(foundSupplier);
		// get all (not including delete)
		suppliers = supplierBrowserManager.getHashMap(false);
		assertThat(allSuppliers).isNotEqualTo(suppliers);
	}

	@Test
	void testSupplierToString() throws Exception {
		Supplier supplier = new Supplier(null, "TestName", "TestAddress", "TestTax", "TestPhone", "TestFax", "TestEmail", "TestNode");
		assertThat(supplier).hasToString("TestName");
	}

	@Test
	void testSupplierEquals() throws Exception {
		Supplier supplier = new Supplier(1, "TestName", "TestAddress", "TestTax", "TestPhone", "TestFax", "TestEmail", "TestNode");

		assertThat(supplier)
				.isEqualTo(supplier)
				.isNotNull()
				.isNotEqualTo("someString");

		Supplier supplier2 = new Supplier(2, "TestName", "TestAddress", "TestTax", "TestPhone", "TestFax", "TestEmail", "TestNode");
		assertThat(supplier).isNotEqualTo(supplier2);

		supplier2.setSupId(supplier.getSupId());
		assertThat(supplier).isEqualTo(supplier2);
	}

	@Test
	void testSupplierHashCode() throws Exception {
		int code = setupTestSupplier(false);
		Supplier supplier = supplierBrowserManager.getByID(code);
		assertThat(supplier).isNotNull();
		// compute value
		int hashCode = supplier.hashCode();
		// use computed stored value
		assertThat(supplier.hashCode()).isEqualTo(hashCode);
	}

	private int setupTestSupplier(boolean usingSet) throws OHException {
		Supplier supplier = testSupplier.setup(usingSet);
		supplierIoOperationRepository.saveAndFlush(supplier);
		return supplier.getSupId();
	}

	private void checkSupplierIntoDb(int code) throws OHServiceException {
		Supplier foundSupplier = supplierIoOperation.getByID(code);
		assertThat(foundSupplier).isNotNull();
		testSupplier.check(foundSupplier);
	}
}