-- OP-892: store a snapshot of the patient's sex and age on each admission (STRIDE I08), so that aggregate
-- statistics can be produced without joining (and exposing) the patient's personal data.

ALTER TABLE `oh`.`oh_admission`
	ADD COLUMN ADM_SEX CHAR(1) NULL,
	ADD COLUMN ADM_AGE INT NULL;

-- Backfill existing admissions with the patient's sex and the patient's age at the admission date.
UPDATE `oh`.`oh_admission` a
	INNER JOIN `oh`.`oh_patient` p ON a.ADM_PAT_ID = p.PAT_ID
	SET a.ADM_SEX = p.PAT_SEX,
		a.ADM_AGE = CASE
			WHEN p.PAT_BDATE IS NOT NULL THEN TIMESTAMPDIFF(YEAR, p.PAT_BDATE, a.ADM_DATE_ADM)
			ELSE NULL
		END;
