-- OP-886: add the PAT_ANONYMIZED flag to OH_PATIENT, marking a patient record whose personal
-- identification data has been irreversibly anonymized (GDPR right to erasure). The flag stays 0
-- for every existing patient; the clinical records are preserved for statistical use.
ALTER TABLE OH_PATIENT
ADD COLUMN PAT_ANONYMIZED TINYINT(1) NOT NULL DEFAULT 0;
