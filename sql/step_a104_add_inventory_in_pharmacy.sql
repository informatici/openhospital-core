CREATE TABLE OH_MEDICALDSRINVENTORY (
	MINVT_ID int NOT NULL AUTO_INCREMENT,
	MINVT_STATUS varchar (10)  NOT NULL,
	MINVT_DATE datetime NOT NULL,
	MINVT_US_ID_A varchar (50) NOT NULL,
	MINVT_REFERENCE varchar (50) NOT NULL,
	MINVT_TYPE varchar(30) NOT NULL,
	MINVT_WRD_ID_A varchar(1) NULL DEFAULT NULL,
	MINVT_LOCK int NOT NULL default 0,
	MINVT_CREATED_BY VARCHAR(50) NULL DEFAULT NULL,
  	MINVT_CREATED_DATE datetime NULL DEFAULT NULL,
  	MINVT_LAST_MODIFIED_BY VARCHAR(50) NULL DEFAULT NULL,
  	MINVT_LAST_MODIFIED_DATE datetime NULL DEFAULT NULL,
  	MINVT_ACTIVE TINYINT(1) NOT NULL DEFAULT 1,
	PRIMARY KEY (MINVT_ID ),
	FOREIGN KEY (MINVT_US_ID_A) REFERENCES OH_USER (US_ID_A),
	FOREIGN KEY (MINVT_WRD_ID_A) REFERENCES OH_WARD (WRD_ID_A)
) ENGINE = INNODB DEFAULT CHARACTER SET utf8;

CREATE TABLE OH_MEDICALDSRINVENTORYROW (
	MINVTR_ID int NOT NULL AUTO_INCREMENT,
	MINVTR_THEORETIC_QTY float  NOT NULL default 0,
	MINVTR_REAL_QTY float  NOT NULL default 0,
	MINVTR_INVT_ID int NOT NULL,
	MINVTR_MDSR_ID int NOT NULL,
	MINVTR_LT_ID_A varchar (50) NULL,
	MINVTR_IS_NEW_LOT TINYINT(1) NOT NULL DEFAULT 0,
	MINVTR_LOCK int NOT NULL default 0,
	MINVTR_CREATED_BY VARCHAR(50) NULL DEFAULT NULL,
  	MINVTR_CREATED_DATE datetime NULL DEFAULT NULL,
  	MINVTR_LAST_MODIFIED_BY VARCHAR(50) NULL DEFAULT NULL,
  	MINVTR_LAST_MODIFIED_DATE datetime NULL DEFAULT NULL,
  	MINVTR_ACTIVE TINYINT(1) NOT NULL DEFAULT 1,
	PRIMARY KEY (MINVTR_ID ),
	FOREIGN KEY (MINVTR_INVT_ID) REFERENCES OH_MEDICALDSRINVENTORY (MINVT_ID),
	FOREIGN KEY (MINVTR_MDSR_ID) REFERENCES OH_MEDICALDSR (MDSR_ID),
	FOREIGN KEY (MINVTR_LT_ID_A) REFERENCES OH_MEDICALDSRLOT (LT_ID_A)
) ENGINE = INNODB DEFAULT CHARACTER SET utf8;

INSERT INTO OH_MENUITEM (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION) VALUES ('inventory','angal.menu.btn.inventory','angal.menu.invertory','x','I','pharmacy','org.isf.medicalinventory.gui.InventoryBrowser','N',6);
INSERT INTO OH_GROUPMENU (GM_ID, GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE) VALUES (345,'admin','inventory',1,NULL,NULL,NULL,NULL);
INSERT INTO OH_MENUITEM (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION) VALUES ('inventoryward','angal.menu.btn.inventoryward','angal.menu.inventoryward','x','P','pharmacy','org.isf.medicalinventory.gui.InventoryWardBrowser','N',5);
INSERT INTO OH_GROUPMENU (GM_ID, GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE) VALUES (346,'admin','inventoryward',1,NULL,NULL,NULL,NULL);
