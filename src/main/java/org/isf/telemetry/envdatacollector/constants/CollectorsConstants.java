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
package org.isf.telemetry.envdatacollector.constants;

public interface CollectorsConstants {

	// TEL_ID
	String TEL_UUID = "tel_uuid";
	String TEL_SENT_DATE = "tel_sent_date";
	String TEL_OPTIN_DATE = "tel_optin_date";
	String TEL_OPTOUT_DATE = "tel_optout_date";

	// TEL_DBMS
	String DBMS_DRIVER_NAME = "dbms_driver_name";
	String DBMS_DRIVER_VERSION = "dbms_driver_version";
	String DBMS_PRODUCT_NAME = "dbms_product_name";
	String DBMS_PRODUCT_VERSION = "dbms_product_version";

	// TEL_HW
	String HW_CPU_NUM_PHYSICAL_PROCESSES = "hw_cpu_number_physical_processes";
	String HW_CPU_NUM_LOGICAL_PROCESSES = "hw_cpu_number_logical_processes";
	String HW_CPU_NAME = "hw_cpu_name";
	String HW_CPU_IDENTIFIER = "hw_cpu_idientifier";
	String HW_CPU_MODEL = "hw_cpu_model";
	String HW_CPU_ARCHITECTURE = "hw_cpu_microarchitecture";
	String HW_CPU_VENDOR = "hw_cpu_vendor";
	String HW_CPU_CTX_SWITCHES = "hw_cpu_context_switches";
	String HW_MEM_TOTAL = "hw_mem_total_memory";

	// TEL_OS
	String OS_FAMILY = "os_family";
	String OS_VERSION = "os_version";
	String OS_MANUFACTURER = "os_manufacturer";
	String OS_BITNESS = "os_bitness";
	String OS_CODENAME = "os_codename";

	// TEL_OH
	String APP_VERSION = "app_version";
	String APP_MODE = "app_mode";
	String APP_DEMODATA = "app_demodata";
	String APP_APISERVER = "app_apiserver";
	String APP_LANGUAGE = "app_language";
	String APP_SINGLEUSER = "app_singleuser";
	String APP_DEBUG = "app_debug";
	String APP_INTERNALVIEWER = "app_internaluser";
	String APP_SMSENABLED = "app_smsenabled";
	String APP_VIDEOMODULEENABLED = "app_videomoduleenabled";
	String APP_XMPPMODULEENABLED = "app_xmppmoduleenabled";
	String APP_ENHANCEDSEARCH = "app_enhancedsearch";
	String APP_INTERNALPHARMACIES = "app_internalpharmacies";
	String APP_LABEXTENDED = "app_labextended";
	String APP_LABMULTIPLEINSERT = "app_labmultipleinsert";
	String APP_MATERNITYRESTARTINJUNE = "app_maternityrestartinjune";
	String APP_MERGEFUNCTION = "app_mergefunction";
	String APP_OPDEXTENDED = "app_opdextended";
	String APP_PATIENTEXTENDED = "app_patientextended";
	String APP_PATIENTVACCINEEXTENDED = "app_patientvaccineextended";
	String APP_MAINMENUALWAYSONTOP = "app_mainmenualwaysontop";
	String APP_ALLOWMULTIPLEOPENEDBILL = "app_allowmultipleopenedbill";
	String APP_ALLOWPRINTOPENEDBILL = "app_allowprintopenedbill";
	String APP_RECEIPTPRINTER = "app_receiptprinter";
	String APP_AUTOMATICLOT_IN = "app_automaticlot_in";
	String APP_AUTOMATICLOT_OUT = "app_automaticlot_out";
	String APP_AUTOMATICLOTWARD_TOWARD = "app_automaticlotward_toward";
	String APP_LOTWITHCOST = "app_lotwithcost";
	String APP_DICOMMODULEENABLED = "app_dicommoduleenabled";
	String APP_DICOMTHUMBNAILS = "app_dicomthumbnails";
	String OH_NUMBER_OF_PATIENTS = "oh_patients";
	String OH_NUMBER_OF_USERS = "oh_users";
	String OH_NUMBER_OF_ROLES = "oh_roles";
	String OH_NUMBER_OF_WARDS = "oh_wards";
	String OH_NUMBER_OF_BEDS = "oh_beds";
	String OH_NUMBER_OF_OPDS = "oh_opds";
	String OH_NUMBER_OF_ADMISSIONS = "oh_admissions";
	String OH_NUMBER_OF_EXAMS = "oh_exams";
	String OH_NUMBER_OF_VACCINES = "oh_vaccines";
	String OH_NUMBER_OF_OPERATIONS = "oh_operations";
	String OH_NUMBER_OF_STOCKMOVEMENTS = "oh_stockmovements";
	String OH_NUMBER_OF_STOCKWMOVEMENTSWARDS = "oh_stockmovementswards";
	String OH_NUMBER_OF_THERAPIES = "oh_therapies";
	String OH_NUMBER_OF_APPOINTMENTS = "oh_appointments";
	String OH_NUMBER_OF_BILLS = "oh_bills";
	String TIME_LAST_USED = "time_last_used";

	// TEL_LOCATION
	String LOC_COUNTRY_NAME = "loc_country_name";
	String LOC_COUNTRY_CODE = "loc_country_code";
	String LOC_REGION_NAME = "loc_region_name";
	String LOC_REGION_CODE = "loc_region_code";
	String LOC_CITY = "loc_city";
	String LOC_ZIP_CODE = "loc_zip_code";
	String LOC_TIMEZONE = "loc_timezone";
	String LOC_CURRENCY_CODE = "currency_code";
	String LOC_CURRENCY_NAME = "currency_name";

}
