-- See OP-760 [OH-GUI] Clinical Sheet report uses wrong OPD date

UPDATE OPD SET OPD_DATE = CAST(CONCAT(OPD_DATE_VIS, ' ', TIME(OPD_DATE)) AS DATETIME);

-- now we could drop OPD_DATE_VIS field for next release