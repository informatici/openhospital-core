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
package org.isf.serviceprinting.manager;

import static org.assertj.core.api.Fail.fail;
import static org.mockito.Mockito.mockStatic;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.isf.OHCoreTestCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import net.sf.jasperreports.engine.JasperPrint;

class PrintReceiptTest extends OHCoreTestCase {

	@Mock
	JasperPrint jasperPrint;
	@Mock
	PrintService defaultPrintService;

	private AutoCloseable closeable;

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
		closeable = MockitoAnnotations.openMocks(this);
	}

	@AfterEach
	void closeService() throws Exception {
		closeable.close();
	}

	@Test
	void constructor_shouldNotThrow_whenPrinterNotFound() {
		try {
			// generates a logger message only
			new PrintReceipt(jasperPrint, "SampleReceipt");
		} catch(Exception exception) {
			fail("testPrintReceiptPrinterNotFound() should not generate an exception");
		}
	}

	@Test
	void constructor_shouldNotThrow_whenPrintingPdfToDefaultPrinter() {
		try (MockedStatic<PrintServiceLookup> mockedPrintServiceLookup = mockStatic(PrintServiceLookup.class)) {
			mockedPrintServiceLookup.when(() -> PrintServiceLookup.lookupDefaultPrintService()).thenReturn(defaultPrintService);

			new PrintReceipt(jasperPrint, "SampleReceipt");
		} catch(Exception exception) {
			fail("testPrintReceiptPrinterPDF() should not generate an exception");
		}
	}

}
