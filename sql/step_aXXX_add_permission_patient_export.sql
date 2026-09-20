-- Add permission
INSERT INTO `oh_permissions` (`P_NAME`, `P_DESCRIPTION`, `P_ACTIVE`, `P_CREATED_BY`, `P_CREATED_DATE`, `P_LAST_MODIFIED_BY`, `P_LAST_MODIFIED_DATE`) VALUES ('patient.export','','1',NULL,NULL,NULL,NULL);
-- Add group permisson
INSERT INTO `oh_grouppermission` (`GP_UG_ID_A`, `GP_P_ID_A`, `GP_ACTIVE`, `GP_CREATED_BY`, `GP_CREATED_DATE`, `GP_LAST_MODIFIED_BY`, `GP_LAST_MODIFIED_DATE`)
	SELECT 'admin', `P_ID_A`, '1', NULL, NULL, NULL, NULL FROM `oh_permissions` WHERE `P_NAME` = 'patient.export';
