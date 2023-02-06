-- This script adds a "link button" to the Laboratory in the Admission/Patient window

-- Add Laboratory button in Admission/Patient
INSERT INTO OH_MENUITEM VALUES ('btnadmlab','angal.menu.btn.laboratory','angal.menu.laboratory','x','L','admission','none','N',3);
INSERT INTO OH_GROUPMENU (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE) VALUES ('admin','btnadmlab',1);
