-- NOTE: part of a contiguous merge-order block (a119-a125) shared by several open PRs;
--       the step number may be reassigned at merge time to match the actual merge order.
CREATE TABLE OH_PROFESSION (
  PRF_ID_A VARCHAR(50) NOT NULL,
  PRF_DESC VARCHAR(50) NOT NULL,
  PRF_CREATED_BY VARCHAR(50) NULL DEFAULT NULL,
  PRF_CREATED_DATE datetime NULL DEFAULT NULL,
  PRF_LAST_MODIFIED_BY VARCHAR(50) NULL DEFAULT NULL,
  PRF_LAST_MODIFIED_DATE datetime NULL DEFAULT NULL,
  PRF_ACTIVE TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (PRF_ID_A)
) ENGINE = INNODB DEFAULT CHARACTER SET utf8;

-- Seed the professions previously hard-coded in PatientBrowserManager.
-- The code keeps the legacy key so existing OH_PATIENT.PAT_PROFESSION values keep resolving.
-- Descriptions are English defaults for migrated databases; fresh installations replace them
-- with the localized values loaded by step_03_dump_profession_data_<lang>.sql.
INSERT INTO OH_PROFESSION (PRF_ID_A, PRF_DESC, PRF_CREATED_BY, PRF_CREATED_DATE, PRF_LAST_MODIFIED_BY, PRF_LAST_MODIFIED_DATE) VALUES
  ('unknown', 'Unknown', 'admin', NOW(), 'admin', NOW()),
  ('other', 'Other', 'admin', NOW(), 'admin', NOW()),
  ('farming', 'Farming', 'admin', NOW(), 'admin', NOW()),
  ('construction', 'Construction', 'admin', NOW(), 'admin', NOW()),
  ('medicine', 'Medicine', 'admin', NOW(), 'admin', NOW()),
  ('foodhospitality', 'Food/Hospitality', 'admin', NOW(), 'admin', NOW()),
  ('homemaker', 'Homemaker', 'admin', NOW(), 'admin', NOW()),
  ('mechanic', 'Mechanic', 'admin', NOW(), 'admin', NOW()),
  ('business', 'Business', 'admin', NOW(), 'admin', NOW()),
  ('janitorial', 'Janitorial Services', 'admin', NOW(), 'admin', NOW()),
  ('mining', 'Mining', 'admin', NOW(), 'admin', NOW()),
  ('engineering', 'Engineering', 'admin', NOW(), 'admin', NOW());

-- Register the Profession browser in the menu, for fresh installations and migrated ones alike.
INSERT INTO OH_MENUITEM (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
  VALUES ('profession', 'angal.menu.btn.profession', 'angal.menu.profession', 'x', 'P', 'generaldata', 'org.isf.profession.gui.ProfessionBrowser', 'N', 11);

-- Grant the new menu item to the admin group.
INSERT INTO OH_GROUPMENU (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE) VALUES ('admin', 'profession', 1);
