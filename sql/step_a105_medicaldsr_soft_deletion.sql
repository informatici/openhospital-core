ALTER TABLE OH_MEDICALDSR 
ADD COLUMN MDSR_DELETED CHAR(1) NOT NULL DEFAULT 'N' AFTER MDSR_LOCK;
