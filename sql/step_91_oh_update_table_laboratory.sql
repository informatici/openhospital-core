ALTER TABLE LABORATORY ADD COLUMN LAB_STATUS VARCHAR(7) NULL DEFAULT 'draft'; -- draft, paid, done, invalid
UPDATE LABORATORY SET LAB_STATUS = 'done'; -- for previous data