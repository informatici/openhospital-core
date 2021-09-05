UPDATE MENUITEM SET MNI_POSITION='1' WHERE MNI_ID_A='btnbillnew';
UPDATE MENUITEM SET MNI_POSITION='2' WHERE MNI_ID_A='btnbilledit';
UPDATE MENUITEM SET MNI_POSITION='4' WHERE MNI_ID_A='btnbilldelete';
UPDATE MENUITEM SET MNI_POSITION='5' WHERE MNI_ID_A='btnbillreport';
UPDATE MENUITEM SET MNI_POSITION='6' WHERE MNI_ID_A='btnbillreceipt';

INSERT INTO MENUITEM (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION) VALUES ('cashiersfilter', 'angal.menu.accounting.cashiersfilter', 'angal.menu.accounting.cashiersfilter', 'x', 'X', 'billsmanager', 'none', 'N', '0');
INSERT INTO MENUITEM (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION) VALUES ('editclosedbills', 'angal.menu.accounting.editclosedbills', 'angal.menu.accounting.editclosedbills', 'x', 'E', 'billsmanager', 'none', 'N', '3');

INSERT INTO GROUPMENU (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE) VALUES ('admin', 'cashiersfilter', 1);
INSERT INTO GROUPMENU (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE) VALUES ('admin', 'editclosedbills', 1);
