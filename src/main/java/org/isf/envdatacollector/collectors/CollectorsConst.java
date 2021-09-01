package org.isf.envdatacollector.collectors;

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
	public static final String OH_ACTIVE_PATIENTS = "oh_active_patiens";
	public static final String OH_ACTIVE_USERS = "oh_active_users";

	// FUN_TIME
	public static final String TIME_LAST_USED = "time_last_used";
	
}
