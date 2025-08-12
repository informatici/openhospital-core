-- Drop the existing table
DROP TABLE IF EXISTS OH_SETTINGS;

-- Create settings table structure
CREATE TABLE OH_SETTINGS (
  SETT_ID INT(11) NOT NULL AUTO_INCREMENT COMMENT 'Default generated ID',
  SETT_CODE VARCHAR(50) NOT NULL,
  SETT_CATEGORY ENUM('general', 'application', 'gui', 'accounting', 'pharmacy', 'imaging', 'reports', 'security', 'telemetry') NOT NULL DEFAULT 'general',
  SETT_VALUE_TYPE ENUM('bool', 'number', 'text', 'select') NOT NULL DEFAULT 'text',
  SETT_VALUE_OPTIONS VARCHAR(500) NULL DEFAULT NULL COMMENT "Comma-separated list of possible values, in case of type 'select'",
  SETT_DEFAULT_VALUE VARCHAR(255) NOT NULL,
  SETT_VALUE VARCHAR(255) NOT NULL,
  SETT_DESCRIPTION VARCHAR(500) NULL,
  SETT_CREATED_BY VARCHAR(50) NULL DEFAULT NULL,
  SETT_CREATED_DATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  SETT_LAST_MODIFIED_BY VARCHAR(50) NULL DEFAULT NULL,
  SETT_LAST_MODIFIED_DATE DATETIME NULL DEFAULT NULL,
  SETT_ACTIVE TINYINT(1) NOT NULL DEFAULT 1,
  SETT_NEED_RESTART TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (SETT_ID),
  CONSTRAINT setting_unique_code UNIQUE (SETT_CODE)
) ENGINE=InnoDB;

-- Create permissions for settings
INSERT INTO `oh_permissions` (`P_ID_A`, `P_NAME`, `P_DESCRIPTION`, `P_ACTIVE`, `P_CREATED_BY`, `P_CREATED_DATE`, `P_LAST_MODIFIED_BY`, `P_LAST_MODIFIED_DATE`) VALUES (172,'settings.read','','1',NULL,NULL,NULL,NULL);
INSERT INTO `oh_permissions` (`P_ID_A`, `P_NAME`, `P_DESCRIPTION`, `P_ACTIVE`, `P_CREATED_BY`, `P_CREATED_DATE`, `P_LAST_MODIFIED_BY`, `P_LAST_MODIFIED_DATE`) VALUES (173,'settings.update','','1',NULL,NULL,NULL,NULL);

-- Assign setting permissions to admin group
INSERT INTO `oh_grouppermission` (`GP_ID`, `GP_UG_ID_A`, `GP_P_ID_A`, `GP_ACTIVE`, `GP_CREATED_BY`, `GP_CREATED_DATE`, `GP_LAST_MODIFIED_BY`, `GP_LAST_MODIFIED_DATE`) VALUES (317,'admin',172,'1',NULL,NULL,NULL,NULL);
INSERT INTO `oh_grouppermission` (`GP_ID`, `GP_UG_ID_A`, `GP_P_ID_A`, `GP_ACTIVE`, `GP_CREATED_BY`, `GP_CREATED_DATE`, `GP_LAST_MODIFIED_BY`, `GP_LAST_MODIFIED_DATE`) VALUES (318,'admin',173,'1',NULL,NULL,NULL,NULL);

-- Load settings into the database
INSERT INTO `OH_SETTINGS` (`SETT_ID`, `SETT_CODE`, `SETT_VALUE_TYPE`, `SETT_VALUE_OPTIONS`, `SETT_DEFAULT_VALUE`, `SETT_VALUE`, `SETT_DESCRIPTION`, `SETT_CREATED_BY`, `SETT_LAST_MODIFIED_BY`, `SETT_CREATED_DATE`, `SETT_LAST_MODIFIED_DATE`, `SETT_ACTIVE`, `SETT_NEED_RESTART`, `SETT_CATEGORY`) VALUES
(NULL, 'SINGLEUSER', 'bool', NULL, 'FALSE', 'FALSE', 'Make the software to work with only one user', 'admin', NULL, NOW(), NULL, 1, 1, 'general'),
(NULL, 'LANGUAGE', 'select', 'am_ET,ar,ar_SA,de,en,es,fr,it,pt,sq,sw,zh_CN', 'en', 'en', 'Display language', 'admin', NULL, NOW(), NULL, 1, 1, 'general'),
(NULL, 'DOC_DIR', 'text', NULL, '../doc', '../doc', 'Directory path in which user guides are stored', 'admin', NULL, NOW(), NULL, 1, 1, 'general'),
(NULL, 'INTERNALVIEWER', 'bool', NULL, 'TRUE', 'TRUE', 'Use the internal viewer', 'admin', NULL, NOW(), NULL, 1, 1, 'general'),
(NULL, 'VIEWER', 'text', NULL, '', '', 'Path to custom PDF viewer', 'admin', NULL, NOW(), NULL, 1, 1, 'general'),
(NULL, 'XMPPMODULEENABLED', 'bool', NULL, 'FALSE', 'FALSE', 'Whether to enable XMPP module or not', 'admin', NULL, NOW(), NULL, 1, 1, 'general'),
(NULL, 'PATIENTPHOTOSTORAGE', 'text', NULL, '', '', 'Path to the directory where patient photos are stored', 'admin', NULL, NOW(), NULL, 1, 1, 'general'),
(NULL, 'DEBUG', 'bool', NULL, 'FALSE', 'FALSE', 'Use debug mode', 'admin', NULL, NOW(), NULL, 1, 1, 'general'),
(NULL, 'MAINMENUALWAYSONTOP', 'bool', NULL, 'FALSE', 'FALSE', 'Always display main menu window on top', 'admin', NULL, NOW(), NULL, 1, 1, 'gui'),
(NULL, 'PATIENTEXTENDED', 'bool', NULL, 'FALSE', 'FALSE', 'Enable extended patient form', 'admin', NULL, NOW(), NULL, 1, 1, 'application'),
(NULL, 'OPDEXTENDED', 'bool', NULL, 'FALSE', 'FALSE', 'Enable extended OPD form', 'admin', NULL, NOW(), NULL, 1, 1, 'application'),
(NULL, 'LABEXTENDED', 'bool', NULL, 'FALSE', 'FALSE', 'Enable extended laboratory', 'admin', NULL, NOW(), NULL, 1, 1, 'application'),
(NULL, 'PATIENTVACCINEEXTENDED', 'bool', NULL, 'FALSE', 'FALSE', 'Use extended patient vaccine', 'admin', NULL, NOW(), NULL, 1, 1, 'application'),
(NULL, 'MERGEFUNCTION', 'bool', NULL, 'TRUE', 'TRUE', 'Enable patient merge feature', 'admin', NULL, NOW(), NULL, 1, 1, 'application'),
(NULL, 'SMSENABLED', 'bool', NULL, 'FALSE', 'FALSE', 'Enable SMS sending', 'admin', NULL, NOW(), NULL, 1, 1, 'application'),
(NULL, 'VIDEOMODULEENABLED', 'bool', NULL, 'FALSE', 'FALSE', 'Whether to enable Video module or not', 'admin', NULL, NOW(), NULL, 1, 1, 'application'),
(NULL, 'ENHANCEDSEARCH', 'bool', NULL, 'FALSE', 'FALSE', 'Enable enhanced search', 'admin', NULL, NOW(), NULL, 1, 1, 'application'),
(NULL, 'LABMULTIPLEINSERT', 'bool', NULL, 'FALSE', 'FALSE', 'Use multiple insert for lab exams', 'admin', NULL, NOW(), NULL, 1, 1, 'application'),
(NULL, 'MATERNITYRESTARTINJUNE', 'bool', NULL, 'FALSE', 'FALSE', 'Restart maternity in June', 'admin', NULL, NOW(), NULL, 1, 1, 'application'),
(NULL, 'TELEMETRYENABLED', 'bool', NULL, 'FALSE', 'FALSE', 'Enable telemetry', 'admin', NULL, NOW(), NULL, 1, 1, 'telemetry'),
(NULL, 'USERSLISTLOGIN', 'bool', NULL, 'FALSE', 'FALSE', 'Whether to show the list', 'admin', NULL, NOW(), NULL, 1, 1, 'security'),
(NULL, 'SESSIONTIMEOUT', 'number', NULL, '5', '5', 'Number in minute for session timeout', 'admin', NULL, NOW(), NULL, 1, 1, 'security'),
(NULL, 'STRONGLENGTH', 'number', NULL, '6', '6', 'The strength level of passwords', 'admin', NULL, NOW(), NULL, 1, 1, 'security'),
(NULL, 'PASSWORDTRIES', 'number', NULL, '5', '5', 'Number of password attempts before locking the account', 'admin', NULL, NOW(), NULL, 1, 1, 'security'),
(NULL, 'PASSWORDLOCKTIME', 'number', NULL, '60', '60', 'Number in minutes for password lock time', 'admin', NULL, NOW(), NULL, 1, 1, 'security'),
(NULL, 'PASSWORDIDLE', 'number', NULL, '360', '360', 'Number in days of password validity', 'admin', NULL, NOW(), NULL, 1, 1, 'security'),
(NULL, 'STRONGPASSWORD', 'bool', NULL, 'TRUE', 'FALSE', 'Whether to required strong passwords or not', 'admin', NULL, NOW(), NULL, 1, 1, 'security'),
(NULL, 'AUTOMATICLOT_IN', 'bool', NULL, 'TRUE', 'TRUE', 'Automatically create and assign lot to new stock entry mouvements', 'admin', NULL, NOW(), NULL, 1, 1, 'pharmacy'),
(NULL, 'AUTOMATICLOT_OUT', 'bool', NULL, 'TRUE', 'TRUE', 'Automatically create and assign lot to new stock out mouvements', 'admin', NULL, NOW(), NULL, 1, 1, 'pharmacy'),
(NULL, 'AUTOMATICLOTWARD_TOWARD', 'bool', NULL, 'TRUE', 'TRUE', 'Automatically create and assign lot to ward to ward stock mouvements', 'admin', NULL, NOW(), NULL, 1, 1, 'pharmacy'),
(NULL, 'INTERNALPHARMACIES', 'bool', NULL, 'FALSE', 'FALSE', 'Allow internal pharmacies', 'admin', NULL, NOW(), NULL, 1, 1, 'pharmacy'),
(NULL, 'LOTWITHCOST', 'bool', NULL, 'TRUE', 'TRUE', 'Use lot with cost', 'admin', NULL, NOW(), NULL, 1, 1, 'pharmacy'),
(NULL, 'RECEIPTPRINTER', 'bool', NULL, 'FALSE', 'FALSE', 'Enable bill receipt printing', 'admin', NULL, NOW(), NULL, 1, 1, 'accounting'),
(NULL, 'ALLOWPRINTOPENEDBILL', 'bool', NULL, 'FALSE', 'FALSE', 'Allow open bills printing', 'admin', NULL, NOW(), NULL, 1, 1, 'accounting'),
(NULL, 'ALLOWMULTIPLEOPENEDBILL', 'bool', NULL, 'FALSE', 'TRUE', 'Allow multiple open bills for a patient', 'admin', NULL, NOW(), NULL, 1, 1, 'accounting'),
(NULL, 'DICOMMODULEENABLED', 'bool', NULL, 'FALSE', 'FALSE', 'Whether to enable DICOM module or not', 'admin', NULL, NOW(), NULL, 1, 1, 'imaging'),
(NULL, 'DICOMTHUMBNAILS', 'bool', NULL, 'TRUE', 'TRUE', 'Enable DICOM thumbnails (previews)', 'admin', NULL, NOW(), NULL, 1, 1, 'imaging'),
(NULL, 'PATIENTSHEET', 'text', NULL, 'patient_clinical_sheet', 'patient_clinical_sheet', 'File name for patient sheet report', 'admin', NULL, NOW(), NULL, 1, 1, 'reports'),
(NULL, 'VISITSHEET', 'text', NULL, 'WardVisits', 'WardVisits', 'File name for visit sheet report', 'admin', NULL, NOW(), NULL, 1, 1, 'reports'),
(NULL, 'BILLSREPORTPENDING', 'text', NULL, 'BillsReportPending', 'BillsReportPending', 'File name for pending bills report', 'admin', NULL, NOW(), NULL, 1, 1, 'reports'),
(NULL, 'BILLSREPORTMONTHLY', 'text', NULL, 'BillsReportMonthly', 'BillsReportMonthly', 'File name for monthly bills report', 'admin', NULL, NOW(), NULL, 1, 1, 'reports'),
(NULL, 'PHARMACEUTICALORDER', 'text', NULL, 'PharmaceuticalOrder', 'PharmaceuticalOrder', 'File name for pharmaceuticals to order report', 'admin', NULL, NOW(), NULL, 1, 1, 'reports'),
(NULL, 'PHARMACEUTICALSTOCK', 'text', NULL, 'PharmaceuticalStock_ver4', 'PharmaceuticalStock_ver4', 'File name for pharmaceutical stock report', 'admin', NULL, NOW(), NULL, 1, 1, 'reports'),
(NULL, 'PHARMACEUTICALSTOCKLOT', 'text', NULL, 'PharmaceuticalStock_ver5', 'PharmaceuticalStock_ver5', 'File name for pharmaceutical lots stock report', 'admin', NULL, NOW(), NULL, 1, 1, 'reports'),
(NULL, 'PHARMACEUTICALAMC', 'text', NULL, 'PharmaceuticalAMC', 'PharmaceuticalAMC', 'File name for pharmaceutical AMC report', 'admin', NULL, NOW(), NULL, 1, 1, 'reports'),
(NULL, 'PATIENTBILLGROUPED', 'text', NULL, 'PatientBillGrouped', 'PatientBillGrouped', 'File name for patient bills grouped report', 'admin', NULL, NOW(), NULL, 1, 1, 'reports'),
(NULL, 'PATIENTBILLSTATEMENT', 'text', NULL, 'PatientBillStatement', 'PatientBillStatement', 'File name for patient bills statement report', 'admin', NULL, NOW(), NULL, 1, 1, 'reports'),
(NULL, 'EXAMINATIONCHART', 'text', NULL, 'patient_examination', 'patient_examination', 'File name for patient examination report', 'admin', NULL, NOW(), NULL, 1, 1, 'reports'),
(NULL, 'OPDCHART', 'text', NULL, 'patient_opd_chart', 'patient_opd_chart', 'File name for OPD chart report', 'admin', NULL, NOW(), NULL, 1, 1, 'reports'),
(NULL, 'ADMCHART', 'text', NULL, 'patient_adm_chart', 'patient_adm_chart', 'File name for admissions chart report', 'admin', NULL, NOW(), NULL, 1, 1, 'reports'),
(NULL, 'DISCHART', 'text', NULL, 'patient_dis_chart', 'patient_dis_chart', 'File name for discharges chart report', 'admin', NULL, NOW(), NULL, 1, 1, 'reports'),
(NULL, 'PATIENTBILL', 'text', NULL, 'PatientBill', 'PatientBill', 'File name for patient bill report', 'admin', NULL, NOW(), NULL, 1, 1, 'reports'),
(NULL, 'BILLSREPORT', 'text', NULL, 'BillsReport', 'BillsReport', 'File name for bills report', 'admin', NULL, NOW(), NULL, 1, 1, 'reports'),
(NULL, 'PARAMSURL', 'text', NULL, '', '', NULL, 'admin', NULL, NOW(), NULL, 1, 1, 'general');
