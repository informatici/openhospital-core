--
-- Add lock (version) column in oh_medicaldsrward
--

ALTER TABLE `oh_medicaldsrward`
    ADD COLUMN `MDSRWRD_LOCK` INT(11) NOT NULL DEFAULT 0;