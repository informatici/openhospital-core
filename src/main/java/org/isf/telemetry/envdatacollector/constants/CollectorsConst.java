/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

public interface CollectorsConst {

	// FUN_DBMS
	public static final String DBMS_DRIVER_NAME = "dbms_driver_name";
	public static final String DBMS_DRIVER_VERSION = "dbms_driver_version";
	public static final String DBMS_USERNAME = "dbms_username";
	public static final String DBMS_PRODUCT_NAME = "dbms_product_name";
	public static final String DBMS_PRODUCT_VERSION = "dbms_product_version";

	// FUN_HW
	public static final String HW_CPU_NUM_PHYSICAL_PROCESSES = "hw_cpu_number_physical_processes";
	public static final String HW_CPU_NUM_LOGICAL_PROCESSES = "hw_cpu_number_logical_processes";
	public static final String HW_CPU_NAME = "hw_cpu_name";
	public static final String HW_CPU_IDENTIFIER = "hw_cpu_idientifier";
	public static final String HW_CPU_MODEL = "hw_cpu_model";
	public static final String HW_CPU_ARCHITECTURE = "hw_cpu_microarchitecture";
	public static final String HW_CPU_VENDOR = "hw_cpu_vendor";
	public static final String HW_CPU_CTX_SWITCHES = "hw_cpu_context_switches";

	// FUN_OS
	public static final String OS_FAMILY = "os_family";
	public static final String OS_VERSION = "os_version";
	public static final String OS_MANUFACTURER = "os_manufacturer";
	public static final String OS_BITNESS = "os_bitness";
	public static final String OS_CODENAME = "os_codename";

	// FUN_APPLICATION
	public static final String APP_VER_MAJOR = "app_ver_major";
	public static final String APP_VER_MINOR = "app_ver_minor";
	public static final String APP_RELEASE = "app_release";

	// FUN_OH
	public static final String OH_TOTAL_ACTIVE_PATIENTS = "oh_active_patiens";
	public static final String OH_TOTAL_ACTIVE_USERS = "oh_active_users";
	public static final String OH_TOTAL_ACTIVE_WARDS = "oh_active_wards";
	public static final String OH_TOTAL_ACTIVE_BEDS = "oh_active_beds";

	// FUN_TIME
	public static final String TIME_LAST_USED = "time_last_used";

	// FUN_LOCATION
	public static final String LOC_COUNTRY_NAME = "loc_country_name";
	public static final String LOC_COUNTRY_CODE = "loc_country_code";
	public static final String LOC_REGION_NAME = "loc_region_name";
	public static final String LOC_REGION_CODE = "loc_region_code";
	public static final String LOC_CITY = "loc_city";
	public static final String LOC_ZIP_CODE = "loc_zip_code";
	public static final String LOC_TIMEZONE = "loc_timezone";
	
}

