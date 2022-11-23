ALTER TABLE oh_bills ADD COLUMN BLL_ADM_ID INT(11) NULL DEFAULT NULL AFTER BLL_USR_ID_A;
ALTER TABLE oh_bills
ADD INDEX FK_BILLS_ADMISSION_idx (BLL_ADM_ID ASC);
ALTER TABLE oh_bills
ADD CONSTRAINT FK_BILLS_ADMISSION
  FOREIGN KEY (BLL_ADM_ID)
  REFERENCES oh_admission (ADM_ID)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;


-- Link previous bills related admissions (using admission and discharge date)
DROP PROCEDURE IF EXISTS link_bill_admission;
DELIMITER //
  CREATE PROCEDURE link_bill_admission()
  BEGIN

	DECLARE done INT;
	DECLARE $adm_id INT;
	DECLARE $adm_pat_id INT;
	DECLARE $adm_date_adm DATETIME;
	DECLARE $adm_date_dis DATETIME;
    
	DECLARE cur1 CURSOR FOR SELECT ADM_ID, ADM_PAT_ID, ADM_DATE_ADM, ADM_DATE_DIS FROM OH_ADMISSION WHERE ADM_DELETED = 'N';
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;
    
	OPEN cur1;
	  read_loop: LOOP
		FETCH cur1 INTO $adm_id, $adm_pat_id, $adm_date_adm, $adm_date_dis;
		IF done = 1 THEN
			LEAVE read_loop;
		END IF;
        
		-- SELECT $adm_id, $adm_pat_id, $adm_date_adm, $adm_date_dis;
		IF $adm_date_dis IS NOT NULL THEN
			-- SELECT "Discharged";
			UPDATE OH_BILLS SET BLL_ADM_ID = $adm_id WHERE BLL_ID_PAT = $adm_pat_id AND BLL_DATE BETWEEN $adm_date_adm AND $adm_date_dis;
		ELSE
			-- SELECT "Current admission";
			UPDATE OH_BILLS SET BLL_ADM_ID = $adm_id WHERE BLL_ID_PAT = $adm_pat_id AND BLL_DATE >= $adm_date_adm;
		END IF;
	  END LOOP;
	CLOSE cur1;
  END //
DELIMITER ;

CALL link_bill_admission();