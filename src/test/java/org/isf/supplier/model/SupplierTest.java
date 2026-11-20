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
package org.isf.supplier.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.supplier.TestSupplier;
import org.isf.supplier.manager.SupplierBrowserManager;
import org.isf.supplier.service.SupplierIoOperationRepository;
import org.isf.supplier.service.SupplierOperations;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SupplierTest extends OHCoreTestCase {

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
	void constructor_shouldSetFields() throws Exception {
		int code = setupTestSupplier(false);
		checkSupplierIntoDb(code);
	}

	@Test
	void setters_shouldSetFields() throws Exception {
		int code = setupTestSupplier(true);
		checkSupplierIntoDb(code);
	}

	@Test
	void toString_shouldReturnName() throws Exception {
		Supplier supplier = new Supplier(null, "TestName", "TestAddress", "TestTax", "TestPhone", "TestFax", "TestEmail", "TestNode");
		assertThat(supplier).hasToString("TestName");
	}

	@Test
	void equals_shouldCompareId() throws Exception {
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
	void hashCode_shouldReturnSameValue_whenCalledTwice() throws Exception {
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
