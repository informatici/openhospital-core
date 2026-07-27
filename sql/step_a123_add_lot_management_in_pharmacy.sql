-- OP-1231 Lot Management: add the standalone Lot Management browser under the Pharmacy sub-menu

INSERT INTO OH_MENUITEM (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION) VALUES ('lotmanagement','angal.menu.btn.lotmanagement','angal.menu.lotmanagement','x','L','pharmacy','org.isf.lotmanagement.gui.LotBrowser','N',7);
INSERT INTO OH_GROUPMENU (GM_ID, GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE) VALUES (350,'admin','lotmanagement',1,NULL,NULL,NULL,NULL);
