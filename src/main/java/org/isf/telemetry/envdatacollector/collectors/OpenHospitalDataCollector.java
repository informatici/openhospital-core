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
package org.isf.telemetry.envdatacollector.collectors;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.isf.accounting.service.AccountingIoOperations;
import org.isf.admission.service.AdmissionIoOperations;
import org.isf.lab.service.LabIoOperations;
import org.isf.medicalstock.service.MedicalStockIoOperations;
import org.isf.medicalstockward.service.MedicalStockWardIoOperations;
import org.isf.menu.service.MenuIoOperations;
import org.isf.opd.service.OpdIoOperations;
import org.isf.operation.service.OperationRowIoOperations;
import org.isf.patient.service.PatientIoOperations;
import org.isf.telemetry.envdatacollector.AbstractDataCollector;
import org.isf.telemetry.envdatacollector.collectors.remote.common.GeoIpInfoBean;
import org.isf.telemetry.envdatacollector.collectors.remote.common.GeoIpInfoCommonService;
import org.isf.telemetry.envdatacollector.collectors.remote.common.GeoIpInfoSettings;
import org.isf.telemetry.envdatacollector.constants.CollectorsConstants;
import org.isf.therapy.service.TherapyIoOperations;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.vaccine.service.VaccineIoOperations;
import org.isf.visits.service.VisitsIoOperations;
import org.isf.ward.service.WardIoOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(value = 30)
@Component
public class OpenHospitalDataCollector extends AbstractDataCollector {

	private static final String ID = "TEL_OH";
	private static final Logger LOGGER = LoggerFactory.getLogger(OpenHospitalDataCollector.class);

	private PatientIoOperations patientIoOperations;

	private MenuIoOperations menuIoOperations;

	private WardIoOperations wardIoOperations;

	private OpdIoOperations opdIoOperations;

	private AdmissionIoOperations admissionIoOperations;

	private LabIoOperations laboratoryIoOperations;

	private VaccineIoOperations vaccineIoOperations;

	private OperationRowIoOperations operationRowIoOperations;

	private MedicalStockIoOperations medicalStockIoOperations;

	private MedicalStockWardIoOperations medicalStockWardIoOperations;

	private TherapyIoOperations therapyIoOperations;

	private VisitsIoOperations visitsIoOperations;

	private AccountingIoOperations accountingIoOperations;

	@Autowired
	private List<GeoIpInfoCommonService> geoIpServices;

	private GeoIpInfoSettings settings;

	public OpenHospitalDataCollector(PatientIoOperations patientIoOperations,
	                                 MenuIoOperations menuIoOperations,
	                                 WardIoOperations wardIoOperations,
	                                 OpdIoOperations opdIoOperations,
	                                 AdmissionIoOperations admissionIoOperations,
	                                 LabIoOperations laboratoryIoOperations,
	                                 VaccineIoOperations vaccineIoOperations,
	                                 OperationRowIoOperations operationRowIoOperations,
	                                 MedicalStockIoOperations medicalStockIoOperations,
	                                 MedicalStockWardIoOperations medicalStockWardIoOperations,
	                                 TherapyIoOperations therapyIoOperations,
	                                 VisitsIoOperations visitsIoOperations,
	                                 AccountingIoOperations accountingIoOperations,
	                                 GeoIpInfoSettings geoIpInfoSettings) {
		this.patientIoOperations = patientIoOperations;
		this.menuIoOperations = menuIoOperations;
		this.wardIoOperations = wardIoOperations;
		this.opdIoOperations = opdIoOperations;
		this.admissionIoOperations = admissionIoOperations;
		this.laboratoryIoOperations = laboratoryIoOperations;
		this.vaccineIoOperations = vaccineIoOperations;
		this.operationRowIoOperations = operationRowIoOperations;
		this.medicalStockIoOperations = medicalStockIoOperations;
		this.medicalStockWardIoOperations = medicalStockWardIoOperations;
		this.therapyIoOperations = therapyIoOperations;
		this.visitsIoOperations = visitsIoOperations;
		this.accountingIoOperations = accountingIoOperations;
		this.settings = geoIpInfoSettings;
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getDescription() {
		return "Hospital general information (Country; Region, City, Postal Code, TimeZone, Currency, OH Version, Number of Patients / Beds / Wards / Users)";
	}

	@Override
	public Map<String, String> retrieveData() throws OHException {
		LOGGER.debug("Collecting Open Hospital data...");
		Map<String, String> result = new LinkedHashMap<>();
		try {
			String geoIpServiceName = settings.getSelectedService();
			LOGGER.debug("{} - {}", geoIpServiceName, geoIpServices.size());
			this.geoIpServices.forEach(service -> {
				if (service.getServiceName().equals(geoIpServiceName)) {
					GeoIpInfoBean json = service.retrieveIpInfo();
					LOGGER.debug(json.toString());
					result.put(CollectorsConstants.LOC_COUNTRY_NAME, json.getCountryName());
					result.put(CollectorsConstants.LOC_COUNTRY_CODE, json.getCountryCode());
					result.put(CollectorsConstants.LOC_REGION_NAME, json.getRegionName());
					result.put(CollectorsConstants.LOC_CITY, json.getCity());
					result.put(CollectorsConstants.LOC_ZIP_CODE, json.getPostalCode());
					result.put(CollectorsConstants.LOC_TIMEZONE, json.getTimeZone());
					result.put(CollectorsConstants.LOC_CURRENCY_CODE, json.getCurrencyCode());
				}
			});

			result.put(CollectorsConstants.OH_NUMBER_OF_PATIENTS, String.valueOf(patientIoOperations.countAllActivePatients()));
			result.put(CollectorsConstants.OH_NUMBER_OF_USERS, String.valueOf(menuIoOperations.countAllActiveUsers()));
			result.put(CollectorsConstants.OH_NUMBER_OF_ROLES, String.valueOf(menuIoOperations.countAllActiveGroups()));
			result.put(CollectorsConstants.OH_NUMBER_OF_WARDS, String.valueOf(wardIoOperations.countAllActiveWards()));
			result.put(CollectorsConstants.OH_NUMBER_OF_BEDS, String.valueOf(wardIoOperations.countAllActiveBeds()));

			result.put(CollectorsConstants.OH_NUMBER_OF_OPDS, String.valueOf(opdIoOperations.countAllActiveOpds()));
			result.put(CollectorsConstants.OH_NUMBER_OF_ADMISSIONS, String.valueOf(admissionIoOperations.countAllActiveAdmissions()));
			result.put(CollectorsConstants.OH_NUMBER_OF_EXAMS, String.valueOf(laboratoryIoOperations.countAllActiveLabs()));
			result.put(CollectorsConstants.OH_NUMBER_OF_VACCINES, String.valueOf(vaccineIoOperations.countAllActiveVaccinations()));
			result.put(CollectorsConstants.OH_NUMBER_OF_OPERATIONS, String.valueOf(operationRowIoOperations.countAllActiveOperations()));
			result.put(CollectorsConstants.OH_NUMBER_OF_STOCKMOVEMENTS, String.valueOf(medicalStockIoOperations.countAllActiveMovements()));
			result.put(CollectorsConstants.OH_NUMBER_OF_STOCKWMOVEMENTSWARDS, String.valueOf(medicalStockWardIoOperations.countAllActiveMovementsWard()));
			result.put(CollectorsConstants.OH_NUMBER_OF_THERAPIES, String.valueOf(therapyIoOperations.countAllActiveTherapies()));
			result.put(CollectorsConstants.OH_NUMBER_OF_APPOINTMENTS, String.valueOf(visitsIoOperations.countAllActiveAppointments()));
			result.put(CollectorsConstants.OH_NUMBER_OF_BILLS, String.valueOf(accountingIoOperations.countAllActiveBills()));

			LocalDateTime lastUsedTime = opdIoOperations.lastOpdCreationDate();
			if (lastUsedTime == null) {
				lastUsedTime = LocalDateTime.now();
			}
			result.put(CollectorsConstants.TIME_LAST_USED, String.valueOf(lastUsedTime));
		} catch (OHServiceException e) {
			LOGGER.error("Something went wrong with " + ID);
			LOGGER.debug(e.getMessage(), e);
			throw new OHException("Data collector [" + ID + ']', e);
		}
		return result;
	}

}
