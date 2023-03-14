CREATE TABLE OH_PATIENT_CONSENSUS (
  PTC_ID INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  PTC_US_ID_A VARCHAR(50) NOT NULL,
  PTC_CONSENSUNS TINYINT(1) NOT NULL DEFAULT 0,
  PTC_ADMINISTRATIVE  TINYINT(1) NOT NULL DEFAULT 0,
  PTC_SERVICE  TINYINT(1) NOT NULL DEFAULT 0,
  PTC_CREATED_BY VARCHAR(50) NULL DEFAULT NULL,
  PTC_CREATED_DATE datetime NULL DEFAULT NULL,
  PTC_LAST_MODIFIED_BY VARCHAR(50) NULL DEFAULT NULL,
  PTC_LAST_MODIFIED_DATE datetime NULL DEFAULT NULL,
  PTC_ACTIVE TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (PTC_ID),
  FOREIGN KEY (PTC_US_ID_A) REFERENCES OH_USER(US_ID_A)
) ENGINE = INNODB DEFAULT CHARACTER SET utf8;
