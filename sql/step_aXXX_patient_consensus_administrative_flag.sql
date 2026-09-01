-- OP-1037: flag the patients whose administrative position has to be solved before they are served again,
-- with the optional reason to show to the staff.
ALTER TABLE OH_PATIENT_CONSENSUS
  ADD COLUMN PTC_ADMINISTRATIVE TINYINT(1) NOT NULL DEFAULT 0 AFTER PTC_CONSENSUS,
  ADD COLUMN PTC_ADMINISTRATIVE_REASON VARCHAR(255) DEFAULT NULL AFTER PTC_ADMINISTRATIVE;
