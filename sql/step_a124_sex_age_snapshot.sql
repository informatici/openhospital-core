-- OP-892: store a snapshot of the patient's sex and age on each admission and each patient examination
-- (STRIDE I08), so that aggregate statistics can be produced without joining (and exposing) the patient's
-- personal data.

ALTER TABLE OH_ADMISSION
	ADD COLUMN ADM_SEX CHAR(1) NULL AFTER ADM_PAT_ID,
	ADD COLUMN ADM_AGE INT NULL AFTER ADM_SEX;

-- Backfill existing admissions with the patient's sex and the patient's age at the admission date.
-- When the birth date is unknown fall back to the recorded PAT_AGE, matching what Admission.applyPatientData()
-- stores at runtime: a NULL here would drop the admission out of every age band in the statistics reports.
UPDATE OH_ADMISSION a
	INNER JOIN OH_PATIENT p ON a.ADM_PAT_ID = p.PAT_ID
	SET a.ADM_SEX = p.PAT_SEX,
		a.ADM_AGE = CASE
			WHEN p.PAT_BDATE IS NOT NULL THEN TIMESTAMPDIFF(YEAR, p.PAT_BDATE, a.ADM_DATE_ADM)
			ELSE p.PAT_AGE
		END;

ALTER TABLE OH_PATIENTEXAMINATION
	ADD COLUMN PEX_SEX CHAR(1) NULL AFTER PEX_PAT_ID,
	ADD COLUMN PEX_AGE INT NULL AFTER PEX_SEX;

-- Backfill existing examinations with the patient's sex and the patient's age at the examination date.
-- Same fallback as above, mirroring PatientExamination.applyPatientData().
UPDATE OH_PATIENTEXAMINATION e
	INNER JOIN OH_PATIENT p ON e.PEX_PAT_ID = p.PAT_ID
	SET e.PEX_SEX = p.PAT_SEX,
		e.PEX_AGE = CASE
			WHEN p.PAT_BDATE IS NOT NULL THEN TIMESTAMPDIFF(YEAR, p.PAT_BDATE, e.PEX_DATE)
			ELSE p.PAT_AGE
		END;
