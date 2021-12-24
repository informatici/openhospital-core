-- This script adds a "link button" to the DICOM in the Admission/Patient window

-- Add DICOM button in Admission/Patient
INSERT INTO MENUITEM VALUES ('btnadmdicom','angal.menu.btn.dicom','angal.menu.dicom','x','L','admission','none','N',4);
INSERT INTO GROUPMENU (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE) VALUES ('admin','btnadmdicom',1);
