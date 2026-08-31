-- OP-1037: add the administrative consensus flag to the patient consensus table.
-- NB: step number may need renumbering at merge time if it collides with another open migration PR.
ALTER TABLE OH_PATIENT_CONSENSUS ADD COLUMN PTC_ADMINISTRATIVE TINYINT(1) NOT NULL DEFAULT 0 AFTER PTC_CONSENSUS;
