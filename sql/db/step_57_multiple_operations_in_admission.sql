DELIMITER //

DROP PROCEDURE IF EXISTS moveOperationsToNewTable;
CREATE PROCEDURE moveOperationsToNewTable()
BEGIN
	DECLARE v_adm_id INT(11);
	DECLARE v_adm_user VARCHAR(50);
    DECLARE v_ope_id VARCHAR(10);
	DECLARE v_ope_date DATETIME;
	DECLARE v_ope_result VARCHAR(10);
	DECLARE v_trans FLOAT;
    DECLARE done INT DEFAULT FALSE;
    
    DECLARE cur CURSOR FOR SELECT ADM_ID, ADM_USR_ID_A, ADM_OPE_ID_A, ADM_DATE_OP, 
    						CASE COALESCE(ADM_RESOP, 'U') WHEN 'P' THEN 'success' WHEN 'N' THEN 'failure' WHEN 'U' THEN 'unknown' END AS ADM_RESOP, ADM_TRANS
							FROM ADMISSION
							WHERE ADM_OPE_ID_A IS NOT NULL;
							
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

    OPEN cur;
    read_loop: LOOP
        FETCH cur INTO v_adm_id, v_adm_user, v_ope_id, v_ope_date, v_ope_result, v_trans;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        INSERT INTO OPERATIONROW SET OPER_ID = v_ope_id, 
        						 OPER_PRESCRIBER = v_adm_user,
        						 OPER_RESULT = v_ope_result,
        						 OPER_OPDATE = v_ope_date,
        						 OPER_ADMISSION_ID = v_adm_id,
        						 OPER_TRANS_UNIT = v_trans;
    END LOOP;
  	CLOSE cur;
END; //

DELIMITER ;

CREATE TABLE OPERATIONROW(
	OPER_ID_A INT (11) NOT NULL AUTO_INCREMENT,
	OPER_ID VARCHAR (11) NOT NULL,
	OPER_PRESCRIBER VARCHAR (150) NOT NULL,
	OPER_RESULT VARCHAR (250) NOT NULL,
	OPER_OPDATE DATETIME NOT NULL,
	OPER_REMARKS VARCHAR (250) NOT NULL,
	OPER_ADMISSION_ID INT(11) DEFAULT NULL, 
	OPER_OPD_ID INT(11) DEFAULT NULL,
	OPER_BILL_ID INT(11) DEFAULT NULL,
	OPER_TRANS_UNIT FLOAT NULL DEFAULT 0,	
	PRIMARY KEY (OPER_ID_A)
);

CALL moveOperationsToNewTable();