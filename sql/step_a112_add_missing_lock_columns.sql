--
-- Add lock (version) column in tables where it's missing
--

ALTER TABLE `oh_bills`
    ADD COLUMN `bll_lock` INT(11) NOT NULL DEFAULT 0;

ALTER TABLE `oh_agetype`
    ADD COLUMN `at_lock` INT(11) NOT NULL DEFAULT 0;

ALTER TABLE `oh_patienthistory`
    ADD COLUMN `pah_lock` INT(11) NOT NULL DEFAULT 0;

ALTER TABLE `oh_patientexamination`
    ADD COLUMN `pex_lock` INT(11) NOT NULL DEFAULT 0;

ALTER TABLE `oh_user_settings`
    ADD COLUMN `uss_lock` INT(11) NOT NULL DEFAULT 0;

ALTER TABLE `oh_prices`
    ADD COLUMN `prc_lock` INT(11) NOT NULL DEFAULT 0;

ALTER TABLE `oh_pricelists`
    ADD COLUMN `lst_lock` INT(11) NOT NULL DEFAULT 0;

ALTER TABLE `oh_supplier`
    ADD COLUMN `sup_lock` INT(11) NOT NULL DEFAULT 0;

ALTER TABLE `oh_pricesothers`
    ADD COLUMN `oth_lock` INT(11) NOT NULL DEFAULT 0;

ALTER TABLE `oh_visits`
    ADD COLUMN `vst_lock` INT(11) NOT NULL DEFAULT 0;





