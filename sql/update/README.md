-- Purpose of this folder

The SQL scripts contained in this folder can be used for updating an existing Open Hospital database.
The scripts must be executed in order; as an example, to upgrade a 1.10 installation to 1.14 the following steps should be perfomed:

update_1_10-1_11.sql
update_1_11-1_12.sql
update_1_12-1_13.sql
update_1_13-1_14.sql

-> Always perform a full database backup before use! -<
