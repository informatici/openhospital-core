-- Ajout de la colonne MH_LOCK pour la gestion du verrouillage optimiste
ALTER TABLE `oh_medicalhistory`
    ADD COLUMN `MH_LOCK` INT(11) NOT NULL DEFAULT 0;
