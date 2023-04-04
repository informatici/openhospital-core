-- add the new table
-- adding primary key with auto increment. We will not be setting this auto increment manually
CREATE TABLE OH_DICOM_DATA (
    DMD_DATA_ID bigint(20) NOT NULL AUTO_INCREMENT,
    DMD_FILE_ID bigint(20),
    DMD_DATA longblob,
    PRIMARY KEY (DMD_DATA_ID)
) ENGINE=InnoDB CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;

-- migrate data from DICOM table to the new table, using the DMD_FILE_ID
INSERT INTO OH_DICOM_DATA(DMD_FILE_ID, DMD_DATA)
    (SELECT d.DM_FILE_ID, d.DM_DATA
     FROM OH_DICOM d
     WHERE d.DM_DATA IS NOT NULL);

-- drop original column
ALTER TABLE OH_DICOM DROP DM_DATA;

-- add DMD_FILE_ID foreign key
ALTER TABLE OH_DICOM_DATA
    ADD CONSTRAINT FK_DICOM_DATA_DICOM
        FOREIGN KEY (DMD_FILE_ID)
        REFERENCES OH_DICOM (DM_FILE_ID)
        ON DELETE CASCADE
        ON UPDATE CASCADE;
