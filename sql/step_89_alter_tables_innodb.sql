--
-- Instead of manipulating and resorting system global variables just
-- drop and regenerate the offending Foreign Key constraint.
-- This makes the script database version independent.
-- See OP-1335  (https://openhospital.atlassian.net/browse/OP-1335)
--
ALTER TABLE OH_DICOM DROP FOREIGN KEY FK_DICOM_DICOMTYPE;

ALTER TABLE OH_DICOM ENGINE = INNODB, CONVERT TO CHARACTER SET utf8;
ALTER TABLE OH_DICOMTYPE ENGINE = INNODB, CONVERT TO CHARACTER SET utf8;

ALTER TABLE OH_DICOM
    ADD CONSTRAINT FK_DICOM_DICOMTYPE
        FOREIGN KEY (DM_DCMT_ID)
            REFERENCES OH_DICOMTYPE (DCMT_ID)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION;
