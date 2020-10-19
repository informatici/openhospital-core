mysqldump --no-tablespaces --compact --skip-extended-insert --protocol tcp -u isf -pisf123 -h localhost oh > dump_mysql.sql
./mysql2sqlite dump_mysql.sql | sqlite3 mysqlite3.db