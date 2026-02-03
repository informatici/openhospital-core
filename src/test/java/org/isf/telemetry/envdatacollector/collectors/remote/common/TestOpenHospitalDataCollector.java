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
package org.isf.telemetry.envdatacollector.collectors.remote.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManagerFactory;

import org.isf.OHCoreTestCase;
import org.isf.accounting.service.AccountingIoOperations;
import org.isf.admission.service.AdmissionIoOperations;
import org.isf.lab.service.LabIoOperations;
import org.isf.medicalstock.service.MedicalStockIoOperations;
import org.isf.medicalstockward.service.MedicalStockWardIoOperations;
import org.isf.menu.manager.Context;
import org.isf.menu.service.MenuIoOperations;
import org.isf.opd.service.OpdIoOperations;
import org.isf.operation.service.OperationRowIoOperations;
import org.isf.patient.service.PatientIoOperations;
import org.isf.telemetry.envdatacollector.collectors.OpenHospitalDataCollector;
import org.isf.therapy.service.TherapyIoOperations;
import org.isf.vaccine.service.VaccineIoOperations;
import org.isf.visits.service.VisitsIoOperations;
import org.isf.ward.service.WardIoOperations;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

class TestOpenHospitalDataCollector extends OHCoreTestCase {

	@Autowired
	private PatientIoOperations patientIoOperations;
	@Autowired
	private MenuIoOperations menuIoOperations;
	@Mock
	private WardIoOperations wardIoOperations;
	@Autowired
	private OpdIoOperations opdIoOperations;
	@Autowired
	private AdmissionIoOperations admissionIoOperations;
	@Autowired
	private LabIoOperations laboratoryIoOperations;
	@Autowired
	private VaccineIoOperations vaccineIoOperations;
	@Autowired
	private OperationRowIoOperations operationRowIoOperations;
	@Autowired
	private MedicalStockIoOperations medicalStockIoOperations;
	@Autowired
	private MedicalStockWardIoOperations medicalStockWardIoOperations;
	@Autowired
	private TherapyIoOperations therapyIoOperations;
	@Autowired
	private VisitsIoOperations visitsIoOperations;
	@Autowired
	private AccountingIoOperations accountingIoOperations;
	@Mock
	private GeoIpInfoSettings geoIpInfoSettingsMock;

	OpenHospitalDataCollector openHospitalDataCollector;

	@Mock
	EntityManagerFactory entityManagerFactoryMock;
	@Mock
	ApplicationContext applicationContextMock;
	@Mock
	private List<GeoIpInfoCommonService> geoIpServicesMock;

	private AutoCloseable closeable;

	@BeforeEach
	void setUp() {
		closeable = MockitoAnnotations.openMocks(this);
		Context.setApplicationContext(applicationContextMock);
		when(applicationContextMock.getBean("entityManagerFactory", EntityManagerFactory.class)).thenReturn(entityManagerFactoryMock);

		openHospitalDataCollector = new OpenHospitalDataCollector(patientIoOperations,
						menuIoOperations,
						wardIoOperations,
						opdIoOperations,
						admissionIoOperations,
						laboratoryIoOperations,
						vaccineIoOperations,
						operationRowIoOperations,
						medicalStockIoOperations,
						medicalStockWardIoOperations,
						therapyIoOperations,
						visitsIoOperations,
						accountingIoOperations,
						geoIpInfoSettingsMock);
	}

	@AfterEach
	void closeService() throws Exception {
		closeable.close();
	}

	@Test
	void testGetId() {
		assertThat(openHospitalDataCollector.getId()).isEqualTo("TEL_OH");
	}

	@Test
	void testGetDescription() {
		assertThat(openHospitalDataCollector.getDescription()).isEqualTo(
						"Hospital general information (Country; Region, City, Postal Code, TimeZone, Currency, OH Version, Number of Patients / Beds / Wards / Users)");
	}

	@Test
	void testRetrieveData() throws Exception {
		ReflectionTestUtils.setField(openHospitalDataCollector, "settings", geoIpInfoSettingsMock);
		when(geoIpInfoSettingsMock.getSelectedService()).thenReturn("theService");
		ReflectionTestUtils.setField(openHospitalDataCollector, "geoIpServices", geoIpServicesMock);
		when(geoIpServicesMock.size()).thenReturn(1);
		when(wardIoOperations.countAllActiveBeds()).thenReturn(100l);

		Map<String, String> data = openHospitalDataCollector.retrieveData();
		assertThat(data).isNotNull();
		assertThat(data).hasSize(16);
	}
}
