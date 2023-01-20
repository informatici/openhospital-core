CREATE TABLE OH_PATIENTHISTORY (
 	PAH_ID int(11) NOT NULL AUTO_INCREMENT,
	PAH_PAT_ID int NOT NULL,
	PAH_ACTIVE tinyint(1) NOT NULL DEFAULT 1,
	PAH_CREATED_BY varchar(50) DEFAULT NULL,
  	PAH_CREATED_DATE datetime DEFAULT NULL,
  	PAH_LAST_MODIFIED_BY varchar(50) DEFAULT NULL,
  	PAH_LAST_MODIFIED_DATE datetime DEFAULT NULL,
	PAH_FAM_NOTHING tinyint(1)  default 1 ,
	PAH_FAM_HYPER tinyint(1)  default 0 ,
	PAH_FAM_DRUGADD tinyint(1)  default 0 ,
	PAH_FAM_CARDIO tinyint(1)  default 0 ,
	PAH_FAM_INFECT tinyint(1)  default 0 ,
	PAH_FAM_ENDO tinyint(1)  default 0 ,
	PAH_FAM_RESP tinyint(1)  default 0 ,
	PAH_FAM_CANCER tinyint(1)  default 0 ,
	PAH_FAM_ORTO tinyint(1)  default 0 ,
	PAH_FAM_GYNO tinyint(1)  default 0 ,
	PAH_FAM_OTHER tinyint(1)  default 0 ,
	PAH_FAM_NOTE varchar(100) NULL ,
	PAH_PAT_CLO_NOTHING tinyint(1)  default 1 ,
	PAH_PAT_CLO_HYPER tinyint(1)  default 0 ,
	PAH_PAT_CLO_DRUGADD tinyint(1)  default 0 ,
	PAH_PAT_CLO_CARDIO tinyint(1)  default 0 ,
	PAH_PAT_CLO_INFECT tinyint(1)  default 0 ,
	PAH_PAT_CLO_ENDO tinyint(1)  default 0 ,
	PAH_PAT_CLO_RESP tinyint(1)  default 0 ,
	PAH_PAT_CLO_CANCER tinyint(1)  default 0 ,
	PAH_PAT_CLO_ORTO tinyint(1)  default 0 ,
	PAH_PAT_CLO_GYNO tinyint(1)  default 0 ,
	PAH_PAT_CLO_OTHER tinyint(1)  default 0 ,
	PAH_PAT_CLO_NOTE varchar(100) NULL ,
	PAH_PAT_OPN_NOTHING tinyint(1)  default 1 ,
	PAH_PAT_OPN_HYPER tinyint(1)  default 0 ,
	PAH_PAT_OPN_DRUGADD tinyint(1)  default 0 ,
	PAH_PAT_OPN_CARDIO tinyint(1)  default 0 ,
	PAH_PAT_OPN_INFECT tinyint(1)  default 0 ,
	PAH_PAT_OPN_ENDO tinyint(1)  default 0 ,
	PAH_PAT_OPN_RESP tinyint(1)  default 0 ,
	PAH_PAT_OPN_CANCER tinyint(1)  default 0 ,
	PAH_PAT_OPN_ORTO tinyint(1)  default 0 ,
	PAH_PAT_OPN_GYNO tinyint(1)  default 0 ,
	PAH_PAT_OPN_OTHER tinyint(1)  default 0 ,
	PAH_PAT_OPN_NOTE varchar(100) NULL ,
	PAH_PAT_SURGERY varchar(200) NULL ,
	PAH_PAT_ALLERGY varchar(100) NULL ,
	PAH_PAT_THERAPY	varchar(200) NULL ,
	PAH_PAT_MEDICINE varchar(200) NULL ,
	PAH_PAT_NOTE varchar(100) NULL ,
	PAH_PHY_NUTR_NOR tinyint(1)  default 1 ,
	PAH_PHY_NUTR_ABN varchar(30) NULL ,
	PAH_PHY_ALVO_NOR tinyint(1)  default 1 ,
	PAH_PHY_ALVO_ABN varchar(30) NULL ,
	PAH_PHY_DIURE_NOR tinyint(1)  NULL default 1 ,
	PAH_PHY_DIURE_ABN varchar(30) NULL ,
	PAH_PHY_ALCOOL tinyint(1)  default 0 ,
	PAH_PHY_SMOKE tinyint(1)  default 0 ,
	PAH_PHY_DRUG tinyint(1)  default 0 ,
	PAH_PHY_PERIOD_NOR tinyint(1)  default 1 ,
	PAH_PHY_PERIOD_ABN varchar(30) NULL ,
	PAH_PHY_MENOP tinyint(1)  default 0 ,
	PAH_PHY_MENOP_Y int  NULL ,
	PAH_PHY_HRT_NOR tinyint(1)  default 1 ,
	PAH_PHY_HRT_ABN varchar(30) NULL ,
	PAH_PHY_PREG tinyint(1)  default 0 ,
	PAH_PHY_PREG_N int  NULL ,
	PAH_PHY_PREG_BIRTH int  NULL ,
	PAH_PHY_PREG_ABORT int  NULL ,
	PAH_DATE_UPDATE	timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP ,

	INDEX ( PAH_PAT_ID  ) ,
	PRIMARY KEY ( PAH_ID )
) ENGINE=MyISAM;


--
-- CONSTRAINTS
--
ALTER TABLE OH_PATIENTHISTORY
	ADD CONSTRAINT FK_PATIENTHISTORY_PATIENT
	FOREIGN KEY (PAH_PAT_ID)
	REFERENCES PATIENT (PAT_ID)
	ON DELETE CASCADE
	ON UPDATE CASCADE;
	
-- Anamnesis Button
INSERT INTO OH_MENUITEM VALUES ('btnadmpatnewanamnesis','angal.patient.anamnesis','angal.patient.anamnesis','x','A','btnadmnew','none','N', 1);
INSERT INTO OH_MENUITEM VALUES ('btnadmpateditanamnesis','angal.patient.anamnesis','angal.patient.anamnesis','x','A','btnadmedit','none','N', 1);
INSERT INTO OH_MENUITEM VALUES ('btnadmanamnesis','angal.admission.anamnesis','angal.admission.anamnesis','x','A','admission','none','N', 1);
INSERT INTO OH_MENUITEM VALUES ('btnopdnewanamnesis','angal.opd.anamnesis','angal.opd.anamnesis','x','A','btnopdnew','none','N', 1);
INSERT INTO OH_MENUITEM VALUES ('btnopdeditanamnesis','angal.opd.anamnesis','angal.opd.anamnesis','x','A','btnopdedit','none','N', 1);


-- Admin activation (default: btnadmpatnewanamnesis, btnadmpateditanamnesis, btnopdnewxamination, btnopdeditxamination)
INSERT INTO OH_GROUPMENU (GM_ID,GM_UG_ID_A,GM_MNI_ID_A,GM_ACTIVE) VALUES  (190, 'admin', 'btnadmanamnesis',1);
INSERT INTO OH_GROUPMENU (GM_ID,GM_UG_ID_A,GM_MNI_ID_A,GM_ACTIVE) VALUES  (191, 'admin', 'btnadmpatnewanamnesis',1);
INSERT INTO OH_GROUPMENU (GM_ID,GM_UG_ID_A,GM_MNI_ID_A,GM_ACTIVE) VALUES  (192, 'admin', 'btnadmpateditanamnesis',1);
INSERT INTO OH_GROUPMENU (GM_ID,GM_UG_ID_A,GM_MNI_ID_A,GM_ACTIVE) VALUES  (193, 'admin', 'btnopdnewanamnesis',0);
INSERT INTO OH_GROUPMENU (GM_ID,GM_UG_ID_A,GM_MNI_ID_A,GM_ACTIVE) VALUES  (194, 'admin', 'btnopdeditanamnesis',0);	
	