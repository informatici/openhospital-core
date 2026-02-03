-- Add category to movementstock types: 'operational' or 'non-operational'
ALTER TABLE OH_MEDICALDSRSTOCKMOVTYPE ADD COLUMN MMVT_CATEGORY VARCHAR(15) NOT NULL DEFAULT 'operational' AFTER `MMVT_TYPE`;

-- Create new table for balances history
CREATE TABLE OH_MEDICALDSRSTOCK (
  MS_ID int(11) NOT NULL AUTO_INCREMENT,
  MS_MDSR_ID int(11) NOT NULL,
  MS_DATE_BALANCE date NOT NULL,
  MS_BALANCE int(11) NOT NULL,
  MS_DATE_NEXT_MOV date DEFAULT NULL,
  MS_DAYS int(11) DEFAULT NULL,
  MS_CREATED_BY VARCHAR(50) NULL DEFAULT NULL,
  MS_CREATED_DATE datetime NULL DEFAULT NULL,
  MS_LAST_MODIFIED_BY VARCHAR(50) NULL DEFAULT NULL,
  MS_LAST_MODIFIED_DATE datetime NULL DEFAULT NULL,
  MS_ACTIVE TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (MS_ID),
  UNIQUE KEY MS_MDSR_ID_DATE_BALANCE_UNIQUE (MS_MDSR_ID,MS_DATE_BALANCE),
  KEY FK_MEDICALDSRSTOCK_MEDICALDSR_idx (MS_MDSR_ID),
  CONSTRAINT FK_MEDICALDSRSTOCK_MEDICALDSR 
	FOREIGN KEY (MS_MDSR_ID) 
	REFERENCES OH_MEDICALDSR (MDSR_ID) 
    ON DELETE NO ACTION 
    ON UPDATE NO ACTION);
    
DROP PROCEDURE IF EXISTS populateMSTable;
DELIMITER //
    
CREATE PROCEDURE populateMSTable()
BEGIN
	DECLARE v_ms_id INT;
    DECLARE v_mdsr_id INT;
    DECLARE v_date DATE;
    DECLARE v_balance INT;
    DECLARE v_qty INT;
    DECLARE no_more_rows boolean DEFAULT FALSE;
	
	DECLARE curMovement CURSOR FOR
			SELECT MMV_MDSR_ID, DATE(MMV_DATE), SUM(IF(MMVT_TYPE LIKE '+', MMV_QTY, -MMV_QTY)) AS QTY
            FROM OH_MEDICALDSRSTOCKMOV
            LEFT JOIN OH_MEDICALDSRSTOCKMOVTYPE ON MMVT_ID_A = MMV_MMVT_ID_A
            GROUP BY DATE(MMV_DATE), MMV_MDSR_ID;
			
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET no_more_rows := true;
	-- DECLARE EXIT HANDLER FOR 1048 SELECT v_mdsr_id; -- needed for handling errors
    
	OPEN curMovement;
	movement_loop: LOOP
		FETCH curMovement INTO v_mdsr_id, v_date, v_qty;
		IF no_more_rows THEN
            CLOSE curMovement;
			LEAVE movement_loop;
		END IF;
        
        SET v_ms_id = NULL;
        SET v_balance = NULL;
        
        SELECT MAX(MS_ID)
			FROM OH_MEDICALDSRSTOCK
            WHERE MS_MDSR_ID = v_mdsr_id
            AND MS_DATE_BALANCE < v_date INTO v_ms_id;
            
            
		IF v_qty <> 0 THEN
			IF v_ms_id IS NOT NULL THEN
				UPDATE OH_MEDICALDSRSTOCK SET 
					MS_DATE_NEXT_MOV = v_date,
					MS_DAYS = DATEDIFF(v_date, MS_DATE_BALANCE)
					WHERE MS_ID = v_ms_id;
				SELECT MS_BALANCE FROM OH_MEDICALDSRSTOCK WHERE MS_ID = v_ms_id INTO v_balance;
				SET v_balance = v_balance + v_qty;
                INSERT INTO OH_MEDICALDSRSTOCK VALUES (0, v_mdsr_id, v_date, v_balance, NULL, NULL, 'admin', NULL, 'admin', NULL, 1);
			ELSE 
				SET v_balance = v_qty;
                INSERT INTO OH_MEDICALDSRSTOCK VALUES (0, v_mdsr_id, v_date, v_qty, NULL, NULL, 'admin', NULL, 'admin', NULL, 1);
			END IF;
            
		END IF;
        
	END LOOP movement_loop;
	
END; //
DELIMITER ;

CALL populateMSTable();