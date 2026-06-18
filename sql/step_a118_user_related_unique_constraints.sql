-- OP-1099: enforce uniqueness on user-related tables (permission name and user group / permission pair).
--
-- Pre-flight checks (run before applying; both should return no rows):
--   SELECT P_NAME, COUNT(*) AS cnt FROM `oh`.`oh_permissions` GROUP BY P_NAME HAVING cnt > 1;
--   SELECT GP_UG_ID_A, GP_P_ID_A, COUNT(*) AS cnt FROM `oh`.`oh_grouppermission` GROUP BY GP_UG_ID_A, GP_P_ID_A HAVING cnt > 1;

-- Unique permission name. This runs first and before any destructive statement: duplicate permission names are
-- NOT removed automatically (a permission can be referenced by group permissions), so on a dirty database this
-- ALTER fails fast, leaving the data untouched. Resolve the duplicates reported by the pre-flight check, then re-run.
ALTER TABLE `oh`.`oh_permissions`
	ADD CONSTRAINT `UX_PERMISSIONS_NAME` UNIQUE (`P_NAME`);

-- Remove duplicate (user group, permission) rows, keeping the one with the lowest GP_ID.
-- Such duplicates could have been produced by the previous updateUserGroup() behaviour.
DELETE gp1 FROM `oh`.`oh_grouppermission` gp1
	INNER JOIN `oh`.`oh_grouppermission` gp2
		ON gp1.GP_UG_ID_A = gp2.GP_UG_ID_A
		AND gp1.GP_P_ID_A = gp2.GP_P_ID_A
		AND gp1.GP_ID > gp2.GP_ID;

-- Unique (user group, permission) pair.
ALTER TABLE `oh`.`oh_grouppermission`
	ADD CONSTRAINT `UX_GP_GROUP_PERM` UNIQUE (`GP_UG_ID_A`, `GP_P_ID_A`);
