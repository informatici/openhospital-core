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