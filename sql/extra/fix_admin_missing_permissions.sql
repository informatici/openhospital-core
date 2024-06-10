-- Fix "admin" user
UPDATE OH_GROUPPERMISSION SET GP_P_ID_A = '140' WHERE (GP_ID = '140');


UPDATE OH_PERMISSIONS SET P_NAME = 'vaccinetypes.create' WHERE (P_ID_A = '151');
UPDATE OH_PERMISSIONS SET P_NAME = 'vaccinetypes.read' WHERE (P_ID_A = '152');
UPDATE OH_PERMISSIONS SET P_NAME = 'vaccinetypes.update' WHERE (P_ID_A = '153');
UPDATE OH_PERMISSIONS SET P_NAME = 'vaccinetypes.delete' WHERE (P_ID_A = '154');
