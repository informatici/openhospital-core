UPDATE oh_admission SET ADM_ACTIVE=IF(adm_deleted='Y', 0, 1);
UPDATE oh_patient SET PAT_ACTIVE=IF(pat_deleted='Y', 0, 1);
UPDATE oh_supplier SET SUP_ACTIVE=IF(sup_deleted='Y', 0, 1);

ALTER TABLE oh_admission DROP COLUMN adm_deleted;
ALTER TABLE oh_patient DROP COLUMN pat_deleted;
ALTER TABLE oh_supplier DROP COLUMN sup_deleted;