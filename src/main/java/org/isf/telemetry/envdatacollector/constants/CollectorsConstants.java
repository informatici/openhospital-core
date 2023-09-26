/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2022 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
	String APP_VER_MAJOR = "app_ver_major";
	String APP_VER_MINOR = "app_ver_minor";
	String APP_RELEASE = "app_release";
	String OH_TOTAL_ACTIVE_PATIENTS = "oh_active_patients";
	String OH_TOTAL_ACTIVE_USERS = "oh_active_users";
	String OH_TOTAL_ACTIVE_WARDS = "oh_active_wards";
	String OH_TOTAL_ACTIVE_BEDS = "oh_active_beds";
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
