-- first, add foreign key from patient to patient_profile_photo
-- this means that the patient table will be responsible in maintaining the relationship between patient and the profile photo table
ALTER TABLE PATIENT ADD COLUMN PROFILE_PHOTO_ID int(11);

-- now add the new table
-- note that:
-- 1. we are adding primary key with auto increment. We will not be setting this auto increment manually
-- 2. we are adding TEMP_PAT_ID for migration purpose, so that we can relate existing photo from patient table to this new table while doing migration
CREATE TABLE PATIENT_PROFILE_PHOTO (
                                       PAT_PROFILE_PHOTO_ID int NOT NULL AUTO_INCREMENT,
                                       TEMP_PAT_ID int NOT NULL,
                                       PAT_PHOTO BLOB,
                                       PRIMARY KEY (PAT_PROFILE_PHOTO_ID)
) ENGINE=MyISAM;

-- now, we migrate photo from PATIENT table to the new table, using the TEMP_PAT_ID
INSERT INTO PATIENT_PROFILE_PHOTO(TEMP_PAT_ID, PAT_PHOTO)
    (SELECT p.PAT_ID, p.PAT_PHOTO
     FROM PATIENT p
     WHERE p.PAT_PHOTO IS NOT NULL);

-- now we can update references from PATIENT table back to PATIENT_PROFILE_PHOTO
UPDATE PATIENT
    INNER JOIN PATIENT_PROFILE_PHOTO
    ON PATIENT.PAT_ID = PATIENT_PROFILE_PHOTO.TEMP_PAT_ID
SET PROFILE_PHOTO_ID=PATIENT_PROFILE_PHOTO.PAT_PROFILE_PHOTO_ID;

-- and finally, we can drop the temporary column
ALTER TABLE PATIENT_PROFILE_PHOTO DROP TEMP_PAT_ID;
ALTER TABLE PATIENT DROP PAT_PHOTO;