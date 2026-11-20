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
package org.isf.supplier.manager;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.isf.OHCoreTestCase;
import org.isf.supplier.TestSupplier;
import org.isf.supplier.model.Supplier;
import org.isf.supplier.service.SupplierIoOperationRepository;
import org.isf.supplier.service.SupplierOperations;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SupplierBrowserManagerTest extends OHCoreTestCase {

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
	void getAll_shouldIncludeSavedSupplier_whenLoadedViaIoOperation() throws Exception {
		int code = setupTestSupplier(false);
		Supplier foundSupplier = supplierIoOperation.getByID(code);
		assertThat(foundSupplier).isNotNull();
		List<Supplier> suppliers = supplierBrowserManager.getAll();
		assertThat(suppliers).contains(foundSupplier);
	}

	@Test
	void getList_shouldIncludeSavedSupplier_whenLoadedViaIoOperation() throws Exception {
		int code = setupTestSupplier(false);
		Supplier foundSupplier = supplierIoOperation.getByID(code);
		assertThat(foundSupplier).isNotNull();
		List<Supplier> suppliers = supplierBrowserManager.getList();
		assertThat(suppliers).contains(foundSupplier);
	}

	@Test
	void saveOrUpdate_shouldSaveSupplier() throws Exception {
		Supplier supplier = testSupplier.setup(true);
		Supplier newSupplier = supplierBrowserManager.saveOrUpdate(supplier);
		checkSupplierIntoDb(newSupplier.getSupId());
	}

	@Test
	void getByID_shouldReturnSupplier_whenIdExists() throws Exception {
		int code = setupTestSupplier(false);
		Supplier foundSupplier = supplierBrowserManager.getByID(code);
		assertThat(foundSupplier).isNotNull();
		checkSupplierIntoDb(foundSupplier.getSupId());
	}

	@Test
	void getAll_shouldIncludeSavedSupplier() throws Exception {
		int code = setupTestSupplier(false);
		Supplier foundSupplier = supplierBrowserManager.getByID(code);
		assertThat(foundSupplier).isNotNull();
		List<Supplier> suppliers = supplierBrowserManager.getAll();
		assertThat(suppliers).contains(foundSupplier);
	}

	@Test
	void getList_shouldIncludeSavedSupplier() throws Exception {
		int code = setupTestSupplier(false);
		Supplier foundSupplier = supplierBrowserManager.getByID(code);
		assertThat(foundSupplier).isNotNull();
		List<Supplier> suppliers = supplierBrowserManager.getList();
		assertThat(suppliers).contains(foundSupplier);
	}

	@Test
	void getHashMap_shouldExcludeDeletedSupplier_whenAllIsFalse() throws Exception {
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
	void getByID_shouldReturnNull_whenSupplierIsSoftDeleted() throws Exception {
		loadSuppliers();

		Supplier supplier = supplierBrowserManager.getByID(1);
		supplierBrowserManager.delete(supplier);

		assertThat(supplierBrowserManager.getByID(1)).isNull();
	}

	@Test
	void delete_shouldSetDeletedFlagAndDeletedDate() throws Exception {
		loadSuppliers();
		Supplier supplier = supplierBrowserManager.getByID(1);
		assertThat(supplier.getDeletedDate()).isNull();

		supplierBrowserManager.delete(supplier);

		Supplier deleted = supplierIoOperationRepository.findById(1).orElse(null);
		assertThat(deleted).isNotNull();
		assertThat(deleted.getSupDeleted()).isEqualTo('Y');
		assertThat(deleted.getDeletedDate()).isNotNull();
	}

	private void loadSuppliers() {
		AtomicInteger i = new AtomicInteger();
		List<Supplier> suppliers = Stream.of('N','N','Y').map(deleted -> {
			i.getAndIncrement();

			return new Supplier(
							null,
							"Supplier " + i,
							"supAddress " + i,
							"supTaxCode " + i,
							"supPhone " + i,
							"supFax " + i,
							"supEmail " + i,
							"supNote " + i,
							deleted
			);
		}).toList();

		supplierIoOperationRepository.saveAllAndFlush(suppliers);
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