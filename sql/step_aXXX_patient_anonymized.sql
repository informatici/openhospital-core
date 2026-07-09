-- OP-886: add the PAT_ANONYMIZED flag to OH_PATIENT, marking a patient record whose personal
-- identification data has been irreversibly anonymized (GDPR right to erasure). The flag stays 0
-- for every existing patient; the clinical records are preserved for statistical use.
ALTER TABLE OH_PATIENT
ADD COLUMN PAT_ANONYMIZED TINYINT(1) NOT NULL DEFAULT 0;

-- Register the patient.anonymize permission (used by the REST API and the GUI to gate the action)
-- and grant it to the admin group. P_ID_A / GP_ID are auto-increment, so no explicit id is assigned.
INSERT INTO OH_PERMISSIONS (P_NAME, P_DESCRIPTION, P_ACTIVE)
	VALUES ('patient.anonymize', 'Anonymize a patient (GDPR right to erasure)', 1);

INSERT INTO OH_GROUPPERMISSION (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE)
	SELECT 'admin', P_ID_A, 1 FROM OH_PERMISSIONS WHERE P_NAME = 'patient.anonymize';
