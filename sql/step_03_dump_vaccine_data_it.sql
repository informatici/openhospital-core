delete from OH_VACCINE;
delete from OH_VACCINETYPE;

-- VACCINETYPE
LOAD DATA LOCAL INFILE './data_it/vaccinetype.csv'
	INTO TABLE OH_VACCINETYPE
	FIELDS TERMINATED BY ';' 
	LINES TERMINATED BY '\n';
	
-- VACCINE
LOAD DATA LOCAL INFILE './data_it/vaccine.csv'
	INTO TABLE OH_VACCINE
	FIELDS TERMINATED BY ';' 
	LINES TERMINATED BY '\n';
