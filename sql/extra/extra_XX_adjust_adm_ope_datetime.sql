-- adjust operations datetime (if has not time), assuming creation date the source of truth, 00:00:00 as last choice
UPDATE OPERATIONROW SET OPER_OPDATE = CAST(CONCAT(DATE(OPER_OPDATE), ' ', COALESCE(TIME(OPER_OPDATE), TIME(OPER_CREATED_DATE), '00:00:00')) AS DATETIME);
