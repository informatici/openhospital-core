ALTER TABLE OPERATION
ADD COLUMN OPE_FOR CHAR(1) DEFAULT '1' COMMENT "'1' = OPD/IPD, '2' = IPD only, '3' = OPD only" AFTER OPE_STAT;

INSERT INTO MENUITEM VALUES ('btnopdnewoperation','angal.opd.operation','angal.opd.operation','x','A','btnopdnew','none','N',2);
INSERT INTO MENUITEM VALUES ('btnopdeditoperation','angal.opd.operation','angal.opd.operation','x','A','btnopdedit','none','N',2);

INSERT INTO GROUPMENU (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE) VALUES ('admin','btnopdnewoperation',1);
INSERT INTO GROUPMENU (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE) VALUES ('admin','btnopdeditoperation',1);
