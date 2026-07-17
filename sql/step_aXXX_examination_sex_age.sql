-- OP-892: store a snapshot of the patient's sex and age on each patient examination (STRIDE I08), so that
-- aggregate statistics can be produced without joining (and exposing) the patient's personal data.

ALTER TABLE OH_PATIENTEXAMINATION
	ADD COLUMN PEX_SEX CHAR(1) NULL,
	ADD COLUMN PEX_AGE INT NULL;

-- Backfill existing examinations with the patient's sex and the patient's age at the examination date.
UPDATE OH_PATIENTEXAMINATION e
	INNER JOIN OH_PATIENT p ON e.PEX_PAT_ID = p.PAT_ID
	SET e.PEX_SEX = p.PAT_SEX,
		e.PEX_AGE = CASE
			WHEN p.PAT_BDATE IS NOT NULL THEN TIMESTAMPDIFF(YEAR, p.PAT_BDATE, e.PEX_DATE)
			ELSE NULL
		END;
