-- OP-1434: add the authority permissions guarding the billing (/bills) and
-- stock movement (/stockmovements) REST endpoints, which previously had no
-- operation-specific authorization rule and fell through to
-- anyRequest().authenticated(), so any authenticated user could reach them.
--
-- The permissions are granted to the admin group ONLY: admin already holds
-- every permission, so existing admin-driven workflows keep working out of the
-- box. Any other user group that needs billing or stock-movement API access
-- must be granted these permissions explicitly by an administrator. That is
-- intentional and is the point of the fix: unconfigured users must no longer
-- reach these endpoints.
INSERT INTO `oh_permissions` (`P_NAME`, `P_DESCRIPTION`, `P_ACTIVE`, `P_CREATED_BY`, `P_CREATED_DATE`, `P_LAST_MODIFIED_BY`, `P_LAST_MODIFIED_DATE`) VALUES ('bills.create','','1',NULL,NULL,NULL,NULL);
INSERT INTO `oh_permissions` (`P_NAME`, `P_DESCRIPTION`, `P_ACTIVE`, `P_CREATED_BY`, `P_CREATED_DATE`, `P_LAST_MODIFIED_BY`, `P_LAST_MODIFIED_DATE`) VALUES ('bills.read','','1',NULL,NULL,NULL,NULL);
INSERT INTO `oh_permissions` (`P_NAME`, `P_DESCRIPTION`, `P_ACTIVE`, `P_CREATED_BY`, `P_CREATED_DATE`, `P_LAST_MODIFIED_BY`, `P_LAST_MODIFIED_DATE`) VALUES ('bills.update','','1',NULL,NULL,NULL,NULL);
INSERT INTO `oh_permissions` (`P_NAME`, `P_DESCRIPTION`, `P_ACTIVE`, `P_CREATED_BY`, `P_CREATED_DATE`, `P_LAST_MODIFIED_BY`, `P_LAST_MODIFIED_DATE`) VALUES ('bills.delete','','1',NULL,NULL,NULL,NULL);
INSERT INTO `oh_permissions` (`P_NAME`, `P_DESCRIPTION`, `P_ACTIVE`, `P_CREATED_BY`, `P_CREATED_DATE`, `P_LAST_MODIFIED_BY`, `P_LAST_MODIFIED_DATE`) VALUES ('stockmovements.create','','1',NULL,NULL,NULL,NULL);
INSERT INTO `oh_permissions` (`P_NAME`, `P_DESCRIPTION`, `P_ACTIVE`, `P_CREATED_BY`, `P_CREATED_DATE`, `P_LAST_MODIFIED_BY`, `P_LAST_MODIFIED_DATE`) VALUES ('stockmovements.read','','1',NULL,NULL,NULL,NULL);
INSERT INTO `oh_permissions` (`P_NAME`, `P_DESCRIPTION`, `P_ACTIVE`, `P_CREATED_BY`, `P_CREATED_DATE`, `P_LAST_MODIFIED_BY`, `P_LAST_MODIFIED_DATE`) VALUES ('stockmovements.update','','1',NULL,NULL,NULL,NULL);
INSERT INTO `oh_permissions` (`P_NAME`, `P_DESCRIPTION`, `P_ACTIVE`, `P_CREATED_BY`, `P_CREATED_DATE`, `P_LAST_MODIFIED_BY`, `P_LAST_MODIFIED_DATE`) VALUES ('stockmovements.delete','','1',NULL,NULL,NULL,NULL);

INSERT INTO `oh_grouppermission` (`GP_UG_ID_A`, `GP_P_ID_A`, `GP_ACTIVE`, `GP_CREATED_BY`, `GP_CREATED_DATE`, `GP_LAST_MODIFIED_BY`, `GP_LAST_MODIFIED_DATE`)
	SELECT 'admin', `P_ID_A`, '1', NULL, NULL, NULL, NULL FROM `oh_permissions`
	WHERE `P_NAME` IN ('bills.create','bills.read','bills.update','bills.delete','stockmovements.create','stockmovements.read','stockmovements.update','stockmovements.delete');
