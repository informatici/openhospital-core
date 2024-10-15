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
package org.isf.serviceprinting;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.awt.HeadlessException;
import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.isf.OHCoreTestCase;
import org.isf.generaldata.GeneralData;
import org.isf.hospital.manager.HospitalBrowsingManager;
import org.isf.hospital.model.Hospital;
import org.isf.serviceprinting.manager.PrintManager;
import org.isf.serviceprinting.print.PriceForPrint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

class TestPrintManager extends OHCoreTestCase {

	@Mock
	HospitalBrowsingManager hospitalBrowsingManager;
	@Mock
	Hospital hospital;
	@Mock
	JasperReport jasperReport;
	@Mock
	JasperPrint jasperPrint;

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
	void testPrintManagerPrintBogusFileName() {
		assertThatThrownBy(() -> {
			PrintManager printManager = new PrintManager(hospitalBrowsingManager);
			when(hospitalBrowsingManager.getHospital()).thenReturn(hospital);
			when(hospital.getDescription()).thenReturn("Description");
			when(hospital.getAddress()).thenReturn("Address");
			when(hospital.getCity()).thenReturn("City");
			when(hospital.getTelephone()).thenReturn("Telephone");
			when(hospital.getFax()).thenReturn("Fax");
			when(hospital.getEmail()).thenReturn("Email");
			// because the Jasper file isn't found the code generates an error message dialog
			// and because this code is headless; the exception is thrown
			printManager.print("BogusFileName", getPricesForPrint(), 0);
		})
			.isInstanceOf(HeadlessException.class);
	}

	@Test
	void testPrintManagerPrint(){
		boolean viewer = GeneralData.INTERNALVIEWER;
		String locale = GeneralData.LANGUAGE;
		GeneralData.INTERNALVIEWER = true;
		GeneralData.LANGUAGE = "en";
		try (MockedStatic<JasperViewer> mockedJasperViewer = mockStatic(JasperViewer.class);
						MockedStatic<JRLoader> mockedJRLoader = mockStatic(JRLoader.class);
						MockedStatic<JasperFillManager> mockedJasperFillManager = mockStatic(JasperFillManager.class)) {
			PrintManager printManager = new PrintManager(hospitalBrowsingManager);

			when(hospitalBrowsingManager.getHospital()).thenReturn(hospital);
			when(hospital.getDescription()).thenReturn("Description");
			when(hospital.getAddress()).thenReturn("Address");
			when(hospital.getCity()).thenReturn("City");
			when(hospital.getTelephone()).thenReturn("Telephone");
			when(hospital.getFax()).thenReturn("Fax");
			when(hospital.getEmail()).thenReturn("Email");

			mockedJRLoader.when(() -> JRLoader.loadObject(any(File.class))).thenReturn(jasperReport);
			mockedJasperFillManager.when(() -> JasperFillManager.fillReport(any(JasperReport.class), any(HashMap.class), any(Connection.class)))
							.thenReturn(jasperPrint);
			mockedJasperViewer.when(() -> JasperViewer.viewReport(any(JasperPrint.class), any(Boolean.class), any(Locale.class)))
							.thenAnswer((Answer<Void>) invocation -> null);

			printManager.print("TestReport", getPricesForPrint(), 0);
		} catch(Exception exception) {
			fail("testPrintManagerPrint() should not generate an exception");
		} finally {
			GeneralData.INTERNALVIEWER = viewer;
			GeneralData.LANGUAGE = locale;
		}
	}

	private List<PriceForPrint> getPricesForPrint() {
		List<PriceForPrint> pricePrint = new ArrayList();
		PriceForPrint price4print = new PriceForPrint();
		price4print.setList("Name");
		price4print.setCurrency("Currency");
		price4print.setDesc("Description");
		price4print.setGroup("Group");
		price4print.setPrice(100.0D);
		pricePrint.add(price4print);
		return pricePrint;
	}
}
