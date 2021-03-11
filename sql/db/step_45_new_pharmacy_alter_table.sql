ALTER TABLE MEDICALDSRSTOCKMOV ADD COLUMN MMV_REFNO VARCHAR(50) NOT NULL DEFAULT ''  AFTER MMV_LOCK;
-- porting previous data
UPDATE MEDICALDSRSTOCKMOV AS t
INNER JOIN 
(SELECT DISTINCT(MMV_MMVT_ID_A) AS type FROM MEDICALDSRSTOCKMOV) AS t1 
ON t.MMV_MMVT_ID_A = t1.type 
SET MMV_REFNO = CONCAT("Auto-Refno-", MMV_MMVT_ID_A);

INSERT INTO MENUITEM VALUES ('btnpharmstockcharge','angal.menu.btn.btnpharmstockcharge','angal.menu.btnpharmstockcharge','x','C','medicalstock','none','N', 1);
INSERT INTO GROUPMENU (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE) VALUES ('admin','btnpharmstockcharge','Y');

INSERT INTO MENUITEM VALUES ('btnpharmstockdischarge','angal.menu.btn.btnpharmstockdischarge','angal.menu.btnpharmstockdischarge','x','D','medicalstock','none','N', 2);
INSERT INTO GROUPMENU (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE) VALUES ('admin','btnpharmstockdischarge','Y');